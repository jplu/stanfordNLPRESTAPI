package fr.eurecom.stanfordnlptonif.datatypes;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.eurecom.stanfordnlptonif.annotations.CoberturaIgnore;

import fr.eurecom.stanfordnlptonif.enums.NlpProcess;

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;
import fr.eurecom.stanfordnlptonif.interfaces.Token;

import fr.eurecom.stanfordnlptonif.nullobjects.NullSentence;
import fr.eurecom.stanfordnlptonif.nullobjects.NullToken;

/**
 * This class represents a NIF sentence that is aligned to the corresponding Stanford NLP
 * annotations.
 *
 * @author Julien Plu
 */
public class SentenceImpl implements Sentence {
  static final Logger LOGGER = LoggerFactory.getLogger(SentenceImpl.class);
  private final String text;
  private final Context context;
  private final List<Token> tokens;
  private final List<Entity> entities;
  private Sentence nextSentence;
  private final Sentence previousSentence;
  private Token firstToken;
  private Token lastToken;
  private final int start;
  private final int end;
  private final int index;

  /**
   * SentenceImpl constructor.
   *
   * @param newText             Text of the sentence.
   * @param newContext          The context where the sentence is.
   * @param newStart            The start offset of the sentence.
   * @param newEnd              The end offset of the entity.
   * @param newIndex            Index of the sentence in the context.
   * @param newPreviousSentence Previous sentence in the context. Null sentence object if none
   *                            exists.
   */
  @CoberturaIgnore
  public SentenceImpl(final String newText, final Context newContext, final int newStart,
                      final int newEnd, final int newIndex, final Sentence newPreviousSentence) {
    this.tokens = new ArrayList<>();
    this.entities = new ArrayList<>();
    this.nextSentence = NullSentence.getInstance();
    this.previousSentence = newPreviousSentence;
    this.text = newText;
    this.context = newContext;
    this.start = newStart;
    this.end = newEnd;
    this.index = newIndex;
    this.firstToken = NullToken.getInstance();
    this.lastToken = NullToken.getInstance();
  }

  @Override
  @CoberturaIgnore
  public final void addToken(final Token newToken) {
    if (this.tokens.isEmpty()) {
      this.firstToken = newToken;
    }

    this.tokens.add(newToken);
    this.lastToken = newToken;
  }

  @Override
  @CoberturaIgnore
  public final void addEntity(final Entity newEntity) {
    this.entities.add(newEntity);
  }

  @Override
  @CoberturaIgnore
  public final void nextSentence(final Sentence newNextSentence) {
    if (this.nextSentence.index() == -1) {
      this.nextSentence = newNextSentence;
    }
  }

  @Override
  @CoberturaIgnore
  public final List<Entity> entities() {
    return Collections.unmodifiableList(this.entities);
  }

  @Override
  @CoberturaIgnore
  public final int index() {
    return this.index;
  }

  @Override
  @CoberturaIgnore
  public final int start() {
    return this.start;
  }

  @Override
  @CoberturaIgnore
  public final int end() {
    return this.end;
  }

  @Override
  public final Model rdfModel(final String tool, final NlpProcess process) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/" + tool + '#';
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Sentence"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.start),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.end),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "char=" + this.context.start() + ','
            + this.context.end()));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createTypedLiteral(this.text));

    if (process == NlpProcess.POS) {
      for (final Token token : this.tokens) {
        model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
            ResourceFactory.createProperty(nif + "word"), ResourceFactory.createResource(base
                + "char=" + token.start() + ',' + token.end()));
        model.add(token.rdfModel(tool));
      }
    } else if (process == NlpProcess.NER) {
      for (final Entity entity : this.entities) {
        model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
            ResourceFactory.createProperty(base + "entity"), ResourceFactory.createResource(base
                + "char=" + entity.start() + ',' + entity.end()));
        model.add(entity.rdfModel(tool));
      }
    }

    if (this.nextSentence.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "nextSentence"), ResourceFactory.createResource(base
              + "char=" + this.nextSentence.start() + ',' + this.nextSentence.end()));
    }

    if (this.previousSentence.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "previousSentence"),
          ResourceFactory.createResource(base + "char=" + this.previousSentence.start() + ','
              + this.previousSentence.end()));
    }

    if (this.firstToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "firstToken"),
          ResourceFactory.createResource(base + "char=" + this.firstToken.start() + ','
              + this.firstToken.end()));
    }

    if (this.lastToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "lastToken"),
          ResourceFactory.createResource(base + "char=" + this.lastToken.start() + ','
              + this.lastToken.end()));
    }

    return model;
  }

  @Override
  @CoberturaIgnore
  public final String toString() {
    return "SentenceImpl{"
        + "text='" + this.text + '\''
        + ", context=[" + this.context.start() + ',' + this.context.end() + ']'
        + ", tokens=" + this.tokens
        + ", entities=" + this.entities
        + ", nextSentence=" + this.nextSentence.index()
        + ", previousSentence=" + this.previousSentence.index()
        + ", firstToken=" + this.firstToken.text()
        + ", lastToken=" + this.lastToken.text()
        + ", start=" + this.start
        + ", end=" + this.end
        + ", index=" + this.index
        + '}';
  }
}
