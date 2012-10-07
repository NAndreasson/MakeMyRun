#!/bin/bash
# I will run through the directory you're in and add header & license stuff
# to java files that are missing it. Ill exclude files put into dontAdd
DIR="$( cd "$( dirname "$0" )" && pwd )"
dontAdd="PolylineDecoder" # Add more with separator: \|
searchPattern="/*     Copyright (c) 2012 Johannes Wikner, Anton Lindgren, Victor Lindhe,"

find . | grep '.java$' | grep src/ | while read a; do

if echo "`cat $a`"  | grep -q "$searchPattern"
then
	echo "HAS HEADER: $a"
else
	if echo "$a" | grep -q "$dontAdd"
	then
		echo "SKIPPING: $a"
	else
	cat $DIR/apacheheader > tmp
	cat $a >> tmp
	mv tmp $a
	echo "ADDING HEADER TO: $a"
	fi
fi
done
if [ -f tmp ]; then rm tmp; fi
