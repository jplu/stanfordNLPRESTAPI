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

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

/**
 * @author Julien Plu
 */
public class TokenImplTest {
  static final Logger LOGGER = LoggerFactory.getLogger(TokenImplTest.class);
  
  public TokenImplTest() {
  }

  /**
   * Test RDF model of a Token with a next Token.
   */
  @Test
  public final void testRdfModelWithNextToken() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("favorite", "JJ", 3, 11, "favorite", NullToken.getInstance(),
        context, sentence, 2);

    token.nextToken(token2);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=0,2"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), RDF.type,
        ResourceFactory.createResource(nif + "Word"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "beginIndex"), ResourceFactory.createTypedLiteral(Integer.toString(0),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "endIndex"), ResourceFactory.createTypedLiteral(Integer.toString(2),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "anchorOf"), ResourceFactory.createPlainLiteral("My"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "sentence"), ResourceFactory.createResource(base + "char=" + sentence.start() + ','
        + sentence.end()));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "referenceContext"), ResourceFactory.createResource(base + "char=" + context.start() + ','
            + context.end()));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "posTag"), ResourceFactory.createPlainLiteral("PRP$"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "lemma"), ResourceFactory.createPlainLiteral("my"));
    model.add(ResourceFactory.createResource(base + "char=0,2"), ResourceFactory.createProperty(nif
        + "nextWord"), ResourceFactory.createResource(base + "char=" + token2.start() + ','
        + token2.end()));

    Assert.assertTrue("Issue to create the model for a Token with a next Token",
        model.isIsomorphicWith(token.rdfModel("stanfordnlp")));
  }

  /**
   * Test RDF model of a Token with a previous Token.
   */
  @Test
  public final void testRdfModelWithPreviousToken() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("favorite", "JJ", 3, 11, "favorite", token,
        context, sentence, 2);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=3,11"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), RDF.type,
        ResourceFactory.createResource(nif + "Word"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "beginIndex"), ResourceFactory.createTypedLiteral(Integer.toString(3),
        XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "endIndex"), ResourceFactory.createTypedLiteral(Integer.toString(11),
        XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "anchorOf"), ResourceFactory.createPlainLiteral("favorite"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "sentence"), ResourceFactory.createResource(base + "char=" + sentence.start() + ','
        + sentence.end()));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "referenceContext"), ResourceFactory.createResource(base + "char=" + context.start() + ','
        + context.end()));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "posTag"), ResourceFactory.createPlainLiteral("JJ"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "lemma"), ResourceFactory.createPlainLiteral("favorite"));
    model.add(ResourceFactory.createResource(base + "char=3,11"), ResourceFactory.createProperty(nif
        + "previousWord"), ResourceFactory.createResource(base + "char=" + token.start() + ','
        + token.end()));

    Assert.assertTrue("Issue to create the model for a Token with a previous Token",
        model.isIsomorphicWith(token2.rdfModel("stanfordnlp")));
  }
}
