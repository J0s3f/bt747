/**
 * 
 */
package gps.mvc.commands;

/** Visits a command and says if it is acknowledged
 * @author Mario
 *
 */
public interface CmdVisitor {
    public boolean isAcknowledgeOf(GpsLinkExecCommand cmd);
}
