package fr.eurecom.stanfordnlptonif.configuration;

import org.hibernate.validator.constraints.*;

/**
 * Created by ovarene on 04/02/2016.
 */
public class NerConfiguration {

  @NotEmpty
  private String model;
  @NotEmpty
  private Boolean useSUTime;
  @NotEmpty
  private Boolean applyNumericClassifiers;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Boolean getUseSUTime() {
    return useSUTime;
  }

  public void setUseSUTime(Boolean useSUTime) {
    this.useSUTime = useSUTime;
  }

  public Boolean getApplyNumericClassifiers() {
    return applyNumericClassifiers;
  }

  public void setApplyNumericClassifiers(Boolean applyNumericClassifiers) {
    this.applyNumericClassifiers = applyNumericClassifiers;
  }

}
