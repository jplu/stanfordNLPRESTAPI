package fr.eurecom.stanfordnlptonif.interfaces;

import org.apache.jena.rdf.model.Model;

/**
 * Interface that represents a NIF and Stanford NLP token.
 *
 * @author Julien Plu
 */
public interface Token {
  void nextToken(final Token newNextToken);

  int index();

  String text();

  int start();

  int end();

  Model rdfModel(final String tool);
}
