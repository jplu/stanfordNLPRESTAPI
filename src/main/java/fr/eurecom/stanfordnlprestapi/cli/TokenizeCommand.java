/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
 * Copyright © 2017 Julien Plu (julien.plu@redaction-developpez.com)
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

import java.io.File;

import java.net.URL;
import java.nio.charset.Charset;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> Read a {@link PipelineConfiguration}
 *
 * @author Julien Plu
 */
public class TokenizeCommand<T extends PipelineConfiguration> extends ConfiguredCommand<T> {
  static final Logger LOGGER = LoggerFactory.getLogger(TokenizeCommand.class);
  private StanfordNlp pipeline;
  
  /**
   * TokenizeCommand constructor.
   */
  public TokenizeCommand() {
    super("tokenize", "Tokenize command on text");
  }
  
  @Override
  public final void configure(final Subparser subparser) {
    super.configure(subparser);
    // Add a command line option
    
    final MutuallyExclusiveGroup group = subparser.addMutuallyExclusiveGroup("inputs")
        .required(true);
    final ArgumentAction urlAction = new UrlAction();
    
    group.addArgument("-t", "--text")
        .dest("text")
        .type(String.class)
        .help("text to analyse");
    group.addArgument("-i", "--input-file")
        .dest("ifile")
        .type(Arguments.fileType().verifyExists().verifyIsFile().verifyCanRead())
        .help("Input file name which contain the text to process");
    group.addArgument("-u", "--url")
        .dest("url")
        .type(String.class)
        .action(urlAction)
        .help("URL to process");
  
    subparser.addArgument("-s", "--setting")
        .dest("setting")
        .type(String.class)
        .required(false)
        .setDefault("none")
        .help("Select the setting");
    subparser.addArgument("-o", "--output-file")
        .dest("ofile")
        .type(Arguments.fileType().verifyNotExists().verifyCanWriteParent())
        .required(false)
        .help("Output file name which will contain the annotations");
    subparser.addArgument("-l", "--lang")
        .dest("lang")
        .type(String.class)
        .required(false)
        .setDefault("en")
        .help("Select the language");
  }
  
  @Override
  protected final void run(final Bootstrap<T> newBootstrap, final Namespace
      newNamespace, final T newT) throws Exception {
    TokenizeCommand.LOGGER.info("Tokenize analysis uses \"{}\" as configuration file",
        newNamespace.getString("file"));
    
    this.pipeline = new StanfordNlp("properties/tokenize_" +  newNamespace.getString("lang") + "_"
        + newNamespace.getString("setting") + ".properties", newT.getName());
    
    this.process(newNamespace, newT);
  }
  
  private void process(final Namespace newNamespace, final T newT)
      throws Exception {
    final String text;
    
    if (newNamespace.getString("ifile") != null) {
      text = FileUtils.readFileToString(new File(newNamespace.getString("ifile")),
          Charset.forName("UTF-8"));
    } else if (newNamespace.getString("url") != null) {
      final String tmp = IOUtils.toString(new URL(newNamespace.getString("url")),
          Charset.forName("UTF-8"));
      
      text = Jsoup.parse(tmp).text();
    } else {
      text = newNamespace.getString("text");
    }
  
    TokenizeCommand.LOGGER.info("Tokenize analysis on \"{}\" :", newNamespace.getString("text"));
    
    final String result = this.pipeline.run(text).rdfString(newT.getName(), NlpProcess.TOKENIZE,
        "http://127.0.0.1");
    
    if (newNamespace.getString("ofile") != null) {
      FileUtils.write(new File(newNamespace.getString("ofile")), result,
          Charset.forName("UTF-8"));
    } else {
      TokenizeCommand.LOGGER.info("{}{}", System.lineSeparator(), result);
    }
  }
  
  @Override
  public final String toString() {
    return "TokenizeCommand{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
