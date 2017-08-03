package scripts.ossbot.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Help extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertCommandName"}};
	private int level = 0;

	public Help()
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
			noParam();
		}
	}

	private void noParam() {
		
		File[] allCommandDirs = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();
		String output = "";
		for(File commandDir : allCommandDirs)
		{
			output += commandDir.getName() + " ";
		}
		Messenger.messageFormatter(output);
	}
	private void checkFirstParam(String[] commandParams) {

		File[] allCommandDirs = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();

		for(File commandDir : allCommandDirs)
		{
			File commandPropertyFile = new File(commandDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			Properties props = BotFiles.getProperties(commandPropertyFile);
			String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

			for(String commandName : commandNames)
			{
				if(commandName.equalsIgnoreCase(commandParams[0]))
				{
					String output = "!!" + commandName + " ";
					File paramDir = new File(commandDir + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR);
					if(paramDir.exists())
					{
						File[] allLevels = paramDir.listFiles();

						for(File level : allLevels)
						{
							String[] allParamInLevelDirs = new File(level.getAbsolutePath() + OssBotConstants.SEPARATOR).list();

							for(int i = 0, n = allParamInLevelDirs.length; i < n; i++)
							{
								allParamInLevelDirs[i] = allParamInLevelDirs[i].replace(level.getAbsolutePath() + OssBotConstants.SEPARATOR, "");
							}

							output += Arrays.toString(allParamInLevelDirs) + " ";

						}
					}
					else
					{
						output += "takes no parameters";
					}
					
					Messenger.messageFormatter(output);
				}
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
