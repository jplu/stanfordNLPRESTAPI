/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework to get
results in NIF format.
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
/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework
                            to get results in NIF format.
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

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;

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
public class EntityTest {
  static final Logger LOGGER = LoggerFactory.getLogger(EntityTest.class);

  public EntityTest() {
  }

  /**
   * Test {@link Entity#rdfModel(String)} method of an {@link Entity}.
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

  /**
   * Test {@link Entity#equals(Object)} method.
   */
  @Test
  public final void testEquals() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 60);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 2, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);
    final Entity entity2 = new Entity("Natalie Portman", "PERSON", sentence2, context, 24, 39);
    final Entity entity3 = new Entity("Natalie Portman", "PERSON", sentence, context2, 24, 39);
    final Entity entity4 = new Entity("Natalie Porman", "PERSON", sentence, context, 24, 39);
    final Entity entity5 = new Entity("Natalie Portman", "PERSN", sentence, context, 24, 39);
    final Entity entity6 = new Entity("Natalie Portman", "PERSON", sentence, context, 2, 39);
    final Entity entity7 = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 3);

    Assert.assertFalse("Issue with equals on the property sentence", entity.equals(entity2));
    Assert.assertFalse("Issue with equals on the property context", entity.equals(entity3));
    Assert.assertFalse("Issue with equals on the property text", entity.equals(entity4));
    Assert.assertFalse("Issue with equals on the property type", entity.equals(entity5));
    Assert.assertFalse("Issue with equals on the property start", entity.equals(entity6));
    Assert.assertFalse("Issue with equals on the property end", entity.equals(entity7));
    Assert.assertEquals("Issue with equals on the same object", entity, entity);
    Assert.assertFalse("Issue with equals on null", entity.equals(null));
    Assert.assertFalse("Issue with equals on different object", entity.equals(sentence));
  }

  /**
   * Test {@link Entity#hashCode()} method.
   */
  @Test
  public final void testHashCode() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);
    final Entity entity2 = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);

    Assert.assertNotSame("The two entities are the same", entity, entity2);
    Assert.assertEquals("Issue to compute the hascode of an entity", (long) entity.hashCode(),
        (long) entity2.hashCode());
  }

  /**
   * Test {@link Entity#toString()} method.
   */
  @Test
  public final void testToString() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Entity entity = new Entity("Natalie Portman", "PERSON", sentence, context, 24, 39);

    Assert.assertEquals("Issue to get the proper toString value", "Entity{text='Natalie Portman',"
        + " type='PERSON', sentence=1, context=[0,62], start=24, end=39}", entity.toString());
  }
}
