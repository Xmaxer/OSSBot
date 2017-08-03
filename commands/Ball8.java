package scripts.ossbot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Ball8 extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};

	public Ball8()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);

	}
	@Override
	public void execute() {
		if(isValidQuestion())
		{
			answerRandomly();
		}
		else
		{
			Messenger.messageFormatter("Input must be a question.");
		}
	}

	private void answerRandomly() {
		File answersFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + "answers.txt");
		if(answersFile.exists())
		{
			try {
				Scanner reader = new Scanner(answersFile);
				ArrayList<String> answers = new ArrayList<String>();
				
				while(reader.hasNextLine())
				{
					answers.add(reader.nextLine());
				}
				reader.close();
				Messenger.messageFormatter(answers.get(General.random(0, answers.size() - 1)));
			} catch (FileNotFoundException e) {
				OssBotMethods.printException(e);
			}
		}
		else
		{
			Messenger.messageFormatter("Answers.txt does not exist...Go tell someone");
		}
		
	}
	private boolean isValidQuestion() {
		
		if(OSSBotV2.getIssuerCommand().endsWith("?"))
		{
			return true;
		}
		return false;
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
