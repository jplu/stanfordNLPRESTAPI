/**
 * This file is part of StanfordNLPRESTAPI.
 *
 * StanfordNLPRESTAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * StanfordNLPRESTAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with StanfordNLPRESTAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.eurecom.stanfordnlprestapi.cli;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import io.dropwizard.cli.ConfiguredCommand;

import io.dropwizard.setup.Bootstrap;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.apache.jena.riot.RDFFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> Read a {@link PipelineConfiguration}
 *
 * @author Olivier Varene
 * @author Julien Plu
 */
public class PosCommand<T extends PipelineConfiguration> extends ConfiguredCommand<T> {
  static final Logger LOGGER = LoggerFactory.getLogger(PosCommand.class);
  private StanfordNlp pipeline;

  /**
   * PosCommand constructor.
   */
  public PosCommand() {
    super("pos", "POS command on text");
  }

  @Override
  public final void configure(final Subparser subparser) {
    super.configure(subparser);
    // Add a command line option
    subparser.addArgument("-t")
        .dest("text")
        .type(String.class)
        .required(true)
        .help("text to analyse");
    subparser.addArgument("-f")
        .dest("format")
        .type(String.class)
        .required(false)
        .help("turtle or jsonld");
  }

  @Override
  protected final void run(final Bootstrap<T> newBootstrap, final Namespace
      newNamespace, final T newT) throws Exception {
    PosCommand.LOGGER.info("POS analysis on \"{}\" :", newNamespace.getString("text"));
    PosCommand.LOGGER.info("POS analysis uses \"{}\" as configuration file", newNamespace.getString(
        "file"));

    this.pipeline = new StanfordNlp(newT);

    if (newNamespace.get("format") == null || "turtle".equals(newNamespace.get("format"))
        || !"jsonld".equals(newNamespace.get("format"))) {
      PosCommand.LOGGER.info(this.pipeline.run(newNamespace.getString("text")).rdfString(
          "stanfordnlp", RDFFormat.TURTLE_PRETTY, NlpProcess.POS));
    }

    if ("jsonld".equals(newNamespace.get("format"))) {
      PosCommand.LOGGER.info(this.pipeline.run(newNamespace.getString("text")).rdfString(
          "stanfordnlp", RDFFormat.JSONLD_PRETTY, NlpProcess.POS));
    }
  }

  @Override
  public final String toString() {
    return "PosCommand{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
