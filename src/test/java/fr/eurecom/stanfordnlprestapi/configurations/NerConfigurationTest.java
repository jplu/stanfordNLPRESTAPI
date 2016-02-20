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
package fr.eurecom.stanfordnlprestapi.configurations;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Julien Plu
 */
public class NerConfigurationTest {
  static final Logger LOGGER = LoggerFactory.getLogger(NerConfigurationTest.class);

  public NerConfigurationTest(){
  }

  /**
   * Test {@link NerConfiguration#toString()} method.
   */
  @Test
  public final void testToString() {
    final NerConfiguration conf = new NerConfiguration();

    conf.setApplyNumericClassifiers(true);
    conf.setUseSuTime(true);
    conf.setModel("");

    Assert.assertEquals("Issue to get the proper toString value", "NerConfiguration{model='', "
        + "useSUTime=true, applyNumericClassifiers=true}",
        conf.toString());
  }
}
