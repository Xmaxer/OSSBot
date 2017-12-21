package scripts.ossbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Player;
import org.tribot.api2007.GameTab.TABS;
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
import scripts.ossbot.threads.Announcer;
import scripts.ossbot.threads.LoginTracker;
import scripts.ossbot.threads.Tracker;
import scripts.ossbot.threads.WebDownloader;
@ScriptManifest(authors = { "xmax" }, category = "OSSBot", name = "OSSBot", version = 1.00, description = "A bot for all", gameMode = 1)

public class OSSBotV2 extends Script implements MessageListening07, Starting, Ending{
	private List<Command> validCommands = new ArrayList<Command>();

	private static boolean PM = false;
	private static String issuerName = null;
	private static String issuerCommand = null;
	private static int issuerRank = -1;
	private static boolean lock = false;
	private static boolean inCC = false;
	private static boolean loggedIn = false;
	private static boolean isBanned = false;
	private static long lastClicked = System.currentTimeMillis();
	private static long lastTracked = ZonedDateTime.now().toInstant().toEpochMilli();
	
	public static boolean getLock() { return lock; }
	public static void setLock(boolean lock) { OSSBotV2.lock = lock; }
	public static boolean getInCC() { return inCC; }
	public static void setInCC(boolean inCC) { OSSBotV2.inCC = inCC; }
	public static boolean getLoggedIn() { return loggedIn; }
	public static void setLoggedIn(boolean loggedIn) { OSSBotV2.loggedIn = loggedIn; }
	public static boolean getIsBanned() { return isBanned; }
	public static void setIsBanned(boolean isBanned) { OSSBotV2.isBanned = isBanned; }
	public static long getLastClicked() { return lastClicked; }
	public static void setLastClicked(long lastClicked) { OSSBotV2.lastClicked = lastClicked; }
	public static long getLastTracked() { return lastTracked; }
	public static void setLastTracked(long lastTracked) { OSSBotV2.lastTracked = lastTracked; }
	public static void setPM(boolean PM) { OSSBotV2.PM = PM; }
	public static boolean getPM() { return PM; }
	
	@Override
	public void run() {

		WebDownloader wd = new WebDownloader();
		Tracker t = new Tracker();
		Announcer a = new Announcer();
		LoginTracker lt = new LoginTracker();
		
		Thread webDownloader = new Thread(wd);
		Thread tracker = new Thread(t);
		Thread announcer = new Thread(a);
		Thread loginTracker = new Thread(lt);
		
		while(!isBanned)
		{
			if(!lock && loggedIn)
			{
				GameTab.open(TABS.CLAN);
			}
			try{
				if(webDownloader.getState().equals(State.TERMINATED))
				{
					webDownloader = new Thread(wd);
				}
				else if(!webDownloader.isAlive())
				{
					webDownloader.start();
				}
				if(announcer.getState().equals(State.TERMINATED))
				{
					announcer = new Thread(a);
				}
				else if(!announcer.isAlive())
				{
					announcer.start();
				}
				if(loginTracker.getState().equals(State.TERMINATED))
				{
					loginTracker = new Thread(lt);
				}
				else if(!loginTracker.isAlive())
				{
					loginTracker.start();
				}
				if(tracker.getState().equals(State.TERMINATED))
				{
					tracker = new Thread(t);
				}
				else if(!tracker.isAlive())
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
				new Info(),
				new Reverse());

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
	@Override
	public void playerMessageReceived(String arg0, String arg1) {

	}

	@Override
	public void serverMessageReceived(String arg0) {

	}

	@Override
	public void tradeRequestReceived(String name) {
/*		lock = true;
		PM = false;
		Keyboard.typeSend("Beep boop I am a bot. Trading me does nothing.");
		Messenger.messageFormatter(name + " has traded me! Why? Fuck do I know. I am a bot. Beep boop.");
		lock = false;*/
	}
}
