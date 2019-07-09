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
package org.jasig.portlet.announcements.mvc;

import javax.portlet.PortletRequest;

/**
 * <p>ThemeViewNameSelector class.</p>
 *
 * @author Unknown
 * @version $Id: $Id
 */
public class ThemeViewNameSelector implements IViewNameSelector {

  /** Constant <code>THEME_NAME_PROPERTY="themeName"</code> */
  protected static final String THEME_NAME_PROPERTY = "themeName";
  /** Constant <code>MOBILE_THEMES_KEY="mobileThemes"</code> */
  protected static final String MOBILE_THEMES_KEY = "mobileThemes";
  /** Constant <code>MOBILE_THEMES_DEFAULT</code> */
  protected static final String[] MOBILE_THEMES_DEFAULT = new String[] {"UniversalityMobile"};

  private String mobileKey = "jqm";

  /**
   * <p>Setter for the field <code>mobileKey</code>.</p>
   *
   * @param mobileKey a {@link java.lang.String} object.
   */
  public void setMobileKey(String mobileKey) {
    this.mobileKey = mobileKey;
  }

  /** {@inheritDoc} */
  public String select(PortletRequest req, String baseViewName) {

    // Assertions.
    if (req == null) {
      String msg = "Argument 'req' cannot be null";
      throw new IllegalArgumentException(msg);
    }

    if (isMobile(req)) {
      return baseViewName.concat(".").concat(mobileKey);
    } else {
      return baseViewName;
    }
  }

  /**
   * <p>isMobile.</p>
   *
   * @param request a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  protected boolean isMobile(PortletRequest request) {
    String[] mobileThemes =
        request.getPreferences().getValues(MOBILE_THEMES_KEY, MOBILE_THEMES_DEFAULT);
    String themeName = request.getProperty(THEME_NAME_PROPERTY);
    if (themeName == null) {
      return false;
    }

    for (String theme : mobileThemes) {
      if (themeName.equals(theme)) {
        return true;
      }
    }

    return false;
  }
}
