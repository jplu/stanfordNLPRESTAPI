package fr.eurecom.stanfordnlprestapi.command;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.apache.jena.riot.RDFFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import io.dropwizard.cli.Command;

import io.dropwizard.setup.Bootstrap;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class PosCommand extends Command {
  private static final Logger LOGGER = LoggerFactory.getLogger(PosCommand.class);
  private final StanfordNlp pipeline;

  /**
   * PosCommand constructor.
   */
  public PosCommand() {
    super("pos", "pos command on text");
    final Properties props = new Properties();

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

    this.pipeline = new StanfordNlp(props);
  }

  @Override
  public final void configure(final Subparser newSubparser) {
    // Add a command line option
    newSubparser.addArgument("-t")
        .dest("text")
        .type(String.class)
        .required(true)
        .help("text to analyse");
  }

  @Override
  public final void run(final Bootstrap<?> newBootstrap, final Namespace newNamespace) throws
      Exception {
    PosCommand.LOGGER.info("POS analysis on \"{}\" :", newNamespace.getString("text"));
    PosCommand.LOGGER.info(this.pipeline.run(newNamespace.getString("text")).rdfString(
        "stanfordnlp", RDFFormat.TURTLE_PRETTY, NlpProcess.POS));
  }

  @Override
  public final String toString() {
    return "PosCommand{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
