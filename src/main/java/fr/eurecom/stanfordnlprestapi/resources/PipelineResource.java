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
package fr.eurecom.stanfordnlprestapi.resources;

import com.codahale.metrics.annotation.Timed;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;
import io.dropwizard.validation.OneOf;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFFormat;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
@Path("/v1")
public class PipelineResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineResource.class);
  private final StanfordNlp pipeline;
  private final String stanford;
  private static final String TURTLE = "turtle";
  private String previousSetting;
  private String lang;

  /**
   * PipelineResource constructor.
   *
   * @param newPipeline The pipeline that will be used
   */
  public PipelineResource(final StanfordNlp newPipeline) {
    PipelineResource.LOGGER.info("PipelineResource init");

    this.pipeline = newPipeline;
    this.previousSetting = "none";
    this.lang = "en";
    this.stanford = this.pipeline.getName();
  }

  /**
   * PipelineResource constructor.
   *
   * @param name a name.
   * @param newLang a language.
   *
   */
  public PipelineResource(final String name, final String newLang) {
    this.pipeline = new StanfordNlp(name, newLang);
    this.previousSetting = "none";
    this.lang = newLang;
    this.stanford = name;
  }

  /**
   * The API call for a NER process via GET.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @GET
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/ner/")
  public final Response nerGet(@QueryParam("text") @Length(max = 200, min = 1, message =
      "must be less than 200 characters long otherwise use the POST method") final String text,
                               @QueryParam("format") @OneOf(value = {"turtle", "jsonld"}, message =
                                   "must be turtle or jsonld")
                                 @DefaultValue("turtle") final String format,
                               @QueryParam("setting") @OneOf(value = {"none", "oke2015", "oke2016",
                                   "neel2015", "neel2014", "neel2016"}, message = "must be "
                                   + "none, oke2015, oke2016, neel2014, neel2015 or neel2016")
                                 @DefaultValue("none") final String setting,
                               @QueryParam("url") @URL final String url,
                               @QueryParam("lang") @OneOf(value = {"en", "es", "de", "zh", "it",
                                   "fr"}, message = "must be en, es, de, zh, it or fr")
                                 @DefaultValue("en") final String lang,
                               @Context final HttpServletRequest request) throws IOException {
    return this.task(text, format, setting, this.getHost(request), NlpProcess.NER, url, lang);
  }

  /**
   * The API call for a POS process via GET.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @GET
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/pos/")
  public final Response posGet(@QueryParam("text") @Length(max = 200, min = 1, message =
      "must be less than 200 characters long otherwise use the POST method") final String text,
                               @QueryParam("format") @OneOf(value = {"turtle", "jsonld"}, message =
                                   "must be turtle or jsonld")
                                 @DefaultValue("turtle") final String format,
                               @QueryParam("setting") @OneOf(value = {"none", "tweet"}, message =
                                   "must be none or tweet")
                                 @DefaultValue("none") final String setting,
                               @QueryParam("url") @URL final String url,
                               @QueryParam("lang") @OneOf(value = {"en", "es", "de", "zh", "fr",
                                   "it"}, message = "must be en, es, de, fr, zh or it")
                                 @DefaultValue("en") final String lang,
                               @Context final HttpServletRequest request) throws IOException {
    return this.task(text, format, setting, this.getHost(request), NlpProcess.POS, url, lang);
  }
  
  /**
   * The API call for a NER process via POST.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/ner/")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public final Response nerPost(@FormParam("text") final String text,
                                @FormParam("format") @OneOf(value = {"turtle", "jsonld"}, message =
                                    "must be turtle or jsonld")
                                  @DefaultValue("turtle") final String format,
                                @FormParam("setting") @OneOf(value = {"none", "oke2015", "oke2016",
                                    "neel2015", "neel2014", "neel2016"}, message = "must be "
                                    + "none, oke2015, oke2016, neel2014, neel2015 or neel2016")
                                  @DefaultValue("none") final String setting,
                                @FormParam("url") @URL final String url,
                                @FormParam("lang") @OneOf(value = {"en", "es", "de", "zh", "it",
                                    "fr"}, message = "must be en, es, de, zh, it or fr")
                                  @DefaultValue("en") final String lang,
                                @Context final HttpServletRequest request) throws IOException {
    return this.task(text, format, setting, this.getHost(request), NlpProcess.NER, url, lang);
  }
  
  /**
   * The API call for a POS process via GET.
   *
   * @param text HTTP query
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"text/turtle;charset=utf-8", "application/json;charset=utf-8"})
  @Path("/pos/")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public final Response posPost(@FormParam("text") final String text,
                                @FormParam("format") @OneOf(value = {"turtle", "jsonld"}, message =
                                    "must be turtle or jsonld")
                                @DefaultValue("turtle") final String format,
                                @FormParam("setting") @OneOf(value = {"none", "tweet"}, message =
                                    "must be none or tweet")
                                  @DefaultValue("none") final String setting,
                                @FormParam("url") @URL final String url,
                                @FormParam("lang") @OneOf(value = {"en", "es", "de", "zh", "fr",
                                    "it"}, message = "must be en, es, de, fr, zh or it")
                                  @DefaultValue("en") final String lang,
                                @Context final HttpServletRequest request) throws IOException {
    return this.task(text, format, setting, this.getHost(request), NlpProcess.POS, url, lang);
  }
  
  private Response task(final String text, final String format, final String setting,
                        final String host, final NlpProcess process, final String url,
                        final String lang) throws IOException {
    if (text == null && url == null) {
      throw new WebApplicationException("Text and Url parameters cannot be both empty.",
          Response.Status.PRECONDITION_FAILED);
    }
    
    final String finalText;
    
    if (url != null) {
      final String tmp = IOUtils.toString(new java.net.URL(url), Charset.forName("UTF-8"));
    
      finalText = Jsoup.parse(tmp).text();
    } else {
      finalText = text;
    }
    
    if (!lang.equals(this.lang)) {
      this.pipeline.setLang(lang);
      
      this.lang = lang;
    }
    
    if (!setting.equals(this.previousSetting)) {
      this.pipeline.setPipeline(setting);
      
      this.previousSetting = setting;
    }
  
    final String res;
  
    if (PipelineResource.TURTLE.equals(format)) {
      res = this.pipeline.run(finalText).rdfString(this.stanford, RDFFormat.TURTLE_PRETTY,
          process, host);
    } else {
      res = this.pipeline.run(finalText).rdfString(this.stanford, RDFFormat.JSONLD_PRETTY,
          process, host);
    }
  
    return Response.ok(res).build();
  }
  
  private String getHost(final HttpServletRequest request) {
    if (request == null) {
      return "http://127.0.0.1";
    }
    
    final StringBuffer url = request.getRequestURL();
    final String uri = request.getRequestURI();
    
    return url.substring(0, url.indexOf(uri));
  }

  @Override
  public final String toString() {
    return "PipelineResource{}";
  }
}
