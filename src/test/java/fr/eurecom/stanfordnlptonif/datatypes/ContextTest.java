package fr.eurecom.stanfordnlptonif.datatypes;

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

import fr.eurecom.stanfordnlptonif.enums.NlpProcess;

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;

import fr.eurecom.stanfordnlptonif.nullobjects.NullSentence;

/**
 * @author Julien Plu
 */
public class ContextTest {
  static final Logger LOGGER = LoggerFactory.getLogger(ContextTest.class);

  public ContextTest() {
  }

  /**
   * Test RDF model of a Context.
   */
  @Test
  public final void testRdfModel() {
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
}
