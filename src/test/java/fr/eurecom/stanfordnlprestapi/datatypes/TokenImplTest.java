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
package fr.eurecom.stanfordnlprestapi.datatypes;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class TokenImplTest {
  static final Logger LOGGER = LoggerFactory.getLogger(TokenImplTest.class);
  
  public TokenImplTest() {
  }

  /**
   * Test {@link TokenImpl#rdfModel(String)} method of a {@link Token} with a next {@link Token}.
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
   * Test {@link TokenImpl#rdfModel(String)} method of a {@link Token} with a previous
   * {@link Token}.
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


  /**
   * Test {@link TokenImpl#nextToken(Token)} method to check the impossibility to change the
   * next token if it has already been set.
   */
  @Test
  public final void testNextToken() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);
    final Token tmpToken = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("favorite", "JJ", 3, 11, "favorite", token, context,
        sentence, 2);

    token.nextToken(token2);
    tmpToken.nextToken(token2);

    final Token token3 = new TokenImpl("actress", "NN", 12, 19, "actress", token, context,
        sentence, 2);

    token.nextToken(token3);

    Assert.assertEquals("The next token property has changed", token.toString(),
        tmpToken.toString());
  }

  /**
   * Test {@link TokenImpl#toString()} method.
   */
  @Test
  public final void testToString() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);

    Assert.assertEquals("Issue to get the proper toString value", "TokenImpl{text='My', tag='PRP$',"
        + " start=0, end=2, lemma='my', previousToken=null, nextToken=null, context=[0,62], "
        + "sentence=1, index=1}",
        token.toString());
  }

  /**
   * Test {@link TokenImpl#text()} method.
   */
  @Test
  public final void testText() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token = new TokenImpl("My", "PRP$", 0, 2, "my", NullToken.getInstance(), context,
        sentence, 1);

    Assert.assertEquals("Issue to get the proper text from a Token", "My", token.text());
  }

  /**
   * Test {@link TokenImpl#hashCode()} method.
   */
  @Test
  public final void testHashCode() {
    final Context context = new Context("I like Natalie Portman", 0, 22);
    final Sentence sentence = new SentenceImpl("I like Natalie Portman", context, 0, 22, 0,
        NullSentence.getInstance());
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);

    Assert.assertNotSame("The two sentences are the same", token1, token2);
    Assert.assertEquals("Issue to compute the hascode of a sentence", (long) token1.hashCode(),
        (long) token2.hashCode());
  }

  /**
   * Test {@link TokenImpl#equals(Object)} method.
   */
  @Test
  public final void testEquals() {
    final Context context = new Context("I like Natalie Portman", 0, 22);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("I like Natalie Portman", context, 0, 22, 0,
        NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Token token1 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token2 = new TokenImpl("A", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token3 = new TokenImpl("I", "PRP", 1, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token4 = new TokenImpl("I", "VBZ", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token5 = new TokenImpl("I", "PRP", 0, 2, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token6 = new TokenImpl("I", "PRP", 0, 1, "A", NullToken.getInstance(), context,
        sentence, 1);
    final Token token7 = new TokenImpl("I", "PRP", 0, 1, "I", new TokenImpl("like", "VBP", 2, 6,
        "like", NullToken.getInstance(), context, sentence, 2), context, sentence, 1);
    final Token token8 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);
    final Token token9 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context2,
        sentence, 1);
    final Token token10 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence2, 1);
    final Token token11 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 2);
    final Token token12 = new TokenImpl("I", "PRP", 0, 1, "I", NullToken.getInstance(), context,
        sentence, 1);

    token8.nextToken(new TokenImpl("like", "VBP", 2, 6, "like", NullToken.getInstance(), context,
        sentence, 2));

    Assert.assertFalse("Issue with equals on the property text", token1.equals(token2));
    Assert.assertFalse("Issue with equals on the property start", token1.equals(token3));
    Assert.assertFalse("Issue with equals on the property tag", token1.equals(token4));
    Assert.assertFalse("Issue with equals on the property end", token1.equals(token5));
    Assert.assertFalse("Issue with equals on the property lemma", token1.equals(token6));
    Assert.assertFalse("Issue with equals on the property previousToken", token1.equals(token7));
    Assert.assertFalse("Issue with equals on the property nextToken", token1.equals(token8));
    Assert.assertFalse("Issue with equals on the property context", token1.equals(token9));
    Assert.assertFalse("Issue with equals on the property sentence", token1.equals(token10));
    Assert.assertFalse("Issue with equals on the property index", token1.equals(token11));
    Assert.assertEquals("Issue with equals", token1, token12);
    Assert.assertEquals("Issue with equals on the same object", token1, token1);
    Assert.assertFalse("Issue with equals on null", token1.equals(null));
    Assert.assertFalse("Issue with equals on different object", token1.equals(context));
  }
}
