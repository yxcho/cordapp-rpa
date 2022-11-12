<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Background
This is the repo for the final project for ISFS603 Corporate Banking & Blockchain which demonstrates a simple smart contract functionality of creating a Receivables Purchase Agreement.
This program is extended from the template on https://github.com/corda/cordapp-template-java


Note on terms used:  
PartyA in the code refers to the liquidity provider in our report/ presentation  
PartyB refers to the core enterprise in our report/ presentation  


# Pre-Requisites
See https://docs.corda.net/getting-set-up.html.

# Usage

## Description of code functionalities  
build.gradle - specifies node configurations  
build/nodes/runnodes - code to start nodes  
clients/build.gradle - start Spring Boot server for each party  
contracts/src/main/java/com/template/schema/RPASchema.java - Database schema for our RPA  
contracts/src/main/java/com/template/schema/RPASchema1.java - Database schema for our RPA  
clients/src/main/java/com/template/webserver/Controller.java - HTTP server endpoints  
workflows/src/main/java/com/template/flows/RPAIssueFlow.java - A sequence of steps that tells a node how to achieve a specific ledger update, such as issuing an RPA  
contracts/src/main/java/com/template/states/RPAState.java - A state is an immutable object representing a fact known by one or more nodes at a specific point in time. You can use states to represent any type of data, and any kind of fact. For example, RPA amount, discount rate etc. State contains a reference to the contract.  
contracts/src/main/java/com/template/contracts/RPAContract.java - Contracts govern the evolution of states. Contracts is similar to a legal contract in real world where it verifies whether the inputs are acceptable according to rules specified.  


## Running the program  
1. Commands to run  
   2.1 Run the deployNodes Gradle task:  
   Unix/Mac OSX: `./gradlew deployNodes`  
   Windows: `gradlew.bat deployNodes`  
   2.2 Start the nodes and the sample CorDapp:  
   Unix/Mac OSX: `./build/nodes/runnodes`  
   Windows: `.\build\nodes\runnodes.bat`  
   2.3 Start a Spring Boot server for Party A. Run the command:  
   Unix/Mac OSX: `./gradlew runPartyAServer`  
   Windows: `gradlew.bat runPartyAServer`  
   Repeat step 4.3 for Party B and then Party C in a new terminal  
   Look for the Started Server in X seconds message — don’t rely on the % indicator.  


2. Communication  
   Use Postman or any other app to interact with CorDapp via HTTP endpoints (import Cordapp.postman_collection.json into postman)  
  

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

When started via the command line, each node will display an interactive shell:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.
    
    Tue Nov 06 11:58:13 GMT 2018>>>

You can use this shell to interact with your node. For example, enter `run networkMapSnapshot` to see a list of 
the other nodes on the network:

    Tue Nov 06 11:58:13 GMT 2018>>> run networkMapSnapshot
    [
      {
      "addresses" : [ "localhost:10002" ],
      "legalIdentitiesAndCerts" : [ "O=Notary, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505484825
    },
      {
      "addresses" : [ "localhost:10005" ],
      "legalIdentitiesAndCerts" : [ "O=PartyA, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505382560
    },
      {
      "addresses" : [ "localhost:10008" ],
      "legalIdentitiesAndCerts" : [ "O=PartyB, L=New York, C=US" ],
      "platformVersion" : 3,
      "serial" : 1541505384742
    }
    ]
    
    Tue Nov 06 12:30:11 GMT 2018>>> 

You can find out more about the node shell [here](https://docs.corda.net/shell.html).

### Client

`clients/src/main/java/com/template/Client.java` defines a simple command-line client that connects to a node via RPC 
and prints a list of the other nodes on the network.

#### Running the client

##### Via the command line

Run the `runTemplateClient` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`.

##### Via IntelliJ

Run the `Run Template Client` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`.

### Webserver

`clients/src/main/java/com/template/webserver/` defines a simple Spring webserver that connects to a node via RPC and 
allows you to interact with the node over HTTP.

The API endpoints are defined here:

     clients/src/main/java/com/template/webserver/Controller.java

And a static webpage is defined here:

     clients/src/main/resources/static/

#### Running the webserver

##### Via the command line

Run the `runTemplateServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

##### Via IntelliJ

Run the `Run Template Server` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The static webpage is served on:

    http://localhost:10050

While the sole template endpoint is served on:

    http://localhost:10050/templateendpoint
