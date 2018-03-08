package scripts.ossbot.commands;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Vote extends Command{

	public Vote() {
		super(2, new String[][] {{"insertCandidate", "candidates"}});
	}

	@Override
	public void execute() {

		if(super.getLevel() > 0)
			checkFirstParam();
		else
			Messenger.messageFormatter("This command requires a candidate to vote for!", true);

	}

	private void checkFirstParam() {

		final String[] candidates = {"febelz", "daddy_ferric", "mr_netty", "f_ll_u_r_y"};
		//final String[] candidates = {"noob1", "noob2", "noob3", "noob4"};
		try {
			Integer c = 0;
			try {
				c = Integer.parseInt(super.getUserCommandParams()[0]);
			} catch(NumberFormatException e)
			{
				String realCommandName = BotFiles.checkLevelParams("level1", super.getUserCommandParams()[0], super.getCommandName());
				if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][1]))
				{
					String msg = "Candidates: ";
					for(int i = 0, n = candidates.length; i < n; i++)
					{
						if(i == (candidates.length - 1))
						{
							msg += "[" + (i+1) + "] " + candidates[i];
							break;
						}
						msg += "[" + (i+1) + "] " + candidates[i] + " || ";
					}
					Messenger.messageFormatter(msg, true);
					return;
				}
				Messenger.messageFormatter("Must select a candidate number [1-4] to vote for, " + OSSBotV2.getIssuerName(), true);
				return;
			}
			if(OSSBotV2.getIssuerRank() == 5)
			{
				Messenger.messageFormatter("Silvers are not allowed to vote, " + OSSBotV2.getIssuerName(), true);
				return;
			}
			if(!OSSBotV2.getPM())
			{
				Messenger.messageFormatter("You can only vote for someone via PMing the bot the command!", true);
				return;
			}
			
			if(c > 0 && c <= candidates.length)
			{
				File f = new File(OssBotConstants.goldVotesFile);
				Scanner s = new Scanner(f);
				String finalData = "";
				boolean changed = false;
				while(s.hasNextLine())
				{
					String line = s.nextLine();
					String[] info = line.split(",");

					if(info.length == 3)
					{
						if(info[0].equalsIgnoreCase(OSSBotV2.getIssuerName()))
						{
							changed = true;
							continue;
						}
						finalData += line + System.lineSeparator();
					}
				}
				s.close();
				finalData += OSSBotV2.getIssuerName() + "," + c + "," + System.currentTimeMillis();
				FileWriter fw = new FileWriter(f, false);
				fw.write(finalData);
				fw.close();

				if(changed)
					Messenger.messageFormatter("You have successfully changed your vote, " + OSSBotV2.getIssuerName(), true);
				else
					Messenger.messageFormatter("You have successfully submitted your vote, " + OSSBotV2.getIssuerName(), true);


			}
			else
				Messenger.messageFormatter("Must select a candidate number [1-4] to vote for, " + OSSBotV2.getIssuerName(), true);
		} catch (Exception e) {
			OssBotMethods.printException(e);
		}
	}

}
