package scripts.ossbot.threads;

import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;

public class LoginTracker implements Runnable {

	@Override
	public void run() {
		while(true)
		{
			if(Login.getLoginState().equals(Login.STATE.INGAME))
			{
				OSSBotV2.setLoggedIn(true);
				if(GameTab.getOpen().equals(TABS.CLAN))
				{
					RSInterface joinButton = Interfaces.get(OssBotConstants.JOIN_CLAN_BUTTON[0], OssBotConstants.JOIN_CLAN_BUTTON[1]);
					if(joinButton != null)
					{
						if(joinButton.getText().toLowerCase().contains("join"))
						{
							OSSBotV2.setInCC(false);
						}
						else
						{
							OSSBotV2.setInCC(true);
						}
						if(!OSSBotV2.getInCC())
						{
							RSInterface chatInterface = Interfaces.get(OssBotConstants.CHAT_INTERFACE[0], OssBotConstants.CHAT_INTERFACE[1]);
							while(chatInterface.isHidden())
							{
								Clicking.click(joinButton);
								General.sleep(1000);
							}

							Keyboard.typeSend("OS Society");
							General.sleep(4000);
						}
					}

				}
				if(!OSSBotV2.getLock())
				{
					RSTile playerPos = Player.getPosition();
					if(System.currentTimeMillis() - OSSBotV2.getLastClicked() >= 120000)
					{
						Camera.setCameraAngle(100);
						Walking.walkTo(playerPos);
						OSSBotV2.setLastClicked(System.currentTimeMillis());
					}
				}
			}
			else
			{
				OSSBotV2.setLoggedIn(false);
				if(Login.getLoginState().equals(Login.STATE.LOGINSCREEN))
				{
					String response = Login.getLoginResponse().toLowerCase();

					if(response.contains("disabled") || response.contains("locked"))
					{
						OSSBotV2.setIsBanned(true);
					}
					else
					{
						WorldHopper.changeWorld(83);
						String[] loginDetails = BotFiles.getLoginDetails();
						BotFiles.botLogger("Logging in with details: " + Arrays.toString(loginDetails));
						Login.login(loginDetails[0], loginDetails[1]);
					}
				}
				if(Login.getLoginState().equals(Login.STATE.WELCOMESCREEN))
				{
					RSInterface clickToPlayButton = Interfaces.get(378, 6);
					if(clickToPlayButton != null)
					{
						Clicking.click(clickToPlayButton);
						BotFiles.botLogger("Had to manually click 'play'...");
					}
				}
			}
			General.sleep(1000);
		}
	}

}
