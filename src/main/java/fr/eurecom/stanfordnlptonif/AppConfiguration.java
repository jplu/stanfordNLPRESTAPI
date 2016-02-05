/**
 * Created by ovarene on 11/08/2015.
 */
package fr.eurecom.stanfordnlptonif;

import com.fasterxml.jackson.annotation.*;
import fr.eurecom.stanfordnlptonif.configuration.*;
import io.dropwizard.*;
import org.hibernate.validator.constraints.*;

import javax.validation.constraints.*;

public class AppConfiguration extends Configuration {

  @JsonProperty
  @NotNull
  private PipelineConfiguration pipeline;

  //###
  public PipelineConfiguration getPipeline() { return pipeline; }
  public void setPipeline(PipelineConfiguration pipeline) { this.pipeline = pipeline; }

}
