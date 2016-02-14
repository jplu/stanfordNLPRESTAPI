package fr.eurecom.stanfordnlprestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.eurecom.stanfordnlprestapi.command.NerCommand;
import fr.eurecom.stanfordnlprestapi.command.PosCommand;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.resource.NerResource;
import fr.eurecom.stanfordnlprestapi.resource.PosResource;

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
    return super.getName() + "ws-corenlp";
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
    this.pipeline = new StanfordNlp(StanfordNlp.confToProp(newT.getPipeline()));

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
