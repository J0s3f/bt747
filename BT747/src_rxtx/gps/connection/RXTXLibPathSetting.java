/*******************************************************************************
 * Copyright (c) 2010 Mario De Weerd.
 * 
 * Project BT747
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE
 * IS ASSUMED BY THE USER.
 * See the GNU General Public License Version 3 for details.
 * 
 * Contributors:
 *     Mario De Weerd - main contributor
 ******************************************************************************/
package gps.connection;

import java.io.File;

/**
 * Determine which native library to use and set java.library.path
 * 
 * Some OS information take from <a
 * href="http://lopica.sourceforge.net/os.html">
 * http://lopica.sourceforge.net/os.html</a> .
 * 
 * Unfortunately, while the code below permits loading the appropriate native
 * library, it has to be loaded in the appropriate context and done inside RXTX.
 * 
 * @author mdeweerd
 * 
 */
public class RXTXLibPathSetting {

	private static final int OS_LINUX = 1;
	private static final int OS_WINDOWS = 2;
	private static final int OS_MAC = 3;
	private static final int OS_SOLARIS = 4;

	private static final String fs = File.separator; // System.getProperty("file.separator");
	private static final String WIN_PATH = "win";
	private static final String WIN32SUBPATH = "32";
	private static final String WIN32SUBPATH2 = "m32";
	private static final String WIN64SUBPATH = "64";
	private static final String WINDLLBASENAME = "ser";

	private static final String LINUX_PATH = "lin";
	private static final String LINUX_ARM_SUBPATH = "arm";
	private static final String LINUX_I686_SUBPATH = "i686";
	private static final String LINUX_IA64_SUBPATH = "ia64";
	private static final String LINUX_X86_64_SUBPATH = "x86_64";
	private static final String LINUX_I686_PC_SUBPATH = "i686pc";
	private static final String LINUXSOBASENAME = "ser";

	private static final String SOL_PATH = "sunos";
	private static final String SOL32SUBPATH = "32";
	private static final String SOL64SUBPATH = "64";
	private static final String SOLSOBASENAME = "ser";

	private static final String MAC_PATH = "mac";
	private static final String MAC_10_5_SUBPATH = "10.5";
	private static final String MACLIB = "ser";
	private static final String RXTX_2_1_7_PATH = "r217";
	private static final String RXTX_2_2_PREPATH = "r22p2";

	public static final String getDllPath() {
		final String OsName = System.getProperty("os.name");
		final String OsArch = System.getProperty("os.arch");
		final String OsVersion = System.getProperty("os.version");
		final String JavaVersion = System.getProperty("java.version");
		final String dataModel = System.getProperty("sun.arch.data.model");

		/* Determine os type */
		int osType = 0;
		if (OsName.startsWith("Mac")) {
			osType = OS_MAC;
		} else if (OsName.startsWith("Linux")) {
			osType = OS_LINUX;
		} else if (OsName.startsWith("Windows")) {
			osType = OS_WINDOWS;
		} else if (OsName.startsWith("SunOS")) {
			osType = OS_SOLARIS;
		}

		String path = "";
		switch (osType) {
		case OS_WINDOWS:
			path += WIN_PATH;
			/*
			 * On Windows, processor type is important. Sometimes system too
			 * (uses os version).
			 */
			if (dataModel.equals("64")) {
				// 64 bit version
				path = RXTX_2_2_PREPATH + path + WIN64SUBPATH;
			} else {
				// 32 bit version
				path = RXTX_2_1_7_PATH + path + WIN32SUBPATH2;
			}
			path += WINDLLBASENAME;
			break;
		case OS_MAC: {
			float osVersion = Float.valueOf(OsVersion);
			if (osVersion < 10.45f) {
				path = RXTX_2_1_7_PATH + MAC_PATH + MACLIB;
			} else {
				path = RXTX_2_2_PREPATH + MAC_PATH + MAC_10_5_SUBPATH + MACLIB;
			}
		}
		case OS_LINUX: {
			if (OsArch.equals("amd64") || OsArch.equals("x86_64")) {
				// 64 bit architecture for java.
				path = RXTX_2_2_PREPATH + LINUX_PATH + LINUX_X86_64_SUBPATH;
			} else if (OsArch.equals("ia64")) {
				path = RXTX_2_1_7_PATH + LINUX_PATH + LINUX_IA64_SUBPATH;
			} else if (OsArch.equals("arm")) {
				path = RXTX_2_1_7_PATH + LINUX_PATH + LINUX_ARM_SUBPATH;
			} else {
				path = RXTX_2_1_7_PATH + LINUX_PATH + LINUX_I686_SUBPATH;
			}
			path += LINUXSOBASENAME;
		}
		default:
			break;
		}

		// System.load(filename);
		return path;
	}

	private static boolean loadedLib = false;

	/**
	 * Load the RXTX library based on system identification.
	 * 
	 * @param basePath
	 *            Root path where to look for DLL ('lib' directory). This path
	 *            will be prefixed to the relative DLL or SO path and fed into
	 *            System.load.
	 */
	public static void loadRxTxLibrary() {
		if (!loadedLib) {
			loadedLib = true;
			String libname = getDllPath();
			try {
				System.loadLibrary(libname);
			} catch (Throwable e) {
				System.err
						.println("While trying alternative RXTX load method:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Local main used for interactive testing.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		loadRxTxLibrary();
	}
}
