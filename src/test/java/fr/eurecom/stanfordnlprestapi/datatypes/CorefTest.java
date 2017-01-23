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

import fr.eurecom.stanfordnlprestapi.interfaces.Sentence;
import fr.eurecom.stanfordnlprestapi.nullobjects.NullSentence;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Julien Plu
 */
public class CorefTest {
  /**
   * Test {@link Coref#equals(Object)} method.
   */
  @Test
  public final void testEquals() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Context context2 = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 60);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Sentence sentence2 = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 2, NullSentence.getInstance());
    final Coref coref = new Coref("She", "Natalie Portman", 41, 44, sentence, context);
    final Coref coref2 = new Coref("She", "Natalie Portman", 41, 44, sentence2, context);
    final Coref coref3 = new Coref("She", "Natalie Portman", 41, 44, sentence, context2);
    final Coref coref4 = new Coref("Se", "Natalie Portman", 41, 44, sentence, context);
    final Coref coref5 = new Coref("She", "Natalie Porman", 41, 44, sentence, context);
    final Coref coref6 = new Coref("She", "Natalie Portman", 40, 44, sentence, context);
    final Coref coref7 = new Coref("She", "Natalie Portman", 41, 43, sentence, context);
    
    Assert.assertFalse("Issue with equals on the property sentence", coref.equals(coref2));
    Assert.assertFalse("Issue with equals on the property context", coref.equals(coref3));
    Assert.assertFalse("Issue with equals on the property coref", coref.equals(coref4));
    Assert.assertFalse("Issue with equals on the property head", coref.equals(coref5));
    Assert.assertFalse("Issue with equals on the property start", coref.equals(coref6));
    Assert.assertFalse("Issue with equals on the property end", coref.equals(coref7));
    Assert.assertEquals("Issue with equals on the same object", coref, coref);
    Assert.assertFalse("Issue with equals on null", coref.equals(null));
    Assert.assertFalse("Issue with equals on different object", coref.equals(sentence));
  }
  
  /**
   * Test {@link Coref#hashCode()} method.
   */
  @Test
  public final void testHashCode() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Coref coref = new Coref("She", "Natalie Portman", 41, 44, sentence, context);
    final Coref coref2 = new Coref("She", "Natalie Portman", 41, 44, sentence, context);
    
    Assert.assertNotSame("The two coref are the same", coref, coref2);
    Assert.assertEquals("Issue to compute the hascode of an entity", (long) coref.hashCode(),
        (long) coref2.hashCode());
  }
  
  /**
   * Test {@link Coref#toString()} method.
   */
  @Test
  public final void testToString() {
    final Context context = new Context("My favorite actress is: Natalie Portman. She is very "
        + "stunning.", 0, 62);
    final Sentence sentence = new SentenceImpl("My favorite actress is: Natalie Portman.", context,
        0, 40, 1, NullSentence.getInstance());
    final Coref coref = new Coref("She", "Natalie Portman", 41, 44, sentence, context);
    
    Assert.assertEquals("Issue to get the proper toString value", "Coref{coref='She', head='Natalie"
            + " Portman', start=41, end=44, sentence=1, context=[0,62]}", coref.toString());
  }
}
