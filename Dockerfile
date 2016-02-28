#
# This file is part of StanfordNLPRESTAPI.
#
# StanfordNLPRESTAPI is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# StanfordNLPRESTAPI is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with StanfordNLPRESTAPI.  If not, see <http://www.gnu.org/licenses/>.
#

FROM 1science/java:oracle-jdk-8

ADD target/stanfordNLPRESTAPI-1.0.0-SNAPSHOT.jar /data/stanfordNLPRESTAPI-1.0.0-SNAPSHOT.jar

ADD conf/config.yaml /data/config.yaml

CMD java -jar /data/stanfordNLPRESTAPI-1.0.0-SNAPSHOT.jar server /data/config.yaml

EXPOSE 7000 7001