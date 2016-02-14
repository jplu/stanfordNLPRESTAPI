package fr.eurecom.stanfordnlprestapi.interfaces;

import org.apache.jena.rdf.model.Model;

import java.util.List;

import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

/**
 * Interface that represents a NIF and Stanford NLP sentence.
 *
 * @author Julien Plu
 */
public interface Sentence {
  void addToken(final Token newToken);

  void addEntity(Entity newEntity);

  void nextSentence(final Sentence newNextSentence);

  List<Entity> entities();

  int index();

  int start();

  int end();

  Model rdfModel(final String tool, final NlpProcess process);
}
