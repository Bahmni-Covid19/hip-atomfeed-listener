# Atomfeed listener

This is a forked repo from Bahmni/pacs-integration and was refactored for Hip initiated linking and notifying user on new care-context generation (both of them are ABDM features)


#### Pre-requisite
- Java 1.8

### To build a docker image,

``mvn clean compile jib:dockerBuild``

Note: you can change the image name in pom.xml

### To start the hip-atomfeed and db container,

``docker-compose up -d``

By default, hip-atomfeed uses db container as its database. You can also connect to different database containers by specifying its url and credentials in environment variables

Note: By default, openmrs urls will be taken from hip-atomfeed/src/main/resources/atomfeed.properties unless OPENMRS_URL and OPENMRS_ENCOUNTER_FEED_URL are not specified in environment variables.

####< IMPORTANT >

Atomfeed set the markers to first page if you don't set it. 

So, Set the markers manually after provisioning and before deployment.

Especially openmrs encounter feed as we are reading encounter feed to figure out the orders.

Use the following sql query to set the markers manually according to the events in your machine. 
(change the last_read_entry_id and feed_uri_for_last_read_entry )

insert into markers (feed_uri, last_read_entry_id, feed_uri_for_last_read_entry) 
    values ('http://loalhost:8080/openmrs/ws/atomfeed/encounter/recent', '?', '?');
