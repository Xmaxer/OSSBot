package scripts.ossbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Cluesolver extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"cipher", "cryptic", "anagram"},{"insertToLookFor"}};
	private int level = 0;

	public Cluesolver()
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
			if(level == 2)
			{
				if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][0]))
				{
					getCipher(commandParams[1]);
				}
				else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][1]))
				{
					getCryptic(commandParams[1]);
				}
				else if(realCommandName.equalsIgnoreCase(STATIC_COMMAND_PARAMS[0][2]))
				{
					getAnagram(commandParams[1]);
				}
			}
			else
			{
				Messenger.messageFormatter("Did you use apostrophes to specify the " + realCommandName + "?");
				return;
			}
		}
	}
	private void getAnagram(String anagramToLookFor) {
		File cipherFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.CIPHER_FILE);

		ArrayList<String[]> data = getData(cipherFile);

		anagramToLookFor = anagramToLookFor.toLowerCase().replaceAll("_", " ");

		for(String[] dataPiece : data)
		{
			if(dataPiece.length >= 4)
			{
				String anagram = dataPiece[0].toLowerCase();
				if(anagram.equalsIgnoreCase(anagramToLookFor))
				{
					Messenger.messageFormatter("Npc: " + dataPiece[1] + " location: " + dataPiece[2] + " answer: " + dataPiece[3]);
					return;
				}
			}
		}
		
		Messenger.messageFormatter("Couldn't find anagram: " + anagramToLookFor);

	}
	private void getCryptic(String crypticToLookFor) {
		File cipherFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.CIPHER_FILE);

		ArrayList<String[]> data = getData(cipherFile);

		crypticToLookFor = crypticToLookFor.toLowerCase().replaceAll("_", " ");

		for(String[] dataPiece : data)
		{
			if(dataPiece.length >= 2)
			{
				String cryptic = dataPiece[0].toLowerCase();
				if(cryptic.contains(crypticToLookFor))
				{
					Messenger.messageFormatter(dataPiece[1]);
					return;
				}
			}
		}
		
		Messenger.messageFormatter("Couldn't find crypt: " + crypticToLookFor);

	}
	private void getCipher(String cipherToLookFor) {

		File cipherFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + COMMAND_NAME + OssBotConstants.SEPARATOR + OssBotConstants.CIPHER_FILE);

		ArrayList<String[]> data = getData(cipherFile);

		cipherToLookFor = cipherToLookFor.toLowerCase().replaceAll("_", " ");

		for(String[] dataPiece : data)
		{
			if(dataPiece.length >= 4)
			{
				String cipher = dataPiece[0].toLowerCase();
				if(cipher.equalsIgnoreCase(cipherToLookFor))
				{
					Messenger.messageFormatter(dataPiece[1] + " answer: " + dataPiece[2]);
					return;
				}
			}
		}
		
		Messenger.messageFormatter("Couldn't find cipher: " + cipherToLookFor);
	}
	private ArrayList<String[]> getData(File file) {
		if(file.exists())
		{
			try {
				Scanner s = new Scanner(file);
				ArrayList<String[]> data = new ArrayList<String[]>();
				while(s.hasNextLine())
				{
					data.add(s.nextLine().split(":"));
				}
				s.close();
				return data;
			} catch (Exception e) {
				OssBotMethods.printException(e);
				return null;
			}
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
