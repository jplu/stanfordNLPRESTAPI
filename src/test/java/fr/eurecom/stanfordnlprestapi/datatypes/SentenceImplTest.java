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

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import fr.eurecom.stanfordnlprestapi.exceptions.InexistentNlpProcessException;

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
public class SentenceImplTest {
  static final Logger LOGGER = LoggerFactory.getLogger(SentenceImplTest.class);

  public SentenceImplTest() {
  }

  /**
   * Test the {@link SentenceImpl#rdfModel(String, NlpProcess)} method with a {@link Sentence} that
   * has a NER annotation and a next {@link Sentence}.
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
   * Test the {@link SentenceImpl#rdfModel(String, NlpProcess)} method with a {@link Sentence} that
   * has a previous {@link Sentence} without NER or POS annotations.
   */
  @Test
  public final void testRdfModelWitPreviousSentenceWithoutAnnotations() {
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
   * Test the {@link SentenceImpl#rdfModel(String, NlpProcess)} method with a {@link Sentence} that
   * has a POS annotation.
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

  /**
   * Test the {@link SentenceImpl#toString()} method.
   */
  @Test
  public final void testToString() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());

    Assert.assertEquals("Issue to get the proper toString value", "SentenceImpl{text='My favorite "
        + "actress is: Natalie Portman.', context=[0,62], tokens=[], entities=[], nextSentence=-1, "
        + "previousSentence=-1, firstToken=null, lastToken=null, start=0, end=40, index=1}",
        sentence.toString());
  }

  /**
   * Test {@link SentenceImpl#nextSentence(Sentence)} method to check the impossibility to change
   * the next sentence if it has already been set.
   */
  @Test
  public final void testNextSentence() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence tmpSentence = new SentenceImpl("My favorite actress is: Natalie Portman.",
        context, 0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("She is very stunning.", context, 41, 62, 2,
        sentence);

    sentence.nextSentence(sentence2);
    tmpSentence.nextSentence(sentence2);

    final Sentence sentence3 = new SentenceImpl("I love Natalie Portman.", context, 21, 44, 3,
        sentence);

    sentence.nextSentence(sentence3);

    Assert.assertEquals("The next sentence property has changed", sentence.toString(),
        tmpSentence.toString());
  }

  /**
   * Test that {@link SentenceImpl#rdfModel(String, NlpProcess)} raise properly the
   * {@link InexistentNlpProcessException} exception in case of inexistent NLP process.
   */
  @Test(expected = InexistentNlpProcessException.class)
  public final void testInexistentNlpProcess() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());

    sentence.rdfModel("stanfordnlp", null);
  }

  /**
   * Test {@link SentenceImpl#hashCode()} method.
   */
  @Test
  public final void testHashCode() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());

    Assert.assertNotSame("The two sentences are the same", sentence, sentence2);
    Assert.assertEquals("Issue to compute the hascode of a sentence", (long) sentence.hashCode(),
        (long) sentence2.hashCode());
  }

  /**
   * Test {@link SentenceImpl#equals(Object)} method.
   */
  @Test
  public final void testEquals() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 63);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("My favorite actress: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence3 = new SentenceImpl("My favorite actress is: Natalie Portman.",
        context2, 0, 40, 1, NullSentence.getInstance());
    final Sentence sentence4 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence5 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence6 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence7 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, new SentenceImpl("Paris is a nice city.", context, 0, 21, 0,
        NullSentence.getInstance()));
    final Sentence sentence8 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        1, 40, 1, NullSentence.getInstance());
    final Sentence sentence9 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 44, 1, NullSentence.getInstance());
    final Sentence sentence10 = new SentenceImpl("My favorite actress is: Natalie Portman.",
        context, 0, 40, 2, NullSentence.getInstance());

    sentence4.addToken(new TokenImpl("like", "VBP", 2, 6, "like", NullToken.getInstance(), context,
        sentence, 2));
    sentence5.addEntity(new Entity("Natalie Portman", "PERSON", sentence, context, 7, 22));
    sentence6.nextSentence(new SentenceImpl("Paris is a nice city.", context, 0, 21, 0,
        NullSentence.getInstance()));

    Assert.assertFalse("Issue with equals on the property text", sentence.equals(sentence2));
    Assert.assertFalse("Issue with equals on the property context", sentence.equals(sentence3));
    Assert.assertFalse("Issue with equals on the property tokens", sentence.equals(sentence4));
    Assert.assertFalse("Issue with equals on the property entities", sentence.equals(sentence5));
    Assert.assertFalse("Issue with equals on the property nextSentence", sentence.equals(
        sentence6));
    Assert.assertFalse("Issue with equals on the property previousSentence", sentence.equals(
        sentence7));
    Assert.assertFalse("Issue with equals on the property start", sentence.equals(sentence8));
    Assert.assertFalse("Issue with equals on the property end", sentence.equals(sentence9));
    Assert.assertFalse("Issue with equals on the property index", sentence.equals(sentence10));
    Assert.assertEquals("Issue with equals on the same object", sentence, sentence);
    Assert.assertFalse("Issue with equals on null", sentence.equals(null));
    Assert.assertFalse("Issue with equals on different object", sentence.equals(context));
  }
}
