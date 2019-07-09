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

/**
 * Utility class to scrub a string of unwanted characters
 *
 * @author Unknown
 * @version $Id: $Id
 */
public class UnwantedCharacterScrubber {
  private static enum UnwantedCharacters {
    CONTROL1("\u00E2\u0080", ""), //Looks to always be together when conversion issues arise.
    SET_TRANSMIT_STATE("\u0093", ""),
    START_OF_STRING("\u0098", ""),
    CONTROL2("\u0099", ""),
    STRING_TERM("\u009C", ""),
    OP_SYS_CMD("\u009D", ""),
    EN_DASH("\u2013", "-"),
    EM_DASH("\u2014", "-"),
    HOR_BAR("\u2015", "-"),
    DOUBLE_VER_LINE("\u2016", "|"),
    DOUBLE_LOW_LINE("\u2017", "_"),
    LEFT_SINGLE_QUOTE("\u2018", "\'"),
    RIGHT_SINGLE_QUOTE("\u2019", "\'"),
    SINGLE_LOW_QUOTE("\u201A", ","),
    SINGLE_HIGH_REV_QUOTE("\u201B", "\'"),
    LEFT_DOUBLE_SMART_QUOTE("\u201C", "\""),
    RIGHT_DOUBLE_SMART_QUOTE("\u201D", "\""),
    DOUBLE_LOW_QUOTE("\u201E", "\""),
    HOR_ELLIPSIS("\u2026", "..."),
    PRIME("\u2032", "\'"),
    DOUBLE_PRIME("\u2033", "\"");

    private String unicodeValue;
    private String replacementValue;

    UnwantedCharacters(String uValue, String rValue) {
      this.unicodeValue = uValue;
      this.replacementValue = rValue;
    }

    public String getUnicodeValue() {
      return unicodeValue;
    }

    public String getReplacementValue() {
      return replacementValue;
    }
  }

  /**
   * Loops through the UnwantedCharacters enum and does a replace all for each value.
   *
   * @param str target string to convert
   * @return a {@link java.lang.String} object.
   */
  public String scrubUnwantedCharacters(String str) {
    for (UnwantedCharacters conversion : UnwantedCharacters.values()) {
      str = str.replaceAll(conversion.getUnicodeValue(), conversion.getReplacementValue());
    }
    return str;
  }
}
