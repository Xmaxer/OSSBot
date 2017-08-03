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
	private final int DEFAULT_RANK_REQUIREMENT = 4;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertCommandName"},{"remove", "add", "disable", "change"},{"minimumRank","exception", "restriction","insertBooleanValue","abbreviation"},{"insertPlayerName", "insertRankNumber", "insertAbbreviation"}};
	private int level = 0;

	public Config()
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

	private void checkFirstParam(String[] commandParams) {

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
					if(commandParams[0].contains("_"))
					{
						File paramDir = new File(commandDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + OssBotConstants.SEPARATOR);
						String[] params = commandParams[0].split("_");
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
																	if(level >= 2)
																	{
																		checkSecondParam(paramConfigFile, paramProps, commandParams);
																	}
																	else
																	{
																		Messenger.messageFormatter("One of the level 2 parameters are required: " + Arrays.toString(STATIC_COMMAND_PARAMS[1]));
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
										Messenger.messageFormatter("No such command: " + commandParams[0].replaceAll("_", " "));
										return;
									}
									Messenger.messageFormatter("Too many parameters for: " + commandParams[0].replaceAll("_", " "));
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
					if(commandName.equalsIgnoreCase(commandParams[0]))
					{
						if(Ranking.hasCommandPrivileges(props))
						{
							if(level >= 2)
							{
							checkSecondParam(configFile, props, commandParams);
							}
							else
							{
								Messenger.messageFormatter("One of the level 2 parameters are required: " + Arrays.toString(STATIC_COMMAND_PARAMS[1]));
							}
							return;
						}
						return;
					}
				}

			}
		}

	}


	private void checkSecondParam(File paramConfigFile, Properties paramProps, String[] commandParams) {

		File[] levelTwoParams = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level2" + OssBotConstants.SEPARATOR).listFiles();

		for(File paramDir : levelTwoParams)
		{
			File configFile = new File(paramDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties commandProps = BotFiles.getProperties(configFile);

				String[] paramNames = BotFiles.getDecimalSeparatedProperty(commandProps, "commandNames");

				for(String paramName : paramNames)
				{
					if(paramName.equalsIgnoreCase(commandParams[1]))
					{
						if(Ranking.hasCommandPrivileges(commandProps))
						{
							for(int i = 0, n = STATIC_COMMAND_PARAMS[1].length; i < n; i++)
							{
								if(paramDir.getName().equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][i]))
								{
									if(level >= 3)
									{
										checkThirdParam(paramConfigFile, paramProps, commandParams, STATIC_COMMAND_PARAMS[1][i]);
									}
									else
									{
										Messenger.messageFormatter("One of the level 3 parameters are required: " + Arrays.toString(STATIC_COMMAND_PARAMS[2]));
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

		Messenger.messageFormatter("Could not find parameter: " + commandParams[1]);

	}
	private void checkThirdParam(File paramConfigFile, Properties paramProps, String[] commandParams, String commandParam) {

		File[] levelThreeParams = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + "level3" + OssBotConstants.SEPARATOR).listFiles();
		for(File paramDir : levelThreeParams)
		{
			File configFile = new File(paramDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties commandProps = BotFiles.getProperties(configFile);

				if((commandParams[2].equalsIgnoreCase("true") || commandParams[2].equalsIgnoreCase("false")) && commandParam.equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][2]))
				{
					if(paramDir.getName().equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][3]))
					{
						if(Ranking.hasCommandPrivileges(commandProps))
						{
							checkFourthParam(paramConfigFile, paramProps, commandParams, commandParams[2], commandParam);
						}
						else
						{
							Messenger.messageFormatter("You do not have the permission to use the level 3 parameter: " + paramDir.getName() + " (" + commandParams[2] + ") for the command '" + COMMAND_NAME + "'");
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
					if(paramName.equalsIgnoreCase(commandParams[2]))
					{
						for(int i = 0, n = STATIC_COMMAND_PARAMS[2].length; i < n; i++)
						{
							if(paramDir.getName().equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][i]))
							{
								if(Ranking.hasCommandPrivileges(commandProps))
								{
									if(level >= 4)
									{
										checkFourthParam(paramConfigFile, paramProps, commandParams, STATIC_COMMAND_PARAMS[2][i], commandParam);
									}
									else
									{
										Messenger.messageFormatter("One of the level 4 parameters are required: " + Arrays.toString(STATIC_COMMAND_PARAMS[3]));
									}
								}
								return;
							}
						}
					}
				}
			}
		}
		Messenger.messageFormatter("Could not find parameter: " + commandParams[2]);
	}

	private void checkFourthParam(File paramConfigFile, Properties paramProps, String[] commandParams, String commandParamTwo,
			String commandParam) {

		if(commandParam.equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][0]))
		{
			removeCommand(paramConfigFile, paramProps, commandParamTwo, commandParams);
		}
		else if(commandParam.equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][1]))
		{
			addCommand(paramConfigFile, paramProps, commandParamTwo, commandParams);
		}
		else if(commandParam.equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][2]))
		{
			disableCommand(paramConfigFile, paramProps, commandParams);
		}
		else if(commandParam.equalsIgnoreCase(STATIC_COMMAND_PARAMS[1][3]))
		{
			changeCommand(paramConfigFile, paramProps, commandParamTwo, commandParams);
		}

	}
	private void changeCommand(File paramConfigFile, Properties paramProps, String commandParamTwo,
			String[] commandParams) {

		if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][0]))
		{
			changeminimumRank(paramConfigFile, paramProps, commandParams[3]);
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
	private void disableCommand(File paramConfigFile, Properties paramProps,
			String[] commandParams) {

		if(commandParams[2].equalsIgnoreCase("true"))
		{
			paramProps.setProperty("nameRestrictions", OSSBotV2.getIssuerName());
			Messenger.messageFormatter("Command disabled.");
		}
		else if(commandParams[2].equalsIgnoreCase("false"))
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
	private void addCommand(File paramConfigFile, Properties paramProps, String commandParamTwo,
			String[] commandParams) {

		if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][1]))
		{
			addException(paramConfigFile, paramProps, OssBotMethods.standardiseName(commandParams[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][2]))
		{
			addRestriction(paramConfigFile, paramProps, OssBotMethods.standardiseName(commandParams[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][4]))
		{
			addAbbreviation(paramConfigFile, paramProps, commandParams[3]);
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

	private void removeCommand(File paramConfigFile, Properties paramProps, String commandParamTwo,
			String[] commandParams) {


		if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][1]))
		{
			removeException(paramConfigFile, paramProps, OssBotMethods.standardiseName(commandParams[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][2]))
		{
			removeRestriction(paramConfigFile, paramProps, OssBotMethods.standardiseName(commandParams[3]));
		}
		else if(commandParamTwo.equalsIgnoreCase(STATIC_COMMAND_PARAMS[2][4]))
		{
			removeAbbreviation(paramConfigFile, paramProps, commandParams[3]);
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
