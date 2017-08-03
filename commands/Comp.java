package scripts.ossbot.commands;

import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Comp extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"endofcomp", "timeleft", "dpfund"},{"insertnewid"}};
	private int level = 0;

	public Comp()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {
		String fullCommand = OSSBotV2.getIssuerCommand();
		String[] commandParams = OssBotMethods.getcommandParams(fullCommand);

		level = OssBotMethods.findMaximumCommandLevel(commandParams, fullCommand);
		if(level > 0)
		{
			checkFirstParam(commandParams);
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}

	}

	private void checkFirstParam(String[] commandParams) {

		String realCommandName = BotFiles.checkLevelParams("level1", commandParams[0], COMMAND_NAME);

		if(realCommandName != null)
		{

			if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][0]))
			{
				if(level >= 2)
				{
					endOfComp(commandParams[1]);
				}
				else
				{
					Messenger.messageFormatter("Valid competition id required.");
				}
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][1]))
			{
				timeLeft();
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][2]))
			{
				calcDPFund();
			}
		}
	}

	private void calcDPFund() {
		File compWinsFile = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY + OssBotConstants.COMP_WINS_DATA_FILE);
		int finalAmount = 0;
		if(compWinsFile.exists())
		{
			try {
				Scanner reader = new Scanner(compWinsFile);
				int counter = 1;
				int lineCounter = 1;
				while(reader.hasNextLine())
				{
					String line = reader.nextLine();
					if(lineCounter == 3)
					{
						if(line.length() == 1 && line.equals("n"))
						{
							if(counter == 1)
							{
								finalAmount += 1000000;
							}
							else if(counter == 2)
							{
								finalAmount += 500000;
							}
							else if(counter == 3)
							{
								finalAmount += 250000;
							}
						}

						lineCounter = 1;
						if(counter == 3)
						{
							counter = 1;
							continue;
						}
						counter++;
						continue;
					}
					lineCounter++;
				}
				reader.close();

				Messenger.messageFormatter("There is currently: " + NumberFormat.getNumberInstance(Locale.US).format(finalAmount) + " gp in the drop party fund.");

			} catch (Exception e) {
				OssBotMethods.printException(e);
			}
		}

	}
	private void timeLeft() {

		ZonedDateTime now = ZonedDateTime.now();
		Long nowMillis = now.toInstant().toEpochMilli();
		Calendar date = Calendar.getInstance();
		int diff = Math.abs(Calendar.SATURDAY - date.get(Calendar.DAY_OF_WEEK));
		date.add(Calendar.DAY_OF_WEEK, diff);
		date.set(Calendar.HOUR_OF_DAY, 20);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.setTimeZone(TimeZone.getDefault());
		Long endTimeMillis = date.getTimeInMillis();
		Long difference = endTimeMillis - nowMillis;
		Messenger.messageFormatter(OssBotMethods.getIntervalFromMillis(difference) + " left till the competition ends.");
	}

	private void endOfComp(String newID) {
		try{
			Integer ID = Integer.valueOf(newID);

			File currentCompIDFile = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.CURRENT_COMP_ID_FILE);
			if(currentCompIDFile.exists())
			{
				try {
					Scanner reader = new Scanner(currentCompIDFile);
					Integer currentCompID = Integer.valueOf(reader.nextLine());
					reader.close();

					if(currentCompID.equals(ID))
					{
						Messenger.messageFormatter(ID + " is already set as the current competition ID. Nothing changed.");
						return;
					}
					else
					{
						File[] allComps = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY).listFiles();

						if(allComps != null)
						{
							FileWriter writer = new FileWriter(currentCompIDFile, false);
							writer.write(newID);
							writer.close();
							for(File comp : allComps)
							{
								if(comp.getName().equalsIgnoreCase(String.valueOf(currentCompID)))
								{
									File endOfCompFile = new File(comp.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.ENDOFCOMP_FILE);

									if(!endOfCompFile.exists())
									{
										endOfCompFile.createNewFile();
									}

									File dataFile = new File(comp.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.COMP_FILE);

									reader = new Scanner(dataFile);
									String finalOutput = "";
									Calendar date = Calendar.getInstance();
									int diff = Calendar.SATURDAY - date.get(Calendar.DAY_OF_WEEK);
									if(diff > 0)
									{
										diff -= 7;
									}

									date.add(Calendar.DAY_OF_WEEK, diff);

									String day = OssBotMethods.ordinal(Integer.valueOf(new SimpleDateFormat("d").format(date.getTime())));
									String month = new SimpleDateFormat("MMMM").format(date.getTime());
									String fullDate = day + " of " + month;
									boolean topThreeDone = false;
									finalOutput += fullDate + " - skill" + System.lineSeparator();
									int total = 0;
									while(reader.hasNextLine())
									{
										String[] line = reader.nextLine().split(",");
										if(!topThreeDone)
										{
											String[] line2 = reader.nextLine().split(",");
											String[] line3 = reader.nextLine().split(",");
											finalOutput += "[gold]1. " + line[0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line[3])) + " xp[/gold]" + System.lineSeparator();
											finalOutput += "[chocolate]2. " + line2[0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line2[3])) + " xp[/chocolate]" + System.lineSeparator();
											finalOutput += "[firebrick]3. " + line3[0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line3[3])) + " xp[/firebrick]" + System.lineSeparator();
											finalOutput += line[0] + "\t" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line[3])) + "\tn" + System.lineSeparator();
											finalOutput += line2[0] + "\t" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line2[3])) + "\tn" + System.lineSeparator();
											finalOutput += line3[0] + "\t" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line3[3])) + "\tn" + System.lineSeparator();
											finalOutput += line[0] + "\t'+" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line[3])) + System.lineSeparator();
											finalOutput += line2[0] + "\t'+" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line2[3])) + System.lineSeparator();
											finalOutput += line3[0] + "\t'+" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line3[3])) + System.lineSeparator();
											topThreeDone = true;
											total += Integer.valueOf(line[3]) + Integer.valueOf(line2[3]) + Integer.valueOf(line3[3]);
											continue;
										}
										finalOutput += line[0] + "\t'+" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(line[3]));
										finalOutput += System.lineSeparator();
										total += Integer.valueOf(line[3]);
									}
									finalOutput += " \t'+" + NumberFormat.getNumberInstance(Locale.US).format(total);
									reader.close();

									FileWriter fw = new FileWriter(endOfCompFile, false);
									fw.write(finalOutput);
									fw.close();

									Messenger.messageFormatter("Generated file and updated competition id to: " + newID);
								}
							}
						}
					}

				} catch (Exception e) {
					OssBotMethods.printException(e);
					return;
				}
			}
		} catch(NumberFormatException e)
		{
			Messenger.messageFormatter(newID + " is not a valid ID.");
			return;
		}
	}


	@Override
	public boolean canExecute() {
		if(Ranking.checkPermissions(COMMAND_NAME))
		{
			BotFiles.addToUsedCounter(COMMAND_NAME);
			return true;
		}
		return false;
	}

	@Override
	public boolean checkCallNames() {
		String[] VALID_COMMAND_NAMES = BotFiles.getValidCommandNames(COMMAND_NAME);
		if(OssBotMethods.isThisCommandCalled(VALID_COMMAND_NAMES))
		{
			return true;
		}
		return false;
	}

}
