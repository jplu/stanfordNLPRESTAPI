/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework to get
results in NIF format.
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
/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework
                            to get results in NIF format.
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
package fr.eurecom.stanfordnlprestapi.configurations;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class PipelineConfiguration extends Configuration {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineConfiguration.class);
  @JsonProperty
  @NotNull
  private PosConfiguration pos;
  @JsonProperty
  @NotNull
  private NerConfiguration ner;
  @JsonProperty
  @NotNull
  private ParseConfiguration parse;
  @JsonProperty
  @NotNull
  private CorefConfiguration coref;

  public PipelineConfiguration() {
  }

  public final ParseConfiguration getParse() {
    return this.parse;
  }

  public final void setParse(final ParseConfiguration newParse) {
    this.parse = newParse;
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

  public final CorefConfiguration getCoref() {
    return this.coref;
  }

  public final void setCoref(final CorefConfiguration newCoref) {
    this.coref = newCoref;
  }

  @Override
  public final String toString() {
    return "PipelineConfiguration{"
        + "pos=" + this.pos
        + ", ner=" + this.ner
        + ", parse=" + this.parse
        + ", parse=" + this.coref
        + '}';
  }
}
