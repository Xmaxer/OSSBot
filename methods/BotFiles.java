package scripts.ossbot.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api2007.types.RSInterface;

import scripts.ossbot.constants.OssBotConstants;

public class BotFiles {
	public static String[] getValidCommandNames(String commandName)
	{
		String value = getSinglePropertyValue(commandName,"commandNames");
		return value.split("\\.");
	}
	public static int getMinimumRank(String commandName)
	{
		String value = getSinglePropertyValue(commandName,"minimumRank");
		return Integer.valueOf(value);
	}
	public static String[] getNameRestrictions(String commandName) {

		String value = getSinglePropertyValue(commandName,"nameRestrictions");
		return value.split("\\.");
	}
	public static String[] getNameExceptions(String commandName) {

		String value = getSinglePropertyValue(commandName,"nameExceptions");
		return value.split("\\.");
	}
	public static String getSinglePropertyValue(String commandName, String property)
	{
		Properties prop = new Properties();
		String directory = OssBotConstants.COMMAND_FILES_DIRECTORY + commandName + OssBotConstants.SEPARATOR;

		try {
			InputStream input = new FileInputStream(directory + OssBotConstants.PROPERTY_FILE);

			prop.load(input);
			String toReturn = prop.getProperty(property);
			input.close();
			return toReturn;

		} catch (IOException e) {
			OssBotMethods.printException(e);
		}
		return null;
	}
	public static void checkProperties(String commandName, int defaultRankRequirement, String[][] commandParams) {
		String directory = OssBotConstants.COMMAND_FILES_DIRECTORY + commandName + OssBotConstants.SEPARATOR;
		File mainDirectory = new File(directory);
		mainDirectory.mkdirs();

		if(commandParams.length >= 1)
		{
			for(int level = 0; level < commandParams.length; level++)
			{
				String levelDir = directory + OssBotConstants.PARAMETER_DIRECTORY + "level" + (level + 1) + OssBotConstants.SEPARATOR;
				File levelDirectory = new File(levelDir);
				levelDirectory.mkdirs();
				for(int param = 0; param < commandParams[level].length; param++)
				{
					String paramDir = levelDir + commandParams[level][param] + OssBotConstants.SEPARATOR;
					File paramDirectory = new File(paramDir);
					paramDirectory.mkdirs();

					File paramPropertyFile = new File(paramDir, OssBotConstants.PROPERTY_FILE);

					if(!paramPropertyFile.exists())
					{
						General.println("'" + commandName + "' didn't have a parameter property '" + commandParams[level][param] + "'. created one in: " + paramPropertyFile.getAbsolutePath());

						setDefaultCommandProperties(paramPropertyFile.getAbsolutePath(), commandParams[level][param], defaultRankRequirement);
					}
				}
			}
		}
		File propertyFile = new File(directory + OssBotConstants.PROPERTY_FILE);

		if(!propertyFile.exists())
		{

			General.println("'" + commandName + "' didn't have a properties, created one in: " + mainDirectory.getPath());

			setDefaultCommandProperties(propertyFile.getAbsolutePath(), commandName, defaultRankRequirement);
		}
		else
		{
			Properties prop = new Properties();
			try {
				InputStream input = new FileInputStream(directory + OssBotConstants.PROPERTY_FILE);
				prop.load(input);
				List<String> properties = Collections.list(prop.keys()).stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList());

				for(int row = 0; row < OssBotConstants.DEFAULT_COMMAND_PROPERTIES.length; row+=2)
				{
					for(int column = 0; column < OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row].length; column++)
					{
						String property = OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row][column];
						if(!properties.contains(property))
						{
							General.println("Missing property: " + property + " in '" + commandName + "'");
							if(property.equalsIgnoreCase(OssBotConstants.DEFAULT_COMMAND_PROPERTIES[0][0]))
							{
								prop.setProperty(property, commandName);
							}
							else if(property.equalsIgnoreCase(OssBotConstants.DEFAULT_COMMAND_PROPERTIES[0][1]))
							{
								prop.setProperty(property, String.valueOf(defaultRankRequirement));
							}
							else
							{
								prop.setProperty(property, OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row + 1][column]);
							}
						}
					}
				}

				OutputStream output = new FileOutputStream(directory + OssBotConstants.PROPERTY_FILE);
				prop.store(output, null);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	private static void setDefaultCommandProperties(String dir, String commandName, int defaultRankRequirement)
	{
		try{
			Properties prop = new Properties();
			OutputStream output = new FileOutputStream(dir);

			for(int row = 0; row < OssBotConstants.DEFAULT_COMMAND_PROPERTIES.length; row+=2)
			{
				for(int column = 0; column < OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row].length; column++)
				{
					prop.setProperty(OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row][column], OssBotConstants.DEFAULT_COMMAND_PROPERTIES[row + 1][column]);
				}
			}
			prop.setProperty(OssBotConstants.DEFAULT_COMMAND_PROPERTIES[0][0], commandName);
			prop.setProperty(OssBotConstants.DEFAULT_COMMAND_PROPERTIES[0][1], String.valueOf(defaultRankRequirement));
			prop.store(output, null);
			output.close();
		} catch(Exception e) {
			OssBotMethods.printException(e);
		}
	}
	public static void updateTypingProperties(String message, File playerCharPropertiesFile) {

		Properties playerProperties = new Properties();
		String charsTypedPropertyKey = "charsTyped";
		int charsInMessage = message.length();
		char[] characters = message.toCharArray();
		try {
			InputStream input = new FileInputStream(playerCharPropertiesFile.getAbsolutePath());
			playerProperties.load(input);
			input.close();

			String charsTyped = playerProperties.getProperty(charsTypedPropertyKey);

			if(charsTyped == null)
			{
				playerProperties.setProperty(charsTypedPropertyKey, "0");
			}

			for(char character : characters)
			{
				if(Character.isLetterOrDigit(character) || Character.isWhitespace(character))
				{
					String key = String.valueOf(character).replace(" ", "space").toLowerCase() + "-typed";
					String charProperty = playerProperties.getProperty(key);

					if(charProperty == null)
					{
						playerProperties.setProperty(key, "0");
					}

					charProperty = String.valueOf(Integer.valueOf(playerProperties.getProperty(key)) + 1);

					playerProperties.setProperty(key, charProperty);
				}
			}
			charsTyped = String.valueOf(Integer.valueOf(playerProperties.getProperty(charsTypedPropertyKey)) + charsInMessage);

			playerProperties.setProperty(charsTypedPropertyKey, charsTyped);
			OutputStream output = new FileOutputStream(playerCharPropertiesFile.getAbsolutePath());
			playerProperties.store(output, null);
			output.close();
		} catch (IOException e) {
			OssBotMethods.printException(e);
		}


	}
	public static void updateCacheProperties(File playerCachePropertiesFile, String playerWorld, String playerName) {

		Properties playerProperties = new Properties();
		String lastSeenWorldPropertyKey = "lastSeenWorld";
		String lastSeenRankPropertiesKey = "lastSeenRank";

		try {
			InputStream input = new FileInputStream(playerCachePropertiesFile.getAbsolutePath());
			playerProperties.load(input);
			input.close();

			playerProperties.setProperty(lastSeenWorldPropertyKey, playerWorld);
			playerProperties.setProperty(lastSeenRankPropertiesKey, String.valueOf(Ranking.getPlayerRank(playerName)));

			OutputStream output = new FileOutputStream(playerCachePropertiesFile.getAbsolutePath());
			playerProperties.store(output, null);
			output.close();
		} catch (IOException e) {
			OssBotMethods.printException(e);
		}
	}
	public static Properties getProperties(File configFile) {
		Properties props = new Properties();
		try {
			InputStream input = new FileInputStream(configFile);
			props.load(input);
			input.close();
			return props;
			//
		} catch (Exception e){
			OssBotMethods.printException(e);
		}
		return null;
	}
	public static String[] getDecimalSeparatedProperty(Properties props, String property) {
		return props.getProperty(property).split("\\.");
	}
	public static void downloadDataSheet(String link) {
		String dataSheet = getLinkData(link);
		if(dataSheet != null)
		{
			String[] allData = dataSheet.split(",");
			File dataSheetFile = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY + OssBotConstants.DATA_SHEET_FILE);
			try {
				if(!dataSheetFile.exists())
				{
					dataSheetFile.createNewFile();
				}

				FileWriter fw = new FileWriter(dataSheetFile, false);
				fw.write("");
				fw.close();
				fw = new FileWriter(dataSheetFile, true);

				for(String dataPiece : allData)
				{
					fw.write(dataPiece + System.lineSeparator());
				}
				fw.close();
			} catch (IOException e) {
				OssBotMethods.printException(e);
			}
		}

	}
	public static String getLinkData(String link) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("User-Agent", OssBotConstants.USER_AGENT);
			con.connect();

			StringBuilder sb = new StringBuilder();
			InputStream input = con.getInputStream();
			int c = -1;
			while((c = input.read()) >= 0)
			{
				sb.append((char) c);
			}

			return sb.toString();

		}catch(IOException e)
		{

		}
		catch (Exception e) {
			OssBotMethods.printException(e);
		}
		return null;
	}
	public static void downloadCMLComp(String link) {

		File idFile = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.CURRENT_COMP_ID_FILE);

		try{

			if(idFile.exists())
			{
				Scanner reader = new Scanner(idFile);
				Integer ID = Integer.valueOf(reader.nextLine());
				reader.close();
				if(ID != null)
				{
					link += ID;
					String dataSheet = getLinkData(link);
					if(dataSheet != null)
					{
						String[] allData = dataSheet.split("\\s");
						File dataFileDir = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY + ID + OssBotConstants.SEPARATOR);
						dataFileDir.mkdirs();
						File dataSheetFile = new File(OssBotConstants.COMPETITION_DATA_DIRECTORY + ID + OssBotConstants.SEPARATOR + OssBotConstants.COMP_FILE);

						if(!dataSheetFile.exists())
						{
							dataSheetFile.createNewFile();
						}
						FileWriter fw = new FileWriter(dataSheetFile, false);
						fw.write("");
						fw.close();

						fw = new FileWriter(dataSheetFile, true);

						for(String dataPiece : allData)
						{
							fw.write(dataPiece + System.lineSeparator());
						}
						fw.close();
					}
				}
			}
		}	catch(Exception e) {
			OssBotMethods.printException(e);
		}
	}
	public static void transferCompWinsData() {
		File dataFile = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY + OssBotConstants.COMP_WINS_DATA_FILE);
		final int[] payments = {1000000, 500000, 250000};
		try{
			ArrayList<String> allData = new ArrayList<String>();
			Scanner reader = new Scanner(dataFile);

			while(reader.hasNextLine())
			{
				allData.add(reader.nextLine());
			}

			reader.close();

			int position = 1;
			ArrayList<String> uniqueNames = new ArrayList<String>();
			for(int i = 0, n = allData.size(); i < n; i+=3)
			{
				String playerName = OssBotMethods.standardiseName(allData.get(i));
				if(!uniqueNames.contains(playerName))
				{
					uniqueNames.add(playerName);
				}
			}
			File[] allPlayerDirs = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();

			for(String name : uniqueNames)
			{
				int finalAmount = 0;
				for(int i = 0, n = allData.size(); i < n; i+=3)
				{
					String playerName = OssBotMethods.standardiseName(allData.get(i));
					boolean hasBeenPaidOff = (allData.get(i + 2).toLowerCase().contains("y")) ? true : false;

					if(!hasBeenPaidOff)
					{
						if(name.equalsIgnoreCase(playerName))
						{
							finalAmount += payments[position - 1];
						}
					}
					if(position == 3)
					{
						position = 1;
					}
					else
					{
						position++;
					}
				}
				for(File playerDir : allPlayerDirs)
				{
					if(playerDir.getName().equalsIgnoreCase(name))
					{
						File sheetFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);
						Properties props = new Properties();
						if(sheetFile.exists())
						{
							props = getProperties(sheetFile);
						}
						props.setProperty("moneyWon", String.valueOf(finalAmount));
						storeProperties(props, sheetFile);
						break;
					}
				}
				General.sleep(10);
			}
		}catch(Exception e) {
			OssBotMethods.printException(e);
		}
	}



	public static void transferSheetData() {

		File dataFile = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY + OssBotConstants.DATA_SHEET_FILE);
		try{
			ArrayList<String> allData = new ArrayList<String>();
			Scanner reader = new Scanner(dataFile);

			while(reader.hasNextLine())
			{
				allData.add(reader.nextLine());
			}

			reader.close();
			boolean exists = false;
			for(int i = 0, n = allData.size(); i < n; i+=4)
			{
				File[] allPlayerDirs = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();
				String playerName = OssBotMethods.standardiseName(allData.get(i));
				for(File playerDir : allPlayerDirs)
				{
					if(playerDir.getName().equalsIgnoreCase(playerName))
					{
						File sheetConfig = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);
						Properties props = new Properties();
						if(sheetConfig.exists())
						{
							props = getProperties(sheetConfig);
						}
						props.setProperty("joinDate", allData.get(i + 2));
						props.setProperty("days", allData.get(i + 3));
						props.setProperty("sheetRank", allData.get(i + 1));
						storeProperties(props, sheetConfig);
						exists = true;
						break;
					}
				}
				General.sleep(10);
				if(exists)
				{
					exists = false;
					continue;
				}
				else
				{
					File playerDir = new File(OssBotConstants.PLAYER_DATA_DIRECTORY + playerName + OssBotConstants.SEPARATOR);
					playerDir.mkdirs();
					i -= 4;
				}
			}
		}catch(Exception e) {
			OssBotMethods.printException(e);
		}
	}
	public static void downloadCompDataSheet(String link) {
		String dataSheet = getLinkData(link);
		if(dataSheet != null)
		{
			String[] lineSplit = dataSheet.split(System.lineSeparator());
			ArrayList<String> allData = new ArrayList<String>();
			for(String line : lineSplit)
			{
				Matcher m = Pattern.compile("(.*),\"(.*)\",(.)").matcher(line);

				while(m.find())
				{
					if(m.groupCount() == 3)
					{
						allData.add(m.group(1));
						allData.add(m.group(2));
						allData.add(m.group(3));
					}
				}
			}
			File dataSheetFile = new File(OssBotConstants.DOWNLOADED_DATA_DIRECTORY + OssBotConstants.COMP_WINS_DATA_FILE);
			try {
				if(!dataSheetFile.exists())
				{
					dataSheetFile.createNewFile();
				}

				FileWriter fw = new FileWriter(dataSheetFile, false);
				fw.write("");
				fw.close();
				fw = new FileWriter(dataSheetFile, true);

				for(String dataPiece : allData)
				{
					fw.write(dataPiece + System.lineSeparator());
				}
				fw.close();
			} catch (IOException e) {
				OssBotMethods.printException(e);
			}
		}

	}
	public static String[] getLoginDetails() {
		File loginPropertiesFile = new File(OssBotConstants.LOGIN_PPROPERTIES_FILE);
		Properties loginProperties = getProperties(loginPropertiesFile);
		String[] loginDetails = new String[2];
		loginDetails[0] = loginProperties.getProperty("username");
		loginDetails[1] = loginProperties.getProperty("password");
		return loginDetails;
	}
	public static void storeProperties(Properties properties, File propertiesFile)
	{
		try{
			OutputStream output = new FileOutputStream(propertiesFile);
			properties.store(output, null);
			output.close();
		} catch(Exception e) {
			OssBotMethods.printException(e);
		}
	}
	public static void updateTimeProperties(String playerWorld, File playerTimePropertiesFile) {

		Properties playerProperties = new Properties();

		String timeSpentDayPropertyKey = DateTimeFormatter.ofPattern("EEEE").format(ZonedDateTime.now());
		String lastSeenPropertyKey = "lastSeen";
		String timeSpentPropertyKey = "timeSpent";
		if(playerTimePropertiesFile.exists())
		{
			try {
				InputStream input = new FileInputStream(playerTimePropertiesFile.getAbsolutePath());
				playerProperties.load(input);
				input.close();

				String lastSeen = playerProperties.getProperty(lastSeenPropertyKey);
				String timeSpent = playerProperties.getProperty(timeSpentPropertyKey);
				String worldTimeSpent = playerProperties.getProperty(playerWorld);
				String dayTimeSpent = playerProperties.getProperty(timeSpentDayPropertyKey);

				if(worldTimeSpent == null)
				{
					playerProperties.setProperty(playerWorld, "0");
				}
				if(lastSeen == null)
				{
					playerProperties.setProperty(lastSeenPropertyKey, String.valueOf(System.currentTimeMillis()));
				}
				if(timeSpent == null)
				{
					playerProperties.setProperty(timeSpentPropertyKey, "0");
				}
				if(dayTimeSpent == null)
				{
					playerProperties.setProperty(timeSpentDayPropertyKey, "0");

				}
				worldTimeSpent = playerProperties.getProperty(playerWorld);
				lastSeen = playerProperties.getProperty(lastSeenPropertyKey);
				timeSpent = playerProperties.getProperty(timeSpentPropertyKey);
				dayTimeSpent = playerProperties.getProperty(timeSpentDayPropertyKey);

				long difference = ZonedDateTime.now().toInstant().toEpochMilli() - Long.valueOf(lastSeen);
				lastSeen = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());

				if(difference < OssBotConstants.TIMEOUT_FOR_LOGOUT)
				{
					timeSpent = String.valueOf(Long.valueOf(timeSpent) + difference);
					worldTimeSpent = String.valueOf(Long.valueOf(worldTimeSpent) + difference);
					dayTimeSpent = String.valueOf(Long.valueOf(dayTimeSpent) + difference);
				}

				playerProperties.setProperty(lastSeenPropertyKey, lastSeen);
				playerProperties.setProperty(timeSpentPropertyKey, timeSpent);
				playerProperties.setProperty(playerWorld, worldTimeSpent);
				playerProperties.setProperty(timeSpentDayPropertyKey, dayTimeSpent);

				OutputStream output = new FileOutputStream(playerTimePropertiesFile.getAbsolutePath());
				playerProperties.store(output, null);
				output.close();
			} catch (IOException e) {
				OssBotMethods.printException(e);
			}
		}
	}
	public static void createPlayerProperties(File playerPropertiesFile) {
		try {
			OutputStream output = new FileOutputStream(playerPropertiesFile.getAbsolutePath());
			Properties playerProperties = new Properties();
			playerProperties.setProperty("creationDate", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z")));
			playerProperties.store(output, null);
			output.close();
		} catch (IOException e) {
			OssBotMethods.printException(e);
		}

	}
	public static void botLogger(String message)
	{
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:ss:mm");
		DateTimeFormatter date = DateTimeFormatter.ofPattern("MMM d, yyyy");
		ZonedDateTime currentTime = ZonedDateTime.now();

		File botLogFile = new File(OssBotConstants.BOT_LOG_DIRECTORY, currentTime.format(date) + ".txt");
		try{

			if(!botLogFile.exists())
			{
				General.println("New day started in bot logs: " + currentTime.format(date));
				botLogFile.createNewFile();
			}

			FileWriter botLogger = new FileWriter(botLogFile, true);
			String log = "[" + currentTime.format(time) + "]: " + message + System.lineSeparator();
			botLogger.write(log);
			botLogger.close();
			General.println(message);

		} catch(IOException e) {
			OssBotMethods.printException(e);
		}
	}
	public static void addToUsedCounter(String commandName) {

		File[] allCommands = new File(OssBotConstants.COMMAND_FILES_DIRECTORY).listFiles();

		for(File commandDir : allCommands)
		{
			if(commandDir.getName().equalsIgnoreCase(commandName))
			{
				File configFile = new File(commandDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

				if(configFile.exists())
				{
					Properties props = getProperties(configFile);
					String usedProp = props.getProperty("used");
					int timesUsed = 0;
					if(usedProp != null)
					{
						timesUsed = Integer.valueOf(usedProp);
					}
					timesUsed++;

					props.setProperty("used", String.valueOf(timesUsed));
					storeProperties(props, configFile);
					botLogger("command used: " + commandName);
				}
			}
		}

	}
	public static void trackPlayersInCC(RSInterface[] memberList) {

		int players = memberList.length/5;

		int[] rankCount = new int[9];
		String[] rankCountString = new String[9];
		for(int i = 0, n = memberList.length; i < n; i+=5)
		{
			int textureID = memberList[i+2].getTextureID();
			switch (textureID){
			case 1004:
				//General.println("Player rank is: smiley");
				rankCount[1]++;
				break;
			case 1012:
				//General.println("Player rank is: 1 banana");
				rankCount[2]++;
				break;
			case 1011:
				//General.println("Player rank is: 2 banana");
				rankCount[3]++;
				break;
			case 1010:
				//General.println("Player rank is: 3 banana");
				rankCount[4]++;
				break;
			case 1009:
				//General.println("Player rank is: Bronze star");
				rankCount[5]++;
				break;
			case 1008:
				//General.println("Player rank is: Silver star");
				rankCount[6]++;
				break;
			case 1007:
				//General.println("Player rank is: Gold star");
				rankCount[7]++;
				break;
			case 1006:
				//General.println("Player rank is: Key");
				rankCount[8]++;
				break;
			default:
				//General.println("Player rank is: unranked");
				rankCount[0]++;
				break;
			}
		}
		File trackingFile = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_COUNT_TRACKING_FILE);
		try{
			if(!trackingFile.exists())
			{
				trackingFile.createNewFile();
			}

			FileWriter fw = new FileWriter(trackingFile, true);
			String time = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			for(int i = 0; i < rankCount.length; i++)
				rankCountString[i] = String.valueOf(rankCount[i]);
			String rankCounts = String.join("\t", rankCountString);
			fw.write(time + "\t" + players + "\t" + rankCounts + System.lineSeparator());
			fw.close();

			findDayAverage(trackingFile);
		} catch(Exception e)
		{
			OssBotMethods.printException(e);
		}
	}
	private static void findDayAverage(File trackingFile) {
		File playerCountFile = trackingFile;
		File dayAverages = new File(OssBotConstants.MAIN_PATH + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_COUNT_DAY_TRACKING_FILE);

		try{
			dayAverages.delete();
		} catch(Exception e){}
		
		try{
			dayAverages.createNewFile();

			Scanner s = new Scanner(playerCountFile);

			LinkedHashMap<String, ArrayList<Integer>> data = new LinkedHashMap<String, ArrayList<Integer>>();

			while(s.hasNextLine())
			{
				String[] lineSplit = s.nextLine().split("\t");

				String day = DateTimeFormatter.ofPattern("EEEE").format(ZonedDateTime.parse(lineSplit[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

				if(!data.containsKey(day))
				{
					ArrayList<Integer> newList = new ArrayList<Integer>();
					for(int i = 0; i <= 10; i++)
						newList.add(0);
					data.put(day, newList);
				}

				ArrayList<Integer> dataList = data.get(day);

				ArrayList<Integer> currentData = new ArrayList<Integer>();

				for(int i = 1; i < lineSplit.length; i++)
					currentData.add(Integer.valueOf(lineSplit[i]));

				for(int i = 0; i < dataList.size(); i++)
				{
					if(i == 0)
					{
						dataList.set(0, dataList.get(0) + 1);
						continue;
					}

					dataList.set(i, dataList.get(i) + currentData.get(i-1));
				}

				data.put(day, dataList);

			}
			s.close();
			LinkedHashMap<String, ArrayList<Double>> averages = new LinkedHashMap<String, ArrayList<Double>>();

			for(Map.Entry<String, ArrayList<Integer>> entry : data.entrySet())
			{
				int x = 0;
				ArrayList<Double> average = new ArrayList<Double>();
				int numberOf = entry.getValue().get(0);
				for(Integer num : entry.getValue())
				{
					if(x == 0)
					{
						x++;
						continue;
					}
					average.add(Double.parseDouble(String.valueOf(Double.valueOf(num)/Double.valueOf(numberOf))));
				}

				averages.put(entry.getKey(), average);
			}


			FileWriter fw = new FileWriter(dayAverages, true);
			for(Map.Entry<String, ArrayList<Double>> entry : averages.entrySet())
			{
				fw.write(entry.getKey() + "\t");
				for(double avg : entry.getValue())
				{	
					fw.write(new DecimalFormat("##0.0#").format(avg) + "\t");
					//System.out.println();
				}
				fw.write(System.lineSeparator());
			}

			fw.close();
		} catch(Exception e)
		{
			OssBotMethods.printException(e);
		}

	}
	public static String checkLevelParams(String level, String command, String executedCommandName) {
		File[] allLevelParams = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + executedCommandName + OssBotConstants.SEPARATOR + OssBotConstants.PARAMETER_DIRECTORY + level + OssBotConstants.SEPARATOR).listFiles();

		for(File levelParam : allLevelParams)
		{
			File configFile = new File(levelParam + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			if(configFile.exists())
			{
				Properties props = BotFiles.getProperties(configFile);
				String[] commandNames = BotFiles.getDecimalSeparatedProperty(props, "commandNames");

				for(String commandName : commandNames)
				{
					if(commandName.equalsIgnoreCase(command))
					{
						return (Ranking.hasCommandPrivileges(props)) ? levelParam.getName() : null;	
					}
				}
			}
		}
		BotFiles.botLogger("Couldn't find parameter for: " + command);
		Messenger.messageFormatter("Couldn't find parameter: " + command);
		return null;
	}
}
