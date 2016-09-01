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
package fr.eurecom.stanfordnlprestapi.core;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import java.nio.file.FileSystems;

import java.util.Properties;

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
public class StanfordNlpTweetsTest {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlpTweetsTest.class);
  private static StanfordNlp stanfordNlp;
  
  public StanfordNlpTweetsTest() {
  }
  
  @BeforeClass
  public static void setUpBeforeClass() {
    final Properties props = new Properties();
    
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
    props.setProperty("pos.model", "models/gate-EN-twitter.model");
    props.setProperty("ner.model", "models/NEEL2016.ser.gz");
    props.setProperty("ner.useSUTime", "false");
    props.setProperty("ner.applyNumericClassifiers", "false");
    props.setProperty("parse.model", "models/englishRNN.ser.gz");
    props.setProperty("coref.doClustering", "true");
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.mode", "statistical");
  
    StanfordNlpTweetsTest.stanfordNlp = new StanfordNlp(props);
  }
  
  /**
   * Test {@link StanfordNlp} for the POS process.
   */
  @Test
  public final void testRunWithPos() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos_tweet.ttl"), Lang.TURTLE);
    
    final String text = "@julienplu Ready for the new #starwars #theforce";
    
    Assert.assertTrue("Issue to get the proper full RDF model of a context for POS",
        fileModel.isIsomorphicWith(StanfordNlpTweetsTest.stanfordNlp.run(text).rdfModel(
            "stanfordnlp", NlpProcess.POS)));
  }
  
  /**
   * Test {@link StanfordNlp} for the NER process.
   */
  @Test
  public final void testRunWithNer() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    
    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner_tweet.ttl"), Lang.TURTLE);
    
    final String text = "@julienplu Ready for the new #starwars #theforce";
    
    Assert.assertTrue("Issue to get the proper full RDF model of a context for NER",
        fileModel.isIsomorphicWith(StanfordNlpTweetsTest.stanfordNlp.run(text).rdfModel(
            "stanfordnlp", NlpProcess.NER)));
  }
}
