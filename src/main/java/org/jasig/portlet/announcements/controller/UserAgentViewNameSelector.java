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
package org.jasig.portlet.announcements.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletPreferences;

import org.springframework.beans.factory.InitializingBean;

public class UserAgentViewNameSelector implements IViewNameSelector, InitializingBean {

    private Map<String,String> userAgentMappings;
    private final Map<Pattern,String> patterns = new HashMap<Pattern,String>();

    private boolean isRespondr;
    public static final String PREFERENCE_RESPONDR = "AnnouncementsViewController.respondr";

    public void afterPropertiesSet() {
        // Compile our patterns
        for (Map.Entry<String,String> y : userAgentMappings.entrySet()) {
            patterns.put(Pattern.compile(y.getKey()), y.getValue());
        }
    }

    public String select(PortletRequest req, String baseViewName) {

        PortletPreferences pref = req.getPreferences();
        isRespondr = Boolean.valueOf(pref.getValue(PREFERENCE_RESPONDR,"true"));

        // Assertions.
        if (req == null) {
            String msg = "Argument 'req' cannot be null";
            throw new IllegalArgumentException(msg);
        }

        StringBuilder rslt = new StringBuilder(baseViewName);

        String userAgent = req.getProperty("user-agent");
        if (isRespondr == true) {
          rslt.append(""); // don't append any view
        } else {
          if (userAgent != null && patterns.size() != 0) {
            for (Map.Entry<Pattern,String> y : patterns.entrySet()) {
              if (y.getKey().matcher(userAgent).matches()) {
                  rslt.append(y.getValue());
                  break;
              }
            }
          }
        }

        return rslt.toString();

    }

    public void setUserAgentMappings(Map<String,String> userAgentMappings) {
        this.userAgentMappings = Collections.unmodifiableMap(userAgentMappings);
    }

}
