package scripts.ossbot.commands;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;

public class Price extends Command{

	public Price()
	{
		super(0, new String[][]{{"insertAmount"},{"insertItemName"}});
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

		if(super.getLevel() > 2)
		{
			Messenger.messageFormatter("Use apostrophe to specify an item name with spaces.");
			return;
		}

		String itemToLookFor = "";
		Integer quantity = 1;
		Integer multiplier = 1;
		if(super.getLevel() == 1)
		{
			itemToLookFor = super.getUserCommandParams()[0].toLowerCase().replaceAll("_", " ");
		}
		else
		{
			try{
				quantity = Integer.valueOf(super.getUserCommandParams()[0]);
			} catch(NumberFormatException e)
			{
				Messenger.messageFormatter("Number formatting exception. Did you use apostrophes?");
				return;
			}
			itemToLookFor = super.getUserCommandParams()[1].toLowerCase().replaceAll("_", " ");
		}
		File[] allItemsInDB = new File(OssBotConstants.ITEM_DATABASE_DIRECTORY).listFiles();
		for(File item : allItemsInDB)
		{
			String itemName = item.getName().replace(".txt", "").replaceAll("'", "");

			if(itemName.equalsIgnoreCase(itemToLookFor))
			{
				try {
					Scanner reader = new Scanner(item);

					String line = reader.nextLine();
					reader.close();

					Matcher matcher = Pattern.compile("\"id\":([\\d]*)").matcher(line);

					if(matcher.find())
					{
						Integer GEPrice = 0;
						Integer OSBPrice = 0;

						Integer itemID = Integer.valueOf(matcher.group(1));

						String GEData = BotFiles.getLinkData(OssBotConstants.GE_LINK + itemID);
						String OSBData = BotFiles.getLinkData(OssBotConstants.OSB_LINK + itemID);

						Matcher matcherGE = Pattern.compile("neutral\",\"price\":(.*?)\\}").matcher(GEData);
						Matcher matcherOSB = Pattern.compile("\"selling\":(\\d*),").matcher(OSBData);

						if(matcherGE.find())
						{
							String extractedValue = matcherGE.group(1).replaceAll("[\",]*", "");

							if(extractedValue.contains("m"))
							{
								extractedValue = extractedValue.replace("m", "");
								multiplier = 1000000;
							}
							else if(extractedValue.contains("k"))
							{
								extractedValue = extractedValue.replace("k", "");
								multiplier = 1000;
							}
							GEPrice = Integer.valueOf(String.valueOf((int) (Double.valueOf(extractedValue) * multiplier)));
						}
						if(matcherOSB.find())
						{
							OSBPrice = Integer.valueOf(matcherOSB.group(1));
						}
						boolean tooLarge = false;
						try{
							GEPrice = Math.multiplyExact(GEPrice, quantity);
							OSBPrice = Math.multiplyExact(OSBPrice, quantity);
						} catch(ArithmeticException e)
						{
							tooLarge = true;
						}

						String toPrintGE = String.valueOf((GEPrice.equals(0)) ? "unknown" : NumberFormat.getNumberInstance(Locale.US).format(GEPrice));
						String toPrintOSB = String.valueOf((OSBPrice.equals(0)) ? "unknown" : NumberFormat.getNumberInstance(Locale.US).format(OSBPrice));

						if(quantity >= 2)
						{
							toPrintOSB = String.valueOf((OSBPrice.equals(0)) ? "unknown" : "(" + NumberFormat.getNumberInstance(Locale.US).format(OSBPrice/quantity) + ") " + NumberFormat.getNumberInstance(Locale.US).format(OSBPrice));
							toPrintGE = String.valueOf((GEPrice.equals(0)) ? "unknown" : "(" + NumberFormat.getNumberInstance(Locale.US).format(GEPrice/quantity) + ") " + NumberFormat.getNumberInstance(Locale.US).format(GEPrice));
						}
						if(tooLarge)
						{
							toPrintOSB = "T—O—O —F—U—C—K—I—N—G —M—U—C—H!";
							toPrintGE = "Too much!";
						}
						Messenger.messageFormatter("[—G—E] " + toPrintGE + " [—O—S—B] " + toPrintOSB);
						return;

					}
				} catch (Exception e) {
					//OssBotMethods.printException(e);
					Messenger.messageFormatter("Error occured getting data, try again?");
					return;
				}
			}
		}
		Messenger.messageFormatter("Couldn't find item: " + itemToLookFor);
	}
}
