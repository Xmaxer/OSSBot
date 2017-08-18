package scripts.ossbot.threads;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Announcer implements Runnable {

	@Override
	public void run() {
		while(true)
		{
			if(OSSBotV2.getLoggedIn() && !OSSBotV2.getLock() && OSSBotV2.getInCC())
			{
				File[] allAnnouncementDirs = new File(OssBotConstants.ANNOUNCER_DIRECTORY).listFiles();

				if(allAnnouncementDirs != null && allAnnouncementDirs.length > 0)
				{
					for(File announcementDir : allAnnouncementDirs)
					{
						long now = ZonedDateTime.now().toInstant().toEpochMilli();
						File propertiesFile = new File(announcementDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

						Properties props = BotFiles.getProperties(propertiesFile);

						Long lastAnnounced = Long.valueOf(props.getProperty("lastAnnounced"));
						Long interval = Long.valueOf(props.getProperty("interval"));
						Integer used = Integer.valueOf(props.getProperty("announced"));

						if(lastAnnounced != null && interval != null && (now - lastAnnounced) >= interval)
						{
							OSSBotV2.setLock(true);
							File messageFile = new File(announcementDir + OssBotConstants.SEPARATOR + OssBotConstants.ANNOUNCEMENT_FILE);
							try {
								Scanner reader = new Scanner(messageFile);
								String announcement = "";
								if(reader.hasNextLine())
								{
									announcement = reader.nextLine();
								}
								reader.close();
								OSSBotV2.setPM(false);
								Messenger.messageFormatter("[" + announcementDir.getName() + "] " + announcement);
								props.setProperty("lastAnnounced", String.valueOf(now));
								if(used != null)
								{
									used++;
									props.setProperty("announced", String.valueOf(used));
								}
								BotFiles.storeProperties(props, propertiesFile);
							} catch (Exception e) {
								OssBotMethods.printException(e);
							}
						}
						OSSBotV2.setLock(false);
					}
				}
			}
			General.sleep(1000);
		}
		
	}

}
