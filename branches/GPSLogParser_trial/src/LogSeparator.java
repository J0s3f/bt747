/*
	(c)2008 Thomas Mohme
	tmohme at sourceforge.net

	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


import java.io.DataInputStream;
import java.io.IOException;

/**
 * LogSeparator objects represent a separator in a chunk.
 * <br>
 * The primary duty of this objects is, to "consume" the bytes from the stream.
 * <br>
 * Additionally there are separator types which mark changes in the LogFormat.
 * @author Thomas Mohme
 */
public class LogSeparator {
	public static final int LOG_SEPARATOR_SIZE = 16;
	
	private boolean isValid = false;
	private Byte cmd;
	private Integer argument; 

	private static final byte[] HOLUX = {(byte)'H', (byte)'O', (byte)'L', (byte)'U', (byte)'X'};
	private static final byte[] HEADER = {(byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA};
	private static final byte[] TRAILER = {(byte)0xBB, (byte)0xBB, (byte)0xBB, (byte)0xBB};

	LogSeparator(DataInputStream dis) throws IOException {
		this.checkStandardHeader(dis);
		if (!this.isValid) {
			dis.reset();
			this.checkHoluxHeader(dis);
		}
	} // constructor LogSeparator(byte[])
	
	private void checkStandardHeader(DataInputStream dis) throws IOException {
		dis.mark(LOG_SEPARATOR_SIZE);
		// check header
		for (int i=0; i < HEADER.length; i++) {
			if (dis.readByte() != HEADER[i]) {
				dis.reset();
				return;
			}
		}

		cmd = dis.readByte();
		argument = Integer.reverseBytes(dis.readInt());

		// check trailer
		for (int i=0; i < TRAILER.length; i++) {
			if (dis.readByte() != TRAILER[i]) {
				dis.reset();
				return;
			}
		}
//		System.out.printf("Got Separator: cmd=0x%02x, arg=0x%08x\n", cmd, argument);
		isValid = true;
	}
	
	private void checkHoluxHeader(DataInputStream dis) throws IOException {
		dis.mark(LOG_SEPARATOR_SIZE);
		// check header
		for (int i=0; i < HOLUX.length; i++) {
			if (dis.readByte() != HOLUX[i]) {
				dis.reset();
				return;
			}
		}
		dis.skipBytes(LOG_SEPARATOR_SIZE-HOLUX.length);
//		System.out.println("Got HOLUX Separator");
		isValid = true;
	}
	
	public boolean changedLogFormat() {
		assert(isValid);
		return ((cmd != null) && (cmd == 0x02));
	}
	
	public int getLogFormat() {
		assert(cmd == 0x02);
		return argument;
	}
	
	public boolean isValid() {
		return isValid;
	}
} // end of class LogSeparator
