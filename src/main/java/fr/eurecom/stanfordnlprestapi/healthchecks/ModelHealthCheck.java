/**
 * This file is part of StanfordNLPRESTAPI.
 *
 * StanfordNLPRESTAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * StanfordNLPRESTAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with StanfordNLPRESTAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.eurecom.stanfordnlprestapi.healthchecks;

import com.codahale.metrics.health.HealthCheck;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class ModelHealthCheck extends HealthCheck {
  static final Logger LOGGER = LoggerFactory.getLogger(ModelHealthCheck.class);
  private final String model;

  public ModelHealthCheck(final String newModel) {
    this.model = newModel;
  }

  @Override
  protected final HealthCheck.Result check() throws Exception {
    final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
        this.model);
    final boolean exists = Files.exists(Paths.get(this.model));

    if (inputStream == null && !exists) {
      return HealthCheck.Result.unhealthy("Model " + this.model + " does not exists");
    }

    return HealthCheck.Result.healthy();
  }

  @Override
  public final String toString() {
    return "ModelHealthCheck{"
        + "model='" + this.model + '\''
        + '}';
  }
}
