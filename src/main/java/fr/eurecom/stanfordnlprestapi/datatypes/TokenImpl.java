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

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;
import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import org.apache.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * @param newPreviousToken Previous token in the sentence. Null token if none exists.
   * @param newContext       The context where the token is.
   * @param newSentence      The sentence where the token is.
   * @param newIndex         Index of the token in the sentence.
   */
  public TokenImpl(final String newText, final String newTag, final int newStart, final int newEnd,
                   final Token newPreviousToken, final Context newContext,
                   final Sentence newSentence, final int newIndex) {
    this.text = newText;
    this.tag = newTag;
    this.start = newStart;
    this.end = newEnd;
    this.previousToken = newPreviousToken;
    this.nextToken = NullToken.getInstance();
    this.context = newContext;
    this.sentence = newSentence;
    this.index = newIndex;
  }
  
  /**
   * TokenImpl constructor.
   *
   * @param newText          Text of the token.
   * @param newStart         The start offset of the token.
   * @param newEnd           The end offset of the token.
   * @param newPreviousToken Previous token in the sentence. Null token if none exists.
   * @param newContext       The context where the token is.
   * @param newSentence      The sentence where the token is.
   * @param newIndex         Index of the token in the sentence.
   */
  public TokenImpl(final String newText, final int newStart, final int newEnd,
                   final Token newPreviousToken, final Context newContext,
                   final Sentence newSentence, final int newIndex) {
    this.text = newText;
    this.tag = "";
    this.start = newStart;
    this.end = newEnd;
    this.previousToken = newPreviousToken;
    this.nextToken = NullToken.getInstance();
    this.context = newContext;
    this.sentence = newSentence;
    this.index = newIndex;
  }

  @Override
  public final void nextToken(final Token newNextToken) {
    if (this.nextToken.index() == -1) {
      this.nextToken = newNextToken;
    }
  }

  @Override
  public final int index() {
    return this.index;
  }

  @Override
  public final String text() {
    return this.text;
  }

  @Override
  public final int start() {
    return this.start;
  }

  @Override
  public final int end() {
    return this.end;
  }

  @Override
  public final Model rdfModel(final String tool, final NlpProcess process, final String host) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = host + '/' + tool;
    final Model model = ModelFactory.createDefaultModel();

    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Word"));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.start),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.end),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral(this.text));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "sentence"), ResourceFactory.createResource(base
            + "/sentence#char=" + this.sentence.start() + ',' + this.sentence.end()));
    model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "/context#char=" + this.context.start() + ','
            + this.context.end()));
    
    if (process == NlpProcess.POS) {
      model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "posTag"), ResourceFactory.createPlainLiteral(
              this.tag));
    }

    if (this.nextToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "nextWord"), ResourceFactory.createResource(base
              + "/token#char=" + this.nextToken.start() + ',' + this.nextToken.end()));
    }

    if (this.previousToken.index() != -1) {
      model.add(ResourceFactory.createResource(base + "/token#char=" + this.start + ',' + this.end),
          ResourceFactory.createProperty(nif + "previousWord"), ResourceFactory.createResource(base
              + "/token#char=" + this.previousToken.start() + ',' + this.previousToken.end()));
    }

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

    final TokenImpl token = (TokenImpl) obj;

    if (this.start != token.start) {
      return false;
    }

    if (this.end != token.end) {
      return false;
    }

    if (this.index != token.index) {
      return false;
    }

    if (!this.text.equals(token.text)) {
      return false;
    }

    if (!this.tag.equals(token.tag)) {
      return false;
    }

    if (!this.previousToken.equals(token.previousToken)) {
      return false;
    }

    if (this.nextToken.index() != token.nextToken.index()) {
      return false;
    }

    if (!this.context.equals(token.context)) {
      return false;
    }

    return this.sentence.index() == token.sentence.index();

  }

  @Override
  public final int hashCode() {
    int result = this.text.hashCode();

    result = 31 * (result + this.tag.hashCode());
    result = 31 * (result + this.start);
    result = 31 * (result + this.end);
    result = 31 * (result + this.previousToken.hashCode());
    result = 31 * (result + this.nextToken.hashCode());
    result = 31 * (result + this.context.hashCode());
    result = 31 * (result + this.sentence.hashCode());
    result = 31 * (result + this.index);

    return result;
  }

  @Override
  public final String toString() {
    return "TokenImpl{"
        + "text='" + this.text + '\''
        + ", tag='" + this.tag + '\''
        + ", start=" + this.start
        + ", end=" + this.end
        + ", previousToken=" + this.previousToken.text()
        + ", nextToken=" + this.nextToken.text()
        + ", context=[" + this.context.start() + ',' + this.context.end() + ']'
        + ", sentence=" + this.sentence.index()
        + ", index=" + this.index
        + '}';
  }
}
