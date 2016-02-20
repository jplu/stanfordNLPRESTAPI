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
}
