package fr.eurecom.stanfordnlptonif.configuration;

import org.hibernate.validator.constraints.*;

/**
 * Created by ovarene on 04/02/2016.
 */
public class PosConfiguration {

  @NotEmpty
  private String model;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

}
