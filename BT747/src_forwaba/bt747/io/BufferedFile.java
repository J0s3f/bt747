/*********************************************************************************
 *  SuperWaba Virtual Machine, version 5                                         *
 *  Copyright (C) 2005 Volker Nigge (proPlant.de)                                *
 *  Copyright (C) 2005-2008 SuperWaba Ltda.                                      *
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  Please see the software license located at /license.txt for more details.    *
 *                                                                               *
 *  You should have received a copy of the License along with this software;     *
 *  if not, write to                                                             *
 *                                                                               *
 *     SuperWaba Ltda.                                                           *
 *     Rua Padre Leonel Franca, 480, sala 27A                                    *
 *     Instituto Genesis - PUC-RIO                                               *
 *     Rio de Janeiro / RJ - Brazil                                              *
 *     Cep: 22451-000                                                            *
 *     E-mail: licensing@superwaba.com.br                                        *
 *********************************************************************************/

package bt747.io;

import waba.io.File;

/**
 * BufferedFile offers a faster way to read data from big files.
 * Here's a test:
 * <pre>
 *   int bytesRead=0;
 *   int fileSize;
 *   File file;
 *   DataStream ds;
 *   BufferedFile file = new BufferedFile(path, mode, buffSize);
 *   fileSize = file.getSize();
 *   bytesRead = 0;
 *   ds = new DataStream(file);
 *   while (bytesRead+4 &lt; fileSize)
 *   {
 *      try
 *      {
 *         ds.readInt();
 *         bytesRead+=4;
 *      }
 *      catch (Exception e)
 *      {
 *         e.printStackTrace();
 *         break;
 *      }
 *   }
 *   file.close();
 * </pre>
 * @since SuperWaba 5.51
 */

public class BufferedFile extends File
{
   private int offPointer;
   private int buffSize;
   private byte[] buff;
   public boolean eof;
   private int bytesRead;

   /**
    * Opens a file with the given name and mode. If mode is CREATE, the file will be
    * created if it does not exist. The DONT_OPEN mode allows the exists(), rename(),
    * delete(), listDir(), createDir() and isDir() methods to be called without requiring
    * the file to be open for reading or writing. Note that the filename must not contain
    * accentuated characters. Also, the slash / MUST be the path separator. The size of
    * the internal Buffer is set to buffSize
    *
    * @param path
    *           the file's path
    * @param mode
    *           one of DONT_OPEN, READ_ONLY, WRITE_ONLY, READ_WRITE or CREATE
    * @param buffSize
    *           size of the internal buffer
    */

   public BufferedFile(String path, int mode, int slot)
   {
      super(path, mode, slot);
      this.buffSize = 1024;
      buff = new byte[buffSize];
      if ((mode == READ_ONLY) || ((mode == READ_WRITE))) readNextBytes();
   }

   /**
    * Opens a file with the given name and mode. If mode is CREATE, the file will be
    * created if it does not exist. The DONT_OPEN mode allows the exists(), rename(),
    * delete(), listDir(), createDir() and isDir() methods to be called without requiring
    * the file to be open for reading or writing. Note that the filename must not contain
    * accentuated characters. Also, the slash / MUST be the path separator. The size of
    * the internal Buffer is set to 1024
    *
    * @param path
    *           the file's path
    * @param mode
    *           one of DONT_OPEN, READ_ONLY, WRITE_ONLY, READ_WRITE or CREATE
    */

   public BufferedFile(String path, int mode)
   {
      this(path, mode,-1);
   }

   
   public BufferedFile(String path)
   {
      this(path, DONT_OPEN, -1);
   }

   /**
    * Reads bytes from the file into a byte array. Returns the number of bytes actually
    * read or -1 if an error prevented the read operation from occurring. After the read
    * is complete, the location of the file pointer (where read and write operations start
    * from) is advanced the number of bytes read.
    *
    * @overrides readBytes in class File
    * @param bytes
    *           the byte array to read data into
    * @param off
    *           the offset position in the array
    * @param len
    *           the number of bytes to read
    * @return the number of bytes actually read or -1 if eof
    */

   public int readBytes(byte[] bytes, int off, int len)
   {
      if (eof) return -1;
      for (int i = 0; i < len; i++)
      {
         eof = (bytesRead < buffSize) && (offPointer >= bytesRead);
         if (eof) return i;
         if (offPointer == bytesRead) readNextBytes();
         bytes[off + i] = buff[offPointer++];
      }
      return len;
   }

   /**
    * Skips countBytes bytes
    *
    * @param countBytes
    *           count of bytes ti skip
    */

   public void skip(int countBytes)
   {
      int restBuffer = bytesRead - offPointer;

      if (countBytes < restBuffer)
         offPointer += countBytes;
      else
      {
         int rest = countBytes - restBuffer;
         if (rest > buffSize)
            super.readBytes(new byte[rest], 0, rest);
         else
         {
            readNextBytes();
            if (!eof) offPointer += rest;
         }
      }
   }

   /**
    * Reads next bytes (count bytes = buffSize or available bytes) if not eof
    */
   private void readNextBytes()
   {
      bytesRead = 0;
      if (eof) return;
      bytesRead = super.readBytes(buff, 0, buffSize);
      offPointer = 0;
      eof = bytesRead == 0;
   }
}