package scripts.ossbot.threads;

import org.tribot.api.General;

import scripts.ossbot.constants.OssBotConstants;
import scripts.ossbot.methods.BotFiles;

public class WebDownloader implements Runnable {

	@Override
	public void run() {
		while(true)
		{
			String link = OssBotConstants.CML_COMP_LINK;
			BotFiles.downloadCMLComp(link);
			link = OssBotConstants.PLAYER_DATA_SHEET_LINK;
			BotFiles.downloadDataSheet(link);
			link = OssBotConstants.PLAYER_WINS_LINK;
			BotFiles.downloadCompDataSheet(link);


			BotFiles.transferSheetData();
			BotFiles.transferCompWinsData();

			//BotFiles.botLogger("Finished updating downloaded data.");
			General.sleep(180000);
		}
		
	}

}
