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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.eurecom.stanfordnlprestapi.core.StanfordNlp;

import fr.eurecom.stanfordnlprestapi.datatypes.Query;
import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
@Path("/v4")
public class PipelineResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineResource.class);
  private final Map<String, StanfordNlp> pipelines;
  private final String stanford;
  private final Map<String, Map> profiles = new HashMap<>();

  /**
   * PipelineResource constructor.
   *
   * @param name a name.
   */
  public PipelineResource(final String name) {
    PipelineResource.LOGGER.info("PipelineResource init");
    
    this.pipelines = new HashMap<>();
    
    this.loadAllProperties();
    
    this.stanford = name;
  }

  /**
   * PipelineResource constructor.
   *
   * @param name a name.
   * @param propertyFile Stanford property file to load.
   *
   */
  public PipelineResource(final String name, final String propertyFile) {
    this.pipelines = new HashMap<>();
    
    this.pipelines.put(propertyFile, new StanfordNlp("properties"
        + FileSystems.getDefault().getSeparator() + propertyFile + ".properties", name));
    
    this.stanford = name;
  }
  
  /**
   * The API call to get all the loaded profiles.
   *
   * @return All the loaded profiles.
   */
  @GET
  @Timed
  @Produces("application/json;charset=utf-8")
  @Path("/profiles/")
  public final Response profiles() {
    final String json;
    
    try {
      json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
          this.profiles.keySet());
    } catch (final IOException ex) {
      throw new WebApplicationException("Issue for reading the profiles", ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return Response.ok(json).build();
  }
  
  /**
   * The API call to get a specific loaded profile.
   *
   * @return The loaded profile.
   */
  @GET
  @Timed
  @Produces("application/json;charset=utf-8")
  @Path("/profile/{name}")
  public final Response profile(@PathParam("name") final String name) {
    final String json;
    
    try {
      if (this.profiles.containsKey(name)) {
        json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
            this.profiles.get(name));
      } else {
        throw new WebApplicationException("The profile " + name + " does not exists.",
            Response.Status.PRECONDITION_FAILED);
      }
    } catch (final IOException ex) {
      throw new WebApplicationException("Issue for reading the profiles", ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return Response.ok(json).build();
  }
  
  /**
   * The API call for a NER process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/ner/")
  public final Response ner(@Context final HttpServletRequest request,
                            @QueryParam("setting") @DefaultValue("none") final String setting,
                            @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
  
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
    
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
      
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
      
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
    
      result = this.task(query, setting, this.getHost(request), NlpProcess.NER, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
  
    return result;
  }
  
  /**
   * The API call for a POS process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/pos/")
  public final Response pos(@Context final HttpServletRequest request,
                            @QueryParam("setting") @DefaultValue("none") final String setting,
                            @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
  
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
    
      final ObjectMapper mapper = new ObjectMapper();
    
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
    
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
      
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
      
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
    
      result = this.task(query, setting, this.getHost(request), NlpProcess.POS, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
  
    return result;
  }
  
  /**
   * The API call for a tokenize process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/tokenize/")
  public final Response tokenize(@Context final HttpServletRequest request,
                                 @QueryParam("setting") @DefaultValue("none") final String setting,
                                 @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
      
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
        
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
        
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
      
      result = this.task(query, setting, this.getHost(request), NlpProcess.TOKENIZE, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return result;
  }
  
  /**
   * The API call for a coref process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/coref/")
  public final Response coref(@Context final HttpServletRequest request,
                              @QueryParam("setting") @DefaultValue("none") final String setting,
                              @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
      
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
        
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
        
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
      
      result = this.task(query, setting, this.getHost(request), NlpProcess.COREF, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return result;
  }
  
  /**
   * The API call for a date process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/date/")
  public final Response date(@Context final HttpServletRequest request,
                             @QueryParam("setting") @DefaultValue("none") final String setting,
                             @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
      
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
        
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
        
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
      
      result = this.task(query, setting, this.getHost(request), NlpProcess.DATE, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return result;
  }
  
  /**
   * The API call for a number process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/number/")
  public final Response number(@Context final HttpServletRequest request,
                               @QueryParam("setting") @DefaultValue("none") final String setting,
                               @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
      
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
        
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
        
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
      
      result = this.task(query, setting, this.getHost(request), NlpProcess.NUMBER, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return result;
  }
  
  /**
   * The API call for a gazetteer process via POST.
   *
   * @return The corresponding response of the query
   */
  @POST
  @Timed
  @Produces({"application/json;charset=utf-8", "text/turtle;charset=utf-8"})
  @Consumes("application/json;charset=utf-8")
  @Path("/gazetteer/")
  public final Response gazetteer(@Context final HttpServletRequest request,
                               @QueryParam("setting") @DefaultValue("none") final String setting,
                               @QueryParam("lang") @DefaultValue("en") final String newLang) {
    final Response result;
    final StringWriter writer = new StringWriter();
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    
    try {
      IOUtils.copy(request.getInputStream(), writer, Charset.forName("UTF-8"));
      
      final ObjectMapper mapper = new ObjectMapper();
      
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
      
      final Query query = mapper.readValue(writer.toString(), Query.class);
      final Set<ConstraintViolation<Query>> violations = validator.validate(query);
      
      if (!violations.isEmpty()) {
        final StringBuilder sb = new StringBuilder();
        
        violations.forEach(error -> sb.append(error.getPropertyPath()
            + error.getMessage()).append("\n"));
        
        throw new WebApplicationException(sb.toString(), Response.Status.PRECONDITION_FAILED);
      }
      
      result = this.task(query, setting, this.getHost(request), NlpProcess.GAZETTEER, newLang);
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the HTTP request " + writer, ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    return result;
  }
  
  private Response task(final Query query, final String setting, final String host,
                        final NlpProcess process, final String lang) throws IOException {
    final String finalText;
    
    if (query.getUrl() != null) {
      final String tmp = IOUtils.toString(new URL(query.getUrl()), Charset.forName("UTF-8"));
    
      finalText = Jsoup.parse(tmp).text();
    } else {
      finalText = query.getContent();
    }
  
    final String res;
    
    if (this.pipelines.containsKey(process.toString().toLowerCase(Locale.ENGLISH) + '_' + lang
        + '_' + setting)) {
      res = this.pipelines.get(process.toString().toLowerCase(Locale.ENGLISH) + '_'
          + lang + '_' + setting).run(finalText).rdfString(this.stanford, process, host);
    } else {
      throw new WebApplicationException("The profile: " + process.toString().toLowerCase(
          Locale.ENGLISH) + '_' + lang + '_' + setting + " does not exists",
          Response.Status.PRECONDITION_FAILED);
    }
    
    return Response.ok(res).build();
  }
  
  private String getHost(final HttpServletRequest request) {
    final StringBuffer url = request.getRequestURL();
    final String uri = request.getRequestURI();
    
    return url.substring(0, url.indexOf(uri));
  }
  
  private void loadAllProperties() {
    try {
      Files.list(Paths.get("properties")).forEach(file -> {
        final StanfordNlp pipeline = new StanfordNlp(file.toString(), this.stanford);
        
        this.pipelines.put(file.getFileName().toString().split("\\.")[0], pipeline);
  
        try (FileInputStream fileInputStream = new FileInputStream(file.toString())) {
          final Properties props = new Properties();
          
          props.load(fileInputStream);
  
          this.profiles.put(file.getFileName().toString().split("\\.")[0], props);
        } catch (final IOException ex) {
          throw new WebApplicationException("Failed to read the profile " + file, ex,
              Response.Status.PRECONDITION_FAILED);
        }
        
      });
    } catch (final IOException ex) {
      throw new WebApplicationException("Failed to read the directory properties", ex,
          Response.Status.PRECONDITION_FAILED);
    }
  }
}
