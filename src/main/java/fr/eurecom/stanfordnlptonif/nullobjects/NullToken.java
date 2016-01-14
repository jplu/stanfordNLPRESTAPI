package fr.eurecom.stanfordnlptonif.nullobjects;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlptonif.interfaces.Token;

/**
 * Null object that represents a null token.
 *
 * @author Julien Plu
 */
public class NullToken implements Token {
  static final Logger LOGGER = LoggerFactory.getLogger(NullToken.class);
  private static final NullToken INSTANCE = new NullToken();

  public static NullToken getInstance() {
    return NullToken.INSTANCE;
  }
  
  private NullToken() {
  }

  @Override
  public final void nextToken(final Token newNextToken) {
  }

  @Override
  public final int index() {
    return -1;
  }

  @Override
  public final String text() {
    return null;
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
  public final Model rdfModel(final String tool) {
    return null;
  }
}
