package scripts.ossbot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Cleanup extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 5;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {};

	public enum Flags {
		RANKED_BUT_NO_SHEET_DATA,
		SHEET_DATA_BUT_NO_BOT_DATA,
		RANK_MISMATCH,
		INACTIVE,
		SHEET_UNRANKED
	}
	public Cleanup()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {
		String response = submitPasteAndGetLink(getListOfNames());
		response = response.replace("https://pastebin.com/", "");
		String result = "";
		for(int i = 0, n = response.length(); i < n; i++)
		{
			if(Character.isUpperCase(response.charAt(i)))
			{
				result += "—" + String.valueOf(response.charAt(i)).toUpperCase();
			}
			else
			{
				result += String.valueOf(response.charAt(i));
			}
		}

		result = "pastebin-—d()t—-c0m/" + result;

		Messenger.messageFormatter(result);
	}

	private String getListOfNames() {

		File playerDirs[] = new File(OssBotConstants.PLAYER_DATA_DIRECTORY).listFiles();
		LinkedHashMap<String, ArrayList<Flags>> data = new LinkedHashMap<String, ArrayList<Flags>>();

		for(File playerDir : playerDirs)
		{
			ArrayList<Flags> flags = new ArrayList<Flags>();
			File cacheFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_CACHE_DATA);
			File sheetFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_SHEET_DATA);

			if(cacheFile.exists())
			{
				Properties props = BotFiles.getProperties(cacheFile);
				String lastSeenRankString = props.getProperty("lastSeenRank");

				if(lastSeenRankString != null && !lastSeenRankString.equalsIgnoreCase("null") && !lastSeenRankString.isEmpty())
				{
					Integer lastSeenRank = Integer.valueOf(lastSeenRankString);
					if(sheetFile.exists())
					{
						props = BotFiles.getProperties(sheetFile);
						String lastSeenRankSheetString = props.getProperty("sheetRank");
						if(lastSeenRankSheetString != null && !lastSeenRankSheetString.equalsIgnoreCase("null") && !lastSeenRankSheetString.isEmpty())
						{
							Integer lastSeenRankSheet = Integer.valueOf(lastSeenRankSheetString);
							if(lastSeenRankSheet == 0)
							{
								lastSeenRankSheet = -1;
							}
							else if(lastSeenRankSheet == 7)
							{
								lastSeenRankSheet = 0;
							}

							if(lastSeenRankSheet != lastSeenRank)
							{
								flags.add(Flags.RANK_MISMATCH);
							}
						}
					}
					if(lastSeenRank >= 0)
					{
						File timeFile = new File(playerDir.getAbsolutePath() + OssBotConstants.SEPARATOR + OssBotConstants.PLAYER_TIMING_DATA);
						if(!sheetFile.exists())
						{
							flags.add(Flags.RANKED_BUT_NO_SHEET_DATA);
						}
						if(timeFile.exists())
						{
							props = BotFiles.getProperties(timeFile);

							Long lastSeen = Long.valueOf(props.getProperty("lastSeen"));

							Long difference = ZonedDateTime.now().toInstant().toEpochMilli() - lastSeen;

							if(difference >= OssBotConstants.INACTIVITY_ALLOWED)
							{
								flags.add(Flags.INACTIVE);
							}
						}
					}
				}
			}
			else if(sheetFile.exists())
			{
				flags.add(Flags.SHEET_DATA_BUT_NO_BOT_DATA);

				Properties props = BotFiles.getProperties(sheetFile);
				String lastSeenRankSheetString = props.getProperty("sheetRank");
				if(lastSeenRankSheetString != null && !lastSeenRankSheetString.equalsIgnoreCase("null") && !lastSeenRankSheetString.isEmpty())
				{
					Integer lastSeenRankSheet = Integer.valueOf(lastSeenRankSheetString);
					if(lastSeenRankSheet == 0)
					{
						lastSeenRankSheet = -1;
					}
					else if(lastSeenRankSheet == 7)
					{
						lastSeenRankSheet = 0;
					}
					
					if(lastSeenRankSheet < 0)
					{
						flags.add(Flags.SHEET_UNRANKED);
					}
				}

			}

			if(!flags.isEmpty())
			{
				data.put(playerDir.getName(), flags);
			}
		}

		String needToRemoveFromCML = "";
		String mismatchedRanks = "";
		String needToUpdateSheet = "";
		String needToDerank = "";
		String needCCRankConfirmationOfRank = "";
		String shouldBeDeranked = "";

		for(Map.Entry<String, ArrayList<Flags>> entry : data.entrySet())
		{
			String name = entry.getKey();
			ArrayList<Flags> flags = entry.getValue();

			if(flags.contains(Flags.RANKED_BUT_NO_SHEET_DATA))
			{
				needToUpdateSheet += name + "\n";
			}

			if(flags.contains(Flags.SHEET_DATA_BUT_NO_BOT_DATA))
			{
				if(flags.contains(Flags.SHEET_UNRANKED))
				{
					needToRemoveFromCML += name + "\n";
					shouldBeDeranked += name + "\n";
				}
				else
				{
					needCCRankConfirmationOfRank += name + "\n";
				}
			}
			else if(flags.contains(Flags.INACTIVE))
			{
				needToRemoveFromCML += name +  "\n";
				needToDerank += name + "\n";
			}

			if(flags.contains(Flags.RANK_MISMATCH))
			{
				mismatchedRanks += name + "\n";
			}
		}

		String finalOutput = "";

		if(!needToRemoveFromCML.isEmpty())
		{
			String title = "The following people need to be removed from the CML group (xmax does this part)";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + needToRemoveFromCML + "\n\n";
		}
		if(!mismatchedRanks.isEmpty())
		{
			String title = "The following people have mismatched ranks when comparing sheet to bot data.";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + mismatchedRanks + "\n\n";
		}
		if(!needToUpdateSheet.isEmpty())
		{
			String title = "The following people have no sheet data, but are ranked in the CC.";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + needToUpdateSheet + "\n\n";
		}
		if(!needToDerank.isEmpty())
		{
			String title = "The following people need deranking and removal from cml in general (Due to inactivity).";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + needToDerank + "\n\n";
		}
		if(!needCCRankConfirmationOfRank.isEmpty())
		{
			String title = "The following people are marked as ranked on sheet, but have no bot data.";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + needCCRankConfirmationOfRank + "\n\n";
		}
		if(!shouldBeDeranked.isEmpty())
		{
			String title = "The following people SHOULD need deranking. (Are unranked on sheet, but no bot data to confirm)";
			finalOutput += decoration(title);
			finalOutput += "\n" + title + "\n";
			finalOutput += decoration(title);
			finalOutput += "\n\n" + shouldBeDeranked + "\n\n";
		}
		return finalOutput;
	}
	private String decoration(String title)
	{
		String finalOutput = "";
		for(int i = 0, n = title.length(); i < n; i++)
			finalOutput += "-";
		return finalOutput;
	}
	public String submitPasteAndGetLink(String pasteText){


		try{
			URL url = new URL("https://pastebin.com/api/api_post.php");

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("User-Agent", OssBotConstants.USER_AGENT);

			con.connect();

			String pasteName = "Cleanup List by " + OSSBotV2.getIssuerName() + " - " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM d yyyy HH-mm-ss z"));
			String pasteFormat = "text";
			String data = "api_option=paste&api_user_key=" + OssBotConstants.PASTE_BIN_USER_KEY + "&api_dev_key=" + OssBotConstants.PASTE_BIN_API_KEY + "&api_paste_private=1&api_paste_name=" + pasteName + "&api_paste_expire_date=1M&api_paste_format=" + pasteFormat + "&api_paste_code=" + pasteText;

			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuilder stb = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null)
			{
				stb.append(line + "\n");
			}

			return stb.toString();

		} catch (Exception e) {
			OssBotMethods.printException(e);
			Messenger.messageFormatter("Error occured during pastebin connection.");
		}
		return null;
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
