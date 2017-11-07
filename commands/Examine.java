package scripts.ossbot.commands;

import java.io.File;
import java.util.Properties;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Examine extends Command{

	public Examine()
	{
		super(0, new String[][]{{"insertName"},{"insertExamine"}});
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
		String name = OssBotMethods.standardiseName(super.getUserCommandParams()[0]);
		File[] allPlayers = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayers)
		{
			if(playerDir.getName().equalsIgnoreCase(name))
			{
				File cacheFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);

				if(cacheFile.exists())
				{
					Properties props = BotFiles.getProperties(cacheFile);

					if(super.getLevel() >= 2)
					{
						if(BotFiles.checkLevelParams("level2", "insertExamine", super.getCommandName()) != null)
						{
							String rankString = props.getProperty("lastSeenRank");
							if(rankString != null)
							{
								Integer rank = Integer.valueOf(rankString);

								if(OSSBotV2.getIssuerRank() >= rank)
								{
									String examine = super.getUserCommandParams()[1].replaceAll("_", " ");
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
								String examine = super.getUserCommandParams()[1].replaceAll("_", " ");
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
					if(super.getLevel() >= 2)
					{
						if(BotFiles.checkLevelParams("level2", "insertExamine", super.getCommandName()) != null)
						{
							String examine = super.getUserCommandParams()[1].replaceAll("_", " ");
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
}
