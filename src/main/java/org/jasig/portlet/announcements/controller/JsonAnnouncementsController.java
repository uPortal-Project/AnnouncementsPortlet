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
package org.jasig.portlet.announcements.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.AnnouncementSortStrategy;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.ITopicSubscriptionService;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * @author eolsson
 *
 */
@Controller
@RequestMapping("VIEW")
public class JsonAnnouncementsController {
 
    private static final String GUEST_USERNAME = "guest";
    private static final Logger logger = Logger.getLogger(JsonAnnouncementsController.class);
    private Cache guestAnnouncementCache = null;

    @Autowired
    private ITopicSubscriptionService tss = null;

    @Autowired
    private final IAnnouncementService announcementService = null;

    @Autowired
    private CacheManager cm = null;
    
    @Autowired(required=true)
    private final IViewNameSelector viewNameSelector = null;

    @Autowired
    private final UserPermissionCheckerFactory userPermissionCheckerFactory = null;

    private static final String PREFERENCE_SORT_STRATEGY = "AnnouncementsViewController.AnnouncementSortStrategy";
    private static final String DEFAULT_SORT_STRATEGY = "START_DISPLAY_DATE_ASCENDING";
        
    
    /**
     * Main method of this json controller. Calculates which topics should be shown to
     * this user and which announcements to show from those topics.
     * @param model
     * @param request
     * @param from
     * @param toimport java.util.Date;
     * @return
     * @throws PortletException
     */
    /*
     * @RequestBody not supported
     */
    @SuppressWarnings("unchecked")
    @ResourceMapping
    public String jsonView(
    		@RequestParam(value="topics") List<String> topicTitlesToSyndicate,
    		Model model, ResourceRequest request)
            
        throws PortletException {

    	logger.debug("in jsonView with "+topicTitlesToSyndicate.toString());
        PortletPreferences prefs = request.getPreferences();

        List<Announcement> announcements;
        List<Announcement> emergencyAnnouncements;
        Map<String,Set<Announcement>> jsonResponse = new HashMap<String, Set<Announcement>>();

        Element guestCacheElement = null;
        Element emergCacheElement = null;
        guestCacheElement = guestAnnouncementCache.get("guest");
        emergCacheElement = guestAnnouncementCache.get("emergency");

        final Boolean isGuest = isGuest(request);
        if (!isGuest || (guestCacheElement == null || emergCacheElement == null)) {

            // create a new announcement list
            announcements = new ArrayList<Announcement>();
            emergencyAnnouncements = new ArrayList<Announcement>();

            
            // fetch the user's topic subscription from the database
            List<TopicSubscription> myTopics = tss.getTopicSubscription(request);

            // add all the published announcements of each subscribed topic to the announcement list
            // to emergency announcements into their own list
            for (TopicSubscription topicSub : myTopics) {

                final Topic topic = topicSub.getTopic();
            	
            	// We only want the requested ones...
                if (!topicTitlesToSyndicate.contains(topic.getTitle())) {
                    continue;
                }
            	
                if (topicSub.getSubscribed() && topicSub.getTopic().getPublishedAnnouncements().size() > 0) {
                	jsonResponse.put(topicSub.getTopic().getTitle(), topicSub.getTopic().getPublishedAnnouncements());
                }
            }

            // sort the list (since they are not sorted from the database)
            Comparator<Announcement> sortStrategy = AnnouncementSortStrategy.getStrategy(prefs.getValue(PREFERENCE_SORT_STRATEGY,DEFAULT_SORT_STRATEGY));
            Collections.sort(announcements,sortStrategy);
            Collections.sort(emergencyAnnouncements,sortStrategy);

            if (isGuest) {
                if (logger.isDebugEnabled())
                    logger.debug("Guest cache expired. Regenerating guest cache.");

                guestAnnouncementCache.put(new Element("guest", announcements));
                guestAnnouncementCache.put(new Element("emergency", emergencyAnnouncements));
            }
        }
        else {
            // we're a guest and we're within the cache timeout period, so return the cached announcements
            if (logger.isDebugEnabled())
                logger.debug("Guest cache valid. Using guest cache.");
            announcements = (List<Announcement>) guestCacheElement.getObjectValue();
            emergencyAnnouncements = (List<Announcement>) emergCacheElement.getObjectValue();
        }

        
        model.addAttribute("jsonResponse", jsonResponse);
        
        return "json";
    }  
    
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        guestAnnouncementCache = cm.getCache("guestAnnouncementCache");
        if (guestAnnouncementCache == null) {
            throw new BeanCreationException("Required guestAnnouncementCache could not be loaded.");
        }
        else {
            logger.debug("guestAnnouncementCache created.");
        }
    }

    public void setCm(CacheManager cm) {
        this.cm = cm;
    }
    
    public boolean isGuest(PortletRequest req) {
        boolean rslt = (req.getRemoteUser() == null || req.getRemoteUser().equalsIgnoreCase(GUEST_USERNAME));
        logger.debug("isGuest is: "+Boolean.toString(rslt));
        logger.debug("remoteUser is: "+req.getRemoteUser());
        return rslt;
  	}
}
