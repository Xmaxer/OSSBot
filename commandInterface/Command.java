package scripts.ossbot.commandInterface;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public abstract class Command {
	
	private final String commandName;
	private final int rankRequirement;
	private final String[][] commandParams;
	private String[] userCommandParams;
	private int level;
	
	public Command(int rankReq, String[][] commandParams)
	{
		this.rankRequirement = rankReq;
		this.commandParams = commandParams;
		this.commandName = this.getClass().getSimpleName();
		this.level = 6;
		BotFiles.checkProperties(this.commandName, this.rankRequirement, this.commandParams);
	}
	
	public abstract void execute();
	
	public boolean canExecute()
	{
		if(Ranking.checkPermissions(this.commandName))
		{
			BotFiles.addToUsedCounter(this.commandName);
			return true;
		}
		return false;
	}
	
	public boolean checkCallNames()
	{
		final String[] VALID_COMMAND_NAMES = BotFiles.getValidCommandNames(this.commandName);
		if(OssBotMethods.isThisCommandCalled(VALID_COMMAND_NAMES))
		{
			this.setLevel();
			return true;
		}
		return false;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel() {
		
		String fullCommand = OSSBotV2.getIssuerCommand();
		this.userCommandParams = OssBotMethods.getcommandParams(fullCommand);

		this.level = OssBotMethods.findMaximumCommandLevel(this.userCommandParams, fullCommand);
		
	}
	
	public String getCommandName() {
		return commandName;
	}
	
	public int getRankRequirement() {
		return rankRequirement;
	}
	
	public String[][] getCommandParams() {
		return commandParams;
	}
	
	public String[] getUserCommandParams() {
		return userCommandParams;
	}
}
