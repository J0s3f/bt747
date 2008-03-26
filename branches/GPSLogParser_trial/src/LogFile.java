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


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * A LogFile represents a whole MTK...-logfile.
 * <br>
 * Logically a LogFile consists of LogPackets (at least this is the "users's" view). Technically this is way more complex.
 * <br>
 * A LogFile knows it's internal structure on the next lower layer and provides methods to 
 * iterate over it's LogPackets.
 * <br>
 * A LogFile consists of 0..n blocks.
 * @author Thomas Mohme
 */
public class LogFile implements Iterable<LogPacket> {
	private final File file;
	private final LogDataParser parser;

	static class LogFileIterator implements Iterator<LogPacket> {
		private File file;
		private LogDataParser parser;
		private DataInputStream dis;
		private long bytesRead = 0;
		private LogBlock lb = null;
		private Iterator<LogPacket> lbi = null;
		private LogPacket nextlogPacket = null;

		
		LogFileIterator(final File aFile, final LogDataParser aParser) {
			this.file = aFile;
			this.parser = aParser;
			
			try {
				this.dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.file)));
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
			
			this.lbi = this.getLogBlockIterator();
			if ((this.lbi != null) && (this.lbi.hasNext())) {
				nextlogPacket = this.lbi.next();
			}
		} // constructor LogFileIterator(File)
		
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			if (this.nextlogPacket != null) return true;
			try {
				this.dis.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
			return false;
		} // boolean hasNext()
		

		/**
		 * @see java.util.Iterator#next()
		 */
		public LogPacket next() {
			LogPacket result = this.nextlogPacket;
			
			// Are there more packets in the current LogBlock?
			if ((this.lbi != null) && (this.lbi.hasNext())) {
				this.nextlogPacket = this.lbi.next();
				return result;
			}
			
			// Are there more LogBlocks?
			if (this.bytesRead < this.file.length()) {
				this.lbi = getLogBlockIterator();
				if ((this.lbi != null) && (this.lbi.hasNext())) {
					this.nextlogPacket = lbi.next();
				} else {
					this.nextlogPacket = null;
				}
			} else {
				// There's nothing more to read 
				this.nextlogPacket = null;
			}
			
			return result;
		} // LogPacket next()

		
		/**
		 * This method is required by the interface Iterator, but not really useful in this context.
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		} // void remove()
		
		
		private Iterator<LogPacket> getLogBlockIterator() {
			Iterator<LogPacket> result = null;
			try {
				if (this.file.length() >= this.bytesRead+LogBlock.LOG_BLOCK_SIZE) {
					this.lb = new LogBlock(dis, this.parser);
					this.bytesRead += LogBlock.LOG_BLOCK_SIZE;
					result = lb.iterator();
				}
			} catch(IOException e) {
				throw new IllegalArgumentException(e);
			}
			return result;
		} // Iterator<LogPacket> getLogBlockIterator()
	} // class LogFileIterator
	
	
	public LogFile(String filename) throws FileNotFoundException  {
		this(filename, null);
	} // constructor LogFile(String, LogDataParser)
	
	
	/**
	 * Construct a LogFile from a filename.
	 * @param filename The name of the file to be interpreted as a LogFile.
	 * @param aParser 
	 * @throws FileNotFoundException Is thrown when the given name doesn't point to a readable file.
	 */
	public LogFile(String filename, LogDataParser aParser) throws FileNotFoundException  {
		this.file = new File(filename);
		this.parser = aParser;
		if (!this.file.isFile() || !this.file.canRead()) {
			throw new FileNotFoundException();
		}
	} // constructor LogFile(String, LogDataParser)


	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<LogPacket> iterator() {
		return new LogFileIterator(this.file, this.parser);
	}
	
	
	public static void main(String[] argv) throws FileNotFoundException  {
		LogFile lf = new LogFile(argv[0], new LogDataParserHolux());
		
		int i=1;
		for (LogPacket packet : lf) {
			System.out.println(""+i+": "+packet);
			i++;
		}
			
	} // void main(String[])
}
