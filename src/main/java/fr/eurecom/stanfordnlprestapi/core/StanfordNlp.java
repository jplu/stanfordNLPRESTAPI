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
package fr.eurecom.stanfordnlprestapi.core;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlprestapi.datatypes.Context;
import fr.eurecom.stanfordnlprestapi.datatypes.Coref;
import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.SentenceImpl;
import fr.eurecom.stanfordnlprestapi.datatypes.TokenImpl;

import fr.eurecom.stanfordnlprestapi.enums.NlpProcess;
import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business class for manipulating Stanford NLP annotations.
 *
 * @author Julien Plu
 */
public class StanfordNlp {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlp.class);
  private StanfordCoreNLP pipeline;
  private final String name;
  private final NlpProcess process;

  /**
   * StanfordNlp constructor.
   *
   * @param newName a name.
   * @param propertyFile a property file that contains the pipeline properties.
   */
  public StanfordNlp(final String propertyFile, final String newName) {
    this.name = newName;
    
    this.process = NlpProcess.valueOf(propertyFile.split(
        FileSystems.getDefault().getSeparator())[propertyFile.split(
        FileSystems.getDefault().getSeparator()).length - 1].split("_")[0].toUpperCase(
            Locale.ENGLISH));
    
    this.createPipelineProperties(propertyFile);
  }
  
  /**
   * Define a new pipeline for Stanford.
   *
   * @param propertyFile Property file that contains the pipeline properties.
   */
  private void createPipelineProperties(final String propertyFile) {
    final Properties props = new Properties();
    
    try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
      props.load(fileInputStream);
    } catch (final IOException ex) {
      throw new WebApplicationException("The profile: " + propertyFile.split(
          FileSystems.getDefault().getSeparator())[1].split("\\.")[0] + " does not exists", ex,
          Response.Status.PRECONDITION_FAILED);
    }
    
    StanfordNlp.LOGGER.info("Run Stanford core NLP with: {}", props);
    
    this.pipeline = new StanfordCoreNLP(props);
    
    if (this.process == NlpProcess.DATE) {
      this.pipeline.addAnnotator(new TimeAnnotator("sutime", new Properties()));
    }
  }
  
  /**
   * The name of the pipeline.
   *
   * @return name of the pipeline.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Create a context from a text.
   *
   * @param newText Text to process.
   *
   * @return The corresponding context of the text.
   */
  public final Context run(final String newText) {
    final Annotation document = new Annotation(newText);

    this.pipeline.annotate(document);
    
    final Context context;
    
    if (this.process == NlpProcess.COREF) {
      context = this.buildCorefContext(document);
    } else if (this.process == NlpProcess.DATE) {
      context = this.buildDateContext(document);
    } else {
      context = this.buildContext(document.get(CoreAnnotations.SentencesAnnotation.class), newText);
    }
    
    return context;
  }
  
  private Context buildDateContext(final Annotation document) {
    final Context tmpContext = new Context(document.toString(), 0, document.toString().length());
    final List<CoreMap> timexAnnsAll = document.get(TimeAnnotations.TimexAnnotations.class);
    final List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
  
    this.buildSentencesFromContext(sentences, tmpContext);
  
    for (final CoreMap cm : timexAnnsAll) {
      final int start = cm.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
      final int end = cm.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
      final Entity entity = new Entity(cm.toString(), "DATE", tmpContext.sentences().get(
          cm.get(CoreAnnotations.SentenceIndexAnnotation.class)), tmpContext, start, end);
      
      tmpContext.sentences().get(cm.get(CoreAnnotations.SentenceIndexAnnotation.class))
          .addEntity(entity);
    }
    
    return tmpContext;
  }
  
  private Context buildCorefContext(final Annotation document) {
    final Context tmpContext = new Context(document.toString(), 0, document.toString().length());
    final List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    final Map<Integer, List<CoreLabel>> tokens = new HashMap<>();
    
    for (final CoreMap map : sentences) {
      tokens.put(sentences.indexOf(map), map.get(CoreAnnotations.TokensAnnotation.class));
    }
    
    this.buildSentencesFromContext(sentences, tmpContext);
    
    final Map<Integer, CorefChain> corefChains = document.get(
        CorefCoreAnnotations.CorefChainAnnotation.class);
    
    for (final CorefChain chain : corefChains.values()) {
      final CorefChain.CorefMention representative = chain.getRepresentativeMention();

      for (final CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
        if (!mention.equals(representative)) {
          final int start = tokens.get(mention.sentNum - 1).get(mention.startIndex - 1)
              .beginPosition();
          final int end = tokens.get(mention.sentNum - 1).get(mention.endIndex - 2)
              .endPosition();
          final Coref coref = new Coref(mention.mentionSpan, representative.mentionSpan, start,
              end, tmpContext.sentences().get(mention.sentNum - 1), tmpContext);

          tmpContext.sentences().get(mention.sentNum - 1).addCoref(coref);
        }
      }
    }
    
    return tmpContext;
  }

  private Context buildContext(final List<CoreMap> sentences, final String newText) {
    final Context tmpContext = new Context(newText, 0, newText.length());

    this.buildSentencesFromContext(sentences, tmpContext);

    return tmpContext;
  }

  private void buildSentencesFromContext(final List<CoreMap> stanfordSentences,
                                         final Context context) {
    Sentence sentence = new SentenceImpl(stanfordSentences.get(0).get(
        CoreAnnotations.TextAnnotation.class), context,
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class),
        NullSentence.getInstance());

    context.addSentence(sentence);
    
    final List<Sentence> sentences = new ArrayList<>();
    
    sentences.add(sentence);
    
    if (this.process == NlpProcess.POS) {
      this.buildPosTokensFromSentence(stanfordSentences.get(0), context, sentence);
    } else if (this.process == NlpProcess.NER || this.process == NlpProcess.NUMBER
        || this.process == NlpProcess.GAZETTEER) {
      this.buildEntitiesFromSentence(stanfordSentences.get(0), context, sentence);
    } else if (this.process == NlpProcess.TOKENIZE) {
      this.buildTokensFromSentence(stanfordSentences.get(0), context, sentence);
    }

    for (int i = 1; i < stanfordSentences.size(); i++) {
      sentence = new SentenceImpl(stanfordSentences.get(i).get(
          CoreAnnotations.TextAnnotation.class), context,
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.SentenceIndexAnnotation.class),
          sentences.get(i - 1));

      context.addSentence(sentence);
      sentences.add(sentence);
      
      if (this.process == NlpProcess.POS) {
        this.buildPosTokensFromSentence(stanfordSentences.get(i), context, sentence);
      } else if (this.process == NlpProcess.NER || this.process == NlpProcess.NUMBER
          || this.process == NlpProcess.GAZETTEER) {
        this.buildEntitiesFromSentence(stanfordSentences.get(i), context, sentence);
      } else if (this.process == NlpProcess.TOKENIZE) {
        this.buildTokensFromSentence(stanfordSentences.get(i), context, sentence);
      }
    }

    for (int i = 0; i < (sentences.size() - 1); i++) {
      sentences.get(i).nextSentence(sentences.get(i + 1));
    }
  }
  
  private void buildTokensFromSentence(final CoreMap stanfordSentence, final Context context,
                                       final Sentence sentence) {
    final CoreLabel firstLabel = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).get(
        0);
  
    Token token = new TokenImpl(firstLabel.get(CoreAnnotations.TextAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        NullToken.getInstance(), context, sentence,
        firstLabel.get(CoreAnnotations.IndexAnnotation.class));
  
    sentence.addToken(token);
  
    final List<Token> tokens = new ArrayList<>();
  
    tokens.add(token);
  
    for (int i = 1; i < stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size(); i++) {
      final CoreLabel currentLabel = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class)
          .get(i);
      token = new TokenImpl(currentLabel.get(CoreAnnotations.TextAnnotation.class),
          currentLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          currentLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
          tokens.get(i - 1), context, sentence,
          currentLabel.get(CoreAnnotations.IndexAnnotation.class));
    
      sentence.addToken(token);
      tokens.add(token);
    }
  
    for (int i = 0; i < (tokens.size() - 1); i++) {
      tokens.get(i).nextToken(tokens.get(i + 1));
    }
  }

  private void buildPosTokensFromSentence(final CoreMap stanfordSentence, final Context context,
                                       final Sentence sentence) {
    final CoreLabel firstLabel = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).get(
        0);
    
    Token token = new TokenImpl(firstLabel.get(CoreAnnotations.TextAnnotation.class),
        firstLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        NullToken.getInstance(), context, sentence,
        firstLabel.get(CoreAnnotations.IndexAnnotation.class));

    sentence.addToken(token);
    
    final List<Token> tokens = new ArrayList<>();
    
    tokens.add(token);

    for (int i = 1; i < stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size(); i++) {
      final CoreLabel currentLabel = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class)
          .get(i);
      token = new TokenImpl(currentLabel.get(CoreAnnotations.TextAnnotation.class),
          currentLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class),
          currentLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          currentLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
          tokens.get(i - 1), context, sentence,
          currentLabel.get(CoreAnnotations.IndexAnnotation.class));

      sentence.addToken(token);
      tokens.add(token);
    }

    for (int i = 0; i < (tokens.size() - 1); i++) {
      tokens.get(i).nextToken(tokens.get(i + 1));
    }
  }

  private void buildEntitiesFromSentence(final CoreMap stanfordSentence,
                                         final Context context, final Sentence sentence) {
    StringBuilder sb = new StringBuilder();
    String type = "";
    int start = 0;

    for (final CoreLabel token : stanfordSentence.get(CoreAnnotations.TokensAnnotation.class)) {
      if (token.get(CoreAnnotations.NamedEntityTagAnnotation.class) != null
          && !"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && sb.toString().isEmpty()) {
        start = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
        type = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        
        sb.append(token.get(CoreAnnotations.TextAnnotation.class));
        
        if (Pattern.compile("@|#").matcher(sb.toString()).find()
            || stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).indexOf(token)
            == stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
          sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start,
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
          
          sb = new StringBuilder();
          type = "";
        }
      } else if (token.get(CoreAnnotations.NamedEntityTagAnnotation.class) != null
          && !"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && type.equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && !Pattern.compile("@|#").matcher(token.get(
              CoreAnnotations.TextAnnotation.class)).find()) {
        sb.append(' ');
        sb.append(token.get(CoreAnnotations.TextAnnotation.class));

        if (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).indexOf(token)
            == stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
          sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start,
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
        }
      } else if (!sb.toString().isEmpty()
          && token.get(CoreAnnotations.NamedEntityTagAnnotation.class) != null
          && !"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && !type.equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))) {
        final int index = stanfordSentence.get(
            CoreAnnotations.TokensAnnotation.class).indexOf(token);
        final int end = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).get(
            index - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
  
        sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start, end));
        sentence.addEntity(new Entity(token.get(CoreAnnotations.TextAnnotation.class),
            token.get(CoreAnnotations.NamedEntityTagAnnotation.class), sentence, context,
            token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
            token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
  
        sb = new StringBuilder();
        type = "";
      } else if (!sb.toString().isEmpty()) {
        if (Pattern.compile("@|#").matcher(token.get(
            CoreAnnotations.TextAnnotation.class)).find()
            && token.get(CoreAnnotations.NamedEntityTagAnnotation.class) != null
            && !"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))) {
          sentence.addEntity(new Entity(token.get(CoreAnnotations.TextAnnotation.class),
              token.get(CoreAnnotations.NamedEntityTagAnnotation.class), sentence, context,
              token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
        }
        
        final int index = stanfordSentence.get(
            CoreAnnotations.TokensAnnotation.class).indexOf(token);
        final int end = stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).get(
            index - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
  
        sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start, end));
  
        sb = new StringBuilder();
        type = "";
      }
    }
  }
}
