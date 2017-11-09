/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.announcements.model.validators;

/**
 * Utility class for validation efforts
 */
public class ValidationHelper {
  private static enum SpecialMsCharacters {
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

    SpecialMsCharacters(String uValue, String rValue) {
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
   * Loops through the SpecialMsCharacters enum and does a replace all for each value.
   * @param str target string to convert
   * @return
   */
  public String convertSpecialMsCharacters(String str) {
    for (SpecialMsCharacters conversion : SpecialMsCharacters.values()) {
      str = str.replaceAll(conversion.getUnicodeValue(), conversion.getReplacementValue());
    }
    return str;
  }
}
