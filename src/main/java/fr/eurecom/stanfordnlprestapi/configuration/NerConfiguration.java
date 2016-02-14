package fr.eurecom.stanfordnlprestapi.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class NerConfiguration {
  static final Logger LOGGER = LoggerFactory.getLogger(NerConfiguration.class);
  @NotEmpty
  private String model;
  @NotEmpty
  private Boolean useSuTime;
  @NotEmpty
  private Boolean applyNumericClassifiers;

  public NerConfiguration() {
  }

  public final String getModel() {
    return this.model;
  }

  public final void setModel(final String newModel) {
    this.model = newModel;
  }

  public final Boolean getUseSuTime() {
    return this.useSuTime;
  }

  public final Boolean getApplyNumericClassifiers() {
    return this.applyNumericClassifiers;
  }

  @Override
  public final String toString() {
    return "NerConfiguration{"
        + "model='" + this.model + '\''
        + ", useSUTime=" + this.useSuTime
        + ", applyNumericClassifiers=" + this.applyNumericClassifiers
        + '}';
  }
}
