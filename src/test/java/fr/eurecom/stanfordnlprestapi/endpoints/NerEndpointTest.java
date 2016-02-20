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
package fr.eurecom.stanfordnlprestapi.endpoints;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;

import fr.eurecom.stanfordnlprestapi.resources.NerResource;

import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * @author Julien Plu
 */
public class NerEndpointTest {
  static final Logger LOGGER = LoggerFactory.getLogger(NerEndpointTest.class);
  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder().addResource(
      new NerResource()).build();

  public NerEndpointTest() {
  }

  /**
   * Test the NER endpoint.
   */
  @Test
  public final void nerResourceDropwizard() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model serviceModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);

    RDFDataMgr.read(serviceModel, new ByteArrayInputStream(this.resources.client().target(
        "/v1/ner").queryParam("q", "My favorite actress is: Natalie Portman. She is very "
        + "stunning.").request().get(String.class).getBytes(Charset.forName("UTF-8"))),
        Lang.TURTLE);

    Assert.assertTrue("Answer given by the server is not valid for NER",
        fileModel.isIsomorphicWith(serviceModel));
  }
}
