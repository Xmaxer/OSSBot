package scripts.ossbot.commands;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import org.tribot.api2007.Player;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Top extends Command{

	public Top()
	{
		super(0, new String[][]{{"comp", "charstyped", "timespent", "commandused"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}

	private void checkFirstParam() {
		String realCommandName = BotFiles.checkLevelParams("level1", super.getUserCommandParams()[0], super.getCommandName());

		if(realCommandName != null)
		{
			if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][0]))
			{
				topComp();
			}
			else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][1]))
			{
				topCharsTyped();
			}
			else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][2]))
			{
				topTimeSpent();
			}
			else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][3]))
			{
				topCommandUsed();
			}
		}

	}
	private void topCommandUsed() {
		File[] allCommandDirs = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();
		ArrayList<Integer> topThree = new ArrayList<Integer>();
		ArrayList<String> names = new ArrayList<String>();
		while(topThree.size() < 3)
		{
			topThree.add(0);
			names.add("unknown");
		}
		for(File commandDir : allCommandDirs)
		{
			File configFile = new File(commandDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties props = BotFiles.getProperties(configFile);
				String usedProp = props.getProperty("used");

				if(usedProp != null)
				{
					Integer used = Integer.valueOf(usedProp);

					if(used > topThree.get(0))
					{
						names.set(0, commandDir.getName());
						topThree.set(0, used);
					}
					else if(used > topThree.get(1))
					{
						names.set(1, commandDir.getName());
						topThree.set(1, used);
					}
					else if(used > topThree.get(2))
					{
						names.set(2, commandDir.getName());
						topThree.set(2, used);
					}

				}
			}
		}
		String output = "";
		for(int i = 0; i < 3; i++)
		{
			output += "[" + (i+1) + "] " + names.get(i) + ": " + NumberFormat.getNumberInstance(Locale.US).format(topThree.get(i)) + " ";
		}
		Messenger.messageFormatter(output);
	}
	private void topTimeSpent() {
		File[] allPlayerDirs = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();
		ArrayList<Long> topThree = new ArrayList<Long>();
		ArrayList<String> names = new ArrayList<String>();
		while(topThree.size() < 3)
		{
			topThree.add(0L);
			names.add("unknown");
		}
		for(File playerDir : allPlayerDirs)
		{
			if(!playerDir.getName().equalsIgnoreCase(OssBotMethods.standardiseName(Player.getRSPlayer().getName())))
			{
				File chatFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);

				if(chatFile.exists())
				{
					Properties props = BotFiles.getProperties(chatFile);
					String timeSpentProp = props.getProperty("timeSpent");

					if(timeSpentProp != null)
					{
						Long timeSpent = Long.valueOf(timeSpentProp);

						if(timeSpent > topThree.get(0))
						{
							names.set(0, playerDir.getName());
							topThree.set(0, timeSpent);
						}
						else if(timeSpent > topThree.get(1))
						{
							names.set(1, playerDir.getName());
							topThree.set(1, timeSpent);
						}
						else if(timeSpent > topThree.get(2))
						{
							names.set(2, playerDir.getName());
							topThree.set(2, timeSpent);
						}

					}
				}
			}
		}
		String output = "";
		for(int i = 0; i < 3; i++)
		{
			output += "[" + (i+1) + "] " + names.get(i) + ": " + OssBotMethods.getIntervalFromMillis(topThree.get(i)) + " ";
		}
		Messenger.messageFormatter(output);
	}
	private void topCharsTyped() {

		File[] allPlayerDirs = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();
		ArrayList<Integer> topThree = new ArrayList<Integer>();
		ArrayList<String> names = new ArrayList<String>();
		while(topThree.size() < 3)
		{
			topThree.add(0);
			names.add("unknown");
		}
		for(File playerDir : allPlayerDirs)
		{
			if(!playerDir.getName().equalsIgnoreCase(OssBotMethods.standardiseName(Player.getRSPlayer().getName())))
			{
				File chatFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TYPING_DATA);

				if(chatFile.exists())
				{
					Properties props = BotFiles.getProperties(chatFile);
					String charsTypedProp = props.getProperty("charsTyped");

					if(charsTypedProp != null)
					{
						Integer charsTyped = Integer.valueOf(charsTypedProp);

						if(charsTyped > topThree.get(0))
						{
							names.set(0, playerDir.getName());
							topThree.set(0, charsTyped);
						}
						else if(charsTyped > topThree.get(1))
						{
							names.set(1, playerDir.getName());
							topThree.set(1, charsTyped);
						}
						else if(charsTyped > topThree.get(2))
						{
							names.set(2, playerDir.getName());
							topThree.set(2, charsTyped);
						}

					}
				}
			}
		}
		String output = "";
		for(int i = 0; i < 3; i++)
		{
			output += "[" + (i+1) + "] " + names.get(i) + ": " + NumberFormat.getNumberInstance(Locale.US).format(topThree.get(i)) + " ";
		}
		Messenger.messageFormatter(output);
	}
	private void topComp() {

		File currentCompIDFile = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.CURRENT_COMP_ID_FILE);

		if(currentCompIDFile.exists())
		{
			try {
				Scanner reader = new Scanner(currentCompIDFile);
				Integer currentCompID = Integer.valueOf(reader.nextLine());
				reader.close();
				File[] allCompDirs = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY).listFiles();

				for(File compDir : allCompDirs)
				{
					if(compDir.getName().equalsIgnoreCase(String.valueOf(currentCompID)))
					{
						File dataFile = new File(compDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.COMP_FILE);

						reader = new Scanner(dataFile);

						String[][] data = {reader.nextLine().split(","), reader.nextLine().split(","), reader.nextLine().split(",")};

						Messenger.messageFormatter("[1] " + data[0][0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(data[0][3])) + " [2] " + data[1][0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(data[1][3])) + " [3] " + data[2][0] + ": +" + NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(data[2][3])));
						reader.close();
						return;
					}
				}
			} catch (Exception e) {
				OssBotMethods.printException(e);
				return;
			}
		}

	}
}
