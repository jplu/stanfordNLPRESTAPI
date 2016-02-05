package fr.eurecom.stanfordnlptonif;

/**
 * Created by ovarene on 11/08/2015.
 */

import fr.eurecom.stanfordnlptonif.command.*;
import fr.eurecom.stanfordnlptonif.core.*;
import fr.eurecom.stanfordnlptonif.resource.*;
import io.dropwizard.*;
import io.dropwizard.setup.*;
import org.slf4j.*;

public class App extends Application<AppConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  private StanfordNlp pipeline;

  @Override
  public String getName() {
    return "ws-corenlp";
  }

  @Override
  public void initialize(Bootstrap<AppConfiguration> bootstrap) {

    // add pos and ner commands on cli
    bootstrap.addCommand(new PosCommand());
    bootstrap.addCommand(new NerCommand());

  }

  @Override
  public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {

    this.pipeline = new StanfordNlp(StanfordNlp.confToProp(appConfiguration.getPipeline()));

    // handles /v1/pos
    environment.jersey().register(new PosResource(this.pipeline));
    // handles /v1/ner
    environment.jersey().register(new NerResource(this.pipeline));
  }


  // MAIN

  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

}
