package scripts.ossbot.commands;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.Messenger;

public class Caps extends Command{

	public Caps()
	{
		super(0, new String[][]{});
	}
	@Override
	public void execute() {
		
		if(super.getLevel() > 0)
		{
			capitalise();
		}
		else
		{
			Messenger.messageFormatter("This command requires something to capitalise.");
		}

	}

	private void capitalise() {
		
		for(int i = 0, n = super.getUserCommandParams().length; i < n; i ++)
		{
			super.getUserCommandParams()[i] = super.getUserCommandParams()[i].replaceAll("_", "").toUpperCase();
		}
		
		char[] allChars = String.join(" ", super.getUserCommandParams()).toCharArray();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0, n = allChars.length; i < n; i++)
		{
			sb.append("—" + allChars[i]);
		}
		
		Messenger.messageFormatter(sb.toString());
	}

}
