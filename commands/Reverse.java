package scripts.ossbot.commands;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Reverse extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};
	private int level = 0;

	public Reverse()
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
			reverseSentence(String.join(" ", commandParams));
		}
		else
		{
			Messenger.messageFormatter("This command requires something to capitalise.");
		}
		

	}
	private void reverseSentence(String sentence) {
		
		char[] sentenceChars = sentence.toCharArray();
		String reversed = "";
		
		for(int i = sentenceChars.length - 1; i >= 0; i--)
		{
			reversed += Character.toString(sentenceChars[i]);
		}
		
		Messenger.messageFormatter(reversed);
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
