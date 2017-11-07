package scripts.ossbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Config extends Command{

	public Config()
	{
		super(4, new String[][]{{"insertCommandName"},{"remove", "add", "disable", "change"},{"minimumRank","exception", "restriction","insertBooleanValue","abbreviation"},{"insertPlayerName", "insertRankNumber", "insertAbbreviation"}});
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

		File[] allCommandDirs = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();
		for(File commandDir : allCommandDirs)
		{
			File configFile = new File(commandDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties props = BotFiles.getProperties(configFile);
				String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");
				for(String commandName : commandNames)
				{
					if(super.getUserCommandParams()[0].contains("_"))
					{
						File paramDir = new File(commandDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR);
						String[] params = super.getUserCommandParams()[0].split("_");
						if(commandName.equalsIgnoreCase(params[0]))
						{
							//Found the command
							if(Ranking.hasCommandPrivileges(props))
							{
								if(paramDir.exists())
								{
									//has parameters
									File[] levelDirs = paramDir.listFiles();
									if(params.length - 1 <= levelDirs.length)
									{
										for(int i = 1; i < params.length; i++)
										{
											File[] levelParams = levelDirs[i-1].listFiles();
											for(File levelParamDir : levelParams)
											{
												File paramConfigFile = new File(levelParamDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

												if(paramConfigFile.exists())
												{
													Properties paramProps = BotFiles.getProperties(paramConfigFile);
													String[] paramNames = BotFiles.getDecimalSeparatedProperty(paramProps, "commandNames");
													boolean foundParam = false;

													for(String paramName : paramNames)
													{
														if(paramName.equalsIgnoreCase(params[i]))
														{
															foundParam = true;
															if(Ranking.hasCommandPrivileges(paramProps))
															{

																if(i == params.length - 1)
																{
																	if(super.getLevel() >= 2)
																	{
																		checkSecondParam(paramConfigFile, paramProps);
																	}
																	else
																	{
																		Messenger.messageFormatter("One of the super.getLevel() 2 parameters are required: " + Arrays.toString(super.getCommandParams()[1]));
																	}
																	return;
																}
																break;
															}
															return;
														}
													}
													if(foundParam)
													{
														break;
													}

												}
											}
										}
										Messenger.messageFormatter("No such command: " + super.getUserCommandParams()[0].replaceAll("_", " "));
										return;
									}
									Messenger.messageFormatter("Too many parameters for: " + super.getUserCommandParams()[0].replaceAll("_", " "));
									return;
								}
								else
								{
									Messenger.messageFormatter(commandDir.getName() + " (" + params[0] + ") does not take any parameters." );
									return;
								}
							}
							return;
						}
					}
					if(commandName.equalsIgnoreCase(super.getUserCommandParams()[0]))
					{
						if(Ranking.hasCommandPrivileges(props))
						{
							if(super.getLevel() >= 2)
							{
							checkSecondParam(configFile, props);
							}
							else
							{
								Messenger.messageFormatter("One of the super.getLevel() 2 parameters are required: " + Arrays.toString(super.getCommandParams()[1]));
							}
							return;
						}
						return;
					}
				}

			}
		}

	}


	private void checkSecondParam(File paramConfigFile, Properties paramProps) {

		File[] levelTwoParams = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level2" + OssBotConstants.SEPARATOR).listFiles();

		for(File paramDir : levelTwoParams)
		{
			File configFile = new File(paramDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties commandProps = BotFiles.getProperties(configFile);

				String[] paramNames = BotFiles.getDecimalSeparatedProperty(commandProps, "commandNames");

				for(String paramName : paramNames)
				{
					if(paramName.equalsIgnoreCase(super.getUserCommandParams()[1]))
					{
						if(Ranking.hasCommandPrivileges(commandProps))
						{
							for(int i = 0, n = super.getCommandParams()[1].length; i < n; i++)
							{
								if(paramDir.getName().equalsIgnoreCase(super.getCommandParams()[1][i]))
								{
									if(super.getLevel() >= 3)
									{
										checkThirdParam(paramConfigFile, paramProps, super.getCommandParams()[1][i]);
									}
									else
									{
										Messenger.messageFormatter("One of the super.getLevel() 3 parameters are required: " + Arrays.toString(super.getCommandParams()[2]));
									}
									return;
								}
							}
						}
						return;
					}
				}
			}
		}

		Messenger.messageFormatter("Could not find parameter: " + super.getUserCommandParams()[1]);

	}
	private void checkThirdParam(File paramConfigFile, Properties paramProps, String commandParam) {

		File[] levelThreeParams = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level3" + OssBotConstants.SEPARATOR).listFiles();
		for(File paramDir : levelThreeParams)
		{
			File configFile = new File(paramDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties commandProps = BotFiles.getProperties(configFile);

				if((super.getUserCommandParams()[2].equalsIgnoreCase("true") || super.getUserCommandParams()[2].equalsIgnoreCase("false")) && commandParam.equalsIgnoreCase(super.getCommandParams()[1][2]))
				{
					if(paramDir.getName().equalsIgnoreCase(super.getCommandParams()[2][3]))
					{
						if(Ranking.hasCommandPrivileges(commandProps))
						{
							checkFourthParam(paramConfigFile, paramProps, super.getUserCommandParams()[2], commandParam);
						}
						else
						{
							Messenger.messageFormatter("You do not have the permission to use the super.getLevel() 3 parameter: " + paramDir.getName() + " (" + super.getUserCommandParams()[2] + ") for the command '" + super.getCommandName() + "'");
						}
					}
					else
					{
						continue;
					}
					return;
				}
				String[] paramNames = BotFiles.getDecimalSeparatedProperty(commandProps, "commandNames");

				for(String paramName : paramNames)
				{
					if(paramName.equalsIgnoreCase(super.getUserCommandParams()[2]))
					{
						for(int i = 0, n = super.getCommandParams()[2].length; i < n; i++)
						{
							if(paramDir.getName().equalsIgnoreCase(super.getCommandParams()[2][i]))
							{
								if(Ranking.hasCommandPrivileges(commandProps))
								{
									if(super.getLevel() >= 4)
									{
										checkFourthParam(paramConfigFile, paramProps, super.getCommandParams()[2][i], commandParam);
									}
									else
									{
										Messenger.messageFormatter("One of the super.getLevel() 4 parameters are required: " + Arrays.toString(super.getCommandParams()[3]));
									}
								}
								return;
							}
						}
					}
				}
			}
		}
		Messenger.messageFormatter("Could not find parameter: " + super.getUserCommandParams()[2]);
	}

	private void checkFourthParam(File paramConfigFile, Properties paramProps, String commandParamTwo,
			String commandParam) {

		if(commandParam.equalsIgnoreCase(super.getCommandParams()[1][0]))
		{
			removeCommand(paramConfigFile, paramProps, commandParamTwo);
		}
		else if(commandParam.equalsIgnoreCase(super.getCommandParams()[1][1]))
		{
			addCommand(paramConfigFile, paramProps, commandParamTwo);
		}
		else if(commandParam.equalsIgnoreCase(super.getCommandParams()[1][2]))
		{
			disableCommand(paramConfigFile, paramProps);
		}
		else if(commandParam.equalsIgnoreCase(super.getCommandParams()[1][3]))
		{
			changeCommand(paramConfigFile, paramProps, commandParamTwo);
		}

	}
	private void changeCommand(File paramConfigFile, Properties paramProps, String commandParamTwo) {

		if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][0]))
		{
			changeminimumRank(paramConfigFile, paramProps, super.getUserCommandParams()[3]);
		}

	}
	private void changeminimumRank(File paramConfigFile, Properties paramProps, String stringRank) {

		Integer rank = Integer.valueOf(stringRank);
		if((rank != null) && (rank >= -2 || rank <= 7))
		{
			paramProps.setProperty("minimumRank", String.valueOf(rank));
			BotFiles.storeProperties(paramProps, paramConfigFile);
			Messenger.messageFormatter("Successfully changed the minimum rank to: " + rank + " for the command " + paramConfigFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, "") + "'");
		}
		else
		{
			Messenger.messageFormatter(stringRank + " is not a valid rank.");
		}
	}
	private void disableCommand(File paramConfigFile, Properties paramProps) {

		if(super.getUserCommandParams()[2].equalsIgnoreCase("true"))
		{
			paramProps.setProperty("nameRestrictions", OSSBotV2.getIssuerName());
			Messenger.messageFormatter("Command disabled.");
		}
		else if(super.getUserCommandParams()[2].equalsIgnoreCase("false"))
		{
			paramProps.setProperty("nameRestrictions", "");
			Messenger.messageFormatter("Command enabled. All restrictions removed");
		}
		else
		{
			Messenger.messageFormatter("Value must be true of false to disable/enable");
			return;
		}
		BotFiles.storeProperties(paramProps, paramConfigFile);
	}
	private void addCommand(File paramConfigFile, Properties paramProps, String commandParamTwo) {

		if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][1]))
		{
			addException(paramConfigFile, paramProps, OssBotMethods.standardiseName(super.getUserCommandParams()[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][2]))
		{
			addRestriction(paramConfigFile, paramProps, OssBotMethods.standardiseName(super.getUserCommandParams()[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][4]))
		{
			addAbbreviation(paramConfigFile, paramProps, super.getUserCommandParams()[3]);
		}

	}
	private void addAbbreviation(File paramConfigFile, Properties paramProps, String abbr) {

		File[] allSameLevelCommands = paramConfigFile.getParentFile().getParentFile().listFiles();

		for(File command : allSameLevelCommands)
		{
			File configFile = new File(command.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);
			if(configFile.exists())
			{
				Properties props = BotFiles.getProperties(configFile);

					String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

					for(String commandName : commandNames)
					{
						if(commandName.equalsIgnoreCase(abbr))
						{
							Messenger.messageFormatter("'" + abbr + "' already exists in: " + configFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, ""));
							return;
						}
					}

			}
		}
		addPropertyPart(paramConfigFile, paramProps, abbr, "commandNames");
	}
	private void addRestriction(File paramConfigFile, Properties paramProps, String nameToAdd) {
		if(checkInputName(nameToAdd))
		{
			addPropertyPart(paramConfigFile, paramProps, nameToAdd, "nameRestrictions");
			return;
		}
		Messenger.messageFormatter(nameToAdd + " is not a valid name.");
	}
	private void addException(File paramConfigFile, Properties paramProps, String nameToAdd) {
		if(checkInputName(nameToAdd))
		{
			addPropertyPart(paramConfigFile, paramProps, nameToAdd, "nameExceptions");
			return;
		}
		Messenger.messageFormatter(nameToAdd + " is not a valid name.");

	}

	private void removeCommand(File paramConfigFile, Properties paramProps, String commandParamTwo) {


		if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][1]))
		{
			removeException(paramConfigFile, paramProps, OssBotMethods.standardiseName(super.getUserCommandParams()[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][2]))
		{
			removeRestriction(paramConfigFile, paramProps, OssBotMethods.standardiseName(super.getUserCommandParams()[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(super.getCommandParams()[2][4]))
		{
			removeAbbreviation(paramConfigFile, paramProps, super.getUserCommandParams()[3]);
		}

	}
	private void removeAbbreviation(File paramConfigFile, Properties paramProps, String abbr) {


		String[] commandNames = paramProps.getProperty("commandNames").split("\\.");

		if(!commandNames[0].equalsIgnoreCase(abbr))
		{
			removePropertyPart(paramConfigFile, paramProps, abbr, "commandNames");

			return;
		}
		Messenger.messageFormatter("Cannot remove the default command name.");
	}
	private void removeRestriction(File paramConfigFile, Properties paramProps, String nameToRemove) {
		if(checkInputName(nameToRemove))
		{
			if(Ranking.getCachedRank(nameToRemove) <= OSSBotV2.getIssuerRank())
			{
				removePropertyPart(paramConfigFile, paramProps, nameToRemove, "nameRestrictions");
				return;
			}
			Messenger.messageFormatter("You are outranked by: " + nameToRemove + " and therefore cannot remove them");
			return;
		}
		Messenger.messageFormatter(nameToRemove + " is not a valid name.");
	}


	private void removeException(File paramConfigFile, Properties paramProps, String nameToRemove) {

		if(checkInputName(nameToRemove))
		{
			if(nameToRemove.equalsIgnoreCase("xmax") || nameToRemove.equalsIgnoreCase("cynes"))
			{
				Messenger.messageFormatter("You cannot remove: " + nameToRemove);
				return;
			}
			if(Ranking.getCachedRank(nameToRemove) <= OSSBotV2.getIssuerRank())
			{

				removePropertyPart(paramConfigFile, paramProps, nameToRemove, "nameExceptions");
				return;

			}
			Messenger.messageFormatter("You are outranked by: " + nameToRemove + " and therefore cannot remove them");
			return;
		}
		Messenger.messageFormatter(nameToRemove + " is not a valid name.");
	}
	private void removePropertyPart(File paramConfigFile, Properties paramProps, String propertyPartToRemove, String property) {
		String[] allPropertyParts = paramProps.getProperty(property).split("\\.");
		String finalOutput = "";
		boolean removed = false;
		for(String propertyPart : allPropertyParts)
		{
			if(propertyPart.equalsIgnoreCase(propertyPartToRemove))
			{
				removed = true;
			}
			else
			{
				finalOutput += propertyPart + ".";
			}
		}

		if(!finalOutput.isEmpty())
		{
			finalOutput = finalOutput.substring(0, finalOutput.length() - 1);
		}
		paramProps.setProperty(property, finalOutput);

		BotFiles.storeProperties(paramProps, paramConfigFile);

		if(removed)
		{
			Messenger.messageFormatter("Successfully removed '" + propertyPartToRemove + "' from '" + property + "' for the command '" + paramConfigFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, "") + "'");
		}
		else
		{
			Messenger.messageFormatter("'" + propertyPartToRemove + "' was not part of the '" + property + "' for the command '" + paramConfigFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, "") + "' no changes done.");
		}
	}
	private boolean checkInputName(String nameToRemove) {
		nameToRemove = nameToRemove.replaceAll("[^a-zA-Z0-9_\\-]+", "");

		if(nameToRemove.length() <= OssBotConstants.MAX_NAME_LENGTH)
		{
			return true;
		}
		return false;
	}
	private void addPropertyPart(File paramConfigFile, Properties paramProps, String propertyPartToAdd, String property) {
		List<String> allPropertyParts = new ArrayList<String>(Arrays.asList(paramProps.getProperty(property).split("\\.")));

		if(allPropertyParts.contains(propertyPartToAdd))
		{
			Messenger.messageFormatter("'" + propertyPartToAdd + "' is already part of the '" + property + "' for the command '"  + paramConfigFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, "") + "'");
		}
		else
		{
			allPropertyParts.add(propertyPartToAdd);
			String finalOutput = String.join(".", allPropertyParts);
			if(finalOutput.startsWith("."))
			{
				finalOutput = finalOutput.substring(1, finalOutput.length());
			}
			paramProps.setProperty(property, finalOutput);
			BotFiles.storeProperties(paramProps, paramConfigFile);
			Messenger.messageFormatter("Successfully added '" + propertyPartToAdd + "' to '" + property + "' for the command '"  + paramConfigFile.getAbsolutePath().replace(OssBotConstants.COMMAND_FILES_DIRECTORY, "").replace(OssBotConstants.PROPERTY_FILE, "") + "'");
		}

	}

}
