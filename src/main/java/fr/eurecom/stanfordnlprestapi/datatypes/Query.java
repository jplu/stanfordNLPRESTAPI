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
package fr.eurecom.stanfordnlprestapi.datatypes;

import fr.eurecom.stanfordnlprestapi.annotations.Content;

import org.hibernate.validator.constraints.URL;

/**
 * @author Julien Plu
 */
@Content
public class Query {
  private String content;
  @URL
  private String url;
  
  public Query() {
  }
  
  public final String getContent() {
    return this.content;
  }
  
  public final void setContent(final String newContent) {
    this.content = newContent;
  }
  
  public final String getUrl() {
    return this.url;
  }
  
  public final void setUrl(final String newUrl) {
    this.url = newUrl;
  }
}
