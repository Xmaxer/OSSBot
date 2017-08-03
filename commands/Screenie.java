package scripts.ossbot.commands;

import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.tribot.api.util.Screenshots;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Screenie extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};

	public Screenie()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {
		String fileName = "screenie - "  + OSSBotV2.getIssuerName() + " - " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d yyyy HH-mm-ss z")) + ".png";
		if(Screenshots.take(fileName, false, true))
		{
			try {
				String imgurID = OssBotMethods.getImgurContent(fileName, "Taken by OSS Bot", Paths.get("").toAbsolutePath().toString() + OssBotConstants.SEPARATOR + "screenshots" + OssBotConstants.SEPARATOR + fileName);
				BotFiles.botLogger("Successfully took a screenshot and uploaded.");
				if(imgurID != null)
				{
					String result = "";
					for(int i = 0, n = imgurID.length(); i < n; i++)
					{
						if(Character.isUpperCase(imgurID.charAt(i)))
						{
							result += "�" + String.valueOf(imgurID.charAt(i)).toUpperCase();
						}
						else
						{
							result += String.valueOf(imgurID.charAt(i));
						}

					}
					result = "i�.�imgur-�d()t�-c0m/" + result + ".�png";
					Messenger.messageFormatter("full link to screenshot: " + result);
				}
			} catch (Exception e) {
				OssBotMethods.printException(e);
			}

		}
		else
		{
			BotFiles.botLogger("Failed to take screenshot.");
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
