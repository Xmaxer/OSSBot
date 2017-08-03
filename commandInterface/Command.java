package scripts.ossbot.commandInterface;

public abstract class Command {
	public abstract void execute();
	public abstract boolean canExecute();
	public abstract boolean checkCallNames();
}
