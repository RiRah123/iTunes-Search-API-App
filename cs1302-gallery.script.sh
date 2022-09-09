#! /bin/bash -ex
check1302 src/main/java/cs1302/gallery/*.java
mvn clean
mvn compile
mvn -e -Dprism.order=sw exec:java -Dexec.mainClass="cs1302.gallery.GalleryDriver"
