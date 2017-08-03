package scripts.ossbot.commands;

import java.text.NumberFormat;
import java.util.Locale;

import scripts.ossbot.OSSBotV2;
import scripts.ossbot.commandInterface.Command;
import scripts.ossbot.methods.BotFiles;
import scripts.ossbot.methods.Messenger;
import scripts.ossbot.methods.OssBotMethods;
import scripts.ossbot.methods.Ranking;

public class Calc extends Command{
	private final int DEFAULT_RANK_REQUIREMENT = 0;
	private final String COMMAND_NAME = this.getClass().getSimpleName();
	private final String[][] STATIC_COMMAND_PARAMS = {{"insertCalculation"}};
	private int level = 0;
	
	public Calc()
	{
		BotFiles.checkProperties(COMMAND_NAME, DEFAULT_RANK_REQUIREMENT, STATIC_COMMAND_PARAMS);
	}
	public void execute() {
		String fullCommand = OSSBotV2.getIssuerCommand();
		String[] commandParams = OssBotMethods.getcommandParams(fullCommand);

		level = OssBotMethods.findMaximumCommandLevel(commandParams, fullCommand);
		
		for(int i = 0; i < commandParams.length; i++)
		{
			commandParams[i] = commandParams[i].replaceAll("_", " ");
		}
		if(level > 0)
		{
			Messenger.messageFormatter(NumberFormat.getNumberInstance(Locale.US).format(eval(String.join(" ", commandParams))));
		}
		else
		{
			Messenger.messageFormatter("This command requires parameters.");
		}
	}
	public double eval(final String str) {
		try{
			return new Object() {
				int pos = -1, ch;

				void nextChar() {
					ch = (++pos < str.length()) ? str.charAt(pos) : -1;
				}

				boolean eat(int charToEat) {
					while (ch == ' ') nextChar();
					if (ch == charToEat) {
						nextChar();
						return true;
					}
					return false;
				}

				double parse() {
					nextChar();
					double x = parseExpression();
					return x;
				}

				// Grammar:
				// expression = term | expression `+` term | expression `-` term
				// term = factor | term `*` factor | term `/` factor
				// factor = `+` factor | `-` factor | `(` expression `)`
				//        | number | functionName factor | factor `^` factor

				double parseExpression() {
					double x = parseTerm();
					for (;;) {
						if      (eat('+')) x += parseTerm(); // addition
						else if (eat('-')) x -= parseTerm(); // subtraction
						else return x;
					}
				}

				double parseTerm() {
					double x = parseFactor();
					for (;;) {
						if      (eat('*')) x *= parseFactor(); // multiplication
						else if (eat('/')) x /= parseFactor(); // division
						else return x;
					}
				}

				double parseFactor() {
					if (eat('+')) return parseFactor(); // unary plus
					if (eat('-')) return -parseFactor(); // unary minus

					double x;
					int startPos = this.pos;
					if (eat('(')) { // parentheses
						x = parseExpression();
						eat(')');
					} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
						while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
						x = Double.parseDouble(str.substring(startPos, this.pos));
					} else if (ch >= 'a' && ch <= 'z') { // functions
						while (ch >= 'a' && ch <= 'z') nextChar();
						String func = str.substring(startPos, this.pos);
						x = parseFactor();
						if (func.equals("sqrt")) x = Math.sqrt(x);
						else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
						else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
						else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					} else {
						x=0;
					}

					if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

					return x;
				}
			}.parse();
		}
		catch(Exception t)
		{
			
		}
		return 0;
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
