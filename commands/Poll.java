package scripts.ossbot.commands;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Poll extends Command{

	public Poll()
	{
		super(0, new String[][]{{"comp"},{"vote", "create"},{"insertVoteHere", "insertThreeSkills"}});
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
				if(super.getLevel() == 1)
				{
					getCurrentCompPoll();
				}
				else
				{
					compPolling();
				}
			}
		}
	}
	private void compPolling() {
		String realCommandName = BotFiles.checkLevelParams("level2", super.getUserCommandParams()[1], super.getCommandName());

		if(realCommandName != null)
		{
			if(super.getLevel() >= 3)
			{
				if(realCommandName.equalsIgnoreCase(super.getCommandParams()[1][0]))
				{
					voteInCompPoll(super.getUserCommandParams()[2]);
				}
				else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[1][1]))
				{
					createNewCompPoll(super.getUserCommandParams()[2]);
				}
			}
		}
	}



	private void createNewCompPoll(String skills) {
		File[] allCompsIDDirs = new File(OssBotConstants.POLLING_DIRECTORY).listFiles();
		String[] allSkills = skills.split("_");
		if(allSkills.length == 3)
		{
			int largestID = 0;
			for(File compIDDir : allCompsIDDirs)
			{
				Integer compID = Integer.valueOf(compIDDir.getName());

				if(compID > largestID)
				{
					largestID = compID;
				}
			}
			largestID++;

			File newIDDir = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR);
			newIDDir.mkdirs();

			File configFile = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);

			Properties props = new Properties();
			props.setProperty(allSkills[0], "0");
			props.setProperty(allSkills[1], "0");
			props.setProperty(allSkills[2], "0");

			BotFiles.storeProperties(props, configFile);

			File votesFile = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR + OssBotConstants.VOTES_FILE);

			try{
				if(!votesFile.exists())
				{
					votesFile.createNewFile();
				}

				Messenger.messageFormatter("Successfully created new comppoll with options: " + allSkills[0] + ", " + allSkills[1] + ", " + allSkills[2]);
				return;
			} catch(Exception e) {
				OssBotMethods.printException(e);
				return;
			}
		}
		Messenger.messageFormatter("Exactly 3 skills are required for a comppoll creation.");
	}
	private void getCurrentCompPoll() {
		File[] allCompsIDDirs = new File(OssBotConstants.POLLING_DIRECTORY).listFiles();
		int largestID = 0;
		for(File compIDDir : allCompsIDDirs)
		{
			Integer compID = Integer.valueOf(compIDDir.getName());

			if(compID > largestID)
			{
				largestID = compID;
			}
		}
		File configFile = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);
		Properties props = BotFiles.getProperties(configFile);
		ArrayList<Object> allProps = Collections.list(props.keys());
		ArrayList<Integer> voteNumbers = new ArrayList<Integer>();
		ArrayList<String> options = new ArrayList<String>();
		double totalVotes = 0.0;
		for(Object prop : allProps)
		{
			String key = String.valueOf(prop);
			Integer votes = Integer.valueOf(props.getProperty(key));
			options.add(key);
			voteNumbers.add(votes);
			totalVotes += votes;
		}
		DecimalFormat df = new DecimalFormat("0.##");
		
		if(totalVotes == 0)
		{
			totalVotes = 1.0;
		}
		Messenger.messageFormatter("["+voteNumbers.get(0)+"] "+ Double.parseDouble(df.format((voteNumbers.get(0)/totalVotes)*100)) + "%/"+ options.get(0) +" || " + "["+voteNumbers.get(1)+"] "+ Double.parseDouble(df.format((voteNumbers.get(1)/totalVotes)*100)) + "%/"+ options.get(1) + " || " + "["+voteNumbers.get(2)+"] "+ Double.parseDouble(df.format((voteNumbers.get(2)/totalVotes)*100)) + "%/"+ options.get(2));
	}
	private void voteInCompPoll(String vote) {
		File[] allCompsIDDirs = new File(OssBotConstants.POLLING_DIRECTORY).listFiles();
		int largestID = 0;
		for(File compIDDir : allCompsIDDirs)
		{
			Integer compID = Integer.valueOf(compIDDir.getName());

			if(compID > largestID)
			{
				largestID = compID;
			}
		}
		File configFile = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR + OssBotConstants.PROPERTY_FILE);
		File votesFile = new File(OssBotConstants.POLLING_DIRECTORY + largestID + OssBotConstants.SEPARATOR + OssBotConstants.VOTES_FILE);

		Properties props = BotFiles.getProperties(configFile);

		ArrayList<Object> allProps = Collections.list(props.keys());

		for(Object prop : allProps)
		{
			String key = String.valueOf(prop);
			if(key.equalsIgnoreCase(vote))
			{
				try {
					Scanner reader = new Scanner(votesFile);

					while(reader.hasNextLine())
					{
						String line = reader.nextLine();
						String[] split = line.split(":");
						if(split.length == 3)
						{
							if(split[0].equalsIgnoreCase(OSSBotV2.getIssuerName()))
							{
								Messenger.messageFormatter(split[0] + ", you have already voted for " + split[1] + " at " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(split[2])), TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("MMM d, HH:mm:ss")));
								reader.close();
								return;
							}
						}
					}
					reader.close();

					FileWriter fw = new FileWriter(votesFile,true);

					fw.write(OSSBotV2.getIssuerName() + ":" + key + ":" + ZonedDateTime.now().toInstant().toEpochMilli() + System.lineSeparator());
					fw.close();
					Integer votes = Integer.valueOf(props.getProperty(key));
					votes++;

					props.setProperty(key, String.valueOf(votes));

					BotFiles.storeProperties(props, configFile);

					Messenger.messageFormatter("Vote successful by " + OSSBotV2.getIssuerName() + " for " + vote);
					return;
				} catch (Exception e) {
					OssBotMethods.printException(e);
					return;
				}
			}
		}
		Messenger.messageFormatter(vote + " is not a valid vote. Try: " + allProps);

	}
}
