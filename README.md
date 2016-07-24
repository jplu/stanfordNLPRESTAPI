# StanfordNLPRESTAPI

[![Build status](https://travis-ci.org/jplu/stanfordNLPRESTAPI.svg?branch=master)](https://travis-ci.org/jplu/stanfordNLPRESTAPI)
[![Coverage Status](https://coveralls.io/repos/github/jplu/stanfordNLPRESTAPI/badge.svg?branch=master)](https://coveralls.io/github/jplu/stanfordNLPRESTAPI?branch=master)
[![License (GPL version 3)](https://img.shields.io/badge/license-GNU%20GPL%20version%203-blue.svg?style=flat-square)](http://opensource.org/licenses/GPL-3.0)

# Introduction
This repository offer a REST API over [Stanford CoreNLP framework](http://stanfordnlp.github.io/CoreNLP/index.html)
to get results in NIF format. The REST API is created via [Dropwizard](http://www.dropwizard.io/).

# Requirements

Java 1.8 and Maven 3.0.3 minimum. Docker is optional.

# Maven

This section is about how to use maven to compile and execute the tests.

## Compilation

To compile StanfordNLPRESTAPI, use the following Maven command:

```
mvn -U clean package
```

The fat JAR will be available in the *target* directory.

## Tests

To run the unit tests, use the following Maven command:

```
mvn clean test
```

To run the integration tests, use the following Maven command:

```
mvn clean verify -P integration-test
```

To run the integration and the unit tests in same time, use the following Maven command:

```
mvn clean verify -P all-tests
```

# Usage

```
usage: java -jar stanfordNLPRESTAPI-1.1.1-SNAPSHOT.jar
       [-h] [-v] {server,check,pos,ner} ...

positional arguments:
  {server,check,pos,ner}
                         available commands

optional arguments:
  -h, --help             show this help message and exit
  -v, --version          show the application version and exit
```

There is two ways to use this wrapper: via a REST API or via CLI. The output is in RDF Turtle or 
JSON-LD format on both CLI and Web Service modes

## CLI

The first way is via CLI with two possible sub-commands, **ner** and **pos**.

### NER

To use the **ner** CLI:

```
usage: java -jar stanfordNLPRESTAPI-1.1.1-SNAPSHOT.jar
       ner -t TEXT [-f FORMAT] [-h] [file]

NER command on text

positional arguments:
  file                   application configuration file

optional arguments:
  -t TEXT                text to analyse
  -f FORMAT              turtle or jsonld
  -h, --help             show this help message and exit
```

### POS

To use the **pos** CLI:

```
usage: java -jar stanfordNLPRESTAPI-1.1.1-SNAPSHOT.jar
       pos -t TEXT [-f FORMAT] [-h] [file]

POS command on text

positional arguments:
  file                   application configuration file

optional arguments:
  -t TEXT                text to analyse
  -f FORMAT              turtle or jsonld
  -h, --help             show this help message and exit

```

## Web Service

The second way is via a Web service:

```
usage: java -jar stanfordNLPRESTAPI-1.1.1-SNAPSHOT.jar
       server [-h] [file]

Runs the Dropwizard application as an HTTP server

positional arguments:
  file                   application configuration file

optional arguments:
  -h, --help             show this help message and exit
```

The format in the HTTP header are respectively **text/turtle** for RDF Turtle or **application/json** for RDF JSON-LD.

## Docker

It is possible to deploy StanfordNLPRESTAPI as a container via Docker. First, be sure to have
compiled StanfordNLPRESTAPI. Next, for deploying the app as a container you have to build the 
Docker image:

```
mvn docker:build
```

Once the image is built, it is possible to run it:

```
docker run -d -p 7000:7000 -p 7001:7001 jplu/stanford-nlp-rest-api:1.1.1-SNAPSHOT
```

## Configuration

The CLI commands and the Web service use the same configuration file (*conf/config.yaml*):

```yaml
pos:
  model: "models/english-bidirectional-distsim.tagger"
ner:
  model: "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz"
  useSuTime: false
  applyNumericClassifiers: false
parse:
  model: "models/englishRNN.ser.gz"
coref:
  md.type: "rule"
  mode: "statistical"
  doClustering: "true"

logging:
  level: INFO
  appenders:
    - type: console
      threshold: ALL
      timeZone: UTC
      target: stdout
    - type: file
      currentLogFilename: /logs/stanford.log
      threshold: ALL
      archive: true
      archivedLogFilenamePattern: /logs/stanford-%d.log
      archivedFileCount: 5
      timeZone: UTC

server:
  requestLog:
    enabled: true
    appenders:
        - type: console
          threshold: ALL
          timeZone: UTC
          target: stdout
        - type: file
          currentLogFilename: /logs/stanford-queries.log
          threshold: ALL
          archive: true
          archivedLogFilenamePattern: /logs/stanford-queries-%d.log
          archivedFileCount: 5
          timeZone: UTC
  applicationConnectors:
    - type: http
      port: 7000
  adminConnectors:
    - type: http
      port: 7001
```


# How to contribute

In case you want to contribute, please read the [CONTRIBUTING](https://github.com/jplu/stanfordNLPRESTAPI/blob/develop/CONTRIBUTING.md) file.

# Opening an issue

If you find a bug, have trouble following the documentation or have a question about the project you
can create an issue. There’s nothing to it and whatever issue you’re having, you’re likely not the
only one, so others will find your issue helpful, too. To open an issue:

* Please, check before to see if not someone else has already had the same issue.
* Be clear in detailing how to reproduce the bug.
* Include system details.
* In case it is an error, paste the error output.


# Team

**Owner**: Julien Plu ([@jplu](https://github.com/jplu))

**Maintainers and Collaborators**:

* Julien Plu (main contact) ([@jplu](https://github.com/jplu))
* Olivier Varene ([@ovarene](https://github.com/ovarene))
* Giuseppe Rizzo ([@giusepperizzo](https://github.com/giusepperizzo))
* Raphaël Troncy ([@rtroncy](https://github.com/rtroncy))

# License

This project is licensed under the terms of the GPL v3 license.
