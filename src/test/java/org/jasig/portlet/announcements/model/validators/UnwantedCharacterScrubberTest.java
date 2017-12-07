/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.model.validators;

import junit.framework.TestCase;
import org.junit.Test;

public class UnwantedCharacterScrubberTest extends TestCase {
  @Test
  public void testReplacementAllVisible() {
    String s = "A–A—A―A‖A‗A‚A‛A“A”A‘A’A„A…A′A″AâA";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "A-A-A-A|A_A,A'A\"A\"A'A'A\"A...A'A\"AâA");
  }

  @Test
  public void testReplacementTypical() {
    String s = "Maintain Don't Gain “test” \n" +
        "‘test’ \n" +
        "Here is some text with a dash – and some other text";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "Maintain Don't Gain \"test\" \n" +
        "'test' \n" +
        "Here is some text with a dash - and some other text");
  }


  @Test
  public void testReplacementControlCharacters() {
    String s = "â â\u0080\u0098A\u0093\u0099\u009C\u009D";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "â A");
  }
}