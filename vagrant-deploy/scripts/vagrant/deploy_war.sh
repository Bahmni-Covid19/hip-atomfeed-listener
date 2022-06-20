#!/usr/bin/env bash
TEMP_LOCATION=/tmp
USER=bahmni
#USER=jss
WEBAPP_LOCATION=/home/$USER/apache-tomcat-8.0.12/webapps

sudo su - $USER -c "cp -f $TEMP_LOCATION/hip-atomfeed-listener.war $WEBAPP_LOCATION/"