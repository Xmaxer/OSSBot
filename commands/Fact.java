package scripts.ossbot.commands;

import java.util.Arrays;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Fact extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"trivia", "math","year","date"}};
	private int level = 0;
	
	public Fact()
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
			getFact(STATIC_COMMAND_PARAMS[0][0]);
		}
	}

	private void checkFirstParam(String[] commandParams) {
		
		String realCommandName = BotFiles.checkLevelParams("level1", commandParams[0], COMMAND_NAME);
		
		if(realCommandName != null)
		{
			getFact(realCommandName);
			return;
		}
		
		Messenger.messageFormatter("Not a valid fact type. Try: " + Arrays.toString(STATIC_COMMAND_PARAMS[0]));
	}
	private void getFact(String factType) {
		
		Messenger.messageFormatter(BotFiles.getLinkData(OssBotConstants.RANDOM_FACT_API + factType));
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
