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
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class PipelineResourceNerTest {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineResourceNerTest.class);
  @ClassRule
  public static final ResourceTestRule RESOURCES = ResourceTestRule.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(
          new PipelineResource("stanfordnlp", Paths.get(
              PipelineResourceGazetteerTest.class.getClassLoader().getResource(
                  "ner_en_test.properties").getFile()))).build();

  public PipelineResourceNerTest() {
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with content.
   */
  @Test
  public final void testNerResponseWithContent() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").queryParam("setting", "test").request("text/turtle;charset=utf-8").post(
            Entity.entity("{\"content\":\"My favorite actress is: Natalie Portman. She is very "
                    + "stunning.\"}", MediaType.APPLICATION_JSON_TYPE));
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, IOUtils.toInputStream(response.readEntity(String.class),
        Charset.forName("UTF-8")), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with URL.
   */
  @Test
  public final void testNerResponseWithUrl() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").queryParam("setting", "test").request("text/turtle;charset=utf-8").post(
            Entity.entity("{\"url\":\"http://adel.eurecom.fr/unit_test_api_with_url.html\"}",
                MediaType.APPLICATION_JSON_TYPE));
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, IOUtils.toInputStream(response.readEntity(String.class),
        Charset.forName("UTF-8")), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the {@link PipelineResource#profiles()} method.
   */
  @Test
  public final void testProfilesResponse() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/profiles").request().get();
    
    Assert.assertEquals("Issue to get the proper profiles", "[ ]",
        response.readEntity(String.class));
  }
  
  /**
   * Test the response returned by the {@link PipelineResource#profile(String)} method when the
   * profile does not exists.
   */
  @Test
  public final void testProfileFailureResponse() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/profile/toto").request().get();
  
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"The profile toto does "
        + "not exists.\"}", response.readEntity(String.class));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with a JSON violation
   * when URL or content are provided.
   */
  @Test
  public final void testNerResponseWithJsonViolationNone() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").request("text/turtle;charset=utf-8").post(Entity.entity("{}",
        MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"Properties content "
        + "and URL cannot be empty or filled in same time.\\n\"}", response.readEntity(
            String.class));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with an invalid JSON.
   */
  @Test
  public final void testNerResponseWithInvalidJson() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").request("text/turtle;charset=utf-8").post(Entity.entity("{\"content\":\"My "
            + "favorite actress is: Natalie Portman. She is very stunning.\", \"toto\":\"titi\"}",
        MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"Failed to read the"
        + " HTTP request {\\\"content\\\":\\\"My favorite actress is: Natalie Portman. She is very"
        + " stunning.\\\", \\\"toto\\\":\\\"titi\\\"}\"}", response.readEntity(String.class));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with a second JSON
   * violation when URL and content are both provided.
   */
  @Test
  public final void testNerResponseWithJsonViolationBoth() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").request("text/turtle;charset=utf-8").post(Entity.entity("{\"content\":\"\","
            + "\"url\":\"\"}", MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"Properties content "
        + "and URL cannot be empty or filled in same time.\\n\"}", response.readEntity(
        String.class));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#ner(HttpServletRequest, String, String)} method with a wrong profile.
   */
  @Test
  public final void testNerResponseWithWrongProfile() {
    final Response response = PipelineResourceNerTest.RESOURCES.getJerseyTest().target(
        "/v4/ner").queryParam("lang", "ty").request("text/turtle;charset=utf-8").post(
            Entity.entity("{\"content\":\"\"}", MediaType.APPLICATION_JSON_TYPE));
    
    Assert.assertEquals("JSON response issue", "{\"code\":412,\"message\":\"The profile: "
        + "ner_ty_none does not exists\"}", response.readEntity(String.class));
  }
}
