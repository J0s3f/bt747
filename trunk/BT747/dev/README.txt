This folder contains some script and files that are or were used during development.

INFORMATION

Further down you can find a short description of file in this directory.
This is partially an attic with stuff that were tried but not fully successful.
Other stuff will help you set up Eclipse.

SETTING UP BT747 FOR DEVELOPMENT

Some background:
BT747 runs on three Java platforms: SuperWaba 5.85, J2SE, J2ME.
SuperWaba was the first platform BT747 was built for.  The goal was to develop
the application to control an iBlue 747 on a Palm LifeDrive and download logs
on the road.  The choice fell on SuperWaba because the serial communication over
bluetooth was quickly set up on the Palm device, as well as the serial communication
on the PC.  Further the application could run in the J2SE VM on the PC because
SuperWaba implements a bridge of the SuperWaba VM to the J2SE VM which means that
debugging was easy.  Today this still proves to be a good choice because J2ME
debugging does not work that perfectly on my computer.

Making the code public and GPL was an invitation to other potential developers.
There were some small code contributions here and there but not that much.
Major contributions came on the documentation side, especially in the German
community.  Further, several people contributed for the translations.  And
last, but not least, people tested the application.

In the mean time, I got the application to run on most available platforms, even
I do not have most of them.  Palm and Windows were my initial targets.  PocketPC
platforms came for free as they were supported by SuperWaba (though some adjustments
were needed) and at some point, I installed the emulator for that.
Linux was added following some adjustments in the serial code, Mac platforms needed
some modifications there too - but most specifically needed to know how to set up
RXTX.

There were quite some 'complaints' regarding the interface.  One can do fancy things
in SuperWaba, but that does require some work and I am not a Graphical Art specialist.
I finally took up the challenge to create a 'nicer' Gui.  Netbeans had the functionality
to build a Gui interactively built in.  Somewhere along the way somebody pointed
into the direction of MVC.  While most of the code was written with that principle in
mind, I had not heard of MVC.  Somewhere along the way, I refactored the code so that
I had a Model, some Views and a Controller.  I even had the J2SE interface and the
SuperWaba interface running at the same time: two different views using the same
controller and model.  Another person started proposing a J2SE interface too, but he
finally abandoned.

