package scripts.ossbot.commands;

import java.io.File;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Data extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"favworld", "favchar", "chars", "time", "joindate", "comprank", "lastseen", "lastworld", "rank", "days", "compmoney"},{"insertplayername"}};
	private int level = 0;

	public Data()
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
			String playerName = (commandParams.length >= 2) ? commandParams[1] : OSSBotV2.getIssuerName();


			if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][0]))
			{
				getFavWorld(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][1]))
			{
				getFavChar(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][2]))
			{
				getChars(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][3]))
			{
				getTime(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][4]))
			{
				getJoindate(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][5]))
			{
				getComprank(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][6]))
			{
				getLastseen(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][7]))
			{
				getLastworld(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][8]))
			{
				getRank(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][9]))
			{
				getDays(playerName);
			}
			else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][10]))
			{
				getCompWins(playerName);
			}
		}
	}

	private void getCompWins(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					Integer moneyWon = Integer.valueOf(props.getProperty("moneyWon"));
					if(moneyWon != null)
					{
						Messenger.messageFormatter(playerName + " has " + NumberFormat.getNumberInstance(Locale.US).format(moneyWon) + " uncollected competition money.");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have moneyWon data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have sheet data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getDays(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					Long days = Long.valueOf(props.getProperty("days"));
					if(days != null)
					{
						Messenger.messageFormatter(playerName + " applied " + days + " days ago to the cc.");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have days data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have sheet data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}


	private void getRank(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					Integer lastSeenRank = Integer.valueOf(props.getProperty("lastSeenRank"));
					if(lastSeenRank != null)
					{
						Messenger.messageFormatter(playerName + " was last seen with rank of: " + Ranking.rankNumberToName(lastSeenRank));
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have lastSeenRank data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have cached data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getLastworld(String playerName) {
		RSInterface memberListParent = Interfaces.get(OssBotConstants.CC_PLAYER_INTERFACE[0], OssBotConstants.CC_PLAYER_INTERFACE[1]);

		if(memberListParent != null)
		{

			RSInterface[] memberList = memberListParent.getChildren();

			if(memberList != null && memberList.length >= 1)
			{

				for(int i = 0; i < memberList.length; i+=5)
				{
					String ccPlayerName = OssBotMethods.standardiseName(memberList[i].getText());
					if(ccPlayerName.equalsIgnoreCase(playerName))
					{
						String world = memberList[i + 1].getText().toLowerCase();
						Messenger.messageFormatter(playerName + " is currently in the clan chat in " + world);
						return;
					}
				}
			}
		}
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					String lastSeenWorld = String.valueOf(props.getProperty("lastSeenWorld"));
					if(lastSeenWorld != null)
					{
						Messenger.messageFormatter(playerName + " was last seen in world " + lastSeenWorld.replaceAll("[^\\d]", ""));
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have lastSeenWorld data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have cached data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getLastseen(String playerName) {

		RSInterface memberListParent = Interfaces.get(OssBotConstants.CC_PLAYER_INTERFACE[0], OssBotConstants.CC_PLAYER_INTERFACE[1]);

		if(memberListParent != null)
		{

			RSInterface[] memberList = memberListParent.getChildren();

			if(memberList != null && memberList.length >= 1)
			{

				for(int i = 0; i < memberList.length; i+=5)
				{
					String ccPlayerName = OssBotMethods.standardiseName(memberList[i].getText());
					if(ccPlayerName.equalsIgnoreCase(playerName))
					{
						Messenger.messageFormatter(playerName + " is currently in the clan chat.");
						return;
					}
				}
			}
		}

		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					Long lastSeenMillis = Long.valueOf(props.getProperty("lastSeen"));
					if(lastSeenMillis != null)
					{
						ZonedDateTime timeLastSeen = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastSeenMillis), ZoneId.systemDefault());

						Messenger.messageFormatter(playerName + " was last seen on " + timeLastSeen.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z")) + " (" + OssBotMethods.getIntervalFromMillis(ZonedDateTime.now().toInstant().toEpochMilli() - lastSeenMillis) + " ago)");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have lastSeen data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have timing data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}
	private void getComprank(String playerName) {

		File[] allComps = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY).listFiles();

		File currentIDFile = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.CURRENT_COMP_ID_FILE);

		if(currentIDFile.exists())
		{
			try {
				Scanner reader = new Scanner(currentIDFile);

				Integer currentID = Integer.valueOf(reader.nextLine());
				reader.close();

				if(currentID != null)
				{
					for(File compDir : allComps)
					{
						Integer compID = Integer.valueOf(compDir.getName());
						if(compID != null)
						{
							if(compID.equals(currentID))
							{
								File compDataFile = new File(compDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.COMP_FILE);
								if(compDataFile.exists())
								{
									reader = new Scanner(compDataFile);
									int position = 1;
									while(reader.hasNextLine())
									{
										String[] dataPieces = reader.nextLine().split(",");
										if(dataPieces.length == 4)
										{
											if(dataPieces[0].equalsIgnoreCase(playerName))
											{
												String xp = NumberFormat.getInstance(Locale.US).format(Integer.valueOf(dataPieces[3]));
												String ordinal = OssBotMethods.ordinal(position);
												Messenger.messageFormatter(playerName + " is currently " + ordinal + " with " + xp + " xp");
												reader.close();
												return;
											}
										}
										position++;
									}
									reader.close();
								}
							}
						}
					}
				}
			} catch (Exception e) {
				OssBotMethods.printException(e);
				return;
			}
		}
		Messenger.messageFormatter("Could not find the current competition ID");
	}

	private void getJoindate(String playerName) {

		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File sheetDataFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);
				if(sheetDataFile.exists())
				{
					Properties props = BotFiles.getProperties(sheetDataFile);

					String joinDate = String.valueOf(props.getProperty("joinDate"));

					if(joinDate != null)
					{
						if(joinDate.length() <= 3)
						{
							Messenger.messageFormatter("Who knows when " + playerName + " applied? Not me.");
							return;
						}
						Messenger.messageFormatter(playerName + " applied on " + joinDate + " to join the cc.");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have joinDate data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have sheet data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");

	}

	private void getTime(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File timeFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);
				if(timeFile.exists())
				{
					Properties props = BotFiles.getProperties(timeFile);

					Long timeSpent = Long.valueOf(props.getProperty("timeSpent"));
					if(timeSpent != null)
					{
						Messenger.messageFormatter(playerName + " has spent " + OssBotMethods.getIntervalFromMillis(timeSpent) + " in the cc.");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have timeSpent data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have timing data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getChars(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File chatFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TYPING_DATA);
				if(chatFile.exists())
				{
					Properties props = BotFiles.getProperties(chatFile);

					Integer charsTyped = Integer.valueOf(props.getProperty("charsTyped"));
					if(charsTyped != null)
					{
						Messenger.messageFormatter(playerName + " has typed '" + charsTyped + "' characters.");
					}
					else
					{
						Messenger.messageFormatter(playerName + " does have charsTyped data.");
					}
					return;
				}
				Messenger.messageFormatter(playerName + " does not have character data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getFavChar(String playerName) {
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File chatFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TYPING_DATA);
				if(chatFile.exists())
				{
					Properties props = BotFiles.getProperties(chatFile);
					ArrayList<?> allProps = Collections.list(props.propertyNames());
					int mostTypes = 0;
					String mostTyped = "none";
					for(Object propObj : allProps)
					{
						String prop = String.valueOf(propObj);

						if(prop.contains("-typed") && !prop.contains("space"))
						{
							int amountTyped = Integer.valueOf(props.getProperty(prop));

							if(amountTyped > mostTypes)
							{
								mostTypes = amountTyped;
								mostTyped = prop.replace("-typed", "");
							}
						}
					}

					Messenger.messageFormatter(playerName + " has typed the character '" + mostTyped + "' the most (" + mostTypes + " times)");
					return;
				}
				Messenger.messageFormatter(playerName + " does not have character data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");
	}

	private void getFavWorld(String playerName) {

		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(playerName))
			{
				File timeFile = new File(playerDir + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);
				if(timeFile.exists())
				{
					Properties props = BotFiles.getProperties(timeFile);
					ArrayList<?> allProps = Collections.list(props.propertyNames());
					Long longestTime = 0L;
					String world = "none";
					for(Object propObj : allProps)
					{
						String prop = String.valueOf(propObj);

						if(prop.contains("World"))
						{
							Long timeSpentInWorld = Long.valueOf(props.getProperty(prop));

							if(timeSpentInWorld > longestTime)
							{
								longestTime = timeSpentInWorld;
								world = prop.replaceAll("\\D+", "");
							}
						}
					}

					Messenger.messageFormatter(playerName + " spent most of their time in world " + world + " (" + OssBotMethods.getIntervalFromMillis(longestTime) + ")");
					return;
				}
				Messenger.messageFormatter(playerName + " does not have timing data.");
				return;
			}
		}

		Messenger.messageFormatter(playerName + " could not be found.");

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
