package gps.parser;

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

/**
 * Dynamically instantiate and return the correct parser for the given
 * LogFormat.
 * 
 * @author Thomas Mohme
 */
public class LogDataParserFactory {

    private LogDataParserFactory() {
        // just to disable instantiation
    }

    public static LogDataParser getParserForFormat(LogFormat format,
            DataInputStream dis) {
        if (format.isLowPrecision()) {
            return new LogDataParserHolux(dis);
        }
        return new LogDataParser(dis);
    } // LogDataParser getParserForFormat(LogFormat)
} // end of class LogDataParserFactory
