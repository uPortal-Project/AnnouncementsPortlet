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
package org.jasig.portlet.announcements.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.AnnouncementFilterType;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 */
public class UserConfiguredTopicSubscriptionService implements ITopicSubscriptionService {

    public static final String PREFERENCE_FILTER_TYPE = AnnouncementPreferences.FILTER_TYPE.getKey();
    public static final String PREFERENCE_FILTER_ITEMS = AnnouncementPreferences.FILTER_ITEMS.getKey();

	private Log log = LogFactory.getLog(getClass());
		
	private IAnnouncementService announcementService;
	private Topic emergencyTopic;

    public void setAnnouncementService(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    public void setEmergencyTopic(Topic emergencyTopic) {
        this.emergencyTopic = emergencyTopic;
        log.debug("Emergency Topic assigned successfully.");
    }

    @PostConstruct
	public void init() {
		// save the emergencyTopic to the database (if it's not there already)
		// if the emergencyTopic exists, update it to reflect changes in the spring config
		Topic t = null;
		try {
			t = announcementService.getEmergencyTopic();
		} catch (Exception ex) {
			log.warn("Could not load emergencyTopic from database (OK during unit tests or after dbinit): " + ex.getMessage());
		}
		
		if (t == null && emergencyTopic != null) {
			announcementService.addOrSaveTopic(emergencyTopic);
		} else {
			emergencyTopic = t;
		}
	}

    public List<TopicSubscription> getTopicSubscription(PortletRequest request, boolean includeEmergency) throws PortletException {
        
        List<TopicSubscription> subscriptions = new ArrayList<TopicSubscription>();
        List<TopicSubscription> subSaved = null;
        // must reload all topics each time, in case new ones were added by admins since last visit
        List<Topic> allTopics = announcementService.getAllTopics();
        
        if (request.getRemoteUser() == null) {
            subSaved = new ArrayList<TopicSubscription>();
        } 
        else { 
            try {
                subSaved = announcementService.getTopicSubscriptionFor(request);
            } catch (Exception e) {
                log.error("ERROR getting topic subscriptions for "+request.getRemoteUser()+": "+e.getMessage());
            }
        }
        
        String user = request.getRemoteUser();
        if (user == null) {
            user = "guest";
        }

        PortletPreferences prefs = request.getPreferences();
        AnnouncementFilterType filterType =
            AnnouncementFilterType.valueOf(prefs.getValue(PREFERENCE_FILTER_TYPE,AnnouncementFilterType.BLACKLIST.getKey()));

        List<String> filterItems = Arrays.asList(prefs.getValues(PREFERENCE_FILTER_ITEMS,new String[0]));

        if (subSaved != null) {
            log.debug("Found DisplayPrefs for "+user);
            // we got some preferences from the database for this user, so
            // lets cycle through all the current topics and if any new PUSHED_FORCED 
            // or PUSHED_INITIAL show up, add them to the subscription
            
            for (Topic topic: allTopics) {

                final String title = topic.getTitle();
                if(isFiltered(topic,filterType,filterItems))
                {
                    if (log.isDebugEnabled()) {
                        log.debug("Topic " + title + " has been filtered from user " + user);
                    }
                    continue;
                }

                boolean allowedToViewTopic = false;
                
                // check that this user should be looking at this topic
                allowedToViewTopic = UserPermissionChecker.inRoleForTopic(request, "audience", topic);
                
                if (allowedToViewTopic && topic.getSubscriptionMethod() == Topic.PUSHED_FORCED /* &&
                        !topicSubscriptionExists( topic, subSaved ) */) {
                    // It's not allowable to have a TopicSubscription record 
                    // disabling this topic, but it's possible if the topic was 
                    // previously optional;  remove the record if so...
                    TopicSubscription invalid = null;
                    for (TopicSubscription ts : subSaved) {
                        if (ts.getTopic().equals(topic) && ts.getSubscribed().equals(Boolean.FALSE)) {
                            // Don't do the work in the loop b/c we also have to 
                            // remove it from the list we're iterating over
                            invalid = ts;
                        }
                    }
                    if (invalid != null) {
                        // Prune it
                        if (log.isDebugEnabled())
                            log.debug("Removing invalid TopicSubscription topic ["+topic.getId()+"] for "+user);
                        subSaved.remove(invalid);
                        announcementService.deleteTopicSubscription(invalid);
                    }
                    if (!topicSubscriptionExists(topic, subSaved)) {
                        if (log.isDebugEnabled())
                            log.debug("Adding missing PUSHED_FORCED topic ["+topic.getId()+"] for "+user);
                        subscriptions.add(new TopicSubscription(user, topic, Boolean.TRUE));
                    }
                }
                else if (allowedToViewTopic && 
                        topic.getSubscriptionMethod() == Topic.PUSHED_INITIAL &&
                        !topicSubscriptionExists( topic, subSaved )) {
                    // this is a PUSHED_INITIAL topic that we have not set a preference for yet
                    if (log.isDebugEnabled())
                        log.debug("Adding missing PUSHED_INITIAL topic ["+topic.getId()+"] for "+user);
                    subscriptions.add(new TopicSubscription(user, topic, Boolean.TRUE));
                }
                else if (allowedToViewTopic && 
                        topic.getSubscriptionMethod() == Topic.PULLED &&
                        !topicSubscriptionExists( topic, subSaved )) {
                    // must be an optional topic that's new and hasn't been seen before
                    if (log.isDebugEnabled())
                        log.debug("Adding missing PULLED topic ["+topic.getId()+"] for "+user);
                    subscriptions.add(new TopicSubscription(user, topic, Boolean.FALSE));
                }
                
                // if the topic is present, but no longer in audience group, we must remove it
                if (!allowedToViewTopic && topicSubscriptionExists(topic, subSaved)) {
                    TopicSubscription toRemove = null;
                    for (TopicSubscription ts: subSaved) {
                        if (ts.getTopic().equals(topic)) {
                            toRemove = ts;
                        }
                    }
                    if (toRemove != null) {
                        subSaved.remove(toRemove);
                        // We're here because a DB record is no longer 
                        // allowable... remove from persistence as well!
                        announcementService.deleteTopicSubscription(toRemove);
                    }
                }
            }
        
            subscriptions.addAll(subSaved);

            if (includeEmergency) {
                // add the emergency topic for everyone, but don't save the topicsubscription to the database since it's implied
                emergencyTopic = announcementService.getEmergencyTopic();
                subscriptions.add(new TopicSubscription(user, emergencyTopic, Boolean.TRUE));
            }
        
            return subscriptions;
            
        }
        else {
            throw new PortletException("Could not determine/create subscription preferences for user "+user);
        }
    }
	
	public List<TopicSubscription> getTopicSubscriptionEdit(RenderRequest request) throws PortletException {
		return getTopicSubscription(request, false);
	}
	
	public List<TopicSubscription> getTopicSubscription(PortletRequest request) throws PortletException {
		return getTopicSubscription(request, true);
	}
	
	/**
	 * Useful for checking if a given topic already has a subscription preference for this user
	 * @param topic
	 * @return
	 */
	private boolean topicSubscriptionExists(Topic topic, List<TopicSubscription> subscriptions) {
		for (TopicSubscription ts: subscriptions) {
			if (ts.getTopic().getId().compareTo(topic.getId()) == 0) {
				if (log.isDebugEnabled()) {
					log.debug("Topic ["+topic.getId()+": "+topic.getTitle()+"] was found in TopicSubscription [Topic: "+ts.getTopic().getId()+" "+ts.getTopic().getTitle()+"] for "+ts.getOwner());
				}
				return true;
			}
			if (log.isDebugEnabled()) {
				log.debug("Topic ["+topic.getId()+": "+topic.getTitle()+"] is not referenced in TopicSubscription [Topic: "+ts.getTopic().getId()+" "+ts.getTopic().getTitle()+"] for "+ts.getOwner());
			}
		}
		return false;
	}

    private boolean isFiltered(Topic topic,AnnouncementFilterType filterType,List<String> filterItems)
    {
        if(topic == null) return false;
        if(filterItems ==  null) return false;

        String title = topic.getTitle();
        switch(filterType)
        {
            case BLACKLIST:
            {
                return filterItems.contains(title);
            }
            case WHITELIST:
            {
                return !filterItems.contains(title);
            }
        }
        return false;
    }
}
