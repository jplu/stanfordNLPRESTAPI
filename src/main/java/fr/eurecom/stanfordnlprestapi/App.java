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
package fr.eurecom.stanfordnlprestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.commands.NerCommand;
import fr.eurecom.stanfordnlprestapi.commands.PosCommand;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.resources.NerResource;
import fr.eurecom.stanfordnlprestapi.resources.PosResource;

import io.dropwizard.Application;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class App extends Application<AppConfiguration> {
  static final Logger LOGGER = LoggerFactory.getLogger(App.class);
  private StanfordNlp pipeline;

  public App() {
  }

  @Override
  public final String getName() {
    return "ws-corenlp";
  }

  @Override
  public final void initialize(final Bootstrap<AppConfiguration> bootstrap) {
    super.initialize(bootstrap);
    // add pos and ner commands on cli
    bootstrap.addCommand(new PosCommand());
    bootstrap.addCommand(new NerCommand());
  }

  @Override
  public final void run(final AppConfiguration newT, final Environment newEnvironment)
      throws Exception {
    this.pipeline = new StanfordNlp(newT.getPipeline());

    // handles /v1/pos
    newEnvironment.jersey().register(new PosResource(this.pipeline));
    // handles /v1/ner
    newEnvironment.jersey().register(new NerResource(this.pipeline));
  }

  // MAIN
  public static void main(final String... args) throws Exception {
    new App().run(args);
  }

  @Override
  public final String toString() {
    return "App{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
