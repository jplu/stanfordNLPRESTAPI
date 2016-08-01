/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework to get
results in NIF format.
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
/**
 * StanfordNLPRESTAPI - This project offer a REST API over Stanford CoreNLP framework
                            to get results in NIF format.
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
package fr.eurecom.stanfordnlprestapi;

import fr.eurecom.stanfordnlprestapi.cli.NerCommand;
import fr.eurecom.stanfordnlprestapi.cli.PosCommand;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.healthchecks.ModelHealthCheck;
import fr.eurecom.stanfordnlprestapi.resources.PipelineResource;

import io.dropwizard.Application;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
public class App extends Application<PipelineConfiguration> {
  static final Logger LOGGER = LoggerFactory.getLogger(App.class);
  private StanfordNlp pipeline;

  public App() {
  }

  @Override
  public final String getName() {
    return "ws-corenlp";
  }

  @Override
  public final void initialize(final Bootstrap<PipelineConfiguration> bootstrap) {
    super.initialize(bootstrap);
    // add pos and ner commands on cli
    bootstrap.addCommand((Command) new PosCommand());
    bootstrap.addCommand((Command) new NerCommand());
  }

  @Override
  public final void run(final PipelineConfiguration newT, final Environment newEnvironment)
      throws Exception {
    this.pipeline = new StanfordNlp(newT);

    newEnvironment.healthChecks().register("pos model", new ModelHealthCheck(
        newT.getPos().getModel()));
    newEnvironment.healthChecks().register("ner model", new ModelHealthCheck(
        newT.getNer().getModel()));

    newEnvironment.jersey().register(new PipelineResource(this.pipeline));
  }

  /**
   * Main entry.
   *
   * @param args command line arguments.
   *
   * @throws Exception
   */
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
