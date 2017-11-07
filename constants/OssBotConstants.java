package scripts.ossbot.constants;

import java.nio.file.Paths;

import scripts.ossbot.hidden.SensitiveData;
import scripts.ossbot.methods.OssBotMethods;

public final class OssBotConstants {
	
	public final static String SEPARATOR = System.getProperty("file.separator");
	public final static int MAX_MESSAGE_LENGTH = 79;
	public final static int GAME_TICK = 650;
	public final static int TIMEOUT_DURATION = 20;
	public final static int[] CC_PLAYER_INTERFACE = {7,14};
	public final static int[] FRIENDS_LIST_PLAYER_INTERFACE = {429,8};
	public final static int[] JOIN_CLAN_BUTTON = {7, 17};
	public final static int[] ADD_FRIEND_BUTTON = {429, 11};
	public final static int[] REMOVE_FRIEND_BUTTON = {429, 13};
	public final static int[] CHAT_INTERFACE = {162, 32};
	public final static String JOIN_CC_TEXT = "Enter the player name who channel you wish to join";
	public final static String ADD_FRIEND_TEXT = "Enter name of friend to add to list";
	public final static String REMOVE_FRIEND_TEXT = "Enter name of friend to delete from list";
	public final static String SEND_PM_TEXT = "Enter message to send to ";
	public final static String MAIN_PATH = Paths.get("").toAbsolutePath().toString() + SEPARATOR + "OSSBot";
	public final static String CHAT_LOG_DIRECTORY = MAIN_PATH + SEPARATOR + "chat_logs" + SEPARATOR;
	public final static String BOT_LOG_DIRECTORY = MAIN_PATH + SEPARATOR + "bot_logs" + SEPARATOR;
	public final static String PROPERTY_FILE = "config.properties";
	public final static String COMMAND_FILES_DIRECTORY = MAIN_PATH + SEPARATOR + "command_files" + SEPARATOR;
	public final static String PLAYER_DATA_DIRECTORY = MAIN_PATH + SEPARATOR + "players" + SEPARATOR;
	public final static String PLAYER_TIMING_DATA = "time.properties";
	public final static String PLAYER_TYPING_DATA = "chat.properties";
	public final static String PLAYER_CACHE_DATA = "cache.properties";
	public final static String PLAYER_SHEET_DATA = "sheet.properties";
	public final static int TIMEOUT_FOR_LOGOUT = 5000;
	public final static String[][] DEFAULT_COMMAND_PROPERTIES = {{"commandNames", "minimumRank", "nameRestrictions", "nameExceptions", "used","enabled"},{"", "", "", "xmax.cynes","0","true"}};
	public final static String PARAMETER_DIRECTORY = "parameters" + SEPARATOR;
	public final static String LOGIN_PPROPERTIES_FILE = MAIN_PATH + SEPARATOR + "loginDetails.properties";
	public final static int MAX_NAME_LENGTH = 12;
	public final static String ANNOUNCER_DIRECTORY = MAIN_PATH + SEPARATOR + "announcer" + SEPARATOR;
	public final static String ANNOUNCEMENT_FILE = "message.txt";
	public final static Long MINIMUM_INTERVAL_FOR_ANNOUNCEMENT = 900000L;
	public final static String DOWNLOADED_DATA_DIRECTORY = MAIN_PATH + SEPARATOR + "downloads" + SEPARATOR;
	public final static String COMPETITION_DATA_DIRECTORY = MAIN_PATH + SEPARATOR + "competitions" + SEPARATOR;
	public final static String DATA_SHEET_FILE = "playerDataSheet.txt";
	public final static String COMP_WINS_DATA_FILE = "playerCompWins.txt";
	public final static String CURRENT_COMP_ID_FILE = "currentCompid.txt";
	public final static String COMP_FILE = "data.txt";
	public final static String LEVELUP_TEXT_FILE = "messages.txt";
	public final static String ENDOFCOMP_FILE = "endofcomp.txt";
	public final static String VOTES_FILE = "votes.txt";
	public final static String JOKES_FILE = "jokes.txt";
	public final static String PLAYER_COUNT_TRACKING_FILE = "playerCount.txt";
	public final static String PLAYER_COUNT_DAY_TRACKING_FILE = "playerCountDay.txt";
	public final static String POLLING_DIRECTORY = MAIN_PATH + SEPARATOR + "competition_polls" + SEPARATOR;
	public final static String SCREENSHOTS_DIRECTORY = MAIN_PATH + SEPARATOR + "screenshots" + SEPARATOR;
	public final static String CML_COMP_LINK = "http://crystalmathlabs.com/tracker/api.php?type=comprankings&competition=";
	public final static String PLAYER_DATA_SHEET_LINK = "https://docs.google.com/spreadsheets/d/1EKvjECjTnwwT9uJzMSu13UN3sYCF-yQaFFyFXUabZhg/pub?gid=298604662&single=true&output=csv";
	public final static String PLAYER_WINS_LINK = "https://docs.google.com/spreadsheets/d/1EKvjECjTnwwT9uJzMSu13UN3sYCF-yQaFFyFXUabZhg/pub?gid=1099814919&single=true&output=csv";
	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
	public final static String ITEM_DATABASE_DIRECTORY = MAIN_PATH + SEPARATOR + "ItemDB" + SEPARATOR;
	public final static String GE_LINK = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";
	public final static String OSB_LINK = "http://api.rsbuddy.com/grandExchange?a=guidePrice&i=";
	public final static String[][] ALL_SKILLS = {{"total","0", "ttl"},{"attack","1","atk","att"},{"defence","2", "defense", "def"},{"strength","3", "str"},{"hitpoints","4","hp"},{"ranged","5","rng","range"},{"prayer","6", "pray"},{"magic","7","mage"},{"cooking","8","cook"},{"woodcutting","9","wc"},{"fletching","10","fletch"},{"fishing","11","fish"},{"firemaking","12","fm"},{"crafting","13","craft"},{"smithing","14","smith"},{"mining","15","mine"},{"herblore","16","herb"},{"agility","17","agil"},{"thieving","18","thief","thieve","thiev"},{"slayer","19","slay"},{"farming","20","farm"},{"runecrafting","21","rc","runecraft"},{"hunter","22","hunt"},{"construction","23","cons","con"},{"ehp","24"}};
	public final static String CML_UPDATE_LINK = "http://crystalmathlabs.com/tracker/api.php?type=update&player=";
	public final static String CML_TRACKING_LINK = "http://crystalmathlabs.com/tracker/api.php?type=track&player=";
	public final static String RANDOM_FACT_API = "http://numbersapi.com/random/";
	public final static String IMGUR_PROPERTIES = "imgur.properties";
	public final static String IMGUR_SCREENIE_ALBUM_ID = "UFPjs";
	public final static String IMGUR_KICK_ALBUM_ID = "5Ei68";
	public final static String ANAGRAM_FILE = "anagrams.txt";
	public final static String CIPHER_FILE = "ciphers.txt";
	public final static String CRYPTIC_FILE = "cryptics.txt";
	public final static String PASTE_BIN_API_KEY = SensitiveData.PASTE_BIN_API_KEY;
	public final static String PASTE_BIN_USER_KEY = SensitiveData.PASTE_BIN_USER_KEY;
	public final static Long INACTIVITY_ALLOWED = OssBotMethods.getTimeInMillis("2w");
	public final static String VULTR_API_KEY = SensitiveData.VULTR_API_KEY;
}

