/**
 * StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
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
package fr.eurecom.stanfordnlprestapi.core;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlprestapi.configurations.PipelineConfiguration;

import fr.eurecom.stanfordnlprestapi.datatypes.Context;
import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.SentenceImpl;
import fr.eurecom.stanfordnlprestapi.datatypes.TokenImpl;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

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
  private final String ner;
  private final String pos;
  private final String name;

  /**
   * StanfordNlp constructor.
   *
   * @param props properties to configure the pipeline.
   */
  public StanfordNlp(final Properties props, final String newName) {
    StanfordNlp.LOGGER.info("Run Stanford core NLP with: {}", props);
    
    this.ner = props.getProperty("ner.model");
    this.pos = props.getProperty("pos.model");
    this.name = newName;
    
    this.pipeline = new StanfordCoreNLP(props);
  }

  /**
   * StanfordNlp constructor.
   *
   * @param conf a pipeline configuration.
   */
  public StanfordNlp(final PipelineConfiguration conf) {
    this.pos = conf.getPos().getModel();
    this.ner = conf.getNer().getModel();
    this.name = conf.getName();
    
    final Properties props = new Properties();
    
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
    props.setProperty("pos.model", conf.getPos().getModel());
    props.setProperty("ner.model", conf.getNer().getModel());
    props.setProperty("ner.useSUTime", Boolean.toString(conf.getNer().getUseSuTime()));
    props.setProperty("ner.applyNumericClassifiers", Boolean.toString(conf.getNer()
        .getApplyNumericClassifiers()));
    props.setProperty("parse.model", conf.getParse().getModel());
    props.setProperty("coref.doClustering", Boolean.toString(conf.getCoref().getDoClustering()));
    props.setProperty("coref.md.type", conf.getCoref().getMdType());
    props.setProperty("coref.mode", conf.getCoref().getMode());

    StanfordNlp.LOGGER.info("Run Stanford core NLP with: {}", props);

    this.pipeline = new StanfordCoreNLP(props);
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
   * Change Stanford CoreNLP settings.
   *
   * @param setting new Settings.
   */
  public final void setPipeline(final String setting) {
    if ("tweet".equals(setting) || "neel2015".equals(setting) || "neel2016".equals(setting)) {
      this.pipeline.getProperties().setProperty("pos.model", "models/gate-EN-twitter.model");
    }
    
    if ("neel2015".equals(setting)) {
      this.pipeline.getProperties().setProperty("ner.model", "models/NEEL2015.ser.gz");
    } else if ("neel2016".equals(setting)) {
      this.pipeline.getProperties().setProperty("ner.model", "models/NEEL2016.ser.gz");
    } else if ("oke2015".equals(setting)) {
      this.pipeline.getProperties().setProperty("ner.model", "models/OKE2015.ser.gz");
    } else if ("oke2016".equals(setting)) {
      this.pipeline.getProperties().setProperty("ner.model", "models/OKE2016.ser.gz");
    } else if ("none".equals(setting)) {
      this.pipeline.getProperties().setProperty("ner.model", this.ner);
      this.pipeline.getProperties().setProperty("pos.model", this.pos);
    }
    
    StanfordNlp.LOGGER.info("Run Stanford core NLP with: {}", this.pipeline.getProperties());
    
    this.pipeline = new StanfordCoreNLP(this.pipeline.getProperties());
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

    return this.buildContext(document.get(CoreAnnotations.SentencesAnnotation.class), newText);
  }

  private Context buildContext(final List<CoreMap> sentences, final String newText) {
    final Context tmpContext = new Context(newText, 0, newText.length());

    this.buildSentencesFromContext(sentences, tmpContext);

    return tmpContext;
  }

  private void buildSentencesFromContext(final List<CoreMap> stanfordSentences,
                                         final Context context) {
    Sentence sentence = new SentenceImpl(stanfordSentences.get(0).toString(), context,
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class),
        NullSentence.getInstance());

    context.addSentence(sentence);
    
    final List<Sentence> sentences = new ArrayList<>();
    
    sentences.add(sentence);
    
    this.buildTokensFromSentence(stanfordSentences.get(0), context, sentence);
    this.buildEntitiesFromSentence(stanfordSentences.get(0), context, sentence);

    for (int i = 1; i < stanfordSentences.size(); i++) {
      sentence = new SentenceImpl(stanfordSentences.get(i).toString(), context,
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.SentenceIndexAnnotation.class),
          sentences.get(i - 1));

      context.addSentence(sentence);
      sentences.add(sentence);
      this.buildTokensFromSentence(stanfordSentences.get(i), context, sentence);
      this.buildEntitiesFromSentence(stanfordSentences.get(i), context, sentence);
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
        firstLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        firstLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        firstLabel.get(CoreAnnotations.LemmaAnnotation.class),
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
          currentLabel.get(CoreAnnotations.LemmaAnnotation.class),
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
      if (!"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
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
      } else if (!"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))) {
        sb.append(' ');
        sb.append(token.get(CoreAnnotations.TextAnnotation.class));

        if (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).indexOf(token)
            == stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
          sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start,
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
        }
      } else if (!sb.toString().isEmpty()) {
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

  @Override
  public final String toString() {
    return "StanfordNlp{}";
  }
}
