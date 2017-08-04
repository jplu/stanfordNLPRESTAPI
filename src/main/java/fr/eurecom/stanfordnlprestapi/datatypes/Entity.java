/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
 * Copyright © 2017 Julien Plu (julien.plu@redaction-developpez.com)
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

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a NIF Entity that is aligned to the corresponding Stanford NLP
 * annotations.
 *
 * @author Julien Plu
 */
public class Entity {
  static final Logger LOGGER = LoggerFactory.getLogger(Entity.class);
  private final String text;
  private final String type;
  private final Sentence sentence;
  private final Context context;
  private final int start;
  private final int end;

  /**
   * Entity constructor.
   *
   * @param newText     Text of the entity.
   * @param newType     Type of the entity.
   * @param newSentence The sentence where the entity is.
   * @param newContext  The context where the entity is.
   * @param newStart    The start offset of the entity.
   * @param newEnd      The end offset of the entity.
   */
  public Entity(final String newText, final String newType, final Sentence newSentence,
                final Context newContext, final int newStart, final int newEnd) {
    this.text = newText;
    this.type = newType;
    this.sentence = newSentence;
    this.context = newContext;
    this.start = newStart;
    this.end = newEnd;
  }

  public final int start() {
    return this.start;
  }

  public final int end() {
    return this.end;
  }

  /**
   * Turn the entity into RDF model.
   *
   * @param tool Tool used to extract the entity.
   * @param host Host from where comes from the request.
   *
   * @return RDF model in NIF of the entity.
   */
  public final Model rdfModel(final String tool, final String host) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = host + '/' + tool;
    final String local = base + "/ontology/";
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Phrase"));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.start),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.end),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral(this.text));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "sentence"), ResourceFactory.createResource(base
            + "/sentence#char=" + this.sentence.start() + ',' + this.sentence.end()));
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "/context#char=" + this.context.start() + ','
            + this.context.end()));
    System.out.println(this.text + " " + this.type);
    model.add(ResourceFactory.createResource(base + "/entity#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(local + "type"),
        ResourceFactory.createPlainLiteral(this.type));

    return model;
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }

    final Entity entity = (Entity) obj;

    if (this.start != entity.start) {
      return false;
    }

    if (this.end != entity.end) {
      return false;
    }

    if (!this.text.equals(entity.text)) {
      return false;
    }

    if (!this.type.equals(entity.type)) {
      return false;
    }

    if (this.sentence.index() != entity.sentence.index()) {
      return false;
    }

    return this.context.equals(entity.context);
  }

  @Override
  public final int hashCode() {
    int result = this.text.hashCode();

    result = 31 * (result + this.type.hashCode());
    result = 31 * (result + this.sentence.hashCode());
    result = 31 * (result + this.context.hashCode());
    result = 31 * (result + this.start);
    result = 31 * (result + this.end);

    return result;
  }

  @Override
  public final String toString() {
    return "Entity{"
        + "text='" + this.text + '\''
        + ", type='" + this.type + '\''
        + ", sentence=" + this.sentence.index()
        + ", context=[" + this.context.start() + ',' + this.context.end() + ']'
        + ", start=" + this.start
        + ", end=" + this.end
        + '}';
  }
}
