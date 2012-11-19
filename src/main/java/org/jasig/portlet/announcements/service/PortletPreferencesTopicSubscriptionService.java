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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation of {@link ITopicSubscriptionService} associates users
 * with topics based on PortletProferences.  These may have been established by
 * the user, or (perhaps more importantly) may have been set by an admin at
 * publish time.  Whether configured by user or admin, this approach allows
 * different topicsin different placements of the portlet.
 *
 * @author awills
 */
public class PortletPreferencesTopicSubscriptionService implements ITopicSubscriptionService {

    // Instance Members.
    private IAnnouncementService announcementService;
    private Log log = LogFactory.getLog(getClass());

    @Autowired(required=true)
    private UserPermissionCheckerFactory userPermissionCheckerFactory = null;
    /*
     * Public API.
     */

    /**
     * Name for the PortletPreference listing topics that will appear in this
     * portlet placement, provided the user is authorized.
     */
    public static final String PREFERENCE_TOPIC_WHITELIST = "PortletPreferencesTopicSubscriptionService.topicWhitelist";

    public static final String PREFERENCE_TOPICS_ADDED = "PortletPreferencesTopicSubscriptionService.topicsAdded";
    public static final String PREFERENCE_TOPICS_REMOVED = "PortletPreferencesTopicSubscriptionService.topicsRemoved";

    public static final String UNAUTHENTICATED_USER = "guest";

    public List<TopicSubscription> getTopicSubscription(RenderRequest req, boolean includeEmergency) throws PortletException {

        List<TopicSubscription> rslt = new ArrayList<TopicSubscription>();

        // Evaluate the user
        String username = req.getRemoteUser();
        if (username == null) {
            // Use the guest user
            username = UNAUTHENTICATED_USER;
        }

        // Evaluate the whitelist
        PortletPreferences prefs = req.getPreferences();
        List<String> whitelist = Arrays.asList(prefs.getValues(PREFERENCE_TOPIC_WHITELIST, new String[0]));

        // Things added or removed by the user
        List<String> topicsAdded = Arrays.asList(prefs.getValues(PREFERENCE_TOPICS_ADDED, new String[0]));
        List<String> topicsRemoved = Arrays.asList(prefs.getValues(PREFERENCE_TOPICS_REMOVED, new String[0]));

        List<Topic> topics = announcementService.getAllTopics();
        for (Topic p : topics) {

            // We only consider topics that are on the whitelist that the user may access
            final String title = p.getTitle();

            // Step [1]:  Skip topics that aren't on the whitelist
            if (!whitelist.contains(title)) {
                if (log.isDebugEnabled()) {
                    log.debug("Topic '" + title + "' removed from user '" +
                            username + "' because it's not on the whitelist");
                }
                continue;
            }

            // Step [2]:  Skip topics for which the user is not in the sudience
            if(!userPermissionCheckerFactory.createUserPermissionChecker(req, p).isAudience()) {
                if (log.isDebugEnabled()) {
                    log.debug("Topic '" + title + "' removed from user '" +
                            username + "' because he/she is not in the audience");
                }
                continue;
            }

            // Now we know the user at least *may* view it;  default is "on"
            TopicSubscription ts = new TopicSubscription(username, p, true);

            // Step [3]:  Disable PULLED topics which the user han't added
            if (p.getSubscriptionMethod() == Topic.PULLED && !topicsAdded.contains(title)) {
                if (log.isDebugEnabled()) {
                    log.debug("Topic '" + title + "' disabled for user '" +
                            username + "' because he/she did not opt-in");
                }
                ts.setSubscribed(false);
            }

            // Step [4]:  Disable PUSHED_INITIAL topics which the user has specifically declined
            if (p.getSubscriptionMethod() == Topic.PUSHED_INITIAL && topicsRemoved.contains(title)) {
                if (log.isDebugEnabled()) {
                    log.debug("Topic '" + title + "' disabled for user '" +
                            username + "' because he/she opted-out");
                }
                ts.setSubscribed(false);
            }

            rslt.add(ts);

        }

        return rslt;

    }

    public List<TopicSubscription> getTopicSubscription(RenderRequest req) throws PortletException {
        return getTopicSubscription(req, true);
    }

    public List<TopicSubscription> getTopicSubscriptionEdit(RenderRequest req) throws PortletException {
        return getTopicSubscription(req, false);
    }

    @Autowired
    public void setAnnouncementService(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

}