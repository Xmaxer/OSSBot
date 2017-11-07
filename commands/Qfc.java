package scripts.ossbot.commands;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.Messenger;

public class Qfc extends Command{

	public Qfc()
	{
		super(0, new String[][]{});
	}
	@Override
	public void execute() {
		
		Messenger.messageFormatter("320-321-359-65818431");
	}
}
