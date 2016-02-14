package fr.eurecom.stanfordnlprestapi.nullobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import fr.eurecom.stanfordnlprestapi.datatypes.Entity;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

/**
 * Null object that represents a null sentence.
 *
 * @author Julien Plu
 */
public final class NullSentence implements Sentence {
  static final Logger LOGGER = LoggerFactory.getLogger(NullSentence.class);
  private static final NullSentence INSTANCE = new NullSentence();
  
  public static NullSentence getInstance() {
    return NullSentence.INSTANCE;
  }
  
  private NullSentence() {
  }

  @Override
  public void addToken(final Token newToken) {

  }

  @Override
  public void addEntity(final Entity newEntity) {

  }

  @Override
  public void nextSentence(final Sentence newNextSentence) {

  }

  @Override
  public List<Entity> entities() {
    return null;
  }

  @Override
  public int index() {
    return -1;
  }

  @Override
  public int start() {
    return 0;
  }

  @Override
  public int end() {
    return 0;
  }

  @Override
  public Model rdfModel(final String tool, final NlpProcess process) {
    return ModelFactory.createDefaultModel();
  }
}
