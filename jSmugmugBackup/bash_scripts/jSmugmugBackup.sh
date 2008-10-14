#!/bin/bash

CLASSPATH=".:../lib/commons-codec-1.3.jar:../lib/commons-httpclient-3.1-rc1.jar:../lib/commons-io-1.3.2.jar:../lib/commons-logging-1.1.1.jar:../lib/json-1.0.0.jar:../lib/junit-4.4.jar:../lib/logback-classic-0.9.8.jar:../lib/logback-core-0.9.8.jar:../lib/migbase64-2.2.0.jar:../lib/slf4j-api-1.4.3.jar:../lib/db-derby-10.3.1.4-bin/derby.jar:../lib/db-derby-10.3.1.4-bin/derbyLocale_de_DE.jar:../lib/db-derby-10.3.1.4-bin/derbyclient.jar:../lib/db-derby-10.3.1.4-bin/derbynet.jar:../lib/db-derby-10.3.1.4-bin/derbyrun.jar:../lib/db-derby-10.3.1.4-bin/derbytools.jar"

cd bin
java -classpath $CLASSPATH jSmugmugBackup.main.Main $1 $2 $3 $4 $5 $6 $7 $8 $9