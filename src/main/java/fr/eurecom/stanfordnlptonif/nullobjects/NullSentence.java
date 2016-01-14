package fr.eurecom.stanfordnlptonif.nullobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import fr.eurecom.stanfordnlptonif.datatypes.Entity;

import fr.eurecom.stanfordnlptonif.enums.NlpProcess;

import fr.eurecom.stanfordnlptonif.interfaces.Sentence;
import fr.eurecom.stanfordnlptonif.interfaces.Token;

/**
 * Null object that represents a null sentence.
 *
 * @author Julien Plu
 */
public class NullSentence implements Sentence {
  static final Logger LOGGER = LoggerFactory.getLogger(NullSentence.class);
  private static final NullSentence INSTANCE = new NullSentence();
  
  public static NullSentence getInstance() {
    return NullSentence.INSTANCE;
  }
  
  private NullSentence() {
  }

  @Override
  public final void addToken(final Token newToken) {

  }

  @Override
  public final void addEntity(final Entity newEntity) {

  }

  @Override
  public final void nextSentence(final Sentence newNextSentence) {

  }

  @Override
  public final List<Entity> entities() {
    return null;
  }

  @Override
  public final int index() {
    return -1;
  }

  @Override
  public final int start() {
    return 0;
  }

  @Override
  public final int end() {
    return 0;
  }

  @Override
  public final Model rdfModel(final String tool, final NlpProcess process) {
    return ModelFactory.createDefaultModel();
  }
}
