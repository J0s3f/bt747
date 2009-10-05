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
    
    /**
     * Check if the data is available and if it is not, requests it.
     * 
     * @param dataType
     * @return
     */
    //public void setDataNeeded(final int dataType);
    
    public void getLog(final String fileName, final int card);
}
