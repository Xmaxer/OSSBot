package scripts.ossbot.commands;

import java.io.File;
import java.util.Properties;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Examine extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertName"},{"insertExamine"}};
	private int level = 0;

	public Examine()
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
		String name = OssBotMethods.standardiseName(commandParams[0]);
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(name))
			{
				File cacheFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);

				if(cacheFile.exists())
				{
					Properties props = BotFiles.getProperties(cacheFile);

					if(level >= 2)
					{
						if(BotFiles.checkLevelParams("level2", "insertExamine", COMMAND_NAME) != null)
						{
							String rankString = props.getProperty("lastSeenRank");
							if(rankString != null)
							{
								Integer rank = Integer.valueOf(rankString);

								if(OSSBotV2.getIssuerRank() >= rank)
								{
									String examine = commandParams[1].replaceAll("_", " ");
									if(!examine.isEmpty())
									{
										props.setProperty("examine", examine);
										BotFiles.storeProperties(props, cacheFile);
										Messenger.messageFormatter("Successfuly set the examine for " + name);
									}
									else
									{
										Messenger.messageFormatter("Examine must not be empty");
									}
								}
								else
								{
									Messenger.messageFormatter("You are outranked by " + name + " and therefore cannot set the examine.");
								}
							}
							else
							{
								String examine = commandParams[1].replaceAll("_", " ");
								if(!examine.isEmpty())
								{
									Properties props2 = new Properties();
									props2.setProperty("examine", examine);
									BotFiles.storeProperties(props2, cacheFile);
									Messenger.messageFormatter("Successfuly set the examine for " + name);
								}
								else
								{
									Messenger.messageFormatter("Examine must not be empty");
								}
							}
						}
					}
					else
					{
						String examine = props.getProperty("examine");

						if(examine != null)
						{
							Messenger.messageFormatter("\"" + examine + "\"");
						}
						else
						{
							Messenger.messageFormatter("Nothing interesting happens.");
						}
					}

				}
				else
				{
					if(level >= 2)
					{
						if(BotFiles.checkLevelParams("level2", "insertExamine", COMMAND_NAME) != null)
						{
							String examine = commandParams[1].replaceAll("_", " ");
							if(!examine.isEmpty())
							{
								Properties props = new Properties();
								props.setProperty("examine", examine);
								BotFiles.storeProperties(props, cacheFile);
								Messenger.messageFormatter("Successfuly set the examine for " + name);
							}
							else
							{
								Messenger.messageFormatter("Examine must not be empty");
							}
						}
					}
					else
					{
						Messenger.messageFormatter("Nothing interesting happens.");
					}
				}
				return;
			}
		}
		Messenger.messageFormatter("Couldn't find player: " + name);
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
