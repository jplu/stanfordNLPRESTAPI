package fr.eurecom.stanfordnlptonif.core;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import java.nio.file.FileSystems;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlptonif.datatypes.Context;
import fr.eurecom.stanfordnlptonif.datatypes.Entity;
import fr.eurecom.stanfordnlptonif.datatypes.SentenceImpl;

import fr.eurecom.stanfordnlptonif.enums.NlpProcess;

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;

import fr.eurecom.stanfordnlptonif.nullobjects.NullSentence;

/**
 * @author Julien Plu on 08/01/2016.
 */
public class StanfordNlpTest {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlpTest.class);

  private static StanfordNlp stanfordNlp;

  public StanfordNlpTest() {
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
    stanfordNlp = new StanfordNlp(props);
  }

  /**
   * Test the full application for POS process.
   */
  @Test
  public final void testRunWithPos() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();

    final String text = "My favorite actress is: Natalie Portman. She is very stunning.";

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "pos.ttl"), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF model of a context for POS",
        fileModel.isIsomorphicWith(stanfordNlp.run(text).rdfModel("stanfordnlp", NlpProcess.POS)));
  }

  /**
   * Test the full application for the NER process.
   */
  @Test
  public final void testRunWithNer() throws Exception {
    final Model fileModel = ModelFactory.createDefaultModel();
    final String text = "My favorite actress is: Natalie Portman. She "
        + "is very stunning.";

    RDFDataMgr.read(fileModel, this.getClass().getResourceAsStream(
        FileSystems.getDefault().getSeparator() + "ner.ttl"), Lang.TURTLE);

    Assert.assertTrue("Issue to get the proper full RDF model of a context for NER",
        fileModel.isIsomorphicWith(stanfordNlp.run(text).rdfModel("stanfordnlp", NlpProcess.NER)));
  }

  /**
   * Test Entity building.
   */
  @Test
  public final void testBuildEntities() throws Exception {
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final Annotation document = new Annotation("I like Paris");
    final Annotation document2 = new Annotation("Paris is a nice city.");
    final Annotation document3 = new Annotation("I like Natalie Portman");

    pipeline.annotate(document);
    pipeline.annotate(document2);
    pipeline.annotate(document3);

    final Class[] cArg = {CoreMap.class, Context.class, Sentence.class};
    final Method method = StanfordNlp.class.getDeclaredMethod("buildEntitiesFromSentence", cArg);
    //final StanfordNlp stanfordNlp = new StanfordNlp("");
    final Context context = new Context("I like Paris", 0, 12);
    final Context context2 = new Context("Paris is a nice city.", 0, 21);
    final Context context3 = new Context("I like Natalie Portman", 0, 22);
    final Sentence sentence = new SentenceImpl("I like Paris", context, 0, 12, 1,
        NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("Paris is a nice city.", context2, 0, 21, 1,
        NullSentence.getInstance());
    final Sentence sentence3 = new SentenceImpl("I like Natalie Portman", context2, 0, 22, 1,
        NullSentence.getInstance());
    final Entity entity = new Entity("Paris", "LOCATION", sentence, context, 7, 12);
    final Entity entity2 = new Entity("Paris", "LOCATION", sentence2, context2, 0, 5);
    final Entity entity3 = new Entity("Natalie Portman", "PERSON", sentence3, context3, 7, 22);

    method.setAccessible(true);

    for (final CoreMap map : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(stanfordNlp, map, context, sentence);
    }

    for (final CoreMap map : document2.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(stanfordNlp, map, context2, sentence2);
    }

    for (final CoreMap map : document3.get(CoreAnnotations.SentencesAnnotation.class)) {
      method.invoke(stanfordNlp, map, context3, sentence3);
    }

    Assert.assertTrue("Issue to build a one token entity at the end of a sentence",
        sentence.entities().contains(entity));
    Assert.assertTrue("Issue to build a one token entity at the beginning of a sentence",
        sentence2.entities().contains(entity2));
    Assert.assertTrue("Issue to build a multiple token entity at the end of a sentence",
        sentence3.entities().contains(entity3));
  }
}
