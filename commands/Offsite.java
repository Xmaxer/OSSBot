package scripts.ossbot.commands;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.Messenger;

public class Offsite extends Command{
	
	public Offsite()
	{
		super(0 , new String[][] {});
	}
	@Override
	public void execute() {
		Messenger.messageFormatter("Ossociety—.—o—r—g");
	}
}
