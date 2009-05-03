dos2unix -U Bundle.properties
dos2unix -U Bundle_$1.properties
perl -p -e 's/^(.*?)=.*/$1=/g' Bundle.properties | sort -u > ${TEMP}/en.lst
perl -p -e 's/^(.*?)\s*=.*/$1=/g' Bundle_$1.properties | sort -u > ${TEMP}/$1.lst
diff ${TEMP}/en.lst ${TEMP}/$1.lst | perl -n -e 'if(s/^<\s+(.*)/$1/) { print; }' > ${TEMP}/$1_missing.lst
grep -F -f ${TEMP}/$1_missing.lst Bundle.properties > $1_missing.lst
cat $1_missing.lst
