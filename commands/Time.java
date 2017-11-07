package scripts.ossbot.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;

public class Time extends Command{

	public Time()
	{
		super(0, new String[][]{{"insertTimeZone"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
		}
		else
		{
			printDefaultTimeZone();
		}
	}

	private void checkFirstParam() {
		String realCommandName = BotFiles.checkLevelParams("level1", "insertTimeZone", super.getCommandName());
		
		if(realCommandName != null)
		{

			String[] tzs = TimeZone.getAvailableIDs();

			for(String tz : tzs)
			{
				if(tz.equalsIgnoreCase(super.getUserCommandParams()[0]))
				{
					Messenger.messageFormatter(ZonedDateTime.now(TimeZone.getTimeZone(tz).toZoneId()).format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z")));
					return;
				}
			}
			printDefaultTimeZone();
			return;
		}
	}
	private void printDefaultTimeZone() {

		Messenger.messageFormatter(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z")));

	}
}
