#!/bin/bash

#cleaning up first
rm -r -f deployment
mkdir deployment
mkdir deployment/jSmugmugBackup

#copy libs
#cp -v lib/commons-codec-1.3.jar deployment/jSmugmugBackup/
#cp -v lib/commons-httpclient-3.1-rc1.jar deployment/jSmugmugBackup/
#cp -v lib/commons-io-1.3.2.jar deployment/jSmugmugBackup/
#cp -v lib/commons-logging-1.1.1.jar deployment/jSmugmugBackup/
#cp -v lib/json-1.0.0.jar deployment/jSmugmugBackup/
#cp -v lib/junit-4.4.jar deployment/jSmugmugBackup/
#cp -v lib/logback-classic-0.9.8.jar deployment/jSmugmugBackup/
#cp -v lib/logback-core-0.9.8.jar deployment/jSmugmugBackup/
#cp -v lib/migbase64-2.2.0.jar deployment/jSmugmugBackup/
#cp -v lib/slf4j-api-1.4.3.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derby.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derbyLocale_de_DE.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derbyclient.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derbynet.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derbyrun.jar deployment/jSmugmugBackup/
#cp -v lib/db-derby-10.3.1.4-bin/derbytools.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/apache-mime4j-0.4.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/commons-codec-1.3.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/commons-logging-1.1.1.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpclient-4.0-beta1.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpcore-4.0-beta2.jar deployment/jSmugmugBackup/
cp -v lib/httpcomponents-client-4.0-beta1/lib/httpmime-4.0-beta1.jar deployment/jSmugmugBackup/



#copy code
#cp -v -r bin/com deployment/jSmugmugBackup/
cp -v -r bin/jSmugmugBackup deployment/jSmugmugBackup/

#extract libs
cd deployment/jSmugmugBackup/
#jar -xvf commons-codec-1.3.jar
#jar -xvf commons-httpclient-3.1-rc1.jar
#jar -xvf commons-io-1.3.2.jar
#jar -xvf commons-logging-1.1.1.jar
#jar -xvf json-1.0.0.jar
#jar -xvf junit-4.4.jar
#jar -xvf logback-classic-0.9.8.jar
#jar -xvf logback-core-0.9.8.jar
#jar -xvf migbase64-2.2.0.jar
#jar -xvf slf4j-api-1.4.3.jar
#jar -xvf derby.jar
#jar -xvf derbyLocale_de_DE.jar
#jar -xvf derbyclient.jar
#jar -xvf derbynet.jar
#jar -xvf derbyrun.jar
#jar -xvf derbytools.jar
jar -xvf apache-mime4j-0.4.jar
jar -xvf commons-codec-1.3.jar
jar -xvf commons-logging-1.1.1.jar
jar -xvf httpclient-4.0-beta1.jar
jar -xvf httpcore-4.0-beta2.jar
jar -xvf httpmime-4.0-beta1.jar

rm -v *.jar
rm -v LICENSE.txt
rm -v REQUIRED.txt
rm -v -r META-INF/
cd ../..

#logback xml-config
#cp -v bin/logback.xml deployment/jSmugmugBackup/

#copy docs ... maybe not too useful to include them in the jar
mkdir deployment/jSmugmugBackup/documentation
cp -v -r docs deployment/jSmugmugBackup/documentation/
#cp -v -r docs_smugmug-java-api-0.5.0 deployment/jSmugmugBackup/documentation/
#cp -v -r docs_smugfig-api-0.1.1 deployment/jSmugmugBackup/documentation/

#remove all subversion files
find deployment -name .svn -exec /bin/rm -r -f '{}' ';'

#create jar
cd deployment/jSmugmugBackup/
jar -cvfe ../jSmugmugBackup.jar jSmugmugBackup.main.Main jSmugmugBackup/ documentation/
cd ../..

#cleanup
rm -r -f deployment/jSmugmugBackup
rm -r -f deployment/.svn


#create unix script
echo "#!/bin/bash" >> deployment/jSmugmugBackup.sh
#echo "CLASSPATH=\".:./lib/apache-mime4j-0.4.jar:./lib/commons-codec-1.3.jar:./lib/commons-logging-1.1.1.jar:./lib/httpclient-4.0-beta1.jar:./lib/httpcore-4.0-beta2.jar:lib/httpmime-4.0-beta1.jar\"" >> deployment/jSmugmugBackup.sh
#echo "java -classpath \$CLASSPATH -jar jSmugmugBackup.jar \$1 \$2 \$3 \$4 \$5 \$6 \$7 \$8 \$9" >> deployment/jSmugmugBackup.sh
echo "java -Xms512m -Xmx1024m -jar jSmugmugBackup.jar \$1 \$2 \$3 \$4 \$5 \$6 \$7 \$8 \$9" >> deployment/jSmugmugBackup.sh
chmod -v u+x deployment/jSmugmugBackup.sh

#create windows .bat
#echo "@set CLASSPATH=\".:lib\\commons-codec-1.3.jar:lib\\commons-httpclient-3.1-rc1.jar:lib\\commons-io-1.3.2.jar:lib\\commons-logging-1.1.1.jar:lib\\json-1.0.0.jar:lib\\junit-4.4.jar:lib\\logback-classic-0.9.8.jar:lib\\logback-core-0.9.8.jar:lib\\migbase64-2.2.0.jar:lib\\slf4j-api-1.4.3.jar:lib\\db-derby-10.3.1.4-bin\\derby.jar:lib\\db-derby-10.3.1.4-bin\\derbyLocale_de_DE.jar:lib\\db-derby-10.3.1.4-bin\\derbyclient.jar:lib\\db-derby-10.3.1.4-bin\\derbynet.jar:lib\\db-derby-10.3.1.4-bin\\derbyrun.jar:lib\\db-derby-10.3.1.4-bin\\derbytools.jar\"" >> deployment/jSmugmugBackup/jSmugmugBackup.bat
#echo "@java -classpath %CLASSPATH% -jar jSmugmugBackup.jar %1 %2 %3 %4 %5 %6 %7 %8 %9" >> deployment/jSmugmugBackup/jSmugmugBackup.bat
#echo "@java -classpath %CLASSPATH% jSmugmugBackup.main.Main %1 %2 %3 %4 %5 %6 %7 %8 %9" >> deployment/jSmugmugBackup/jSmugmugBackup.bat
echo "@java -Xms512m -Xmx1024m -jar jSmugmugBackup.jar %1 %2 %3 %4 %5 %6 %7 %8 %9" >> deployment/jSmugmugBackup.bat
