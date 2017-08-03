package scripts.ossbot.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Time extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertTimeZone"}};
	private int level = 0;

	public Time()
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
			printDefaultTimeZone();
		}
	}

	private void checkFirstParam(String[] commandParams) {
		String realCommandName = BotFiles.checkLevelParams("level1", "insertTimeZone", COMMAND_NAME);
		
		if(realCommandName != null)
		{

			String[] tzs = TimeZone.getAvailableIDs();

			for(String tz : tzs)
			{
				if(tz.equalsIgnoreCase(commandParams[0]))
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
