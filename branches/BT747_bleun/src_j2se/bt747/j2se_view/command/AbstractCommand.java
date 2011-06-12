package bt747.j2se_view.command;

/**
 * Actract command for position operations. Could be used for more than
 * positions (to be evaluated later).
 * 
 * @author mdeweerd
 * 
 */
public abstract class AbstractCommand {
	/** The command type. */
	private Commands.commandType cmd;
	/** The arguments for the command. */
	private Object[] args;

	/**
	 * Constructor for instance.
	 * 
	 * @param cmd
	 * @param args
	 */
	protected AbstractCommand(Commands.commandType cmd, Object[] args) {
		this.cmd = cmd;
		this.args = args;
	}

	/**
	 * Get the arguments for the command.
	 * 
	 * @return
	 */
	protected Object[] getArgs() {
		return args;
	}

	/**
	 * Get the command type.
	 * 
	 * @return
	 */
	public Commands.commandType getCmd() {
		return cmd;
	}

	/**
	 * Execute the command.
	 * 
	 * @return 0 on succes.
	 */
	public abstract int exec();
}
