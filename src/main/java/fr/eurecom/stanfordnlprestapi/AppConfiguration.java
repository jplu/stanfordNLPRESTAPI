package fr.eurecom.stanfordnlprestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.eurecom.stanfordnlprestapi.configuration.PipelineConfiguration;

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
