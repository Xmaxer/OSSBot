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

	public Levelup()
	{
		super(0, new String[][]{{"insertLevel", "add"},{"insertSkill", "insertCustomMessage"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}
	private void checkFirstParam() {

		File addConfig = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level1" + OssBotConstants.SEPARATOR + "add" + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

		Properties props = BotFiles.getProperties(addConfig);
		String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

		for(String commandName : commandNames)
		{
			if(commandName.equalsIgnoreCase(super.getUserCommandParams()[0]))
			{
				if(Ranking.hasCommandPrivileges(props))
				{
					if(super.getLevel() >= 2)
					{
						addCommand(super.getUserCommandParams()[1]);
					}
					return;
				}
				return;
			}
		}
		String skill = "";
		Integer level = 0;
		try{
			level = Integer.valueOf(super.getUserCommandParams()[0]);
		} catch(Exception e)
		{
			level = -99;
		}
		if(super.getLevel() < 2 || super.getUserCommandParams()[1].length() <= 1)
		{
			skill = "???";
		}

		skill = super.getUserCommandParams()[1].replaceAll("_", " ");
		try {
			Scanner reader = new Scanner(new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.LEVELUP_TEXT_FILE));
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

		try {
			FileWriter pw = new FileWriter(new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.LEVELUP_TEXT_FILE), true);

			pw.write(message + System.lineSeparator());
			pw.close();

			Messenger.messageFormatter("Successfully added levelup message");
		} catch (Exception e) {
			OssBotMethods.printException(e);
			return;
		}
	}
}
