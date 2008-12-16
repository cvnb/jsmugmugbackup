#!/bin/bash

#CLASSPATH=".:bin:lib/httpcomponents-client-4.0-beta1/lib/apache-mime4j-0.4.jar:lib/httpcomponents-client-4.0-beta1/lib/commons-codec-1.3.jar:lib/httpcomponents-client-4.0-beta1/lib/commons-logging-1.1.1.jar:lib/httpcomponents-client-4.0-beta1/lib/httpclient-4.0-beta1.jar:lib/httpcomponents-client-4.0-beta1/lib/httpcore-4.0-beta2.jar:lib/httpcomponents-client-4.0-beta1/lib/httpmime-4.0-beta1.jar:lib/json_simple/lib/json_simple.jar"
#java -classpath $CLASSPATH jSmugmugBackup.main.Main $1 $2 $3 $4 $5 $6 $7 $8 $9

cp -v config.xml dist/
cd dist
java -jar jSmugmugBackup.jar $1 $2 $3 $4 $5 $6 $7 $8 $9