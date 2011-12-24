#!/bin/sh -vx
LANGS="ca de es fa fr it nl pl pt ro ru zh"
for i in $LANGS ; do
./missing.sh $i
../../../../uploadBT747.bat ${i}_missing.lst missing/${i}_missing.lst
done