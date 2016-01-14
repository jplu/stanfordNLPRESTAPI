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

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;

import fr.eurecom.stanfordnlptonif.nullobjects.NullSentence;

/**
 * @author Julien Plu
 */
public class EntityTest {
  static final Logger LOGGER = LoggerFactory.getLogger(EntityTest.class);

  public EntityTest() {
  }

  /**
   * Test RDF model of an Entity.
   */
  @Test
  public final void testRdfModel() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/stanfordnlp#";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=24,39"),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        RDF.type, ResourceFactory.createResource(nif + "Phrase"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(24),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(39),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral("Natalie Portman"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(nif + "sentence"), ResourceFactory.createResource(base
            + "char=0,40"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "char=0,62"));
    model.add(ResourceFactory.createResource(base + "char=24,39"),
        ResourceFactory.createProperty(base + "type"),
        ResourceFactory.createPlainLiteral("PERSON"));

    Assert.assertTrue("Issue to create the model for an Entity",
        model.isIsomorphicWith(entity.rdfModel("stanfordnlp")));
  }
}
