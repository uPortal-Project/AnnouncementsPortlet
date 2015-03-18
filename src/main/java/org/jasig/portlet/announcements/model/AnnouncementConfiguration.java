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
package org.jasig.portlet.announcements.model;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
public class AnnouncementConfiguration implements Serializable {
    private static final long serialVersionUID = 0L;

    public static final String NEWLINE = System.getProperty("line.separator");
    private String filterType = AnnouncementFilterType.BLACKLIST.getKey();
    private List<String> filterItems = new ArrayList<String>();

    public String getFilterType()
    {
        return filterType;
    }

    public void setFilterType(String filterType)
    {
        this.filterType = filterType;
    }

    public List<String> getFilterItems()
    {
        return filterItems;
    }

    public void setFilterItems(List<String> filterItems)
    {
        this.filterItems = filterItems;
    }

    @SuppressWarnings("unchecked")
    public void setFilterItems(String[] filterArray)
    {
        this.filterItems = Arrays.asList(filterArray);
    }

    public String getFilterContent()
    {
        StringBuilder sb = new StringBuilder();
        for(String item : filterItems)
        {
            sb.append(item).append(NEWLINE);
        }
        return sb.toString();
    }


    @SuppressWarnings("unchecked")
    public void setFilterContent(String filterContent)
    {
        if(filterContent != null)
        {
            filterContent = filterContent.replace("\r","");
            this.filterItems =
                Arrays.asList(filterContent.split(NEWLINE));
        } else {
            this.filterItems = new ArrayList<String>();
        }
    }

}
