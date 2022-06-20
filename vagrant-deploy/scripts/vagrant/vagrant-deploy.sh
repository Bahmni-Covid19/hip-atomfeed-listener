#!/bin/bash -x -e

PATH_OF_CURRENT_SCRIPT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $PATH_OF_CURRENT_SCRIPT/vagrant_functions.sh
#All config is here
WEBAPP_FOLDER=/home/bahmni/apache-tomcat-8.0.12/webapps
MODULE_DEPLOYMENT_FOLDER=/tmp/
CWD=$1
VERSION=$2
PROJECT_BASE=$PATH_OF_CURRENT_SCRIPT/../../..
SCRIPTS_DIR=$CWD/scripts/vagrant


# Deploy pacs
scp_to_vagrant $PROJECT_BASE/hip-atomfeed/target/hip-atomfeed-listener.war $MODULE_DEPLOYMENT_FOLDER/hip-atomfeed-listener.war

run_in_vagrant -f "$SCRIPTS_DIR/deploy_war.sh"