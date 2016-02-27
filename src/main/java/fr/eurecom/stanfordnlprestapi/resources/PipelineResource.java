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
package fr.eurecom.stanfordnlprestapi.resources;

import com.codahale.metrics.annotation.Timed;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import java.util.Properties;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import javax.ws.rs.core.Response;

import org.apache.jena.riot.RDFFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
@Path("/v1")
public class PipelineResource {
  static final Logger LOGGER = LoggerFactory.getLogger(PipelineResource.class);
  private final StanfordNlp pipeline;

  /**
   * PipelineResource constructor.
   *
   * @param newPipeline The pipeline that will be used
   */
  public PipelineResource(final StanfordNlp newPipeline) {
    PipelineResource.LOGGER.info("PipelineResource init");

    this.pipeline = newPipeline;
  }

  /**
   * PipelineResource constructor.
   *
   * @param annotators List of annotators to use for StanfordNLP
   */
  public PipelineResource(final String annotators) {
    final Properties props = new Properties();

    props.setProperty("annotators", annotators);

    this.pipeline = new StanfordNlp(props);
  }

  /**
   * The API call for a NER process.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @GET
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/ner/")
  public final Response getNer(@QueryParam("text") final String text,
                               @QueryParam("format") @DefaultValue("turtle") final String format) {
    if ((text == null)) {
      throw new WebApplicationException("Text parameter is not provided",
          Response.Status.PRECONDITION_FAILED);
    }

    if (text.isEmpty()) {
      throw new WebApplicationException("Text parameter is empty",
          Response.Status.PRECONDITION_FAILED);
    }

    if (!"jsonld".equals(format) && !"turtle".equals(format) && !format.isEmpty()) {
      throw new WebApplicationException("Format parameter " + format + " does not exists",
          Response.Status.PRECONDITION_FAILED);
    }

    String res = "";

    if ("turtle".equals(format) || format.isEmpty()) {
      res = this.pipeline.run(text).rdfString("stanfordnlp",
          RDFFormat.TURTLE_PRETTY, NlpProcess.NER);
    }

    if ("jsonld".equals(format)) {
      res = this.pipeline.run(text).rdfString("stanfordnlp",
          RDFFormat.JSONLD_PRETTY, NlpProcess.NER);
    }

    return Response.ok(res).build();
  }

  /**
   * The API call for a POS process.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @GET
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/pos/")
  public final Response getPos(@QueryParam("text") final String text,
                               @QueryParam("format") @DefaultValue("turtle") final String format) {
    if ((text == null)) {
      throw new WebApplicationException("Text parameter is not provided",
          Response.Status.PRECONDITION_FAILED);
    }

    if (text.isEmpty()) {
      throw new WebApplicationException("Text parameter is empty",
          Response.Status.PRECONDITION_FAILED);
    }

    if (!"jsonld".equals(format) && !"turtle".equals(format) && !format.isEmpty()) {
      throw new WebApplicationException("Format parameter " + format + " does not exists",
          Response.Status.PRECONDITION_FAILED);
    }

    String res = "";

    if ("turtle".equals(format) || format.isEmpty()) {
      res = this.pipeline.run(text).rdfString("stanfordnlp",
          RDFFormat.TURTLE_PRETTY, NlpProcess.POS);
    }

    if ("jsonld".equals(format)) {
      res = this.pipeline.run(text).rdfString("stanfordnlp",
          RDFFormat.JSONLD_PRETTY, NlpProcess.POS);
    }

    return Response.ok(res).build();
  }

  @Override
  public final String toString() {
    return "PipelineResource{}";
  }
}
