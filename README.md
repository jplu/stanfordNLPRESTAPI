# StanfordNLPRESTAPI

[![Build status](https://travis-ci.org/jplu/stanfordNLPRESTAPI.svg?branch=develop)](https://travis-ci.org/jplu/stanfordNLPRESTAPI)
[![Coverage Status](https://coveralls.io/repos/github/jplu/stanfordNLPRESTAPI/badge.svg?branch=develop)](https://coveralls.io/github/jplu/stanfordNLPRESTAPI?branch=develop)
[![License (GPL version 3)](https://img.shields.io/badge/license-GNU%20GPL%20version%203-blue.svg?style=flat-square)](http://opensource.org/licenses/GPL-3.0)

# Introduction
This repository offer a REST API over [Stanford CoreNLP framework](http://stanfordnlp.github.io/CoreNLP/index.html)
to get results in NIF format. The REST API is created via [Dropwizard](http://www.dropwizard.io/).

# Requirements

Java 1.8 and Maven 3.0.5 minimum. Docker (1.6 or later) is optional.

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
mvn clean verify -P unit-tests
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
usage: java -jar stanfordNLPRESTAPI-1.2.1-SNAPSHOT.jar
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
usage: java -jar stanfordNLPRESTAPI-1.2.1-SNAPSHOT.jar
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
usage: java -jar stanfordNLPRESTAPI-1.2.1-SNAPSHOT.jar
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
usage: java -jar stanfordNLPRESTAPI-1.2.1-SNAPSHOT.jar
       server [-h] [file]

Runs the Dropwizard application as an HTTP server

positional arguments:
  file                   application configuration file

optional arguments:
  -h, --help             show this help message and exit
```

The format in the HTTP header are respectively **text/turtle** for RDF Turtle or
**application/json** for RDF JSON-LD.

## Docker

It is possible to deploy StanfordNLPRESTAPI as a container via Docker. First, be sure to have
compiled StanfordNLPRESTAPI. Next, for deploying the app as a container you have to build the 
Docker image:

```
docker build -t jplu/java github.com/jplu/docker-java
mvn docker:build
```

Once the image is built, it is possible to run it with:

```
docker run -d -p 7000:7000 -p 7001:7001 -v models:/maven/models -v conf:/maven/conf jplu/stanford-nlp-rest-api:1.2.1-SNAPSHOT
```

Or with:

```
mvn docker:start
```

The container needs at most 5 minutes (depending of the power of your machine) to be up because of 
the loading of all the models of Stanford CoreNLP.

## Configuration

The CLI commands and the Web service use the same [configuration file](https://github.com/jplu/stanfordNLPRESTAPI/blob/develop/conf/config.yaml).

## Used Models

This application contains by default all the English models provided by Stanford CoreNLP team. In
case you want to add models you will have to download and put them in the *models* folder. You can
also download the jar files provided by Stanford with models for other languages. To use them you
will have to include them in the CLASSPATH. We provide two models:

* OKE2016 [1]: NER model trained with the OKE2016 challenge training dataset.
* NEEL2016 [2][3][4]: NER model for tweets trained with the NEEL2016 challenge training dataset.
* gate-EN-twitter [5]: POS tagger model for tagging tweets.

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

All the content of this repository is licensed under the terms of the GPL v3 license.

# References

* [1]: Plu J., Rizzo G., Troncy R. (2016) Enhancing Entity Linking by Combining NER Models. In: 13th Extended Semantic Web Conference (ESWC'16), Challenges Track, Heraklion, Greece.
* [2]: Rizzo G., van Erp M., Plu J., Troncy R. (2015), NEEL 2016: Named Entity rEcognition & Linking Challenge Report. In (WWW'16), 6th International Workshop on Making Sense of Microposts (#Microposts'16), Montréal, Québec, Canada.
* [3]: Rizzo G., Cano A.E., Pereira B., Varga A. (2015), Making Sense of Microposts (#Microposts2015) Named Entity rEcognition & Linking Challenge. In (WWW'15), 5th International Workshop on Making Sense of Microposts (#Microposts'15), Florence, Italy.
* [4]: Cano A.E., Rizzo G., Varga A., Rowe M., Stankovic M., Dadzie A.S. (2014), Making Sense of Microposts (#Microposts2014) Named Entity Extraction & Linking Challenge. In (WWW'14),4th International Workshop on Making Sense of Microposts (#Microposts'14), Seoul, Korea.
* [5]: Derczynski L., Ritter A., Clark S., Bontcheva K. (2013), Twitter Part-of-Speech Tagging for All: Overcoming Sparse and Noisy Data. In: Association for Computational Linguistics (ACL'13), Sofia, Bulgaria