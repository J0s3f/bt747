#!/bin/sh
SORT=/usr/bin/sort
DOS2UNIX=dos2unix
DOS2UNIX="recode ibmpc..lat1"

# Bundle.properties
${DOS2UNIX} Bundle.properties
${DOS2UNIX} Bundle_$1.properties
perl -p -e 's/^(.*?)=.*/$1=/g' Bundle.properties | ${SORT} -u > ${TEMP}/en.lst
perl -p -e 's/^(.*?)\s*=.*/$1=/g' Bundle_$1.properties | ${SORT} -u > ${TEMP}/$1.lst
diff ${TEMP}/en.lst ${TEMP}/$1.lst | perl -n -e 'if(s/^<\s+(.*)/$1/) { print; }' > ${TEMP}/$1_missing.lst
grep -F -f ${TEMP}/$1_missing.lst Bundle.properties > $1_missing.lst

# BT747base
${DOS2UNIX} ../../net/sf/bt747/j2se/app/resources/BT747base.properties
${DOS2UNIX} ../../net/sf/bt747/j2se/app/resources/BT747base_$1.properties
perl -p -e 's/^(.*?)=.*/$1=/g' ../../net/sf/bt747/j2se/app/resources/BT747base.properties | ${SORT} -u > ${TEMP}/en.lst
perl -p -e 's/^(.*?)\s*=.*/$1=/g' ../../net/sf/bt747/j2se/app/resources/BT747base_$1.properties | ${SORT} -u > ${TEMP}/$1.lst
diff ${TEMP}/en.lst ${TEMP}/$1.lst | perl -n -e 'if(s/^<\s+(.*)/$1/) { print; }' > ${TEMP}/$1_missing.lst
echo '# BT747base strings:' >> $1_missing.lst
grep -F -f ${TEMP}/$1_missing.lst ../../net/sf/bt747/j2se/app/resources/BT747base.properties >> $1_missing.lst


cat $1_missing.lst
