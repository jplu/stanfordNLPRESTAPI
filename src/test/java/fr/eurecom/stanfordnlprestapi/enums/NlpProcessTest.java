/**
 * This file is part of StanfordNLPRESTAPI.
 *
 * StanfordNLPRESTAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * StanfordNLPRESTAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with StanfordNLPRESTAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.eurecom.stanfordnlprestapi.enums;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julien Plu
 */
public class NlpProcessTest {
  static final Logger LOGGER = LoggerFactory.getLogger(NlpProcessTest.class);

  public NlpProcessTest() {
  }

  /**
   * Test {@link NlpProcess#property()} method.
   */
  @Test
  public final void testProperty() {
    Assert.assertEquals("Issue with the NER property name", "NER",  NlpProcess.NER.property());
    Assert.assertEquals("Issue with the POS property name", "POS",  NlpProcess.POS.property());
  }

  /**
   * Test {@link NlpProcess#valueOf(String)} method.
   */
  @Test
  public final void testValueOf() {
    Assert.assertEquals("Issue with the value of NER", NlpProcess.NER, NlpProcess.valueOf("NER"));
    Assert.assertEquals("Issue with the value of POS", NlpProcess.POS, NlpProcess.valueOf("POS"));
  }

  /**
   * Test {@link NlpProcess#values()} method.
   */
  @Test
  public final void testValues() {
    Assert.assertArrayEquals("Issue to get all values", new NlpProcess[]{NlpProcess.NER,
        NlpProcess.POS}, NlpProcess.values());
  }
}
