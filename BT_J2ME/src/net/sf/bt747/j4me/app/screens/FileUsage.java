// Taken from J2ME GPS Track
// Copyright (C) 2007 Dana Peters
// http://www.qcontinuum.org/gpstrack

package net.sf.bt747.j4me.app.screens;

import java.util.*;
import java.io.*;

// optional package wrapper interface
// technique described in Chapter 8 of JSR 248

public interface FileUsage {

    public Enumeration listRoots();
    public Enumeration getFiles(String path) throws IOException;
    public DataOutputStream open(String filename) throws IOException;
    public void close() throws IOException;

}
