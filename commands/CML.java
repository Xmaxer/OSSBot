package scripts.ossbot.commands;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class CML extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertSkill", "update"},{"insertInterval"},{"insertName"}};
	private int level = 0;

	public CML()
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
		if(commandParams[0].equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][0]))
		{
			return;
		}

		String playerName = OSSBotV2.getIssuerName();
		if(level == 3)
		{
			playerName = OssBotMethods.standardiseName(commandParams[2]);
		}
		for(int i = 0, skills = OssBotConstants.ALL_SKILLS.length; i < skills; i++)
		{
			for(int j = 0, abvs = OssBotConstants.ALL_SKILLS[i].length; j < abvs; j++)
			{
				if(OssBotConstants.ALL_SKILLS[i][j].equalsIgnoreCase(commandParams[0]))
				{
					if(level >= 2)
					{
						getCMLXP(OssBotConstants.ALL_SKILLS[i][0],OssBotConstants.ALL_SKILLS[i][1], playerName, commandParams[1]);
					}
					else
					{
						Messenger.messageFormatter("A valid time interval is required.");
					}
					return;
				}
			}
		}
		String realCommandName = BotFiles.checkLevelParams("level1", commandParams[0], COMMAND_NAME);

		if(realCommandName != null)
		{
			updateCML();
		}

	}
	private void updateCML() {
		String name = OSSBotV2.getIssuerName();
		int response = Integer.valueOf(BotFiles.getLinkData(OssBotConstants.CML_UPDATE_LINK + name));
		String output = "";
		switch(response)
		{
		case 1:
			output = "Successfully updated " + OSSBotV2.getIssuerName();
			break;
		case 2:
			output = name + " does no exist on the Runescape hiscores";
			break;
		case 3:
			output = "Negative xp gain? Not updated.";
			break;
		case 4:
			output = "Unknown cml error";
			break;
		case 5:
			output = name + " was already updated in the last 30 seconds";
			break;
		case 6:
			output = name + " is an invalid playername";
			break;
		default:
			output = "Something weird happened with cml";
			break;
		}
		
		Messenger.messageFormatter(output);

	}
	private void getCMLXP(String skill,String number, String playerName, String interval) {
		Long timeIntervalLong = OssBotMethods.getTimeInMillis(interval);
		String timeInterval = OssBotMethods.getIntervalFromMillis(timeIntervalLong);
		String xpData = BotFiles.getLinkData(OssBotConstants.CML_TRACKING_LINK + playerName + "&time=" + timeInterval);
		String output = "";
		int skillNumber = Integer.valueOf(number);
		if(xpData.length() >= 10)
		{
			Matcher m = Pattern.compile("([\\d-]+),([\\d-]+),([\\d-]+),([\\d-]+)").matcher(xpData);
			
			int counter = 0;
			while(m.find())
			{
				if(m.groupCount() == 4 && skillNumber != 24)
				{
					if(skillNumber == counter)
					{
						NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
						
						String xpGain = nf.format(Integer.valueOf(m.group(1)));
						String rankGain = nf.format(Integer.valueOf(m.group(2)) * -1);
						String rank = nf.format(Integer.valueOf(m.group(4)));
						String xp = nf.format(Integer.valueOf(m.group(3)));
						if(!rankGain.startsWith("-"))
						{
							rankGain = "+" + rankGain;
						}
						output =  "[" + skill + "] Rank: " + rank + "(" + rankGain + ") X—P: " + xp + "(+" + xpGain + ")";
						break;
					}
					counter++;
				}
			}
		}
		else
		{
			if(xpData.equalsIgnoreCase("-1"))
			{
				output = playerName + " is not in the C—M—L database.";
			}
			else if(xpData.equalsIgnoreCase("-2"))
			{
				output = playerName + " is an invalid name";
			}
			else if(xpData.equalsIgnoreCase("-3"))
			{
				output = "C—M—L database error";
			}
			else if(xpData.equalsIgnoreCase("-4"))
			{
				output = "C—M—L is under heavy seer load";
			}
			else
			{
				output = "Unexpected stuff happened. Try again?";
			}
		}
		
		Messenger.messageFormatter(output);
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
