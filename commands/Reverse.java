package scripts.ossbot.commands;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.Messenger;

public class Reverse extends Command{

	public Reverse()
	{
		super(0, new String[][] {});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			reverseSentence(String.join(" ", super.getUserCommandParams()));
		}
		else
		{
			Messenger.messageFormatter("This command requires something to reverse.");
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
}
