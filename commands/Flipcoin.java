package scripts.ossbot.commands;

import org.tribot.api.General;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.Messenger;

public class Flipcoin extends Command{

	public Flipcoin()
	{
		super(0, new String[][] {});
	}
	
	@Override
	public void execute() {
		Messenger.messageFormatter((General.random(0, 1) == 1) ? "Heads" : "Tails");
	}
}
