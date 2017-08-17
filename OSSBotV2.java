package scripts.ossbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Starting;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.commands.*;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;
@ScriptManifest(authors = { "xmax" }, category = "OSSBot", name = "OSSBot", version = 1.00, description = "A bot for all", gameMode = 1)

public class OSSBotV2 extends Script implements MessageListening07,Starting,Ending{
	private List<Command> validCommands = new ArrayList<Command>();

	private static boolean PM = false;
	private static String issuerName = null;
	private static String issuerCommand = null;
	private static int issuerRank = -1;
	private boolean lock = false;
	private boolean inCC = false;
	private boolean loggedIn = false;
	private boolean isBanned = false;
	private long lastClicked = System.currentTimeMillis();
	private long lastTracked = ZonedDateTime.now().toInstant().toEpochMilli();
	
	@Override
	public void run() {

		Thread tracker = new Thread(new Runnable() {

			@Override
			public void run() {

				while(true)
				{
					if(loggedIn && inCC)
					{
						RSInterface memberListParent = Interfaces.get(OssBotConstants.CC_PLAYER_INTERFACE[0], OssBotConstants.CC_PLAYER_INTERFACE[1]);

						if(memberListParent != null)
						{

							RSInterface[] memberList = memberListParent.getChildren();

							if(memberList != null && memberList.length >= 1)
							{
								Long currentMillis = ZonedDateTime.now().toInstant().toEpochMilli();
								Long diff = currentMillis - lastTracked;
								if(diff >= 300000L)
								{
									BotFiles.trackPlayersInCC(memberList);
									lastTracked = currentMillis;
								}
								for(int i = 0; i < memberList.length; i+=5)
								{
									String playerName = OssBotMethods.standardiseName(memberList[i].getText());
									String playerWorld = memberList[i + 1].getText().replaceAll(" ", "");

									File playerPropertiesDirectory = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR);
									playerPropertiesDirectory.mkdirs();

									File playerTimePropertiesFile = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);

									if(!playerTimePropertiesFile.exists())
									{
										if(!lock)
										{
											lock = true;
											PM = false;
											BotFiles.createPlayerProperties(playerTimePropertiesFile);
											BotFiles.botLogger("New player: " + playerName);
											Messenger.messageFormatter("Welcome " + playerName + " to the clan chat!");
											lock = false;
										}
									}

									BotFiles.updateTimeProperties(playerWorld, playerTimePropertiesFile);

									File playerCachePropertiesFile = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);

									if(!playerCachePropertiesFile.exists())
									{
										BotFiles.createPlayerProperties(playerCachePropertiesFile);
									}

									BotFiles.updateCacheProperties(playerCachePropertiesFile, playerWorld, playerName);
								}
							}
						}
					}
					else
					{
						BotFiles.botLogger("Not tracking!");
					}

					General.sleep(1000);

				}
			}
		});

		Thread loginTracker = new Thread(new Runnable() {

			@Override
			public void run() {

				while(true)
				{
					if(Login.getLoginState().equals(Login.STATE.INGAME))
					{
						loggedIn = true;
						if(GameTab.getOpen().equals(TABS.CLAN))
						{
							RSInterface joinButton = Interfaces.get(OssBotConstants.JOIN_CLAN_BUTTON[0], OssBotConstants.JOIN_CLAN_BUTTON[1]);
							if(joinButton != null)
							{
								if(joinButton.getText().toLowerCase().contains("join"))
								{
									inCC = false;
								}
								else
								{
									inCC = true;
								}
								if(!inCC)
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
						if(!lock)
						{
							RSTile playerPos = Player.getPosition();
							if(System.currentTimeMillis() - lastClicked >= 120000)
							{
								Camera.setCameraAngle(100);
								Walking.walkTo(playerPos);
								lastClicked = System.currentTimeMillis();
							}
						}
					}
					else
					{
						loggedIn = false;
						if(Login.getLoginState().equals(Login.STATE.LOGINSCREEN))
						{
							String response = Login.getLoginResponse().toLowerCase();

							if(response.contains("disabled") || response.contains("locked"))
							{
								isBanned = true;
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

		});

		Thread announcer = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true)
				{
					if(loggedIn && !lock && inCC)
					{
						File[] allAnnouncementDirs = new File(OssBotConstants.ANNOUNCER_DIRECTORY).listFiles();

						if(allAnnouncementDirs != null && allAnnouncementDirs.length > 0)
						{
							for(File announcementDir : allAnnouncementDirs)
							{
								long now = ZonedDateTime.now().toInstant().toEpochMilli();
								File propertiesFile = new File(announcementDir + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

								Properties props = BotFiles.getProperties(propertiesFile);

								Long lastAnnounced = Long.valueOf(props.getProperty("lastAnnounced"));
								Long interval = Long.valueOf(props.getProperty("interval"));
								Integer used = Integer.valueOf(props.getProperty("announced"));

								if(lastAnnounced != null && interval != null && (now - lastAnnounced) >= interval)
								{
									lock = true;
									File messageFile = new File(announcementDir + OssBotConstants.SEPARATOR + OssBotConstants.ANNOUNCEMENT_FILE);
									try {
										Scanner reader = new Scanner(messageFile);
										String announcement = "";
										if(reader.hasNextLine())
										{
											announcement = reader.nextLine();
										}
										reader.close();
										PM = false;
										Messenger.messageFormatter("[" + announcementDir.getName() + "] " + announcement);
										props.setProperty("lastAnnounced", String.valueOf(now));
										if(used != null)
										{
											used++;
											props.setProperty("announced", String.valueOf(used));
										}
										BotFiles.storeProperties(props, propertiesFile);
									} catch (Exception e) {
										OssBotMethods.printException(e);
									}
								}
								lock = false;
							}
						}
					}
					General.sleep(1000);
				}
			}

		});

		Thread webDownloader = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true)
				{
					String link = OssBotConstants.CML_COMP_LINK;
					BotFiles.downloadCMLComp(link);
					link = OssBotConstants.PLAYER_DATA_SHEET_LINK;
					BotFiles.downloadDataSheet(link);
					link = OssBotConstants.PLAYER_WINS_LINK;
					BotFiles.downloadCompDataSheet(link);


					BotFiles.transferSheetData();
					BotFiles.transferCompWinsData();

					//BotFiles.botLogger("Finished updating downloaded data.");
					General.sleep(180000);
				}

			}

		});
		while(!isBanned)
		{
			if(!lock && loggedIn)
			{
				GameTab.open(TABS.CLAN);
			}
			try{
				if(!webDownloader.isAlive())
				{
					webDownloader.start();
				}
				if(!announcer.isAlive())
				{
					announcer.start();
				}
				if(!loginTracker.isAlive())
				{
					loginTracker.start();
				}
				if(!tracker.isAlive())
				{
					tracker.start();
				}
			} 
			catch (Exception e)
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				
				General.println(sw.toString());
				General.println("Web downloader: " + webDownloader.getState());
				General.println("Announcer: " + announcer.getState());
				General.println("Login Tracker: " + loginTracker.getState());
				General.println("Tracker: " + tracker.getState());
			}
			General.sleep(150);
		}

		BotFiles.botLogger("Account is now banned or locked, shutting down.");
	}

	@Override
	public void onEnd() {
		General.println("Shutting down...");
	}

	@Override
	public void onStart() {
		this.setAIAntibanState(false);
		this.setLoginBotState(false);
		General.useAntiBanCompliance(false);

		General.println("Creating all command classes...");

		Collections.addAll(validCommands,
				new Ball8(),
				new Config(),
				new Announcement(),
				new Help(),
				new Data(),
				new Comp(),
				new Top(),
				new Time(),
				new Price(),
				new Levelup(),
				new Joke(),
				new Poll(),
				new CML(),
				new Calc(),
				new Caps(),
				new Fact(),
				new Offsite(),
				new Examine(),
				new Screenie(),
				new Flipcoin(),
				new Cluesolver(),
				new Qfc(),
				new Cleanup(),
				new Info());

		General.println("Commands created.");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		General.println("Time and date: " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z")));
		Keyboard.setSpeed(0.1);

		File playerDataDirectory = new File(OssBotConstants.PLAYER_DATA_DIRECTORY);
		File chatLogsDirectory = new File(OssBotConstants.CHAT_LOG_DIRECTORY);
		File botLogDirectory = new File(OssBotConstants.BOT_LOG_DIRECTORY);
		File announcerDirectory = new File(OssBotConstants.ANNOUNCER_DIRECTORY);
		File downloadsDirectory = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY);
		File competitionDirectory = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY);
		competitionDirectory.mkdirs();
		downloadsDirectory.mkdirs();
		announcerDirectory.mkdirs();
		botLogDirectory.mkdirs();
		playerDataDirectory.mkdirs();
		chatLogsDirectory.mkdirs();
	}

	@Override
	public void clanMessageReceived(String name, String message) {
		setPM(false);
		name = OssBotMethods.standardiseName(name);
		String botName = OssBotMethods.standardiseName(Player.getRSPlayer().getName());
		chatLogger(name, message);
		charsTypedTracker(name, message);
		if(message.toLowerCase().contains("Attempting to kick player from friends chat...".toLowerCase()))
		{
			kickMessage(name);
		}
		isItACommand(name, message, botName);
	}
	private void kickMessage(String name) {
		
		BotFiles.botLogger(("Kick message detected."));
		String fileName = "kick - "+ name + " - " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d yyyy HH-mm-ss z")) + ".png";
		
		if(Screenshots.take(fileName, false, false))
		{
			OssBotMethods.uploadToImgur(fileName, OssBotConstants.IMGUR_KICK_ALBUM_ID);
		}
		else
		{
			BotFiles.botLogger("Failed to take screenshot of kick message.");
		}
	}





	private void charsTypedTracker(String name, String message) {

		File[] allPlayerDirectories = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

		for(File playerDirectory : allPlayerDirectories)
		{
			String playerName = OssBotMethods.standardiseName(playerDirectory.getName());

			if(playerName.equalsIgnoreCase(name))
			{

				File playerCharPropertiesFile = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TYPING_DATA);

				if(!playerCharPropertiesFile.exists())
				{
					BotFiles.createPlayerProperties(playerCharPropertiesFile);
				}

				BotFiles.updateTypingProperties(message, playerCharPropertiesFile);
				return;
			}
		}

	}
	private void preCommandInitiation() {
		lock = true;
		final Duration timeout = Duration.ofSeconds(OssBotConstants.TIMEOUT_DURATION);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<String> Handler = executor.submit(new Callable<String>(){

			@Override
			public String call() throws Exception {
				findCommand();
				return null;
			}

		});

		try{
			Handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch(Exception e) {

			Handler.cancel(true);
			OssBotMethods.printException(e);

		}

		executor.shutdownNow();
		lock = false;
	}
	private void findCommand() {
		for(Command command : validCommands)
		{
			if(command.checkCallNames())
			{
				if(command.canExecute())
				{
					command.execute();
				}
				return;
			}
		}

		General.println(getIssuerCommand() + " is not a valid command");

	}

	private void setIssuerName(String issuerName) {

		OSSBotV2.issuerName = issuerName;
	}
	public static String getIssuerName() {

		return issuerName;
	}
	private void setIssuerCommand(String issuerCommand) {

		OSSBotV2.issuerCommand = issuerCommand;
	}
	public static String getIssuerCommand() {

		return issuerCommand;
	}
	private void setIssuerRank(int issuerRank) {

		OSSBotV2.issuerRank = issuerRank;
	}
	public static int getIssuerRank() {

		return issuerRank;
	}
	@Override
	public void duelRequestReceived(String arg0, String arg1) {

	}

	@Override
	public void personalMessageReceived(String name, String message) {
		setPM(true);
		name = OssBotMethods.standardiseName(name);
		String botName = OssBotMethods.standardiseName(Player.getRSPlayer().getName());
		chatLogger(name, message);
		isItACommand(name, message, botName);
	}

	private void chatLogger(String name, String message) {

		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
		DateTimeFormatter date = DateTimeFormatter.ofPattern("MMM d, yyyy");
		ZonedDateTime currentTime = ZonedDateTime.now();
		File todaysChatLogFile = new File(OssBotConstants.CHAT_LOG_DIRECTORY, currentTime.format(date) + ".txt");
		try{
			if(!todaysChatLogFile.exists())
			{
				todaysChatLogFile.createNewFile();
				General.println("New day started in chat logs: " + currentTime.format(date));
			}

			FileWriter chatLogger = new FileWriter(todaysChatLogFile, true);

			String log = "[" + currentTime.format(time) + "] " + name + ": " + message + System.lineSeparator();
			if(getPM())
			{
				log = "[PM]" + log;
			}
			chatLogger.write(log);
			chatLogger.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void isItACommand(String name, String message, String botName)
	{
		if(message.startsWith("!!") && !name.equals(botName))
		{
			GameTab.open(TABS.CLAN);
			while(lock)
			{
				General.sleep(1000);
				General.println("Waiting...");
			}
			setIssuerName(name);
			setIssuerCommand(message.substring(2));
			setIssuerRank(Ranking.getPlayerRank(""));

			preCommandInitiation();
		}
	}
	private void setPM(boolean PM) {
		OSSBotV2.PM = PM;
	}
	public static boolean getPM() {
		return PM;
	}
	@Override
	public void playerMessageReceived(String arg0, String arg1) {

	}

	@Override
	public void serverMessageReceived(String arg0) {

	}

	@Override
	public void tradeRequestReceived(String name) {
		lock = true;
		PM = false;
		Keyboard.typeSend("Beep boop I am a bot. Trading me does nothing.");
		Messenger.messageFormatter(name + " has traded me! Why? Fuck do I know. I am a bot. Beep boop.");
		lock = false;
	}
}
