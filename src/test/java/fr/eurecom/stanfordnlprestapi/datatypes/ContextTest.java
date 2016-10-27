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

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;

import java.util.HashMap;
import java.util.Map;

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
public class ContextTest {
  static final Logger LOGGER = LoggerFactory.getLogger(ContextTest.class);

  public ContextTest() {
  }

  /**
   * Test {@link Context#rdfModel(String, NlpProcess, String)} method for POS.
   */
  @Test
  public final void testPosRdfModel() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = NullSentence.getInstance();
    final Model model = ModelFactory.createDefaultModel();

    context.addSentence(sentence);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final Map<String, String> prefixes = new HashMap<>();

    prefixes.put("nif", nif);

    final String base = "http://127.0.0.1/stanfordnlp/context#";

    prefixes.put("local", base);
    prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

    model.setNsPrefixes(prefixes);

    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "Context"));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral("0", XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "endIndex"), ResourceFactory.createTypedLiteral("62",
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "isString"),
        ResourceFactory.createTypedLiteral("My favorite actress is: Natalie Portman. She is very "
            + "stunning."));

    Assert.assertTrue("Issue to create the model for a Context",
        model.isIsomorphicWith(context.rdfModel("stanfordnlp", NlpProcess.POS,
            "http://127.0.0.1")));
  }

  /**
   * Test {@link Context#rdfModel(String, NlpProcess, String)} method for NER.
   */
  @Test
  public final void testNerRdfModel() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = NullSentence.getInstance();
    final Model model = ModelFactory.createDefaultModel();

    context.addSentence(sentence);

    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final Map<String, String> prefixes = new HashMap<>();

    prefixes.put("nif", nif);

    final String base = "http://127.0.0.1/stanfordnlp/context#";

    prefixes.put("local", base);
    prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

    model.setNsPrefixes(prefixes);

    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=0,62"), RDF.type,
        ResourceFactory.createResource(nif + "Context"));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral("0", XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "endIndex"), ResourceFactory.createTypedLiteral("62",
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=0,62"),
        ResourceFactory.createProperty(nif + "isString"),
        ResourceFactory.createTypedLiteral("My favorite actress is: Natalie Portman. She is very "
            + "stunning."));

    Assert.assertTrue("Issue to create the model for a Context",
        model.isIsomorphicWith(context.rdfModel("stanfordnlp", NlpProcess.NER,
            "http://127.0.0.1")));
  }

  /**
   * Test {@link Context#toString()} method.
   */
  @Test
  public final void testToString() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);

    Assert.assertEquals("Issue to get the proper toString value", "Context{text='My favorite"
        + " actress is: Natalie Portman. She is very stunning.', start=0, end=62, sentences=[]}",
        context.toString());
  }

  /**
   * Test {@link Context#text()} method.
   */
  @Test
  public final void testText() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);

    Assert.assertEquals("Issue to get the proper text from a Context", "My favorite actress is:"
        + " Natalie Portman. She is very stunning.", context.text());
  }

  /**
   * Test {@link Context#hashCode()} method.
   */
  @Test
  public final void testHashCode() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);

    Assert.assertNotSame("The two context are the same", context, context2);
    Assert.assertEquals("Issue to compute the hascode of a context", (long) context.hashCode(),
        (long) context2.hashCode());
  }

  /**
   * Test {@link Context#equals(Object)} method.
   */
  @Test
  public final void testEquals() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context3 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 1, 60);
    final Context context4 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 64);
    final Context context5 = new Context("My favorite actress: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());

    context2.addSentence(sentence);

    Assert.assertFalse("Issue with equals on the property sentences", context.equals(context2));
    Assert.assertFalse("Issue with equals on the property start", context.equals(context3));
    Assert.assertFalse("Issue with equals on the property end", context.equals(context4));
    Assert.assertFalse("Issue with equals on the property text", context.equals(context5));
    Assert.assertEquals("Issue with equals on the same object", context, context);
    Assert.assertFalse("Issue with equals on null", context.equals(null));
    Assert.assertFalse("Issue with equals on different object", context.equals(sentence));
  }
}
