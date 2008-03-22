This is the README file for the SuperWaba Collections Library.
by Silvio Moioli, 2006

What is the SuperWaba Collections Library?
------------------------------------------
SWCollections is a port of the popular java.util classes to the SuperWaba platform.SuperWaba doesn't have Collections support by itself, and also misses many of the basic
classes in the java.lang package.
If your application needs the Collections framework, chances are your porting to
SuperWaba will be much easier with SuperWaba Collections

What's in this package?
-----------------------
Relevant files:
-dist/collections-superwaba.jar: you need this to compile and test your applications
on the desktop;
-dist/collections-superwaba.pdb: SuperWaba version of the library, to be used at runtime;
-lib/superwaba: SuperWaba jars needed to rebuild the library;
-src: full sources;
-build.xml: Apache Ant build file.

How to use
----------
In most cases, simply use the moio.util prefix instead of java.util and java.lang.
Almost all classes and interfaces in the Collections framework have identical names and
interfaces in SuperWaba Collections. If you're unsure wether a class is included or
not, take a look at the sources.

Dependencies
------------
To use this package you need:
-the SuperWaba Virtual Machine, available at: http://www.superwaba.com.br (free
registration required);
-a Java SDK, version 1.4 or newer, available for free at: http://java.sun.com;

To build this package you need:
-a Java SDK, version 1.4 or newer, available for free at: http://java.sun.com;
-Apache Ant, available for free at: http://ant.apache.org/.

License
-------
This library is based on GNU Classpath (http://www.gnu.org/software/classpath/)
and made available under the terms of the GPL license: contact me if
you need to use it in a closed-source program.
You can find the full license in the LICENSE.txt file provided in this package.

Contact
-------
If you need help, have questions, suggestions, etc. please contact me:

Silvio Moioli
Web: http://www.moioli.net
E-mail: silvio@moioli.net
