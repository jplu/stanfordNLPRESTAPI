package fr.eurecom.stanfordnlptonif.configuration;

import org.hibernate.validator.constraints.*;

/**
 * Created by ovarene on 17/12/2015.
 */
public class PipelineConfiguration {

  // TODO - use Ner and Pos configuration classes and migrate configuration file
  @NotEmpty
  private String annotators;
//  @NotEmpty
//  private String pos_model;
  @NotEmpty
  private PosConfiguration pos;

/*
  @NotEmpty
  private String ner_model;
  @NotEmpty
  private String ner_useSUTime;
  @NotEmpty
  private String ner_applyNumericClassifiers;
*/
  @NotEmpty
  private NerConfiguration ner;


  private String parse_model;

  //###
  public String getAnnotators() { return annotators; }
  public void setAnnotators(String a) { annotators = a; }

  public PosConfiguration getPos() {
    return pos;
  }

  public void setPos(PosConfiguration pos) {
    this.pos = pos;
  }

  public NerConfiguration getNer() {
    return ner;
  }

  public void setNer(NerConfiguration ner) {
    this.ner = ner;
  }

  /*
  public String getPos_model() {
    return pos_model;
  }

  public String getNer_model() {
    return ner_model;
  }

  public void setPos_model(String pos_model) {
    this.pos_model = pos_model;
  }

  public void setNer_model(String ner_model) {
    this.ner_model = ner_model;
  }


  public String getNer_useSUTime() {
    return ner_useSUTime;
  }

  public void setNer_useSUTime(String ner_useSUTime) {
    this.ner_useSUTime = ner_useSUTime;
  }

  public String getNer_applyNumericClassifiers() {
    return ner_applyNumericClassifiers;
  }

  public void setNer_applyNumericClassifiers(String ner_applyNumericClassifiers) {
    this.ner_applyNumericClassifiers = ner_applyNumericClassifiers;
  }
  // */

  public String getParse_model() {
    return parse_model;
  }

  public void setParse_model(String parse_model) {
    this.parse_model = parse_model;
  }

}
