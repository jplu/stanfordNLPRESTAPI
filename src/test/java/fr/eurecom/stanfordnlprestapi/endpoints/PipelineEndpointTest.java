/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
 * Copyright © 2017 Julien Plu (julien.plu@redaction-developpez.com)
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
package fr.eurecom.stanfordnlprestapi.endpoints;

import fr.eurecom.stanfordnlprestapi.resources.PipelineResource;

import io.dropwizard.testing.junit.DropwizardClientRule;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class PipelineEndpointTest {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineEndpointTest.class);
  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder().addResource(
      new PipelineResource("stanfordnlp", "en")).build();
  
  @ClassRule
  public static final DropwizardClientRule DROPWIZARD = new DropwizardClientRule(
      new PipelineResource("stanfordnlp", "en"));

  public PipelineEndpointTest() {
  }
  
  /**
   * Test the POS client.
   */
  @Test
  public final void clientPos() throws Exception {
    final URL url = new URL(PipelineEndpointTest.DROPWIZARD.baseUri() + "/v1/pos?text=test");
    try (final BufferedReader response = new BufferedReader(new InputStreamReader(
        url.openStream(), Charset.forName("UTF-8")))) {
  
      Assert.assertEquals("Issue with the POS client",
          "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .", response.readLine());
    }
  }
  
  /**
   * Test the NER client.
   */
  @Test
  public final void clientNer() throws Exception {
    final URL url = new URL(PipelineEndpointTest.DROPWIZARD.baseUri() + "/v1/ner?text=Paris");
    try (final BufferedReader response = new BufferedReader(new InputStreamReader(
        url.openStream(), Charset.forName("UTF-8")))) {
    
      Assert.assertEquals("Issue with the NER client",
          "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .", response.readLine());
    }
  }

  /**
   * Test the NER endpoint.
   */
  @Test
  public final void pipelineResourceNerDropwizard() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model serviceModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);

    RDFDataMgr.read(serviceModel, new ByteArrayInputStream(this.resources.client().target(
        "/v1/ner").queryParam("text", "My favorite actress is: Natalie Portman. She is very "
            + "stunning.").request().get(String.class).getBytes(Charset.forName("UTF-8"))),
        Lang.TURTLE);

    Assert.assertTrue("Answer given by the server is not valid for NER",
        fileModel.isIsomorphicWith(serviceModel));
  }

  /**
   * Test the POS endpoint.
   */
  @Test
  public final void pipelineResourcePosDropwizard() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model serviceModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);

    RDFDataMgr.read(serviceModel, new ByteArrayInputStream(this.resources.client().target(
        "/v1/pos").queryParam("text", "My favorite actress is: Natalie Portman. She is very "
            + "stunning.").request().get(String.class).getBytes(Charset.forName("UTF-8"))),
        Lang.TURTLE);

    Assert.assertTrue("Answer given by the server is not valid for POS",
        fileModel.isIsomorphicWith(serviceModel));
  }
}
