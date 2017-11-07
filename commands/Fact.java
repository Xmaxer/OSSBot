package scripts.ossbot.commands;

import java.util.Arrays;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;

public class Fact extends Command{
	
	public Fact()
	{
		super(0, new String[][]{{"trivia", "math","year","date"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
		}
		else
		{
			getFact(super.getCommandParams()[0][0]);
		}
	}

	private void checkFirstParam() {
		
		String realCommandName = BotFiles.checkLevelParams("level1", super.getUserCommandParams()[0], super.getCommandName());
		
		if(realCommandName != null)
		{
			getFact(realCommandName);
			return;
		}
		
		Messenger.messageFormatter("Not a valid fact type. Try: " + Arrays.toString(super.getCommandParams()[0]));
	}
	private void getFact(String factType) {
		
		Messenger.messageFormatter(BotFiles.getLinkData(OssBotConstants.RANDOM_FACT_API + factType));
	}
}
