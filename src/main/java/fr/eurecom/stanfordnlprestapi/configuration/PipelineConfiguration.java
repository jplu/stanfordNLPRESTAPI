package fr.eurecom.stanfordnlprestapi.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class PipelineConfiguration {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineConfiguration.class);
  // TODO(Olivier Varene): use Ner and Pos configuration classes and migrate configuration file
  @NotEmpty
  private PosConfiguration pos;
  @NotEmpty
  private NerConfiguration ner;
  private String annotators;

  public PipelineConfiguration() {
  }

  public final String getAnnotators() {
    return this.annotators;
  }

  public final PosConfiguration getPos() {
    return this.pos;
  }

  public final void setPos(final PosConfiguration newPos) {
    this.pos = newPos;
  }

  public final NerConfiguration getNer() {
    return this.ner;
  }

  public final void setNer(final NerConfiguration newNer) {
    this.ner = newNer;
  }

  @Override
  public final String toString() {
    return "PipelineConfiguration{"
        + "pos=" + this.pos
        + ", ner=" + this.ner
        + ", annotators=" + this.annotators
        + '}';
  }
}
