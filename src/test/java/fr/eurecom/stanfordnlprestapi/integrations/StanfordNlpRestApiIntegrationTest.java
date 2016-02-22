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
package fr.eurecom.stanfordnlprestapi.integrations;

import fr.eurecom.stanfordnlprestapi.App;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;
import io.dropwizard.testing.ResourceHelpers;

import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.ByteArrayInputStream;

import java.nio.charset.Charset;

import java.nio.file.FileSystems;

import javax.ws.rs.client.Client;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.glassfish.jersey.client.JerseyClientBuilder;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.junit.experimental.categories.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
@Category(IntegrationTest.class)
public class StanfordNlpRestApiIntegrationTest {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlpRestApiIntegrationTest.class);
  @Rule
  public final DropwizardAppRule<PipelineConfiguration> rule = new DropwizardAppRule<>(App.class,
      ResourceHelpers.resourceFilePath("config.yaml"));

  public StanfordNlpRestApiIntegrationTest() {
  }

  /**
   * Run and test the server like if we would do it in command line for NER.
   */
  @Test
  public final void runServerTestNer() {
    final Client client = new JerseyClientBuilder().build();
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model serviceModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);
    RDFDataMgr.read(serviceModel, new ByteArrayInputStream(client.target(
        String.format("http://localhost:%d/v1/ner", this.rule.getLocalPort())).queryParam("q", "My "
            + "favorite actress is: Natalie Portman. She is very stunning.").request().get(
            String.class).getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Answer given by the server is not valid for NER during integration test",
        fileModel.isIsomorphicWith(serviceModel));
  }

  /**
   * Run and test the server like if we would do it in command line for POS.
   */
  @Test
  public final void runServerTestPos() {
    final Client client = new JerseyClientBuilder().build();
    final Model fileModel = ModelFactory.createDefaultModel();
    final Model serviceModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);
    RDFDataMgr.read(serviceModel, new ByteArrayInputStream(client.target(
        String.format("http://localhost:%d/v1/pos", this.rule.getLocalPort())).queryParam("q", "My "
        + "favorite actress is: Natalie Portman. She is very stunning.").request().get(
        String.class).getBytes(Charset.forName("UTF-8"))), Lang.TURTLE);

    Assert.assertTrue("Answer given by the server is not valid for POS during integration test",
        fileModel.isIsomorphicWith(serviceModel));
  }
}
