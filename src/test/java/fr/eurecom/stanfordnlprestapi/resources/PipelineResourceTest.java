/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
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
package fr.eurecom.stanfordnlprestapi.resources;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;
import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.charset.Charset;

import java.nio.file.FileSystems;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.WebApplicationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class PipelineResourceTest {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineResourceTest.class);
  private static PipelineResource resource;

  public PipelineResourceTest() {
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    PipelineResourceTest.resource = new PipelineResource(new StanfordNlp(
        new PipelineConfiguration(), "en"));
    PipelineResourceTest.resource = new PipelineResource("stanfordnlp", "fr");
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#nerPost(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format.
   */
  @Test
  public final void testNerResponseTurtleWithPost() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerPost("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#posPost(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format.
   */
  @Test
  public final void testPosResponseTurtleWithPost() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posPost("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format.
   */
  @Test
  public final void testNerResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format.
   */
  @Test
  public final void testPosResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format in French.
   */
  @Test
  public final void testPosFrenchResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos_fr.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posGet(
        "Comment va-t-il depuis qu'il est en vacances ?", "turtle", "none", null, "fr",
        null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for French POS",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with JSON-LD format.
   */
  @Test
  public final void testNerResponseJsonldWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.jsonld"), Lang.JSONLD);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "jsonld", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.JSONLD);
    
    Assert.assertTrue("Issue to get the proper full RDF JSON-LD model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method with JSON-LD format.
   */
  @Test
  public final void testPosResponseJsonldWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.jsonld"), Lang.JSONLD);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "jsonld", "none", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.JSONLD);

    Assert.assertTrue("Issue to get the proper full RDF JSON-LD model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format for NEEL2016.
   */
  @Test
  public final void testNerNeel2016ResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner_tweet.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet(
        "@julienplu Ready for the new #starwars #theforce", "turtle", "neel2016", null, "en",
        null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format for NEEL2015.
   */
  @Test
  public final void testNerNeel2015ResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner_neel2015.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet(
        "@julienplu Ready for the new #starwars #theforce", "turtle", "neel2015", null, "en",
        null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format for OKE2016.
   */
  @Test
  public final void testNerOke2016ResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner_oke.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "oke2016",
        null, "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))),
        Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#nerGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format for OKE2015.
   */
  @Test
  public final void testNerOke2015ResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner_oke.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.nerGet("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "turtle", "oke2015", null,
        "en", null).getEntity().toString().getBytes(Charset.forName("UTF-8"))),
        Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method with Turtle format for Tweets.
   */
  @Test
  public final void testPosTweetResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos_tweet.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posGet(
        "@julienplu Ready for the new #starwars #theforce", "turtle", "tweet", null, "en",
        null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method when text and url are null.
   */
  @Test(expected = WebApplicationException.class)
  public final void testException() throws IOException {
    PipelineResourceTest.resource.posGet(null, "turtle", "none", null, "en", null);
  }
  
  /**
   * Test the response returned by the
   * {@link PipelineResource#posGet(String, String, String, String, String, HttpServletRequest)}
   * method when the parameter url is given.
   */
  @Test
  public final void testPosUrlResponseTurtleWithGet() throws IOException {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos_tweet.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.posGet(
        null, "turtle", "tweet", "http://adel.eurecom.fr/test_pos_url.html", "en",
        null).getEntity().toString().getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);
    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for POS from a "
            + "URL",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test {@link PipelineResource#toString()} method.
   */
  @Test
  public final void testToString() {
    Assert.assertEquals("Issue to get the proper toString value", "PipelineResource{}",
        PipelineResourceTest.resource.toString());
  }
}
