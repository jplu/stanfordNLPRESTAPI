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
package fr.eurecom.stanfordnlprestapi.interfaces;

import fr.eurecom.stanfordnlprestapi.datatypes.Entity;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import java.util.List;

import org.apache.jena.rdf.model.Model;

/**
 * Interface that represents a NIF and Stanford NLP sentence.
 *
 * @author Julien Plu
 */
public interface Sentence {
  void addToken(final Token newToken);

  void addEntity(Entity newEntity);

  void nextSentence(final Sentence newNextSentence);

  List<Entity> entities();

  int index();

  int start();

  int end();

  Model rdfModel(final String tool, final NlpProcess process);
}
