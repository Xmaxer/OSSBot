package scripts.ossbot.threads;

import java.io.File;
import java.time.ZonedDateTime;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Tracker implements Runnable {

	@Override
	public void run() {
		while(true)
		{
			if(OSSBotV2.getLoggedIn() && OSSBotV2.getInCC())
			{
				RSInterface memberListParent = Interfaces.get(OssBotConstants.CC_PLAYER_INTERFACE[0], OssBotConstants.CC_PLAYER_INTERFACE[1]);

				if(memberListParent != null)
				{

					RSInterface[] memberList = memberListParent.getChildren();

					if(memberList != null && memberList.length >= 1)
					{
						Long currentMillis = ZonedDateTime.now().toInstant().toEpochMilli();
						Long diff = currentMillis - OSSBotV2.getLastTracked();
						if(diff >= 300000L)
						{
							BotFiles.trackPlayersInCC(memberList);
							OSSBotV2.setLastTracked(currentMillis);
						}
						for(int i = 0; i < memberList.length; i+=5)
						{
							String playerName = OssBotMethods.standardiseName(memberList[i].getText());
							String playerWorld = memberList[i + 1].getText().replaceAll(" ", "");

							File playerPropertiesDirectory = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR);
							playerPropertiesDirectory.mkdirs();

							File playerTimePropertiesFile = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);

							if(!playerTimePropertiesFile.exists())
							{
								if(!OSSBotV2.getLock())
								{
									OSSBotV2.setLock(true);
									OSSBotV2.setPM(false);
									BotFiles.createPlayerProperties(playerTimePropertiesFile);
									BotFiles.botLogger("New player: " + playerName);
									Messenger.messageFormatter("Welcome " + playerName + " to the clan chat!");
									OSSBotV2.setLock(false);
								}
							}

							BotFiles.updateTimeProperties(playerWorld, playerTimePropertiesFile);

							File playerCachePropertiesFile = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);

							if(!playerCachePropertiesFile.exists())
							{
								BotFiles.createPlayerProperties(playerCachePropertiesFile);
							}

							BotFiles.updateCacheProperties(playerCachePropertiesFile, playerWorld, playerName);
						}
					}
				}
			}
			else
			{
				BotFiles.botLogger("Not tracking!");
			}

			General.sleep(1000);

		}
	}

}
