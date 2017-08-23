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
 * @author Julien Plu
 */
public class Coref {
  static final Logger LOGGER = LoggerFactory.getLogger(Coref.class);
  private final String coreference;
  private final String head;
  private final int startHead;
  private final int endHead;
  private final int start;
  private final int end;
  private final Sentence sentence;
  private final Context context;
  
  /**
   * Coref constructor.
   *
   * @param newCoreference    Text of the coreference.
   * @param newHead           Text of the head.
   * @param newStart          The start offset of the coreference.
   * @param newEnd            The end offset of the coreference.
   * @param newStartHead      The start offset of the head of the coreference.
   * @param newEndHead        The end offset of the head of the coreference.
   * @param newSentence       The sentence where the coreference is.
   * @param newContext        The context where the coreference is.
   */
  public Coref(final String newCoreference, final String newHead, final int newStart,
               final int newEnd, final int newStartHead, final int newEndHead,
               final Sentence newSentence, final Context newContext) {
    this.coreference = newCoreference;
    this.head = newHead;
    this.start = newStart;
    this.end = newEnd;
    this.sentence = newSentence;
    this.context = newContext;
    this.startHead = newStartHead;
    this.endHead = newEndHead;
  }
  
  /**
   * Turn the coref into RDF model.
   *
   * @param tool Tool used to extract the coref.
   * @param host Host from where comes from the request.
   *
   * @return RDF model in NIF of the coref.
   */
  public final Model rdfModel(final String tool, final String host) {
    final String nif = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
    final String base = host + '/' + tool;
    final Model model = ModelFactory.createDefaultModel();
  
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        RDF.type, ResourceFactory.createResource(nif + "Phrase"));
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.startHead),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.endHead),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral(this.head));
    
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "String"));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "RFC5147String"));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        RDF.type, ResourceFactory.createResource(nif + "Phrase"));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "beginIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.start),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "endIndex"),
        ResourceFactory.createTypedLiteral(Integer.toString(this.end),
            XSDDatatype.XSDnonNegativeInteger));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "anchorOf"),
        ResourceFactory.createPlainLiteral(this.coreference));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "sentence"), ResourceFactory.createResource(base
            + "/sentence#char=" + this.sentence.start() + ',' + this.sentence.end()));
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(nif + "referenceContext"),
        ResourceFactory.createResource(base + "/coref#char=" + this.context.start() + ','
            + this.context.end()));
    
    final String local = base + "/ontology/";
    
    model.add(ResourceFactory.createResource(base + "/coref#char=" + this.start + ',' + this.end),
        ResourceFactory.createProperty(local + "head"),
        ResourceFactory.createResource(base + "/head#char=" + this.startHead + ','
            + this.endHead));
    
    return model;
  }
  
  public final int start() {
    return this.start;
  }
  
  public final int end() {
    return this.end;
  }
  
  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    
    final Coref coref1 = (Coref) obj;
    
    if (this.start != coref1.start) {
      return false;
    }
  
    if (this.end != coref1.end) {
      return false;
    }
  
    if (this.startHead != coref1.startHead) {
      return false;
    }
  
    if (this.endHead != coref1.endHead) {
      return false;
    }
    
    if (!this.coreference.equals(coref1.coreference)) {
      return false;
    }
    
    if (!this.head.equals(coref1.head)) {
      return false;
    }
    
    if (!this.sentence.equals(coref1.sentence)) {
      return false;
    }
  
    return this.context.equals(coref1.context);
  }
  
  @Override
  public final int hashCode() {
    int result = this.coreference.hashCode();
  
    result = 31 * (result + this.head.hashCode());
    result = 31 * (result + this.start);
    result = 31 * (result + this.end);
    result = 31 * (result + this.startHead);
    result = 31 * (result + this.endHead);
    result = 31 * (result + this.sentence.hashCode());
    result = 31 * (result + this.context.hashCode());
    
    return result;
  }
  
  @Override
  public final String toString() {
    return "Coref{"
        + "coreference='" + this.coreference + '\''
        + ", head='" + this.head + '\''
        + ", start=" + this.start
        + ", end=" + this.end
        + ", startHead=" + this.startHead
        + ", endHead=" + this.endHead
        + ", sentence=" + this.sentence.index()
        + ", context=[" + this.context.start() + ',' + this.context.end() + ']'
        + '}';
  }
}
