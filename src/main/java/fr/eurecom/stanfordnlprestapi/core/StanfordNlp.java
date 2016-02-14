package fr.eurecom.stanfordnlprestapi.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import fr.eurecom.stanfordnlprestapi.configuration.PipelineConfiguration;

import fr.eurecom.stanfordnlprestapi.datatypes.Context;
import fr.eurecom.stanfordnlprestapi.datatypes.Entity;
import fr.eurecom.stanfordnlprestapi.datatypes.SentenceImpl;
import fr.eurecom.stanfordnlprestapi.datatypes.TokenImpl;

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.interfaces.Token;

import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullToken;

/**
 * Business class for manipulating Stanford NLP annotations.
 *
 * @author Julien Plu
 */
public class StanfordNlp {
  static final Logger LOGGER = LoggerFactory.getLogger(StanfordNlp.class);
  private String text;
  private final StanfordCoreNLP pipeline;

  public StanfordNlp(final Properties props) {
    this.pipeline = new StanfordCoreNLP(props);
  }

  /**
   * Turn a pipeline configuration into Stanford Core NLP properties.
   *
   * @param conf a pipeline configuration.
   *
   * @return Stanford Core NLP properties from a pipeline configuration.
   */
  public static Properties confToProp(final PipelineConfiguration conf) {
    final Properties props = new Properties();

    props.setProperty("annotators", conf.getAnnotators());
    props.setProperty("pos.model", conf.getPos().getModel());
    props.setProperty("ner.model", conf.getNer().getModel());
    props.setProperty("ner.useSUTime", conf.getNer().getUseSuTime() ? "true" : "false");
    props.setProperty("ner.applyNumericClassifiers",
        conf.getNer().getApplyNumericClassifiers() ? "true" : "false");
    props.setProperty("pos.model", conf.getPos().getModel());

    return props;
  }

  /**
   * Create a context from a text.
   *
   * @param newText Text to process.
   *
   * @return The corresponding context of the text.
   */
  public final Context run(final String newText) {
    this.text = newText; // pas beau !
    final Annotation document = new Annotation(newText);

    this.pipeline.annotate(document);

    return this.buildContext(document.get(CoreAnnotations.SentencesAnnotation.class));
  }

  private Context buildContext(final List<CoreMap> sentences) {
    final Context tmpContext = new Context(this.text, 0, this.text.length());

    this.buildSentencesFromContext(sentences, tmpContext);

    return tmpContext;
  }

  private void buildSentencesFromContext(final List<CoreMap> stanfordSentences,
                                         final Context context) {
    final List<Sentence> sentences = new ArrayList<>();
    Sentence sentence = new SentenceImpl(stanfordSentences.get(0).toString(), context,
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
        stanfordSentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class),
        NullSentence.getInstance());

    context.addSentence(sentence);
    sentences.add(sentence);
    this.buildTokensFromSentence(stanfordSentences.get(0), context, sentence);

    for (int i = 1; i < stanfordSentences.size(); i++) {
      sentence = new SentenceImpl(stanfordSentences.get(i).toString(), context,
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.CharacterOffsetEndAnnotation.class),
          stanfordSentences.get(i).get(CoreAnnotations.SentenceIndexAnnotation.class),
          sentences.get(i - 1));

      context.addSentence(sentence);
      sentences.add(sentence);
      this.buildTokensFromSentence(stanfordSentences.get(i), context, sentence);
    }

    for (int i = 0; i < (sentences.size() - 1); i++) {
      sentences.get(i).nextSentence(sentences.get(i + 1));
    }
  }

  private void buildTokensFromSentence(final CoreMap stanfordSentence, final Context context,
                                       final Sentence sentence) {
    final List<Token> tokens = new ArrayList<>();
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
    tokens.add(token);
    this.buildEntitiesFromSentence(stanfordSentence, context, sentence);

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
      this.buildEntitiesFromSentence(stanfordSentence, context, sentence);
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

        sb.append(token.get(CoreAnnotations.TextAnnotation.class));

        type = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

        if (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).indexOf(token)
            == (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1)) {
          sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start,
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
        }
      } else if (!"O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && !sb.toString().isEmpty()) {
        sb.append(' ');
        sb.append(token.get(CoreAnnotations.TextAnnotation.class));

        if (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).indexOf(token)
            == (stanfordSentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1)) {
          sentence.addEntity(new Entity(sb.toString(), type, sentence, context, start,
              token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
        }
      } else if ("O".equals(token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
          && !sb.toString().isEmpty()) {

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
    return "StanfordNlp{"
        + "text='" + this.text + '\''
        + ", pipeline=" + this.pipeline
        + '}';
  }
}
