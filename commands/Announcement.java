package scripts.ossbot.commands;

import java.io.File;
import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Properties;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Announcement extends Command{


	public Announcement()
	{
		super(4, new String[][]{{"add", "remove"},{"insertId", "insertMessage"},{"insertInterval"}});
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
			if(super.getLevel() >= 2)
			{
				if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][0]))
				{
					if(super.getLevel() >= 3)
					{
						addCommand();
					}
					else
					{
						Messenger.messageFormatter("Must include one of the level 3 parameters: " + Arrays.toString(super.getCommandParams()[2]));
					}
					return;
				}
				else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][1]))
				{
					removeCommand();
				}
			}
			else
			{
				Messenger.messageFormatter("Must include one of the level 2 parameters: " + Arrays.toString(super.getCommandParams()[1]));
			}
			return;
		}

	}
	private void removeCommand() {
		File configFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR + "level2" + OssBotConstants.SEPARATOR + super.getCommandParams()[1][0] + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

		Properties props = BotFiles.getProperties(configFile);

		if(Ranking.hasCommandPrivileges(props))
		{
			Integer toDelete = Integer.valueOf(super.getUserCommandParams()[1]);
			if(toDelete != null)
			{
				File[] allAnnouncements = new File(OssBotConstants.ANNOUNCER_DIRECTORY).listFiles();

				if(allAnnouncements != null && allAnnouncements.length >= 1)
				{
					for(File announcementID : allAnnouncements)
					{
						Integer ID = Integer.valueOf(announcementID.getName());
						if(ID != null)
						{
							if(ID == toDelete)
							{

								deleteDirectory(announcementID);
								Messenger.messageFormatter("Successfully deleted announcement with I.D: " + toDelete);
								return;
							}
						}
					}
					Messenger.messageFormatter("Could not find announcement with I.D: " + toDelete);
					return;
				}
				Messenger.messageFormatter("There are currently no announcements to delete.");
				return;
			}
			Messenger.messageFormatter("Level 2 parameter must be a valid integer I.D");
			return;
		}
		Messenger.messageFormatter("You don't have the required permissions for the parameter: " + super.getCommandParams()[1][0]);
	}
	private void addCommand() {
		final Long minimumInterval = OssBotConstants.MINIMUM_INTERVAL_FOR_ANNOUNCEMENT;
		File configFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR + "level2" + OssBotConstants.SEPARATOR + super.getCommandParams()[1][1] + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

		Properties props = BotFiles.getProperties(configFile);

		if(Ranking.hasCommandPrivileges(props))
		{
			if(!super.getUserCommandParams()[1].isEmpty())
			{
				configFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR + "level3" + OssBotConstants.SEPARATOR + super.getCommandParams()[2][0] + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);
				props = BotFiles.getProperties(configFile);

				if(Ranking.hasCommandPrivileges(props))
				{
					Long timeInMillis = OssBotMethods.getTimeInMillis(super.getUserCommandParams()[2]);
					if(timeInMillis >= minimumInterval)
					{
						Properties announcerProps = new Properties();

						announcerProps.setProperty("interval", String.valueOf(timeInMillis));
						announcerProps.setProperty("madeBy", OSSBotV2.getIssuerName());
						announcerProps.setProperty("lastAnnounced", String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));
						announcerProps.setProperty("announced", "0");

						File[] allAnnouncements = new File(OssBotConstants.ANNOUNCER_DIRECTORY).listFiles();
						int highestID = 0;
						if(allAnnouncements != null && allAnnouncements.length >= 1)
						{
							for(File announcementID : allAnnouncements)
							{
								Integer ID = Integer.valueOf(announcementID.getName());
								if(ID != null)
								{
									if(ID > highestID)
									{
										highestID = ID;
									}
								}
							}
						}

						highestID = highestID + 1;
						File announcementDir = new File(OssBotConstants.ANNOUNCER_DIRECTORY + highestID + OssBotConstants.SEPARATOR);
						announcementDir.mkdirs();

						File announcementConfig = new File(announcementDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

						BotFiles.storeProperties(announcerProps, announcementConfig);

						File announcementMessageFile = new File(announcementDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.ANNOUNCEMENT_FILE);
						try {
							if(!announcementMessageFile.exists())
							{
								announcementMessageFile.createNewFile();
							}

							FileWriter fw = new FileWriter(announcementMessageFile, false);
							fw.write(super.getUserCommandParams()[1].replaceAll("_", " "));
							fw.close();
							Messenger.messageFormatter("Added new announcement with I.D: " + highestID + " with the interval: " + OssBotMethods.getIntervalFromMillis(timeInMillis));
							return;
						} catch(Exception e) {
							OssBotMethods.printException(e);
							return;
						}
					}
					Messenger.messageFormatter("Interval must be greater than or equal to: " + OssBotMethods.getIntervalFromMillis(minimumInterval));
					return;
				}
				Messenger.messageFormatter("You don't have the required permissions for the parameter: " + super.getCommandParams()[2][0]);
				return;
			}
			Messenger.messageFormatter("There must be a message included in the announcement.");
			return;
		}
		Messenger.messageFormatter("You don't have the required permissions for the parameter: " + super.getCommandParams()[1][1]);
	}
	private boolean deleteDirectory(File directory) {
		if(directory.exists())
		{
			File[] files = directory.listFiles();
			if(files != null)
			{
				for(int i = 0; i < files.length; i++) 
				{
					if(files[i].isDirectory()) 
					{
						deleteDirectory(files[i]);
					}
					else 
					{
						files[i].delete();
					}
				}
			}
		}
		return(directory.delete());
	}



}
