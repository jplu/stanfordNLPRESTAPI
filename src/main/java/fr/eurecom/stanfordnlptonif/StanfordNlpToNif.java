package fr.eurecom.stanfordnlptonif;

import org.apache.jena.riot.RDFFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlptonif.core.StanfordNlp;

import fr.eurecom.stanfordnlptonif.enums.NlpProcess;

/**
 * Main class.
 *
 * @author Julien Plu
 */
final class StanfordNlpToNif {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlpToNif.class);

  private StanfordNlpToNif() {
  }

  public static void main(final String... args) {
    final String text = "My favorite actress is: Natalie Portman. She is very stunning.";
    final StanfordNlp stanfordNlp = new StanfordNlp(text);

    StanfordNlpToNif.LOGGER.info(stanfordNlp.run().rdfString("stanfordnlp", RDFFormat.TURTLE_PRETTY,
        NlpProcess.POS));
  }
}
