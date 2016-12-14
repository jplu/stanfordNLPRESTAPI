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
package fr.eurecom.stanfordnlprestapi.datatypes;

/**
 * @author Julien Plu on 12/12/2016.
 */
public enum Languages {
  EN("en", "properties/en.properties"),
  ES("es", "properties/es.properties"),
  DE("de", "properties/de.properties"),
  FR("fr", "properties/fr.properties"),
  ZH("zh", "properties/zh.properties");
  
  private String name;
  private String location;
  
  Languages(final String newName, final String newLocation) {
    this.name = newName;
    this.location = newLocation;
  }
  
  public final String getLocation() {
    return this.location;
  }
}
