package scripts.ossbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Joke extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};

	public Joke()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {

		printJoke();

	}
	private void printJoke() {
		File jokeFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.JOKES_FILE);
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
