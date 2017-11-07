package scripts.ossbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Joke extends Command{

	public Joke()
	{
		super(0, new String[][] {});
	}
	@Override
	public void execute() {

		printJoke();

	}
	private void printJoke() {
		File jokeFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.JOKES_FILE);
		if(jokeFile.exists())
		{
			try {
				Scanner reader = new Scanner(jokeFile);
				ArrayList<String> jokes = new ArrayList<String>();
				while(reader.hasNextLine())
				{
					jokes.add(reader.nextLine());
				}
				reader.close();
				Messenger.messageFormatter(jokes.get(General.random(0, jokes.size() - 1)));
				return;
			} catch (Exception e) {
				OssBotMethods.printException(e);
			}
			
		}
	}
}
