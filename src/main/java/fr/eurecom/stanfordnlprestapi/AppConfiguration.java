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
package fr.eurecom.stanfordnlprestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;

import io.dropwizard.Configuration;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class AppConfiguration extends Configuration {
  static final Logger LOGGER = LoggerFactory.getLogger(AppConfiguration.class);
  @JsonProperty
  @NotNull
  private PipelineConfiguration pipeline;

  public AppConfiguration() {
  }

  public final PipelineConfiguration getPipeline() {
    return this.pipeline;
  }

  public final void setPipeline(final PipelineConfiguration newPipeline) {
    this.pipeline = newPipeline;
  }

  @Override
  public final String toString() {
    return "AppConfiguration{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
