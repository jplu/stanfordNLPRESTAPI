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

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.TokenImpl;
import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;
import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

/**
 * @author Julien Plu
 */
public class NullSentenceTest {
  static final Logger LOGGER = LoggerFactory.getLogger(NullSentenceTest.class);

  public NullSentenceTest() {
  }

  /**
   * Test {@link NullSentence#end()} method.
   */
  @Test
  public final void testEnd() {
    Assert.assertTrue("Issue to get the end of the sentence", NullSentence.getInstance().end()
        == 0);
  }

  /**
   * Test {@link NullSentence#start()} method.
   */
  @Test
  public final void testStart() {
    Assert.assertTrue("Issue to get the start of the sentence", NullSentence.getInstance().start()
        == 0);
  }

  /**
   * Test {@link NullSentence#index()} method.
   */
  @Test
  public final void testIndex() {
    Assert.assertTrue("Issue to get the index of the sentence", NullSentence.getInstance().index()
        == -1);
  }

  /**
   * Test {@link NullSentence#entities()} method.
   */
  @Test
  public final void testEntities() {
    Assert.assertNull("Issue to get the entities of the sentence",
        NullSentence.getInstance().entities());
  }

  /**
   * Test {@link NullSentence#rdfModel(String, NlpProcess)} method.
   */
  @Test
  public final void testRdfModel() {
    Assert.assertTrue("Issue to get the RDF model for NER", NullSentence.getInstance().rdfModel(
        "", NlpProcess.NER).isIsomorphicWith(ModelFactory.createDefaultModel()));
    Assert.assertTrue("Issue to get the RDF model for POS", NullSentence.getInstance().rdfModel(
        "", NlpProcess.POS).isIsomorphicWith(ModelFactory.createDefaultModel()));
  }

  /**
   * Test {@link NullSentence#addEntity(Entity)} method.
   */
  @Test(expected = UnsupportedOperationException.class)
  public final void testAddEntity() {
    NullSentence.getInstance().addEntity(null);
  }

  /**
   * Test {@link NullSentence#addToken(Token)} method.
   */
  @Test(expected = UnsupportedOperationException.class)
  public final void testAddToken() {
    NullSentence.getInstance().addToken(NullToken.getInstance());
  }

  /**
   * Test {@link NullSentence#nextSentence(Sentence)} method.
   */
  @Test(expected = UnsupportedOperationException.class)
  public final void testNextSentence() {
    NullSentence.getInstance().nextSentence(NullSentence.getInstance());
  }
}
