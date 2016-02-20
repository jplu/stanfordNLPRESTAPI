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

import java.util.HashMap;
import java.util.Map;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;

/**
 * @author Julien Plu
 */
public class ContextTest {
  static final Logger LOGGER = LoggerFactory.getLogger(ContextTest.class);

  public ContextTest() {
  }

  /**
   * Test {@link Context#rdfModel(String, NlpProcess)} method for POS.
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

    final String base = "http://127.0.0.1/stanfordnlp#";

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
        model.isIsomorphicWith(context.rdfModel("stanfordnlp", NlpProcess.POS)));
  }

  /**
   * Test {@link Context#rdfModel(String, NlpProcess)} method for NER.
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

    final String base = "http://127.0.0.1/stanfordnlp#";

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
        model.isIsomorphicWith(context.rdfModel("stanfordnlp", NlpProcess.NER)));
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
}
