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
import fr.eurecom.stanfordnlptonif.interfaces.Token;

import fr.eurecom.stanfordnlptonif.nullobjects.NullToken;

/**
 * This class represents a NIF token that is aligned to the corresponding Stanford NLP annotations.
 *
 * @author Julien Plu
 */
public class TokenImpl implements Token {
  static final Logger LOGGER = LoggerFactory.getLogger(TokenImpl.class);
  private final String text;
  private final String tag;
  private final int start;
  private final int end;
  private final String lemma;
  private final Token previousToken;
  private Token nextToken;
  private final Context context;
  private final Sentence sentence;
  private final int index;

  /**
   * TokenImpl constructor.
   *
   * @param newText          Text of the token.
   * @param newTag           Tag of the token.
   * @param newStart         The start offset of the token.
   * @param newEnd           The end offset of the token.
   * @param newLemma         Lemma of the token.
   * @param newPreviousToken Previous token in the sentence. Null token if none exists.
   * @param newContext       The context where the token is.
   * @param newSentence      The sentence where the token is.
   * @param newIndex         Index of the token in the sentence.
   */
  @CoberturaIgnore
  public TokenImpl(final String newText, final String newTag, final int newStart, final int newEnd,
                   final String newLemma, final Token newPreviousToken, final Context newContext,
                   final Sentence newSentence, final int newIndex) {
    this.text = newText;
    this.tag = newTag;
    this.start = newStart;
    this.end = newEnd;
    this.lemma = newLemma;
    this.previousToken = newPreviousToken;
    this.nextToken = NullToken.getInstance();
    this.context = newContext;
    this.sentence = newSentence;
    this.index = newIndex;
  }

  @Override
  @CoberturaIgnore
  public final void nextToken(final Token newNextToken) {
    if (this.nextToken.index() == -1) {
      this.nextToken = newNextToken;
    }
  }

  @Override
  @CoberturaIgnore
  public final int index() {
    return this.index;
  }

  @Override
  @CoberturaIgnore
  public final String text() {
    return this.text;
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
  public final Model rdfModel(final String tool) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = "http://127.0.0.1/" + tool + '#';
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Word"));
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
        ResourceFactory.createProperty(nif + "posTag"),
        ResourceFactory.createPlainLiteral(this.tag));
    model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "lemma"),
        ResourceFactory.createPlainLiteral(this.lemma));

    if (this.nextToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "nextWord"), ResourceFactory.createResource(base
              + "char=" + this.nextToken.start() + ',' + this.nextToken.end()));
    }

    if (this.previousToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "previousWord"), ResourceFactory.createResource(base
              + "char=" + this.previousToken.start() + ',' + this.previousToken.end()));
    }

    return model;
  }

  @Override
  @CoberturaIgnore
  public final String toString() {
    return "TokenImpl{"
        + "text='" + this.text + '\''
        + ", tag='" + this.tag + '\''
        + ", start=" + this.start
        + ", end=" + this.end
        + ", lemma='" + this.lemma + '\''
        + ", previousToken=" + this.previousToken.text()
        + ", nextToken=" + this.nextToken.text()
        + ", context=[" + this.context.start() + ',' + this.context.end() + ']'
        + ", sentence=" + this.sentence.index()
        + ", index=" + this.index
        + '}';
  }
}
