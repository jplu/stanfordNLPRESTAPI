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
package fr.eurecom.stanfordnlprestapi.annotations;

import fr.eurecom.stanfordnlprestapi.datatypes.Query;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Julien Plu
 */
public class ContentValidator implements ConstraintValidator<Content, Query> {
  @Override
  public final void initialize(final Content newA) {
  }
  
  @Override
  public final boolean isValid(final Query newT,
                               final ConstraintValidatorContext newConstraintValidatorContext) {
    boolean res = true;
    
    if (newT.getContent() == null && newT.getUrl() == null) {
      res = false;
    }
    
    if (newT.getContent() != null && newT.getUrl() != null) {
      res = false;
    }
    
    return res;
  }
}
