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

public class CML extends Command{

	public CML()
	{
		super(0, new String[][]{{"insertSkill", "update"},{"insertInterval"},{"insertName"}});
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
		if(super.getUserCommandParams()[0].equalsIgnoreCase(super.getCommandParams()[0][0]))
		{
			return;
		}

		String playerName = OSSBotV2.getIssuerName();
		if(super.getLevel() == 3)
		{
			playerName = OssBotMethods.standardiseName(super.getUserCommandParams()[2]);
		}
		for(int i = 0, skills = OssBotConstants.ALL_SKILLS.length; i < skills; i++)
		{
			for(int j = 0, abvs = OssBotConstants.ALL_SKILLS[i].length; j < abvs; j++)
			{
				if(OssBotConstants.ALL_SKILLS[i][j].equalsIgnoreCase(super.getUserCommandParams()[0]))
				{
					if(super.getLevel() >= 2)
					{
						getCMLXP(OssBotConstants.ALL_SKILLS[i][0],OssBotConstants.ALL_SKILLS[i][1], playerName, super.getUserCommandParams()[1]);
					}
					else
					{
						Messenger.messageFormatter("A valid time interval is required.");
					}
					return;
				}
			}
		}
		String realCommandName = BotFiles.checkLevelParams("level1", super.getUserCommandParams()[0], super.getCommandName());

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

}
