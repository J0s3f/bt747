#!/bin/sh -vx
LANGS="ca de es fr it nl pl pt ru zh ro"
for i in $LANGS ; do
./missing.sh $i
../../../../uploadBT747.bat ${i}_missing.lst missing/${i}_missing.lst
done
