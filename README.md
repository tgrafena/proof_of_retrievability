# Authors

This project was implemented by:

Martin Fillafer <martin.fillafer@edu.aau.at> and 
Thomas Grafenauer <thomas.grafenauer@edu.aau.at>

with the support of:

Stefan Rass <stefan.rass@aau.at>

# Description

This project is a contribution to the paper "Dynamic proofs of retrievability from Chameleon-Hashes" by Stefan Rass:

S. Rass, "Dynamic proofs of retrievability from Chameleon-Hashes," 2013 International Conference on Security and Cryptography (SECRYPT), Reykjavik, Iceland, 2013, pp. 1-9.
keywords: {Cloud computing;Cryptography;Encoding;Error correction codes;Protocols;Servers;Cloud Storage;Data Availability;Proofs of Retrievability;Security},
URL: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=7223178&isnumber=7223120

We implemented the project as a console tool in Java and used Maven to build it.
In order to use the tool, open a console, move to the project directory and type in the PoR-commands described below.
With "./por" you get a usages description printed in the console.
For more details on how the project is structured and used please have a look at the "USAGE_AND_DESCRIPTION.pdf".

# PoR

* ./por clean -- cleans the created por directory and builds the project
* ./por build -- builds the project and creates the por directory
* ./por test  -- cleans, builds and runs the project tests
* ./por <cmd> -- run actual PoR

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
