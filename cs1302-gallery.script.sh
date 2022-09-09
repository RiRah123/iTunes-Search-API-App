#! /bin/bash -ex
mvn clean
mvn compile
mvn -e -Dprism.order=sw exec:java -Dexec.mainClass="cs1302.gallery.GalleryDriver"
