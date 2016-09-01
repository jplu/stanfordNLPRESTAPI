/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
 * Copyright © 2016 Julien Plu (julien.plu@redaction-developpez.com)
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
public class NerCommand<T extends PipelineConfiguration> extends ConfiguredCommand<T> {
  static final Logger LOGGER = LoggerFactory.getLogger(NerCommand.class);
  private StanfordNlp pipeline;

  /**
   * NerCommand constructor.
   */
  public NerCommand() {
    super("ner", "NER command on text");
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
    NerCommand.LOGGER.info("NER analysis on \"{}\" :", newNamespace.getString("text"));
    NerCommand.LOGGER.info("NER analysis uses \"{}\" as configuration file", newNamespace.getString(
        "file"));

    this.pipeline = new StanfordNlp(newT);

    if (newNamespace.get("format") == null || "turtle".equals(newNamespace.get("format"))
        || !"jsonld".equals(newNamespace.get("format"))) {
      NerCommand.LOGGER.info(System.lineSeparator() + this.pipeline.run(newNamespace.getString(
          "text")).rdfString("stanfordnlp", RDFFormat.TURTLE_PRETTY, NlpProcess.NER));
    }
    
    if ("jsonld".equals(newNamespace.get("format"))) {
      NerCommand.LOGGER.info(System.lineSeparator() + this.pipeline.run(newNamespace.getString(
          "text")).rdfString("stanfordnlp", RDFFormat.JSONLD_PRETTY, NlpProcess.NER));
    }
  }

  @Override
  public final String toString() {
    return "NerCommand{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
