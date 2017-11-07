package scripts.ossbot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Ball8 extends Command{

	public Ball8()
	{
		super(0, new String[][] {});
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
		File answersFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + "answers.txt");
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

}
