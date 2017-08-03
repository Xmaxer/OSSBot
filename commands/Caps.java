package scripts.ossbot.commands;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Caps extends Command{

	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};
	private int level = 0;

	public Caps()
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
			capitalise(commandParams);
		}
		else
		{
			Messenger.messageFormatter("This command requires something to capitalise.");
		}

	}

	private void capitalise(String[] commandParams) {
		
		for(int i = 0, n = commandParams.length; i < n; i ++)
		{
			commandParams[i] = commandParams[i].replaceAll("_", "").toUpperCase();
		}
		
		char[] allChars = String.join(" ", commandParams).toCharArray();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0, n = allChars.length; i < n; i++)
		{
			sb.append("—" + allChars[i]);
		}
		
		Messenger.messageFormatter(sb.toString());
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
