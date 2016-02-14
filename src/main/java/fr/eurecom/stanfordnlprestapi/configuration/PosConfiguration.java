package fr.eurecom.stanfordnlprestapi.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class PosConfiguration {
  static final Logger LOGGER = LoggerFactory.getLogger(PosConfiguration.class);
  @NotEmpty
  private String model;

  public PosConfiguration() {
  }

  public final String getModel() {
    return this.model;
  }

  public final void setModel(final String newModel) {
    this.model = newModel;
  }

  @Override
  public final String toString() {
    return "PosConfiguration{"
        + "model='" + this.model + '\''
        + '}';
  }
}
