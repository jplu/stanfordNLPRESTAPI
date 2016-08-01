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

import org.hibernate.validator.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class CorefConfiguration {
  static final Logger LOGGER = LoggerFactory.getLogger(CorefConfiguration.class);
  @NotEmpty
  private String mdType;
  @NotEmpty
  private String mode;
  @NotEmpty
  private Boolean doClustering;

  /**
   * CorefConfiguration constructor.
   */
  public CorefConfiguration() {
  }

  public final String getMdType() {
    return this.mdType;
  }

  public final void setMdType(final String newMdType) {
    this.mdType = newMdType;
  }

  public final String getMode() {
    return this.mode;
  }

  public final void setMode(final String newMode) {
    this.mode = newMode;
  }

  public final Boolean getDoClustering() {
    return this.doClustering;
  }

  public final void setDoClustering(final Boolean newDoClustering) {
    this.doClustering = newDoClustering;
  }

  @Override
  public final String toString() {
    return "NerConfiguration{"
        + "mdType='" + this.mdType + '\''
        + "mode='" + this.mode + '\''
        + "doClustering='" + this.doClustering + '\''
        + '}';
  }
}
