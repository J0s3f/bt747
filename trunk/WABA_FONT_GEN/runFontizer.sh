#!/bin/sh
pwd
#java -cp ./xerces/xerces.jar:./xerces/xercesSamples.jar:./CUP:./gnugetopt/java-getopt-1.0.9.jar:./superwaba/SuperWaba.jar:./ufolib.jar ufolib.fontizer.Controller
java -cp './xerces/xerces.jar;./xerces/xercesSamples.jar;./CUP;./gnugetopt/java-getopt-1.0.9.jar;./superwaba/SuperWaba.jar;./ufolib.jar;./ufolibengine.jar' ufolib.fontizer.Controller

