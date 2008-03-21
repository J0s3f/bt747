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
import java.util.Iterator;

/**
 * LogBlockChunk objects represent a "chunk" of data in the logfile.
 * <br>
 * A LogBlockChunk knows it's internal structure on the next lower layer and provides methods to 
 * iterate over it's LogPackets.
 * <br>
 * A LogBlockChunk consists of 
 * <ul>
 * <li>0..n separator(s)
 * <li>0..n data packet(s)
 * <li>0..n filler-bytes
 * </ul>
 * @author Thomas Mohme
 */
public class LogBlockChunk implements Iterable<LogPacket> {
//	private static int LOG_BLOCK_CHUNK_SIZE = 0xFE00;
	private DataInputStream dis;
	private LogDataParser parser;
	private LogFormat format;
	private int minLogPacketSize;
	
	
	static class ChunkIterator implements Iterator<LogPacket> {
		private DataInputStream dis;
		private LogDataParser parser;
		private LogFormat format;
		private int minLogPacketSize;
		private LogPacket nextLogPacket;
		
		
		ChunkIterator(final DataInputStream aDIS, final LogDataParser aParser, final LogFormat aFormat, int aMinLogPacketSize) {
//			System.out.println("creating chunk iterator"); //$NON-NLS-1$
			
			this.dis = aDIS;
			this.parser = aParser;
			this.format = new LogFormat(aFormat); // create a copy 'cause we may modify it!
			this.minLogPacketSize = aMinLogPacketSize;
			
			this.nextLogPacket = this.nextLogPacketFromStream();
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return (nextLogPacket != null);
		} // boolean hasNext();

		
		/**
		 * @see java.util.Iterator#next()
		 */
		public LogPacket next()  {
			LogPacket result = this.nextLogPacket;
			
			this.nextLogPacket = this.nextLogPacketFromStream();
			return result;
		} // LogPacket next()

		
		/**
		 * This method is required by the interface Iterator, but not really useful in this context.
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		} // void remove()
		
		
		private LogPacket nextLogPacketFromStream() {
			LogPacket result = null;
			
			// determine how much bytes are left . . .
			int available;
			try {
				available = this.dis.available();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			
			if (available < this.minLogPacketSize) {
				// there's less then a full packet readable . . . this must be filler!
				return null;
			}
			

			
			// First, check for the presence of a separator 
			if (available >= LogSeparator.LOG_SEPARATOR_SIZE) {
				try {
					LogSeparator sep;
					sep = new LogSeparator(dis);
					if (sep.isValid()) {
						if (sep.changedLogFormat()) {
							this.format.update(sep.getLogFormat());
							this.minLogPacketSize = this.format.getMinPacketSize(this.parser);
						}
						// instead of rolling up the whole story again, just recurse . . .
						return nextLogPacketFromStream();
					}
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}

			
			// Check if the bytes to come are just filler . . .
			try {
				dis.mark(minLogPacketSize);
				int nrFiller;
				for (nrFiller=0; nrFiller < minLogPacketSize; nrFiller++) {
					byte filler = dis.readByte();
					if (filler != (byte)0xFF) {
						break;
					}
				}
				if (nrFiller == minLogPacketSize) {
					// skip to end-of-chunk
//					System.out.println("filler found!"); //$NON-NLS-1$
					return null;
				}

				// push-back read bytes and continue
				dis.reset();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException();
			}
			try {
				result = new LogPacket(parser, this.format);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException();
			}
			
			return result;
		} // LogPacket nextLogPacketFromStream()
	} // end of class ChunkIterator
	
	

	LogBlockChunk(DataInputStream aDIS, LogDataParser aParser, LogFormat aFormat) {
		this.dis = aDIS;
		this.parser = aParser;
		this.format = aFormat;
		this.minLogPacketSize = aFormat.getMinPacketSize(parser);
	} // constructor LogBlockChunk(bytes[])

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<LogPacket> iterator() {
		return new ChunkIterator(this.dis, this.parser, this.format, this.minLogPacketSize);
	}

} // end of class LogBlockChunk

