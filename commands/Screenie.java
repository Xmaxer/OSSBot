package scripts.ossbot.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.tribot.api.util.Screenshots;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Screenie extends Command{

	public Screenie()
	{
		super(0, new String[][] {});
	}
	@Override
	public void execute() {
		String fileName = "screenie - "  + OSSBotV2.getIssuerName() + " - " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d yyyy HH-mm-ss z")) + ".png";
		if(Screenshots.take(fileName, false, true))
		{
			Messenger.messageFormatter("full link to album: " + OssBotMethods.uploadToImgur(fileName, OssBotConstants.IMGUR_SCREENIE_ALBUM_ID));
		}
		else
		{
			BotFiles.botLogger("Failed to take screenshot.");
		}
	}
}
