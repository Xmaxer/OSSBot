package scripts.ossbot.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;

public class Ranking {
	public static Integer getPlayerRank(String playerName) {
		if(playerName.isEmpty())
		{
			playerName = OSSBotV2.getIssuerName();
		}
		RSInterfaceChild CCList = Interfaces.get(OssBotConstants.CC_PLAYER_INTERFACE[0], OssBotConstants.CC_PLAYER_INTERFACE[1]);
		RSInterfaceComponent playerComponent = null;

		if(CCList != null)
		{
			for(RSInterfaceComponent z : CCList.getChildren())
			{
				if(OssBotMethods.standardiseName(z.getText()).equals(OssBotMethods.standardiseName(playerName)))
				{
					playerComponent = z;
				}
			}

			if(playerComponent != null)
			{
				int textureID = CCList.getChild(playerComponent.getIndex()+2).getTextureID();
				switch (textureID){
				case 1004:
					//General.println("Player rank is: smiley");
					return 0;
				case 1012:
					//General.println("Player rank is: 1 banana");
					return 1;
				case 1011:
					//General.println("Player rank is: 2 banana");
					return 2;
				case 1010:
					//General.println("Player rank is: 3 banana");
					return 3;
				case 1009:
					//General.println("Player rank is: Bronze star");
					return 4;
				case 1008:
					//General.println("Player rank is: Silver star");
					return 5;
				case 1007:
					//General.println("Player rank is: Gold star");
					return 6;
				case 1006:
					//General.println("Player rank is: Key");
					return 7;
				default:
					//General.println("Player rank is: unranked");
					return -1;
				}
			}
		}
		return null;
	}
	public static int getCachedRank(String name) {

		name = OssBotMethods.standardiseName(name);
		File[] allPlayerDirs = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDir : allPlayerDirs)
		{
			String player = OssBotMethods.standardiseName(playerDir.getName());
			if(player.equalsIgnoreCase(name))
			{
				File cacheFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);
				if(cacheFile.exists())
				{
					Properties props = new Properties();
					try {
						InputStream input = new FileInputStream(cacheFile.getAbsolutePath());
						props.load(input);
						input.close();
					} catch (Exception e) {
						OssBotMethods.printException(e);
					}

					String textRank = props.getProperty("lastSeenRank");
					if(textRank != null && !textRank.isEmpty())
					{
						return Integer.valueOf(textRank);
					}
				}
				break;
			}
		}
		return -1;
	}
	public static boolean hasCommandPrivileges(Properties paramProps) {

		int playerRank = getPlayerRank("");
		String[] exceptions = paramProps.getProperty("nameExceptions").split("\\.");
		String[] restrictions = paramProps.getProperty("nameRestrictions").split("\\.");
		int minimumRank = Integer.valueOf(paramProps.getProperty("minimumRank"));
		String playerName = OSSBotV2.getIssuerName();

		if(playerName.equalsIgnoreCase("xmax") || playerName.equalsIgnoreCase("cynes"))
		{
			return true;
		}
		if(restrictions.length > 0 && !restrictions[0].isEmpty())
		{
			for(String name : restrictions)
			{
				if(name.equalsIgnoreCase(playerName))
				{
					return true;
				}
			}
			Messenger.messageFormatter("This command is restricted, and definitely not to you.");
			return false;
		}

		for(String name : exceptions)
		{
			if(name.equalsIgnoreCase(playerName))
			{
				return true;
			}
		}

		if(minimumRank <= playerRank)
		{
			return true;
		}
		Messenger.messageFormatter("You don't have the required permissions to do this.");
		return false;
	}
	public static String rankNumberToName(int rankNumber)
	{
		if(rankNumber == 0)
		{
			return "smiley";
		}
		else if(rankNumber == 1)
		{
			return "1 banana";
		}
		else if(rankNumber == 2)
		{
			return "2 banana";
		}
		else if(rankNumber == 3)
		{
			return "3 banana";
		}
		else if(rankNumber == 4)
		{
			return "bronze";
		}
		else if(rankNumber == 5)
		{
			return "silver";
		}
		else if(rankNumber == 6)
		{
			return "gold";
		}
		else if(rankNumber == 7)
		{
			return "key master";
		}
		else
		{
			return "unknown rank number: " + rankNumber;
		}
	}
	public static boolean checkPermissions(String COMMAND_NAME) {
		int minimumRank = BotFiles.getMinimumRank(COMMAND_NAME);
		String[] nameExceptions = BotFiles.getNameExceptions(COMMAND_NAME);
		String[] nameRestrictions = BotFiles.getNameRestrictions(COMMAND_NAME);
		String playerName = OSSBotV2.getIssuerName();
		if(playerName.equalsIgnoreCase("xmax") || playerName.equalsIgnoreCase("cynes"))
		{
			return true;
		}
		if(nameRestrictions != null && nameRestrictions[0].length() == 0)
		{
			if(nameExceptions != null && nameExceptions[0].length() != 0)
			{
				for(String name : nameExceptions)
				{
					name = OssBotMethods.standardiseName(name);
					if(name.equalsIgnoreCase(playerName))
					{
						BotFiles.botLogger(playerName + " is an exception for '" + COMMAND_NAME + "'. Ignoring rank.");
						return true;
					}
				}
			}
			if(OSSBotV2.getIssuerRank() >= minimumRank)
			{
				return true;
			}
		}
		else
		{
			for(String name : nameRestrictions)
			{
				name = OssBotMethods.standardiseName(name);
				if(name.equalsIgnoreCase(playerName))
				{
					General.println(playerName + " is executing '" + COMMAND_NAME + "' with restriction privileges. Ignoring rank");
					return true;
				}
			}
			Messenger.messageFormatter("This command is restricted, and definitely not to you.");
			BotFiles.botLogger("Command is restricted: " + COMMAND_NAME);
			return false;
		}
		
		Messenger.messageFormatter("You don't have the permissions to do this.");
		BotFiles.botLogger("No required permissions to run the command: " + COMMAND_NAME);
		return false;
	}
}
