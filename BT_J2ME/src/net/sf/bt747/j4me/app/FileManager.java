// Taken from J2ME GPS Track
// Copyright (C) 2007 Dana Peters
// http://www.qcontinuum.org/gpstrack

package net.sf.bt747.j4me.app;

import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;

// optional package wrapper class
// technique described in Chapter 8 of JSR 248

public class FileManager implements FileUsage {
    
    FileConnection mFileConnection;

    public FileManager() {
   
    }

    public Enumeration listRoots() {
        return FileSystemRegistry.listRoots();
    }

    public Enumeration getFiles(String path) throws IOException {
        FileConnection fileConnection = null;
        try {
            fileConnection = (FileConnection)Connector.open(path, Connector.READ);
            return fileConnection.list();
        } finally {
            if (fileConnection != null)
                fileConnection.close();
        }
    }

    public DataOutputStream open(String filename) throws IOException {
        mFileConnection = (FileConnection)Connector.open(filename);
        if (!mFileConnection.exists())
            mFileConnection.create();
        mFileConnection.setWritable(true);
        return mFileConnection.openDataOutputStream();
    }
    
    public void close() throws IOException {
        if (mFileConnection != null)
            mFileConnection.close();
    }
}
