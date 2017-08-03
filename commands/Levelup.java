package scripts.ossbot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.tribot.api.General;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Levelup extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertLevel", "add"},{"insertSkill", "insertCustomMessage"}};
	private int level = 0;

	public Levelup()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {
		String fullCommand = OSSBotV2.getIssuerCommand();
		String[] commandParams = OssBotMethods.getcommandParams(fullCommand);

		level = OssBotMethods.findMaximumCommandLevel(commandParams, fullCommand);
		if(level > 0)
		{
			checkFirstParam(commandParams);
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}
	private void checkFirstParam(String[] commandParams) {

		File addConfig = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level1" + OssBotConstants.SEPARATOR + "add" + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

		Properties props = BotFiles.getProperties(addConfig);
		String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

		for(String commandName : commandNames)
		{
			if(commandName.equalsIgnoreCase(commandParams[0]))
			{
				if(Ranking.hasCommandPrivileges(props))
				{
					if(level >= 2)
					{
						addCommand(commandParams[1]);
					}
					return;
				}
				return;
			}
		}
		String skill = "";
		Integer level = 0;
		try{
			level = Integer.valueOf(commandParams[0]);
		} catch(Exception e)
		{
			level = -99;
		}
		if(level < 2 || commandParams[1].length() <= 1)
		{
			skill = "???";
		}
		
		skill = commandParams[1].replaceAll("_", " ");
		try {
			Scanner reader = new Scanner(new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.LEVELUP_TEXT_FILE));
			ArrayList<String> allMessages = new ArrayList<String>();
			
			while(reader.hasNextLine())
			{
				allMessages.add(reader.nextLine());
			}
			reader.close();
			
			Messenger.messageFormatter(allMessages.get(General.random(0, allMessages.size() - 1)).replaceAll("\\{skill\\}", skill).replaceAll("\\{level\\}", String.valueOf(level)).replaceAll("\\{name\\}", OSSBotV2.getIssuerName()));
			
		} catch (FileNotFoundException e) {
			OssBotMethods.printException(e);
		}


	}
	private void addCommand(String message) {

		message = message.replaceAll("_", " ");
		//Matcher skillMatcher = Pattern.compile("\\{skill\\}").matcher(message);
		//Matcher levelMatcher = Pattern.compile("\\{level\\}").matcher(message);

/*		if(skillMatcher.find() && levelMatcher.find())
		{ */
			try {
				FileWriter pw = new FileWriter(new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.LEVELUP_TEXT_FILE), true);

				pw.write(message + System.lineSeparator());
				pw.close();

				Messenger.messageFormatter("Successfully added levelup message");
			} catch (Exception e) {
				OssBotMethods.printException(e);
				return;
			}
		
		//Messenger.messageFormatter("Must have a {skill} and {level} in the message somewhere.");
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
