package fr.eurecom.stanfordnlprestapi.datatypes;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;
import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;

import fr.eurecom.stanfordnlprestapi.interfaces.Token;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

/**
 * @author Julien Plu
 */
public class SentenceImplTest {
  static final Logger LOGGER = LoggerFactory.getLogger(SentenceImplTest.class);

  public SentenceImplTest() {
  }

  /**
   * Test RDF model of a Sentence with NER annotations and a next Sentence.
   */
  @Test
  public final void testRdfModelforNerWithNextSentence() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("She is very stunning.", context, 41, 62, 2,
        NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);

    sentence.addEntity(entity);
    sentence.nextSentence(sentence2);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=0,40"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=0,40"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=0,40"), RDF.type,
        ResourceFactory.createResource(nif + "Sentence"));
    model.add(ResourceFactory.createResource(base + "char=0,40"), ResourceFactory.createProperty(nif
        + "beginIndex"), ResourceFactory.createTypedLiteral("0",
        XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,40"), ResourceFactory.createProperty(nif
        + "endIndex"), ResourceFactory.createTypedLiteral("40",
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,40"), ResourceFactory.createProperty(nif
        + "referenceContext"), ResourceFactory.createResource(base + "char=" + context.start() + ','
            + context.end()));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createTypedLiteral("My favorite actress is: Natalie Portman."));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(base + "entity"), ResourceFactory.createResource(base
            + "char=" + entity.start() + ',' + entity.end()));
    model.add(entity.rdfModel("stanfordnlp"));
    model.add(ResourceFactory.createResource(base + "char=0,40"), ResourceFactory.createProperty(nif
        + "nextSentence"), ResourceFactory.createResource(base + "char=" + sentence2.start() + ','
        + sentence2.end()));

    Assert.assertTrue("Issue to create the model for a Sentence for NER with a next Sentence",
        model.isIsomorphicWith(sentence.rdfModel("stanfordnlp", NlpProcess.NER)));
  }

  /**
   * Test RDF model of a Sentence with a previous Sentence.
   */
  @Test
  public final void testRdfModelWitPreviousSentence() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("She is very stunning.", context, 41, 62, 2,
        sentence);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=41,62"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=41,62"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=41,62"), RDF.type,
        ResourceFactory.createResource(nif + "Sentence"));
    model.add(ResourceFactory.createResource(base + "char=41,62"),
        ResourceFactory.createProperty(nif + "beginIndex"), ResourceFactory.createTypedLiteral("41",
        XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=41,62"),
        ResourceFactory.createProperty(nif + "endIndex"), ResourceFactory.createTypedLiteral("62",
        XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=41,62"),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "char=" + context.start() + ',' + context.end()));
    model.add(ResourceFactory.createResource(base + "char=41,62"),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createTypedLiteral("She is very stunning."));
    model.add(ResourceFactory.createResource(base + "char=41,62"),
        ResourceFactory.createProperty(nif + "previousSentence"),
        ResourceFactory.createResource(base + "char=" + sentence.start() + ',' + sentence.end()));

    Assert.assertTrue("Issue to create the model for a Sentence with a previous Sentence",
        model.isIsomorphicWith(sentence2.rdfModel("stanfordnlp", NlpProcess.NER)));
  }

  /**
   * Test RDF model of a sentence with POS annotations.
   */
  @Test
  public final void testRdfModelforPos() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);

    sentence.addToken(token);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=0,40"),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        RDF.type, ResourceFactory.createResource(nif + "Sentence"));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral("0",
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral("40",
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "firstToken"),
        ResourceFactory.createResource(base + "char=" + token.start() + ','
            + token.end()));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "lastToken"),
        ResourceFactory.createResource(base + "char=" + token.start() + ','
            + token.end()));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "char=" + context.start() + ','
            + context.end()));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createTypedLiteral("My favorite actress is: Natalie Portman."));
    model.add(ResourceFactory.createResource(base + "char=0,40"),
        ResourceFactory.createProperty(nif + "word"), ResourceFactory.createResource(base
            + "char=" + token.start() + ',' + token.end()));
    model.add(token.rdfModel("stanfordnlp"));

    Assert.assertTrue("Issue to create the model for a Sentence for POS",
        model.isIsomorphicWith(sentence.rdfModel("stanfordnlp", NlpProcess.POS)));
  }
}
