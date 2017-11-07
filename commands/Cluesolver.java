package scripts.ossbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;

public class Cluesolver extends Command{

	public Cluesolver()
	{
		super(0, new String[][]{{"cipher", "cryptic", "anagram"},{"insertToLookFor"}});
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
			if(super.getLevel() == 2)
			{
				if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][0]))
				{
					getCipher();
				}
				else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][1]))
				{
					getCryptic();
				}
				else if(realCommandName.equalsIgnoreCase(super.getCommandParams()[0][2]))
				{
					getAnagram();
				}
			}
			else
			{
				Messenger.messageFormatter("Did you use apostrophes to specify the " + realCommandName + "?");
				return;
			}
		}
	}
	private void getAnagram() {
		File anagramFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.ANAGRAM_FILE);

		ArrayList<String[]> data = getData(anagramFile);

		String anagramToLookFor = super.getUserCommandParams()[1].toLowerCase().replaceAll("_", " ");

		for(String[] dataPiece : data)
		{
			if(dataPiece.length >= 4)
			{
				String anagram = dataPiece[0].toLowerCase();
				if(anagram.equalsIgnoreCase(anagramToLookFor))
				{
					Messenger.messageFormatter("Npc: " + dataPiece[1] + " | location: " + dataPiece[2] + " | answer: " + dataPiece[3]);
					return;
				}
			}
		}
		
		Messenger.messageFormatter("Couldn't find anagram: " + anagramToLookFor);

	}
	private void getCryptic() {
		File crypticFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.CRYPTIC_FILE);

		ArrayList<String[]> data = getData(crypticFile);

		String crypticToLookFor = super.getUserCommandParams()[1].toLowerCase().replaceAll("_", " ");

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
	private void getCipher() {

		File cipherFile = new File(OssBotConstants.COMMAND_FILES_DIRECTORY + super.getCommandName() + OssBotConstants.SEPARATOR + OssBotConstants.CIPHER_FILE);

		ArrayList<String[]> data = getData(cipherFile);

		String cipherToLookFor = super.getUserCommandParams()[1].toLowerCase().replaceAll("_", " ");

		for(String[] dataPiece : data)
		{
			if(dataPiece.length >= 4)
			{
				String cipher = dataPiece[0].toLowerCase();
				if(cipher.equalsIgnoreCase(cipherToLookFor))
				{
					Messenger.messageFormatter(dataPiece[1] + " | answer: " + dataPiece[2]);
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
}
