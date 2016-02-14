package fr.eurecom.stanfordnlprestapi.nullobjects;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.interfaces.Token;

/**
 * Null object that represents a null token.
 *
 * @author Julien Plu
 */
public final class NullToken implements Token {
  static final Logger LOGGER = LoggerFactory.getLogger(NullToken.class);
  private static final NullToken INSTANCE = new NullToken();

  public static NullToken getInstance() {
    return NullToken.INSTANCE;
  }
  
  private NullToken() {
  }

  @Override
  public void nextToken(final Token newNextToken) {
  }

  @Override
  public int index() {
    return -1;
  }

  @Override
  public String text() {
    return null;
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
  public Model rdfModel(final String tool) {
    return null;
  }
}
