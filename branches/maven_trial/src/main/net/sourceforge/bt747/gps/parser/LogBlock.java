package net.sourceforge.bt747.gps.parser;

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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * LogBlock objects represent a structural block in the logfile. <br>
 * A LogBlock knows it's internal structure on the next lower layer and provides
 * methods to iterate over it's LogPackets. <br>
 * A LogBlock consists of a header and a "chunk" of data.
 * 
 * @author Thomas Mohme
 */
public class LogBlock {
    public static final int LOG_BLOCK_SIZE = 0x10000;

    private LogBlockHeader hdr;
    private LogBlockChunk chunk;

    LogBlock(DataInputStream aDIS, LogDataParser aParser) throws IOException {
        // System.out.println("creating LogBlock"); //$NON-NLS-1$

        byte[] ba = new byte[LOG_BLOCK_SIZE];
        aDIS.readFully(ba, 0, LOG_BLOCK_SIZE);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ba));
        this.hdr = new LogBlockHeader(dis);

        LogDataParser parser;
        if (aParser != null) {
            parser = aParser;
            parser.setStream(dis);
        } else {
            parser = LogDataParserFactory.getParserForFormat(hdr.getFormat(),
                    dis);
        }
        this.chunk = new LogBlockChunk(dis, parser, hdr.getFormat());

    } // constructor LogBlock(InputStream)

    Iterator<LogPacket> iterator() {
        return this.chunk.iterator();
    } // LogBlockIterator getIterator()
} // class LogBlock
