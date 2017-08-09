package scripts.ossbot.methods;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;
import scripts.ossbot.OSSBotV2;
import scripts.ossbot.constants.OssBotConstants;

public class OssBotMethods {

	public static boolean isThisCommandCalled(String[] validCalls)
	{
		String playerCall = OSSBotV2.getIssuerCommand();
		if(playerCall.contains(" "))
		{
			String[] playerParams = playerCall.split(" ");
			if(playerParams.length >= 1)
			{
				playerCall = playerParams[0];
			}
		}
		for(String validCall : validCalls)
		{
			if(playerCall.equalsIgnoreCase(validCall))
			{
				return true;
			}
		}
		return false;
	}

	public static void addPlayer() {

		RSInterface addFriendButton = Interfaces.get(OssBotConstants.ADD_FRIEND_BUTTON[0], OssBotConstants.ADD_FRIEND_BUTTON[1]);
		while(addFriendButton == null)
		{
			GameTab.open(TABS.FRIENDS);
			General.sleep(OssBotConstants.GAME_TICK);
		}
		RSInterface addFriendDialogue = Interfaces.get(OssBotConstants.CHAT_INTERFACE[0], OssBotConstants.CHAT_INTERFACE[1]);
		RSInterface friendsPlayerList = Interfaces.get(OssBotConstants.FRIENDS_LIST_PLAYER_INTERFACE[0], OssBotConstants.FRIENDS_LIST_PLAYER_INTERFACE[1]);
		if(friendsPlayerList.getChildren() == null)
		{
			while(addFriendDialogue == null || addFriendDialogue.isHidden() || !addFriendDialogue.getText().equalsIgnoreCase(OssBotConstants.ADD_FRIEND_TEXT))
			{
				Clicking.click(addFriendButton);
				General.sleep(OssBotConstants.GAME_TICK);
			}
			General.sleep(OssBotConstants.GAME_TICK);
			Keyboard.typeSend(OSSBotV2.getIssuerName());
		}
		else
		{
			if(standardiseName(friendsPlayerList.getChild(0).getText()).equalsIgnoreCase(OSSBotV2.getIssuerName()))
			{
				return;
			}
			while(friendsPlayerList.getChildren() != null) {
				removePlayer(friendsPlayerList);
				General.sleep(OssBotConstants.GAME_TICK*2);
			}
			addPlayer();
		}
	}
	private static void removePlayer(RSInterface friendsPlayerList) {

		RSInterface[] addedPlayers = friendsPlayerList.getChildren();
		String playerToRemove = standardiseName(addedPlayers[0].getText());

		RSInterface removeFriendButton = Interfaces.get(OssBotConstants.REMOVE_FRIEND_BUTTON[0], OssBotConstants.REMOVE_FRIEND_BUTTON[1]);
		while(removeFriendButton == null)
		{
			GameTab.open(TABS.FRIENDS);
			General.sleep(OssBotConstants.GAME_TICK);
		}

		RSInterface removeFriendDialogue = Interfaces.get(OssBotConstants.CHAT_INTERFACE[0], OssBotConstants.CHAT_INTERFACE[1]);

		while(removeFriendDialogue == null || removeFriendDialogue.isHidden() || !removeFriendDialogue.getText().equalsIgnoreCase(OssBotConstants.REMOVE_FRIEND_TEXT))
		{
			Clicking.click(removeFriendButton);
			General.sleep(OssBotConstants.GAME_TICK);
		}
		Keyboard.typeSend(playerToRemove);
	}

	public static int findMaximumCommandLevel(String[] commandParams, String fullCommand) {

		if(fullCommand.startsWith(commandParams[0]))
		{
			return commandParams.length - 1;
		}
		return commandParams.length;
	}
	public static String standardiseName(String oldName)
	{
		if(oldName.startsWith("<"))
		{
			while(!oldName.startsWith(">"))
			{
				oldName = oldName.substring(1);
			}
			if(oldName.startsWith(">"))
			{
				oldName = oldName.substring(1);
			}
		}


		return oldName.replaceAll(" ", "_").replaceAll(" ", "_").toLowerCase();
	}



	public static void printException(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);

		String[] errorLines = sw.toString().replaceAll(System.lineSeparator(), "").split("\t");

