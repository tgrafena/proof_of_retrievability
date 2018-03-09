#!/bin/bash

function splitter {
    local IFS= LINE
    while read -r LINE; do
        [[ $LINE == '[ERROR]'* ]] && {
             echo "$LINE" >&2
             continue
        }
        echo "$LINE"
    done
}

if [ $# -eq 1 -a "$1" == "build" ]
then
    echo "building ..."
    mvn clean dependency:copy-dependencies compile assembly:single | splitter 1> /dev/null
    mkdir ~/.por
    touch ~/.por/server.seqid
    echo "1000" > ~/.por/server.seqid
    echo "build finished!"
elif [ $# -eq 1 -a "$1" == "test" ]
then
    mvn dependency:copy-dependencies compile test
elif [ $# -eq 1 -a "$1" == "clean" ]
then
    rm -r ~/.por/
    echo "~/.por deleted"
else
    java -jar target/por-1.0-SNAPSHOT-jar-with-dependencies.jar $@
fi
