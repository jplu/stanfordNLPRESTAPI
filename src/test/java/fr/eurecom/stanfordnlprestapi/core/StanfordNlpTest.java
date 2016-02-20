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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import java.nio.file.FileSystems;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlprestapi.configurations.NerConfiguration;
import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;
import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfigurationTest;
import fr.eurecom.stanfordnlprestapi.datatypes.Context;
import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.SentenceImpl;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;

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

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

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
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("I like Paris");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("I like Paris", 0, 12);
    final Sentence sentence = new SentenceImpl("I like Paris", context, 0, 12, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 7, 12);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a one token entity at the end of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity at the end of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleEnd() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("I like Natalie Portman");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("I like Natalie Portman", 0, 22);
    final Sentence sentence = new SentenceImpl("I like Natalie Portman", context, 0, 22, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 7, 22);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a multiple token entity at the end of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * an entity at the beginning of a sentence.
   */
  @Test
  public final void testBuildEntitiesStart() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("Paris is a nice city.");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("Paris is a nice city.", 0, 21);
    final Sentence sentence = new SentenceImpl("Paris is a nice city.", context, 0, 21, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 0, 5);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a one token entity at the beginning of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity at the beginning of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleStart() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("Natalie Portman is a beautiful girl.");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("Natalie Portman is a beautiful girl.", 0, 36);
    final Sentence sentence = new SentenceImpl("Natalie Portman is a beautiful girl.", context, 0,
        36, 1, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 0, 15);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a multiple token entity at the beginning of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * an entity in the middle of a sentence.
   */
  @Test
  public final void testBuildEntitiesMiddle() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("I like Paris very much.");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("I like Paris very much.", 0, 23);
    final Sentence sentence = new SentenceImpl("I like Paris very much.", context, 0, 23, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 7, 12);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a one token entity in the middle of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test {@link StanfordNlp#buildEntitiesFromSentence(CoreMap, Context, Sentence)} method with
   * a multiple token entity in the middle of a sentence.
   */
  @Test
  public final void testBuildEntitiesMultipleMiddle() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("I love Natalie Portman a lot.");

    pipeline.annotate(document);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    final Context context = new Context("I love Natalie Portman a lot.", 0, 29);
    final Sentence sentence = new SentenceImpl("I love Natalie Portman a lot.", context, 0, 29, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 7, 22);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(StanfordNlpTest.stanfordNlp, map, context, sentence);
    }

    Assert.assertTrue("Issue to build a multiple token entity in the middle of a sentence",
        sentence.entities().contains(entity));
  }

  /**
   * Test{@link StanfordNlp#toString()} method with
   * {@link StanfordNlp#StanfordNlp(PipelineConfiguration) constructor.
   */
  @Test
  public final void testToStringWithFalse() {
    final PipelineConfiguration pipeline = new PipelineConfiguration();
    final StanfordNlp stanford = new StanfordNlp(pipeline);

    pipeline.getNer().setApplyNumericClassifiers(true);
    pipeline.getNer().setUseSuTime(true);

    Assert.assertEquals("Issue to get the proper toString value", "StanfordNlp{}",
        stanford.toString());
    Assert.assertEquals("Issue to get the proper toString value", "StanfordNlp{}",
        new StanfordNlp(pipeline).toString());
  }
}
