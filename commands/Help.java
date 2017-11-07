package scripts.ossbot.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;

public class Help extends Command{

	public Help()
	{
		super(0, new String[][]{{"insertCommandName"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
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
	private void checkFirstParam() {

		File[] allCommandDirs = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();

		for(File commandDir : allCommandDirs)
		{
			File commandPropertyFile = new File(commandDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			Properties props = BotFiles.getProperties(commandPropertyFile);
			String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

			for(String commandName : commandNames)
			{
				if(commandName.equalsIgnoreCase(super.getUserCommandParams()[0]))
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
}
