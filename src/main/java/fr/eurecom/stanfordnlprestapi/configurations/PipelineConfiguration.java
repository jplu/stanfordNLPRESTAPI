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
  @NotEmpty
  private final String annotators;

  /**
   * PipelineConfiguration constructor.
   */
  public PipelineConfiguration() {
    this.pos = new PosConfiguration();
    this.ner = new NerConfiguration();
    this.annotators = "tokenize, ssplit, pos, lemma, ner";
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
