Sample code for using Jena, topbraid spin and Virtuoso. Most dependencies are installed by Maven automatically. 
Because the virtuoso drivers aren't available as maven artifacts, you need to download and manually install--

* **Command line:**
* 1. Download or checkout Virtuoso Jena 2.10.x provider Jar and Virtuoso JDBC 4 Driver JAr
* 2. 
*   Download:
` http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VOSDownload#Other%20Projects`
*   Run:
`mvn install:install-file -Dfile=/path/to/virt_jena2.jar -DgroupId=virtuoso.jena.driver -DartifactId=virtjena -Dversion=2 -Dpackaging=jar`
    * Run:  
`mvn install:install-file -Dfile=/path/to/virtjdbc4.jar -DgroupId=virtuoso -DartifactId=vjdbc41 -Dversion=4.1 -Dpackaging=jar`  
   3. Go to /path/to/targetDir/fagi-gis and build the project by running:  
`mvn package`

Virtuoso is installed from Docker.
Docker command to create base image
docker build -f Dockerfile.vos_base -t openlink/vos_base:v0 .
Create openlink docker image from base image.
docker build -f Dockerfile.vos -t openlink/vos:v0 .

Assuming that virtuoso.ini configuration is in the DB directory

sudo docker run -v /Users/cvardema/IdeaProjects/jenatest/db:/opt/virtuoso-opensource/var/lib/virtuoso/db -t -p 1111:1111 -p 8890:8890 -i openlink/vos:v0

See Virtuoso wiki page:
http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtuosoDocker
