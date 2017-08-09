package scripts.ossbot.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Info extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"vpscredit"}};
	private int level = 0;

	public Info()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	@Override
	public void execute() {
		String fullCommand = OSSBotV2.getIssuerCommand();
		String[] commandParams = OssBotMethods.getcommandParams(fullCommand);

		level = OssBotMethods.findMaximumCommandLevel(commandParams, fullCommand);
		if(level > 0)
		{
			checkFirstParam(commandParams);
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}
	private void checkFirstParam(String[] commandParams) {
		String realCommandName = BotFiles.checkLevelParams("level1", commandParams[0], COMMAND_NAME);
		
		if(realCommandName != null)
		{
			if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][0]))
			{
				getVPSCredit();
			}
		}
		
	}
	private void getVPSCredit() {
		String urlText = "https://api.vultr.com/v1/account/info";
		try{
		URL url = new URL(urlText);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", OssBotConstants.USER_AGENT);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("API-Key", OssBotConstants.VULTR_API_KEY);

		con.connect();

		//		String data = "API-Key: " + API_KEY;
		//		
		//		OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
		//		writer.write(data);
		//		writer.flush();
		//		writer.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String inputLine;
		StringBuffer bufferResponse = new StringBuffer();
		while((inputLine = in.readLine()) != null)
		{
			bufferResponse.append(inputLine);
		}
		in.close();

		String response = bufferResponse.toString();

		Matcher m = Pattern.compile("balance\":\"(.*?)\",\"pending_charges\":\"(.*?)\"").matcher(response);

		if(m.find())
		{
			if(m.groupCount() == 2)
			{
				Double balance = Double.valueOf(m.group(1));
				Double charges = Double.valueOf(m.group(2));
				
				double credit = (balance + charges)*-1;
				
				Messenger.messageFormatter("Vps credit left: $" + credit);
				return;
			}
		}
		} catch(Exception e)
		{
			OssBotMethods.printException(e);
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
