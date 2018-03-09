# PoR

./por clean -- clean dirs and builds
./por build -- build all
./por test  -- clean, build and run tests
./por <cmd> -- run actual PoR

# Maven

* download depending libraries
  mvn dependency:copy-dependencies
  
* execute with java
  mvn exec:java

* build jar
  mvn clean dependency:copy-dependencies compile assembly:single

* run server (path is ~/.por/objects)
  java -jar target/por-1.0-SNAPSHOT-jar-with-dependencies.jar server

* run client
  java -jar target/por-1.0-SNAPSHOT-jar-with-dependencies.jar create <file-path>