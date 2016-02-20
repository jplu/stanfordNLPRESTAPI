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
package fr.eurecom.stanfordnlprestapi.nullobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.interfaces.Token;

/**
 * Null object that represents a null token.
 *
 * @author Julien Plu
 */
public final class NullToken implements Token {
  static final Logger LOGGER = LoggerFactory.getLogger(NullToken.class);
  private static final NullToken INSTANCE = new NullToken();

  public static NullToken getInstance() {
    return NullToken.INSTANCE;
  }
  
  private NullToken() {
  }

  @Override
  public void nextToken(final Token newNextToken) {
    throw new UnsupportedOperationException("Not imlpemented");
  }

  @Override
  public int index() {
    return -1;
  }

  @Override
  public String text() {
    return null;
  }

  @Override
  public int start() {
    return 0;
  }

  @Override
  public int end() {
    return 0;
  }

  @Override
  public Model rdfModel(final String tool) {
    return ModelFactory.createDefaultModel();
  }
}
