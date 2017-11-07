package scripts.ossbot.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Info extends Command{

	public Info()
	{
		super(0, new String[][]{{"vpscredit"}});
	}
	@Override
	public void execute() {

		if(super.getLevel() > 0)
		{
			checkFirstParam();
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}
	private void checkFirstParam() {
		String realCommandName = BotFiles.checkLevelParams("level1", super.getUserCommandParams()[0], super.getCommandName());
		
		if(realCommandName != null)
		{
			if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][0]))
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
}
