package fr.eurecom.stanfordnlptonif.datatypes;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlptonif.annotations.CoberturaIgnore;

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;

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

  @CoberturaIgnore
  public final int start() {
    return this.start;
  }

  @CoberturaIgnore
  public final int end() {
    return this.end;
  }

  /**
   * Turn the entity into RDF model.
   *
   * @param tool Tool used to extract the entity.
   * @return RDF model in NIF of the entity.
   */
  public final Model rdfModel(final String tool) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/" + tool + '#';
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Phrase"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.start),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.end),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral(this.text));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "sentence"), ResourceFactory.createResource(base
            + "char=" + this.sentence.start() + ',' + this.sentence.end()));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "char=" + this.context.start() + ','
            + this.context.end()));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(base + "type"),
        ResourceFactory.createPlainLiteral(this.type));

    return model;
  }

  @Override
  @CoberturaIgnore
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if ((obj == null) || (this.getClass() != obj.getClass())) {
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

    if (!this.sentence.equals(entity.sentence)) {
      return false;
    }

    return this.context.equals(entity.context);
  }

  @Override
  @CoberturaIgnore
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
  @CoberturaIgnore
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