		for(String errorLine : errorLines)
		{
			General.println(errorLine);
			BotFiles.botLogger(errorLine);
		}
		//Messenger.messageFormatter("Error occured, logged it. (Tell xmax or cynes or something though)");
	}

	public static String[] getcommandParams(String fullCommand)
	{
		if(fullCommand.contains("'"))
		{
			int count = 0;
			for(int i = 0, n = fullCommand.length(); i < n; i++)
			{
				char c = fullCommand.charAt(i);

				if(c == '\'')
				{
					count++;
				}
			}

			if(count%2 != 0)
			{
				return null;
			}

			while(fullCommand.contains("'"))
			{
				fullCommand = (fullCommand.substring(0, fullCommand.indexOf("'"))) + (fullCommand.substring(fullCommand.indexOf("'") + 1, fullCommand.indexOf("'", fullCommand.indexOf("'") + 1)).replaceAll(" ", "_")) + (fullCommand.substring(fullCommand.indexOf("'", fullCommand.indexOf("'") + 1) + 1, fullCommand.length()));
			}
		}
		return fullCommand.substring((fullCommand.contains(" ")) ? fullCommand.indexOf(" ") + 1 : fullCommand.length(), fullCommand.length()).split(" ");
	}

	public static String getIntervalFromMillis(Long timeInMillis) {
		final Long SECOND = 1000L;
		final Long MINUTE = 60*SECOND;
		final Long HOUR = 60*MINUTE;
		final Long DAY = 24*HOUR;
		final Long WEEK = 7*DAY;

		String output = "";

		output += (timeInMillis/WEEK >= 1) ? String.valueOf((int) (Math.floor(timeInMillis/WEEK))) + "w": "";
		timeInMillis -= WEEK*((int) Math.floor(timeInMillis/WEEK));
		output += (timeInMillis/DAY >= 1) ? String.valueOf((int) (Math.floor(timeInMillis/DAY))) + "d": "";
		timeInMillis -= DAY*((int) Math.floor(timeInMillis/DAY));
		output += (timeInMillis/HOUR >= 1) ? String.valueOf((int) (Math.floor(timeInMillis/HOUR))) + "h": "";
		timeInMillis -= HOUR*((int) Math.floor(timeInMillis/HOUR));
		output += (timeInMillis/MINUTE >= 1) ? String.valueOf((int) (Math.floor(timeInMillis/MINUTE))) + "m": "";
		timeInMillis -= MINUTE*((int) Math.floor(timeInMillis/MINUTE));
		output += (timeInMillis/SECOND >= 1) ? String.valueOf((int) (Math.floor(timeInMillis/SECOND))) + "s": "";
		return output;
	}

	public static String ordinal(int number)
	{
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

		switch (number % 100) {
		case 11:
		case 12:
		case 13:
			return number + "th";
		default:
			return number + sufixes[number % 10];

		}
	}
	public static Long getTimeInMillis(String interval) {
		final Long SECOND = 1000L;
		final Long MINUTE = 60*SECOND;
		final Long HOUR = 60*MINUTE;
		final Long DAY = 24*HOUR;
		final Long WEEK = 7*DAY;
		Long totalTimeInMillis = 0L;
		Matcher m = Pattern.compile("(\\d*\\w)").matcher(interval);
		while(m.find())
		{
			String group = m.group().toLowerCase();

			if(group.matches("\\d+[wdhms]{1}"))
			{
				Integer numberOf = Integer.valueOf(group.replaceAll("[wdhms]", ""));
				if(group.contains("w"))
				{
					totalTimeInMillis += numberOf*WEEK;
				}
				else if(group.contains("d"))
				{
					totalTimeInMillis += numberOf*DAY;
				}
				else if(group.contains("h"))
				{
					totalTimeInMillis += numberOf*HOUR;
				}
				else if(group.contains("m"))
				{
					totalTimeInMillis += numberOf*MINUTE;
				}
				else if(group.contains("s"))
				{
					totalTimeInMillis += numberOf*SECOND;
				}
			}
		}
		return totalTimeInMillis;
	}


	private static String getAccessToken()
	{
		try{
			File imgurFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + "Screenie" + OssBotConstants.SEPARATOR + OssBotConstants.IMGUR_PROPERTIES);
			Properties props = BotFiles.getProperties(imgurFile);

			String clientID = props.getProperty("client_id");
			String clientSecret = props.getProperty("client_secret");
			String refreshToken = props.getProperty("refresh_token");
			String accessToken = props.getProperty("access_token");


			URL url = new URL("https://api.imgur.com/oauth2/token");
			String data = "refresh_token=" + refreshToken + "&client_id=" + clientID + "&client_secret=" + clientSecret + "&grant_type=refresh_token";
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");


			conn.connect();
			StringBuilder stb = new StringBuilder();
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				stb.append(line).append("\n");
			}
			wr.close();
			rd.close();

			String response = stb.toString();

			Matcher m = Pattern.compile("access_token\":\"(.*?)\".*refresh_token\":\"(.*?)\"").matcher(response);

			if(m.find())
			{
				if(m.groupCount() == 2)
				{
					accessToken = m.group(1);
					refreshToken = m.group(2);

					props.setProperty("access_token", accessToken);
					props.setProperty("refresh_token", refreshToken);

					BotFiles.storeProperties(props, imgurFile);

					return accessToken;
				}
			}
		} catch(Exception e) {
			printException(e);
		}
		return null;
	}
	public static String getImgurContent(String title, String desc, String fileURL) throws Exception {
		String accessToken = getAccessToken();
		URL url;
		url = new URL("https://api.imgur.com/3/image");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		BufferedImage image = null;
		File file = new File(fileURL);
		//read image
		image = ImageIO.read(file);
		int x = 0, y = 480, w = 520, h = 140;
		BufferedImage dst = new BufferedImage(w , h, BufferedImage.TYPE_INT_ARGB);
		dst.getGraphics().drawImage(image, 0, 0, w, h, x, y - h, x + w, y, null);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ImageIO.write(dst, "png", byteArray);
		byte[] byteImage = byteArray.toByteArray();
		String dataImage = Base64.getEncoder().encodeToString(byteImage);
		String data = URLEncoder.encode("image", "UTF-8") + "="
				+ URLEncoder.encode(dataImage, "UTF-8") + "&title=" + title + "&description=" + desc + "&album=" + OssBotConstants.IMGUR_ALBUM_ID;
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");


		conn.connect();
		StringBuilder stb = new StringBuilder();
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			stb.append(line).append("\n");
		}
		wr.close();
		rd.close();

		Matcher m = Pattern.compile("\\{\"id\":\"(.*?)\"").matcher(stb.toString());

		if(m.find())
		{
			if(m.groupCount() == 1)
			{
				return m.group(1);
			}
		}
		return null;
	}
}