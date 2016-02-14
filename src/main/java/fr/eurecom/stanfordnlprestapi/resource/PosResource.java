package fr.eurecom.stanfordnlprestapi.resource;

import org.apache.jena.riot.RDFFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;
import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

/**
 * @author Olivier Varene
 * @author Julien Plu
 */
@Path("/v1/pos")
public class PosResource {
  static final Logger LOGGER = LoggerFactory.getLogger(PosResource.class);
  private final StanfordNlp pipeline;

  /**
   * PosResource constructor.
   *
   * @param newPipeline The pipeline that will be used.
   */
  public PosResource(final StanfordNlp newPipeline) {
    PosResource.LOGGER.info("PosResource init");

    this.pipeline = newPipeline;
  }

  /**
   * The API call for a POS process.
   *
   * @param rawRequest HTTP query.
   *
   * @return The corresponding response of the query.
   */
  @GET
  @Timed
  @Produces("text/turtle; charset=utf-8")
  public final Response get(@QueryParam("q") final String rawRequest) {
    if (rawRequest == null) {
      throw new WebApplicationException("Empty request", Response.Status.PRECONDITION_FAILED);
    }

    final String res = this.pipeline.run(rawRequest).rdfString("stanfordnlp",
        RDFFormat.TURTLE_PRETTY, NlpProcess.POS);

    return Response.ok(res).build();
  }

  @Override
  public final String toString() {
    return "PosResource{"
        + "pipeline=" + this.pipeline
        + '}';
  }
}
