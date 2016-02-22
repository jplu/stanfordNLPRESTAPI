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
package fr.eurecom.stanfordnlprestapi.core;

import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlprestapi.datatypes.Context;
import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.SentenceImpl;
import fr.eurecom.stanfordnlprestapi.datatypes.TokenImpl;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

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
public class StanfordNlpTest {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlpTest.class);
  private static StanfordNlp stanfordNlp;

  public StanfordNlpTest() {
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

    StanfordNlpTest.stanfordNlp = new StanfordNlp(props);
  }

  /**
   * Test {@link StanfordNlp} for the POS process.
   */
  @Test
  public final void testRunWithPos() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);

    final String text = "My favorite actress is: Natalie Portman. She is very stunning.";

    Assert.assertTrue("Issue to get the proper full RDF model of a context for POS",
        fileModel.isIsomorphicWith(StanfordNlpTest.stanfordNlp.run(text).rdfModel("stanfordnlp",
            NlpProcess.POS)));
  }

  /**
   * Test {@link StanfordNlp} for the NER process.
   */
  @Test
  public final void testRunWithNer() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);

    final String text = "My favorite actress is: Natalie Portman. She "
        + "is very stunning.";

    Assert.assertTrue("Issue to get the proper full RDF model of a context for NER",
        fileModel.isIsomorphicWith(StanfordNlpTest.stanfordNlp.run(text).rdfModel("stanfordnlp",
            NlpProcess.NER)));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * an entity at the end of a sentence.
   */
  @Test
  public final void testBuildEntitiesEnd() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("I like Paris");
    final Context context = new Context("I like Paris", 0, 12);
    final Sentence sentence = new SentenceImpl("I like Paris", context, 0, 12, 0,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 7, 12);
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("like", "VBP", 2, 6, "like", token1, context, sentence, 2);
    final Token token3 = new TokenImpl("Paris", "NNP", 7, 12, "Paris", token2, context, sentence,
        3);

    token1.nextToken(token2);
    token2.nextToken(token3);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a one token entity at the end of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity at the end of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleEnd() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("I like Natalie Portman");
    final Context context = new Context("I like Natalie Portman", 0, 22);
    final Sentence sentence = new SentenceImpl("I like Natalie Portman", context, 0, 22, 0,
        NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 7, 22);
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("like", "VBP", 2, 6, "like", token1, context, sentence, 2);
    final Token token3 = new TokenImpl("Natalie", "NNP", 7, 14, "Natalie", token2, context,
        sentence, 3);
    final Token token4 = new TokenImpl("Portman", "NNP", 15, 22, "Portman", token3, context,
        sentence, 4);

    token1.nextToken(token2);
    token2.nextToken(token3);
    token3.nextToken(token4);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addToken(token4);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a multiple token entity at the end of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * an entity at the beginning of a sentence.
   */
  @Test
  public final void testBuildEntitiesStart() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("Paris is a nice city.");
    final Context context = new Context("Paris is a nice city.", 0, 21);
    final Sentence sentence = new SentenceImpl("Paris is a nice city.", context, 0, 21, 0,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 0, 5);
    final Token token1 = new TokenImpl("Paris", "NNP", 0, 5, "Paris", NullToken.getInstance(),
        context, sentence, 1);
    final Token token2 = new TokenImpl("is", "VBZ", 6, 8, "be", token1, context, sentence, 2);
    final Token token3 = new TokenImpl("a", "DT", 9, 10, "a", token2, context, sentence, 3);
    final Token token4 = new TokenImpl("nice", "JJ", 11, 15, "nice", token3, context, sentence, 4);
    final Token token5 = new TokenImpl("city", "NN", 16, 20, "city", token4, context, sentence, 5);
    final Token token6 = new TokenImpl(".", ".", 20, 21, ".", token5, context, sentence, 6);

    token1.nextToken(token2);
    token2.nextToken(token3);
    token3.nextToken(token4);
    token4.nextToken(token5);
    token5.nextToken(token6);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addToken(token4);
    sentence.addToken(token5);
    sentence.addToken(token6);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a one token entity at the beginning of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity at the beginning of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleStart() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("Natalie Portman is a beautiful "
        + "girl.");
    final Context context = new Context("Natalie Portman is a beautiful girl.", 0, 36);
    final Sentence sentence = new SentenceImpl("Natalie Portman is a beautiful girl.", context, 0,
        36, 0, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 0, 15);
    final Token token1 = new TokenImpl("Natalie", "NNP", 0, 7, "Natalie", NullToken.getInstance(),
        context, sentence, 1);
    final Token token2 = new TokenImpl("Portman", "NNP", 8, 15, "Portman", token1, context,
        sentence, 2);
    final Token token3 = new TokenImpl("is", "VBZ", 16, 18, "be", token2, context, sentence, 3);
    final Token token4 = new TokenImpl("a", "DT", 19, 20, "a", token3, context, sentence, 4);
    final Token token5 = new TokenImpl("beautiful", "NN", 21, 30, "beautiful", token4, context,
        sentence, 5);
    final Token token6 = new TokenImpl("girl", "NN", 31, 35, "girl", token5, context, sentence, 6);
    final Token token7 = new TokenImpl(".", ".", 35, 36, ".", token6, context, sentence, 7);

    token1.nextToken(token2);
    token2.nextToken(token3);
    token3.nextToken(token4);
    token4.nextToken(token5);
    token5.nextToken(token6);
    token6.nextToken(token7);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addToken(token4);
    sentence.addToken(token5);
    sentence.addToken(token6);
    sentence.addToken(token7);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a multiple token entity at the beginning of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * an entity in the middle of a sentence.
   */
  @Test
  public final void testBuildEntitiesMiddle() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("I support Paris very much.");
    final Context context = new Context("I support Paris very much.", 0, 26);
    final Sentence sentence = new SentenceImpl("I support Paris very much.", context, 0, 26, 0,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 10, 15);
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("support", "VBP", 2, 9, "support", token1, context, sentence,
        2);
    final Token token3 = new TokenImpl("Paris", "NNP", 10, 15, "Paris", token2, context, sentence,
        3);
    final Token token4 = new TokenImpl("very", "RB", 16, 20, "very", token3, context, sentence, 4);
    final Token token5 = new TokenImpl("much", "RB", 21, 25, "much", token4, context, sentence, 5);
    final Token token6 = new TokenImpl(".", ".", 25, 26, ".", token5, context, sentence, 6);

    token1.nextToken(token2);
    token2.nextToken(token3);
    token3.nextToken(token4);
    token4.nextToken(token5);
    token5.nextToken(token6);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addToken(token4);
    sentence.addToken(token5);
    sentence.addToken(token6);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a one token entity in the middle of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity in the middle of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleMiddle() throws Exception {
    final Context contextTest = StanfordNlpTest.stanfordNlp.run("I love Natalie Portman a lot.");
    final Context context = new Context("I love Natalie Portman a lot.", 0, 29);
    final Sentence sentence = new SentenceImpl("I love Natalie Portman a lot.", context, 0, 29, 0,
        NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 7, 22);
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(),
        context, sentence, 1);
    final Token token2 = new TokenImpl("love", "VBP", 2, 6, "love", token1, context, sentence, 2);
    final Token token3 = new TokenImpl("Natalie", "NNP", 7, 14, "Natalie", token2, context,
        sentence, 3);
    final Token token4 = new TokenImpl("Portman", "NNP", 15, 22, "Portman", token3, context,
        sentence, 4);
    final Token token5 = new TokenImpl("a", "DT", 23, 24, "a", token4, context, sentence, 5);
    final Token token6 = new TokenImpl("lot", "NN", 25, 28, "lot", token5, context, sentence, 6);
    final Token token7 = new TokenImpl(".", ".", 28, 29, ".", token6, context, sentence, 7);

    token1.nextToken(token2);
    token2.nextToken(token3);
    token3.nextToken(token4);
    token4.nextToken(token5);
    token5.nextToken(token6);
    token6.nextToken(token7);

    sentence.addToken(token1);
    sentence.addToken(token2);
    sentence.addToken(token3);
    sentence.addToken(token4);
    sentence.addToken(token5);
    sentence.addToken(token6);
    sentence.addToken(token7);
    sentence.addEntity(entity);

    context.addSentence(sentence);

    Assert.assertTrue("Issue to build a multiple token entity in the middle of a sentence",
        contextTest.sentences().get(0).entities().contains(entity));
  }

  /**
   * Test{@link StanfordNlp#toString()} method.
   */
  @Test
  public final void testToString() {
    Assert.assertEquals("Issue to get the proper toString value", "StanfordNlp{}",
        StanfordNlpTest.stanfordNlp.toString());
  }
}
