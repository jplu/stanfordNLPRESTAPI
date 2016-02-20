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
package fr.eurecom.stanfordnlprestapi.configurations;

import org.hibernate.validator.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

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

  /**
   * NerConfiguration constructor.
   */
  public NerConfiguration() {
    this.model = "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz";
    this.useSuTime = false;
    this.applyNumericClassifiers = false;
  }

  @JsonProperty
  public final String getModel() {
    return this.model;
  }

  @JsonProperty
  public final void setModel(final String newModel) {
    this.model = newModel;
  }

  @JsonProperty
  public final Boolean getUseSuTime() {
    return this.useSuTime;
  }

  @JsonProperty
  public final void setUseSuTime(final Boolean newUseSuTime) {
    this.useSuTime = newUseSuTime;
  }

  @JsonProperty
  public final Boolean getApplyNumericClassifiers() {
    return this.applyNumericClassifiers;
  }

  @JsonProperty
  public final void setApplyNumericClassifiers(final Boolean newApplyNumericClassifiers) {
    this.applyNumericClassifiers = newApplyNumericClassifiers;
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
