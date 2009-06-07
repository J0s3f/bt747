/**
 * 
 */
package gps.mvc;

/**
 * @author Mario
 * 
 */
public interface DeviceControllerIF {
    /**
     * Perform a command.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public boolean cmd(final int cmd);

    /**
     * Check if a command is supported.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public boolean isSupportedCmd(final int cmd);

    /**
     * Perform a command.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public boolean cmd(final int cmd, final CmdParam param);
}