Somewhere over time I had tried setting up the Nullsoft Installer which proved more
work then expected.  I succeeded in compiling BT747 in native code for windows using
"Java Native Compiler" which uses 'gcj' (http://jnc.mtsystems.ch).  The cost is $40.
Maybe I'll try it again and buy a license with the donations that I received.

When I got a phone capable of doing J2ME, I had some extra motivation to port the
application there when a user mentioned it again on a forum.  The difficult piece
of code to get running, is the bluetooth communication.  I had tried in the past,
looked around for sample code, but setting it up needed extra investment.
Eventually I found the appropriate sample and succeeded in getting the link working
on J2ME.  I also chose J4ME to build the GUI since it has a smaller learning
curve than the other systems around (as far as I perceived on a first look).
One could expect that J2SE system functions and J2ME system functions are the same,
but they are not!  First, J2ME is lacking a lot of stuff because it is for the mobile
platform.  Second, even if the name of the function is the same, and, the parameters
are the same, the behavior not the same (just 'similar').  Especially regarding dates
one must be very careful.  Once the bluetooth connection was working and I had the
'framework' for the GUI, reusing the core functionality was quite easy.

Basically, all three implementations use the same core code.  What differs is the
GUI code (the View) and the VM (+libraries).  Regarding the VM, a Bridge translates
the BT747 system interface to the actual system interface.  If you want to know
what a Bridge is, you'll need to learn about Design Patterns.  A good start is at
'www.sourcemaking.com', but I eventually got the book that started it all
("Design patterns: Elements of reusable object-oriented software" by 4 authors called
 the Gang of Four (GoF)).
 To my astonishment, nobody told me about design patterns and I discovered it when
 I was curious about the fact that some programs has a lot of classes called Factory
 and other repeated names.  A search on some of these keywords confirmed what I
 suspected: this was 'known theory'.  By being persistent, I found some good
 resources and I started applying it to BT747.  At first that implied renaming some
 of the existing classes to reflect the Patterns that they implemented, then it
 implied benefiting from the extra abstraction level to implement the more complex
 Patterns without being having complexes about it.
 
 In the mean time, I also added a map to the J2SE interface.  This code is taken from
 Swing-WS but was modified for performance (cache) and functionality.  It is used
 as a library.
 For the command line interface of BT747 (BT747cmd, a 4th View actually), jopt was
 used.  Jopt is integrated in the project using Maven.  Maven is something else
 that I should study in more detail, but that may be done as opportunity arises.
 The drag-and-drop functionality was added using dnd.iharder.net.  I changed the
 code slightly - not really for bug fixing, but mostly for compiler messages.
 
 The build is done using an ant script that can be run outside any gui if you really
 want to.  I run it from within Eclipse and from within Netbeans.  The J2ME version
 is not in that Ant script.  I use Eclipse for J2ME (J2ME->Create obfuscated package).
 The J2ME code has its own location in the SVN database, but for the core code it
 points to the BT747 location (a symbolic link in Eclipse is used).
 The J2ME had some trouble running on a BlackBerry device where a mysterious
 Java Verification Code error was reported.  I had to install the SDK for BlackBerry
 to find out what that meant.  Even with the SDK that was pretty mysterious, but
 I got to make more relevant educated guesses as to where the problem was.  The problem
 eventually was a too advanced factory using getInstance functionality of the Class
 object.  I changed my identification token from the Class to a string and it worked.
 I now build the 'cod' file for BlackBerry automatically before uploading the
 application using 'build_cod.bat'.
 
 The core part of the scripts to package and upload the files are in the SVN
 database.  They do rely on local scripts and configuration though - I do not want
 to share passwords for instance.
 In BT747, local properties should be set in the 'local.properties' file which
 has higher priority than the default settings.  To know what you can set, the
 default.properties files can give some hints.
 My file looks something like this (the password is different of course):
 
======== local.properties sample start ============= 
user.email = mymail@myserver.dom
user.name = First Surname
do.upload = false
do.package = true
keytool.keystore=../myKeystore
keytool.passwd = myPass
keytool.alias = myself
keytool.sign = true
#build.current.version=trial
#build.current.revision=trial
svnversion=c:\\cygwin\\bin\\svnversion
svn=c:\\cygwin\\bin\\svn

#superwaba_root=c:\\noSuperWaba
#skip.proguard=true
#proguard.java.rt.lib= ${java.home}/lib/rt.jar
#obfuscate=false

#skip.proguard=true
proguard.activateoptimization=true
proguard.obfuscate=${proguard.activateoptimization}
proguard.accessmodification=${proguard.activateoptimization}
proguard.shrink=${proguard.activateoptimization}
proguard.optimize=${proguard.activateoptimization}
proguard.overload=${proguard.activateoptimization}
proguard.flatten=${proguard.activateoptimization}
proguard.repackage=${proguard.activateoptimization}
proguard.optimizationpasses=4 
======== local.properties sample end =============

You may need to change some more settings.


A good start in the code is probably a read of BT747cmd.java.
This single file constitutes the command line view.  You'll be able
to see that the Controller is used to initiate the work and that the
Model is used to get the data.  There are several cases where the
Controller delegates directly to the Model.  I prefered to follow the MVC
paradigm rather than calling the Model to set values.  It allows future
flexibility for extended controller operations (like checks) when
values are changed.

At the time of writing I intensively started 'refactoring' the application.
Classes had gotten too big and extra ease of flexibility was welcome.
This may or may not make the code easier to understand.  My recommendation
is to learn about Design Patterns if 'Factory', 'State', 'Bridge' or
'Singleton' does not sound familiar to you when their meaning is to be
taken in a programming context.

For me, this project is for the fun of tackling challenges while pushing
the limits of my sphere of know-how further from its center.  It also
proved to be useful at work where mysteriously enough recent self-learning
usually becomes useful some months later.    

Have fun with BT747 too!

FILELIST

bt747.jnc
  File for use with Java Native Compiler.
  Java Native Compiler can build an executable and binaries for windows without
  needing a java distribution in the end.
  
change
  Script used to change the order of some keywords in the source files.
  
README.txt
  This file
  

debug.bat
debugn.bat
  Used the 'Omniscient Debugger' to help debug the application.
  This was used a few times in the beginning.

gcj_comp.bat
  Script trying to compile the application using gcj
  
compile_with_swt.sh
compile_swt.sh
  Scripts 'trying' to compile with the swing awt to build a native
  executable.
  
prof.bat
  Script to profile the application.
  
run.bat
  Script to run the application with debug.
  
build_small.xml
  Reference file extracted from SuperwabaSDK - intention to use this to make
  the standard build file more generic.
  
package.bat
  File used to build actual packaging script in root directory.
  
*.launch
  Files that you can import in eclipse as launch configurations.
  
BT747codestyle.xml
  Codestyle for BT747 - you can import that in eclipse.