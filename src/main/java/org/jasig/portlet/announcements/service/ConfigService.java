/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.service;

import org.jasig.portlet.announcements.model.AnnouncementConfiguration;
import org.jasig.portlet.announcements.model.AnnouncementFilterType;
import org.springframework.stereotype.Component;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Component
public class ConfigService implements IConfigService {

    public AnnouncementConfiguration getConfiguration(PortletRequest request)
    {
        PortletPreferences prefs = request.getPreferences();
        AnnouncementConfiguration config = new AnnouncementConfiguration();
        config.setFilterType(prefs.getValue(AnnouncementPreferences.FILTER_TYPE.getKey(), AnnouncementFilterType.BLACKLIST.getKey()));
        config.setFilterItems(prefs.getValues(AnnouncementPreferences.FILTER_ITEMS.getKey(), new String[0]));
        return config;
    }

    public void saveConfiguration(ActionRequest request, AnnouncementConfiguration config)
    {
        PortletPreferences prefs = request.getPreferences();

        try
        {
            prefs.setValue(AnnouncementPreferences.FILTER_TYPE.getKey(), config.getFilterType());
            prefs.setValues(AnnouncementPreferences.FILTER_ITEMS.getKey(),config.getFilterItems().toArray(new String[0]));

            prefs.store();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store configuration", e);
        }
    }
}
