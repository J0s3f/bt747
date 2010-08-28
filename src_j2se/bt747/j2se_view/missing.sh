#!/bin/sh
SORT=/usr/bin/sort
# Bundle.properties
dos2unix -U Bundle.properties
dos2unix -U Bundle_$1.properties
perl -p -e 's/^(.*?)=.*/$1=/g' Bundle.properties | ${SORT} -u > ${TEMP}/en.lst
perl -p -e 's/^(.*?)\s*=.*/$1=/g' Bundle_$1.properties | ${SORT} -u > ${TEMP}/$1.lst
diff ${TEMP}/en.lst ${TEMP}/$1.lst | perl -n -e 'if(s/^<\s+(.*)/$1/) { print; }' > ${TEMP}/$1_missing.lst
grep -F -f ${TEMP}/$1_missing.lst Bundle.properties > $1_missing.lst

# BT747base
dos2unix -U ../../net/sf/bt747/j2se/app/resources/BT747base.properties
dos2unix -U ../../net/sf/bt747/j2se/app/resources/BT747base_$1.properties
perl -p -e 's/^(.*?)=.*/$1=/g' ../../net/sf/bt747/j2se/app/resources/BT747base.properties | ${SORT} -u > ${TEMP}/en.lst
perl -p -e 's/^(.*?)\s*=.*/$1=/g' ../../net/sf/bt747/j2se/app/resources/BT747base_$1.properties | ${SORT} -u > ${TEMP}/$1.lst
diff ${TEMP}/en.lst ${TEMP}/$1.lst | perl -n -e 'if(s/^<\s+(.*)/$1/) { print; }' > ${TEMP}/$1_missing.lst
echo '# BT747base strings:' >> $1_missing.lst
grep -F -f ${TEMP}/$1_missing.lst ../../net/sf/bt747/j2se/app/resources/BT747base.properties >> $1_missing.lst


cat $1_missing.lst
