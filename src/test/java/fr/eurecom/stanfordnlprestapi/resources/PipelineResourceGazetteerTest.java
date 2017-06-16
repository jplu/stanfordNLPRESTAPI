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
package fr.eurecom.stanfordnlprestapi.resources;

import io.dropwizard.testing.junit.ResourceTestRule;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class PipelineResourceGazetteerTest {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineResourceGazetteerTest.class);
  @ClassRule
  public static final ResourceTestRule RESOURCES = ResourceTestRule.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(
          new PipelineResource("stanfordnlp", "gazetteer_en_oke2015")).build();
  
  public PipelineResourceGazetteerTest() {
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#gazetteer(HttpServletRequest, String, String)} method with content.
   */
  @Test
  public final void testGazetteerResponseWithContent() {
    final Response response = PipelineResourceGazetteerTest.RESOURCES.getJerseyTest().target(
        "/v4/gazetteer").queryParam("setting", "oke2015").request("text/turtle;charset=utf-8").post(
            Entity.entity("{\"content\":\"This guy is cool.\"}",
                MediaType.APPLICATION_JSON_TYPE));
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "gazetteer.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, IOUtils.toInputStream(response.readEntity(String.class),
        Charset.forName("UTF-8")), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for gazetteer",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#gazetteer(HttpServletRequest, String, String)} method with a JSON
   * violation.
   */
  @Test
  public final void testGazetteerResponseWithJsonViolation() {
    final Response response = PipelineResourceGazetteerTest.RESOURCES.getJerseyTest().target(
        "/v4/gazetteer").request("text/turtle;charset=utf-8").post(Entity.entity("{}",
        MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"Properties content "
        + "and URL cannot be empty or filled in same time.\\n\"}", response.readEntity(
        String.class));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#gazetteer(HttpServletRequest, String, String)} method with an invalid
   * JSON.
   */
  @Test
  public final void testGazetteerResponseWithInvalidJson() {
    final Response response = PipelineResourceGazetteerTest.RESOURCES.getJerseyTest().target(
        "/v4/gazetteer").request("text/turtle;charset=utf-8").post(Entity.entity("{\"content\":\"My"
            + "favorite actress is: Natalie Portman. She is very stunning.\", \"toto\":\"titi\"}",
        MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"Failed to read the"
        + " HTTP request {\\\"content\\\":\\\"Myfavorite actress is: Natalie Portman. She is very"
        + " stunning.\\\", \\\"toto\\\":\\\"titi\\\"}\"}", response.readEntity(String.class));
  }
}
