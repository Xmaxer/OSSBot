package scripts.ossbot.methods;

import java.util.ArrayList;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;

public class Messenger {
	public static void messageFormatter(String finalMessage) {

		int messageLength = finalMessage.length();
		ArrayList<String> messageParts = new ArrayList<String>();

		if(messageLength > OssBotConstants.MAX_MESSAGE_LENGTH)
		{
			messageParts = messageSplitter(finalMessage, messageLength);
		}
		else
		{
			messageParts.add(finalMessage);
		}
		if(!OSSBotV2.getPM())
		{
			for(int i = 0; i < messageParts.size(); i++)
			{
				messageParts.set(i, "/" + messageParts.get(i));
			}
		}
		else
		{
			GameTab.open(TABS.FRIENDS);
			OssBotMethods.addPlayer();
		}
		typer(messageParts);
	}
	private static void typer(ArrayList<String> messageParts) {

		if(!OSSBotV2.getPM())
		{
			while(!messageParts.isEmpty())
			{
				Keyboard.typeSend(messageParts.get(0));
				messageParts.remove(0);
			}
		}
		else
		{
			while(!messageParts.isEmpty())
			{
				RSInterface sendPMDialogue = Interfaces.get(OssBotConstants.CHAT_INTERFACE[0], OssBotConstants.CHAT_INTERFACE[1]);
				RSInterface friendsPlayerList = Interfaces.get(OssBotConstants.FRIENDS_LIST_PLAYER_INTERFACE[0], OssBotConstants.FRIENDS_LIST_PLAYER_INTERFACE[1]);
				while(friendsPlayerList.getChildren() == null)
				{
					GameTab.open(TABS.FRIENDS);
					General.sleep(OssBotConstants.GAME_TICK);
				}
				boolean isOnline = (friendsPlayerList.getChild(2).getText().equalsIgnoreCase("offline")) ? false : true;
				if(isOnline && OssBotMethods.standardiseName(friendsPlayerList.getChild(0).getText()).equalsIgnoreCase(OSSBotV2.getIssuerName()))
				{
					while(sendPMDialogue == null || sendPMDialogue.isHidden() || !sendPMDialogue.getText().contains(OssBotConstants.SEND_PM_TEXT))
					{
						GameTab.open(TABS.FRIENDS);
						Clicking.click(friendsPlayerList.getChild(0));
						General.sleep(OssBotConstants.GAME_TICK);
					}
					Keyboard.typeSend(messageParts.get(0));
					messageParts.remove(0);
					General.sleep(OssBotConstants.GAME_TICK);
					while(!sendPMDialogue.isHidden())
					{
						General.sleep(OssBotConstants.GAME_TICK);
					}
				}
			}
		}
	}
	private static ArrayList<String> messageSplitter(String finalMessage, int messageLength) {
		ArrayList<String> messageParts = new ArrayList<String>();
		int counter = 0;
		while(Double.valueOf(messageLength)/OssBotConstants.MAX_MESSAGE_LENGTH >= 1)
		{
			messageParts.add(finalMessage.substring(counter*OssBotConstants.MAX_MESSAGE_LENGTH,(counter + 1)*OssBotConstants.MAX_MESSAGE_LENGTH));
			counter++;
			messageLength -= OssBotConstants.MAX_MESSAGE_LENGTH;
		}
		messageParts.add(finalMessage.substring((counter)*OssBotConstants.MAX_MESSAGE_LENGTH));
		return messageParts;
	}
}
