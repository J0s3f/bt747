#!/bin/sh -x
PROF=./myJap.profile
echo '<?xml version="1.0"?><UTFPROFILE><TITLECOMMENT VALUE="JapForBT747"/><RANGES>' > $PROF
echo '<RANGE START="9" END="10"/>' >> $PROF
echo '<RANGE START="13" END="13"/>' >> $PROF
echo '<RANGE START="32" END="126"/>' >> $PROF
echo '<RANGE START="160" END="255"/>' >> $PROF
cp pre_jp.lst new_jp.lst
perl -n -e 'while( /\\u(....)/gc ) { print "<RANGE START=\"".hex($1)."\" END=\"".hex($1)."\"/>\n" }' ../BT747/src/bt747/Txt_jp.java >> new_jp.lst
cat pre_jp.lst >> new_jp.lst
sort -u new_jp.lst > pre_jp.lst
cat pre_jp.lst >> $PROF
echo '</RANGES></UTFPROFILE>' >> $PROF

CMD=./myJap.cmd
rm $CMD
echo 'open_font_file sazanami-20040629/sazanami-gothic.ttf' >> $CMD
#echo 'open_font_file sazanami-20040629/sazanami-mincho.ttf' >> $CMD
echo 'open_profile_file '$PROF >> $CMD
echo 'set_uff_family Jap' >> $CMD
echo 'save_uff_file' >> $CMD
echo 'exit' >> $CMD
java -cp './xerces/xerces.jar;./xerces/xercesSamples.jar;./CUP;./gnugetopt/java-getopt-1.0.9.jar;superwaba/SuperWaba.jar;ufolib.jar' ufolib.fontizer.Controller -c < $CMD

