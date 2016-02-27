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

import fr.eurecom.stanfordnlprestapi.configurations.NerConfiguration;
import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;
import fr.eurecom.stanfordnlprestapi.configurations.PosConfiguration;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import java.io.ByteArrayInputStream;

import java.nio.charset.Charset;

import java.nio.file.FileSystems;

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
    final PipelineConfiguration pipeline = new PipelineConfiguration();

    pipeline.setNer(new NerConfiguration());
    pipeline.setPos(new PosConfiguration());
    pipeline.getNer().setApplyNumericClassifiers(false);
    pipeline.getNer().setUseSuTime(false);
    pipeline.getNer().setModel("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    pipeline.getPos().setModel("edu/stanford/nlp/models/pos-tagger/english-left3words/"
        + "english-left3words-distsim.tagger");

    final StanfordNlp stanford = new StanfordNlp(pipeline);

    PipelineResourceTest.resource = new PipelineResource(stanford);
  }

  /**
   * Test the response provided by the {@link PipelineResource#getNer(String, String)} method with
   * Turtle format.
   */
  @Test
  public final void testNerResponseTurtle() {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.getNer("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "").getEntity().toString()
        .getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response provided by the {@link PipelineResource#getPos(String, String)} method with
   * Turtle format.
   */
  @Test
  public final void testPosResponseTurtle() {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.getPos("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "").getEntity().toString()
        .getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF Turtle model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response provided by the {@link PipelineResource#getNer(String, String)} method with
   * JSON-LD format.
   */
  @Test
  public final void testNerResponseJsonld() {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.jsonld"), Lang.JSONLD);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.getNer("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "jsonld").getEntity()
        .toString().getBytes(Charset.forName("UTF-8"))), Lang.JSONLD);

    Assert.assertTrue("Issue to get the proper full RDF JSON-LD model of a context for NER",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response provided by the {@link PipelineResource#getPos(String, String)} method with
   * JSON-LD format.
   */
  @Test
  public final void testPosResponseJsonld() {
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model testModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.jsonld"), Lang.JSONLD);
    RDFDataMgr.read(testModel, new ByteArrayInputStream(PipelineResourceTest.resource.getPos("My "
        + "favorite actress is: Natalie Portman. She is very stunning.", "jsonld").getEntity()
        .toString().getBytes(Charset.forName("UTF-8"))), Lang.JSONLD);

    Assert.assertTrue("Issue to get the proper full RDF JSON-LD model of a context for POS",
        fileModel.isIsomorphicWith(testModel));
  }

  /**
   * Test the response provided by the {@link PipelineResource#getNer(String, String)} method
   * with a null parameter.
   */
  @Test(expected = WebApplicationException.class)
  public final void testNerResponseWithNull() {
    PipelineResourceTest.resource.getNer(null, "");
  }

  /**
   * Test the response provided by the {@link PipelineResource#getNer(String, String)} method with
   * an empty string.
   */
  @Test(expected = WebApplicationException.class)
  public final void testNerResponseWithEmptyString() {
    PipelineResourceTest.resource.getNer("", "");
  }

  /**
   * Test the response provided by the {@link PipelineResource#getPos(String, String)} method
   * with a null parameter.
   */
  @Test(expected = WebApplicationException.class)
  public final void testPosResponseWithNull() {
    PipelineResourceTest.resource.getPos(null, "");
  }

  /**
   * Test the response provided by the {@link PipelineResource#getNer(String, String)} method with
   * invalid format.
   */
  @Test(expected = WebApplicationException.class)
  public final void testNerResponseWithInvalidFormat() {
    PipelineResourceTest.resource.getNer("coucou", "toto");
  }

  /**
   * Test the response provided by the {@link PipelineResource#getPos(String, String)} method
   * invalid format.
   */
  @Test(expected = WebApplicationException.class)
  public final void testPosResponseWithInvalidFormat() {
    PipelineResourceTest.resource.getPos("coucou", "toto");
  }

  /**
   * Test the response provided by the {@link PipelineResource#getPos(String, String)} method with
   * an empty string.
   */
  @Test(expected = WebApplicationException.class)
  public final void testPosResponseWithEmptyString() {
    PipelineResourceTest.resource.getPos("", "");
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
