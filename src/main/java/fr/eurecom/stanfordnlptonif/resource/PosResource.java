package fr.eurecom.stanfordnlptonif.resource;

import com.codahale.metrics.annotation.*;
import fr.eurecom.stanfordnlptonif.core.*;
import fr.eurecom.stanfordnlptonif.enums.*;
import org.apache.jena.riot.*;
import org.slf4j.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.concurrent.atomic.*;

/**
 * Created by ovarene on 17/12/2015.
 */

@Path("/v1/pos")
public class PosResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(PosResource.class);

  private AtomicLong  counter;
  private StanfordNlp pipeline;

  public PosResource(StanfordNlp pipeline) {
    LOGGER.info("PosResource init");
    this.pipeline = pipeline;
  }

  @GET
  @Timed
  @Produces("text/turtle; charset=utf-8")
  public Response get(@QueryParam("q") String rawRequest) {
    if(null == rawRequest) { throw new WebApplicationException("Empty request",Response.Status.PRECONDITION_FAILED); }

    String res = this.pipeline.run(rawRequest).rdfString("stanfordnlp", RDFFormat.TURTLE_PRETTY, NlpProcess.POS);
    return Response.ok(res).build();
  }


}
