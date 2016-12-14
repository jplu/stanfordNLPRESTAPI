/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
 * Copyright © 2016 Julien Plu (julien.plu@redaction-developpez.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.eurecom.stanfordnlprestapi.healthchecks;

import com.codahale.metrics.health.HealthCheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class ModelHealthCheck extends HealthCheck {
  static final Logger LOGGER = LoggerFactory.getLogger(ModelHealthCheck.class);
  private final String location;

  public ModelHealthCheck(final String newLocation) {
    this.location = newLocation;
  }

  @Override
  protected final HealthCheck.Result check() throws Exception {
    if (!Files.exists(Paths.get(this.location))) {
      return HealthCheck.Result.unhealthy("Properties file " + this.location + " does not exists");
    }
    
    final Properties props = new Properties();
  
    try (FileInputStream fileInputStream = new FileInputStream(this.location)) {
      props.load(fileInputStream);
    } catch (final IOException ex) {
      ModelHealthCheck.LOGGER.error("Error to load a property file.", ex);
    }
  
    InputStream inputStream;
    boolean exists;
    
    if (props.getProperty("ner.model") != null) {
      inputStream = this.getClass().getClassLoader().getResourceAsStream(props.getProperty(
          "ner.model"));
      exists = Files.exists(Paths.get(props.getProperty("ner.model")));
  
      if (inputStream == null && !exists) {
        return HealthCheck.Result.unhealthy("NER model " + props.getProperty("ner.model") + " does"
            + " not exists");
      }
    }
  
    inputStream = this.getClass().getClassLoader().getResourceAsStream(props.getProperty(
        "pos.model"));
    exists = Files.exists(Paths.get(props.getProperty("pos.model")));
  
    if (inputStream == null && !exists) {
      return HealthCheck.Result.unhealthy("POS model " + props.getProperty("pos.model") + " does"
          + " not exists");
    }
  
    inputStream = this.getClass().getClassLoader().getResourceAsStream(props.getProperty(
        "parse.model"));
    exists = Files.exists(Paths.get(props.getProperty("parse.model")));
  
    if (inputStream == null && !exists) {
      return HealthCheck.Result.unhealthy("Parse model " + props.getProperty("parse.model")
          + " does not exists");
    }
    
    if (inputStream != null) {
      inputStream.close();
    }

    return HealthCheck.Result.healthy();
  }

  @Override
  public final String toString() {
    return "ModelHealthCheck{"
        + "model='" + this.location + '\''
        + '}';
  }
}
