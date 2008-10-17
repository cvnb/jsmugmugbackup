#!/bin/bash

#cleaning up first
rm -r -f deployment
mkdir deployment
mkdir deployment/jSmugmugBackup

#copy libs
cp -v lib/httpcomponents-client-4.0-beta1/lib/apache-mime4j-0.4.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/commons-codec-1.3.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/commons-logging-1.1.1.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpclient-4.0-beta1.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpcore-4.0-beta2.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpmime-4.0-beta1.jar deployment/jSmugmugBackup/
cp -v lib/json_simple/lib/json_simple.jar deployment/jSmugmugBackup/

#copy code
cp -v -r bin/jSmugmugBackup deployment/jSmugmugBackup/

#extract libs
cd deployment/jSmugmugBackup/
jar -xvf apache-mime4j-0.4.jar
jar -xvf commons-codec-1.3.jar
jar -xvf commons-logging-1.1.1.jar
jar -xvf httpclient-4.0-beta1.jar
jar -xvf httpcore-4.0-beta2.jar
jar -xvf httpmime-4.0-beta1.jar
jar -xvf json_simple.jar

rm -v *.jar
rm -v -r META-INF/
cd ../..


#copy docs ... maybe not too useful to include them in the jar
cp -v -r docs deployment/jSmugmugBackup/

#remove all subversion files
find deployment -name .svn -exec /bin/rm -r -f '{}' ';'

#create jar
cd deployment/jSmugmugBackup/
jar -cvfe ../jSmugmugBackup.jar jSmugmugBackup.main.Main jSmugmugBackup/ org/ documentation/
cd ../..

#cleanup
rm -r -f deployment/jSmugmugBackup


#create unix script
echo "#!/bin/bash" >> deployment/jSmugmugBackup.sh
#echo "CLASSPATH=\".:./lib/apache-mime4j-0.4.jar:./lib/commons-codec-1.3.jar:./lib/commons-logging-1.1.1.jar:./lib/httpclient-4.0-beta1.jar:./lib/httpcore-4.0-beta2.jar:lib/httpmime-4.0-beta1.jar\"" >> deployment/jSmugmugBackup.sh
#echo "java -classpath \$CLASSPATH -jar jSmugmugBackup.jar \$1 \$2 \$3 \$4 \$5 \$6 \$7 \$8 \$9" >> deployment/jSmugmugBackup.sh
echo "java -Xms512m -Xmx1024m -jar jSmugmugBackup.jar \$1 \$2 \$3 \$4 \$5 \$6 \$7 \$8 \$9" >> deployment/jSmugmugBackup.sh
chmod -v u+x deployment/jSmugmugBackup.sh

#create windows .bat
#echo "@set CLASSPATH=\".:lib\\commons-codec-1.3.jar:lib\\commons-httpclient-3.1-rc1.jar:lib\\commons-io-1.3.2.jar:lib\\commons-logging-1.1.1.jar:lib\\json-1.0.0.jar:lib\\junit-4.4.jar:lib\\logback-classic-0.9.8.jar:lib\\logback-core-0.9.8.jar:lib\\migbase64-2.2.0.jar:lib\\slf4j-api-1.4.3.jar:lib\\db-derby-10.3.1.4-bin\\derby.jar:lib\\db-derby-10.3.1.4-bin\\derbyLocale_de_DE.jar:lib\\db-derby-10.3.1.4-bin\\derbyclient.jar:lib\\db-derby-10.3.1.4-bin\\derbynet.jar:lib\\db-derby-10.3.1.4-bin\\derbyrun.jar:lib\\db-derby-10.3.1.4-bin\\derbytools.jar\"" >> deployment/jSmugmugBackup/jSmugmugBackup.bat
#echo "@java -classpath %CLASSPATH% -jar jSmugmugBackup.jar %1 %2 %3 %4 %5 %6 %7 %8 %9" >> deployment/jSmugmugBackup/jSmugmugBackup.bat
echo "@java -Xms512m -Xmx1024m -jar jSmugmugBackup.jar %1 %2 %3 %4 %5 %6 %7 %8 %9" >> deployment/jSmugmugBackup.bat
