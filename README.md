[![Coverage Status](https://coveralls.io/repos/github/jplu/stanfordNLPRESTAPI/badge.svg?branch=master)](https://coveralls.io/github/jplu/stanfordNLPRESTAPI?branch=master)

# stanfordNLPWebAPI
Stanford NLP framework wrapped with a REST API.

# Usage

The output is in **text/turtle** or **application/json** format on both CLI and Web Service modes

## CLI :
java -jar StanfordNLP2NIF-X.Y.jar [pos|ner] -t "text to analyse" [-f format [turtle|jsonld]] conf/config.yaml

## Web Service :
jar -jar StanfordNLP2NIF-X.Y.jar server conf/config.yaml

# Configuration :

File in *conf/config.yaml*

```yaml
pos:
  model: "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger"
ner:
  model: "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz"
  useSuTime: false
  applyNumericClassifiers: false

logging:
  level: INFO

server:
  applicationConnectors:
    - type: http
      port: 7000
  adminConnectors:
    - type: http
      port: 7001
```