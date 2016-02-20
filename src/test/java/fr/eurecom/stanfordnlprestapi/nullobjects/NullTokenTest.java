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

import fr.eurecom.stanfordnlprestapi.interfaces.Token;

/**
 * @author Julien Plu
 */
public class NullTokenTest {
  static final Logger LOGGER = LoggerFactory.getLogger(NullTokenTest.class);

  public NullTokenTest() {
  }

  /**
   * Test {@link NullToken#end()} method.
   */
  @Test
  public final void testEnd() {
    Assert.assertTrue("Issue to get the end of the token", NullToken.getInstance().end() == 0);
  }

  /**
   * Test {@link NullToken#start()} method.
   */
  @Test
  public final void testStart() {
    Assert.assertTrue("Issue to get the start of the token", NullToken.getInstance().start() == 0);
  }

  /**
   * Test {@link NullToken#index()} method.
   */
  @Test
  public final void testIndex() {
    Assert.assertTrue("Issue to get the index of the token", NullToken.getInstance().index() == -1);
  }

  /**
   * Test {@link NullToken#text()} method.
   */
  @Test
  public final void testText() {
    Assert.assertNull("Issue to get the text of the token", NullToken.getInstance().text());
  }

  /**
   * Test {@link NullToken#rdfModel(String)} method.
   */
  @Test
  public final void testRdfModel() {
    Assert.assertTrue("Issue to get the RDF model of the token", NullToken.getInstance().rdfModel(
        "").isIsomorphicWith(ModelFactory.createDefaultModel()));
  }

  /**
   * Test {@link NullToken#nextToken(Token)} method.
   */
  @Test(expected = UnsupportedOperationException.class)
  public final void testNextToken() {
    NullToken.getInstance().nextToken(NullToken.getInstance());
  }
}
