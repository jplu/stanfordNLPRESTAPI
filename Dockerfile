FROM 1science/java:oracle-jdk-8

ADD target/stanfordNLPRESTAPI-1.0-SNAPSHOT.jar /data/stanfordNLPRESTAPI-1.0-SNAPSHOT.jar

ADD conf/config.yaml /data/config.yaml

CMD java -jar /data/stanfordNLPRESTAPI-1.0-SNAPSHOT.jar server /data/config.yaml

EXPOSE 7000 7001