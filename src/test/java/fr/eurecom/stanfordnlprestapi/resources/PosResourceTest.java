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
package fr.eurecom.stanfordnlprestapi.resources;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;

import java.util.Properties;

import javax.ws.rs.WebApplicationException;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

/**
 * @author Julien Plu
 */
public class PosResourceTest {
  static final Logger LOGGER = LoggerFactory.getLogger(PosResourceTest.class);
  private PosResource resource;

  public PosResourceTest() {
  }

  @Before
  public final void setUp() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordNlp pipeline = new StanfordNlp(props);

    this.resource = new PosResource(pipeline);
  }

  /**
   * Test the response provided by the resource.
   */
  @Test
  public final void testPosResponse() {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);

    testModel.read(new ByteArrayInputStream(this.resource.get("My favorite actress is: Natalie"
        + " Portman. She is very stunning.").getEntity().toString().getBytes(Charset.forName(
        "UTF-8"))), null, "TTL");

    Assert.assertTrue("Issue to get the proper full RDF model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response provided by the resource with a null parameter.
   */
  @Test(expected = WebApplicationException.class)
  public final void testPosResponseWithNull() {
    this.resource.get(null);
  }

  /**
   * Test the response provided by the resource with an empty string.
   */
  @Test(expected = WebApplicationException.class)
  public final void testPosResponseWithEmptyString() {
    this.resource.get("");
  }

  /**
   * Test {@link PosResource#toString()} method.
   */
  @Test
  public final void testToString() {
    Assert.assertEquals("Issue to get the proper toString value", "PosResource{}",
        new PosResource().toString());
  }
}
