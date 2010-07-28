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
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.apache.log4j.Logger;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.ITopicSubscriptionService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author eolsson
 * 
 */
@Controller
public class AnnouncementsViewController implements InitializingBean {
	
	private static final String GUEST_USERNAME = "guest";
	private static final Logger logger = Logger.getLogger(AnnouncementsViewController.class);
	private Cache guestAnnouncementCache = null;
	private final int pageSize = 5;
	private Boolean showDate = Boolean.TRUE;
	
	@Autowired
	private ITopicSubscriptionService tss = null;
	
	@Autowired
	private IAnnouncementService announcementService = null;
	
	@Autowired
	private CacheManager cm = null;
	
	@Autowired(required=true)
	private IViewNameSelector viewNameSelector = null;
	
	public static final String PREFERENCE_DISABLE_EDIT = "AnnouncementsViewController.PREFERENCE_DISABLE_EDIT";
	
	/**
	 * Main method of this display controller. Calculates which topics should be shown to 
	 * this user and which announcements to show from those topics. 
	 * @param model
	 * @param request
	 * @param from
	 * @param to
	 * @return
	 * @throws PortletException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("VIEW")
	public String mainView(Model model, RenderRequest request,
			@RequestParam(value="from",required=false) Integer from, 
			@RequestParam(value="to",required=false) Integer to) 
		throws PortletException {
				
		if (from == null || to == null) {
			from = 0;
			to = this.pageSize;
		}
		
		boolean isGuest = (request.getRemoteUser() == null 
				|| request.getRemoteUser().equalsIgnoreCase(GUEST_USERNAME));
			
		List<Announcement> announcements;
		List<Announcement> emergencyAnnouncements;

		
		if (!isGuest || guestAnnouncementCache.getStatus() != Status.STATUS_ALIVE || 
				(new Date(guestAnnouncementCache.getTimeToLiveSeconds() * 1000)).before(new Date())) {
			
			// create a new announcement list 
			announcements = new ArrayList<Announcement>();
			emergencyAnnouncements = new ArrayList<Announcement>();
			
			// fetch the user's topic subscription from the database
			List<TopicSubscription> myTopics = tss.getTopicSubscription(request);
			
			// add all the published announcements of each subscribed topic to the announcement list
			// to emergency announcements into their own list
			for (TopicSubscription ts: myTopics) {
				if (ts.getSubscribed() && ts.getTopic().getSubscriptionMethod() != Topic.EMERGENCY) {
					announcements.addAll(ts.getTopic().getPublishedAnnouncements());
				}
				else if (ts.getSubscribed() && ts.getTopic().getSubscriptionMethod() == Topic.EMERGENCY) {
					emergencyAnnouncements.addAll(ts.getTopic().getPublishedAnnouncements());
				}
			}
			
			// sort the list (since they are not sorted from the database)
			Collections.sort(announcements);
			Collections.sort(emergencyAnnouncements);
			
			if (isGuest) {
				if (logger.isDebugEnabled())
					logger.debug("Guest cache expired. Regenerating guest cache.");
				
				// if the cache is dead, recreate it
				if (guestAnnouncementCache.getStatus() != Status.STATUS_ALIVE) {
					try {
						this.afterPropertiesSet();
					} catch (Exception e) {
						logger.error("Failed to recreate cache",e);
						throw new PortletException("Problem initializing announcement cache.");
					}
				}
				
				guestAnnouncementCache.put(new Element("guest", announcements));
				guestAnnouncementCache.put(new Element("emergency", emergencyAnnouncements));
			}
		} 
		else {
			// we're a guest and we're within the cache timeout period, so return the cached announcements
			if (logger.isDebugEnabled())
				logger.debug("Guest cache valid until "+(new Date(guestAnnouncementCache.getTimeToLiveSeconds() * 1000)).toString()+". Using guest cache.");
			announcements = (List<Announcement>) guestAnnouncementCache.get("guest").getObjectValue();
			emergencyAnnouncements = (List<Announcement>) guestAnnouncementCache.get("emergency").getObjectValue();
		}
		
		// create a shortened list
		List<Announcement> announcementsShort = new ArrayList<Announcement>();
		
		// if the announcement list is already short, then just reference it
		if (announcements.size() < to - from) {
			announcementsShort = announcements;
		}
		// otherwise, just take the range requested and pass it along to the view
		else {
			for (int i=from; i<to && announcements.size() > i; i++) {
				if (announcements.get(i) != null) {
					announcementsShort.add(announcements.get(i));
				}
			}
		}
		
		// add a marker to the view to render different content for guest user
		if (isGuest) {
			model.addAttribute("isGuest", Boolean.TRUE);
		} else {
			model.addAttribute("isGuest", Boolean.FALSE);
		}
		
		// Disable the edit link where appropriate
        PortletPreferences prefs = request.getPreferences();
        Boolean disableEdit = Boolean.valueOf(prefs.getValue(PREFERENCE_DISABLE_EDIT, "false"));
        model.addAttribute("disableEdit", disableEdit);
		
		model.addAttribute("showDate", showDate);
		model.addAttribute("from", new Integer(from));
		model.addAttribute("to", new Integer(to));
		model.addAttribute("hasMore", (announcements.size() > to));
		model.addAttribute("increment", new Integer(pageSize));
		model.addAttribute("announcements", announcementsShort);
		model.addAttribute("emergency", emergencyAnnouncements);
		
		return viewNameSelector.select(request, "displayAnnouncements");
	}
	
	@RequestMapping(value="VIEW",params="action=displayFullAnnouncement")
	public String displayFullAnnouncement(Model model, RenderRequest request,
			@RequestParam("announcementId") String announcementId) throws Exception {
		
		Long annId = Long.valueOf( announcementId );
		
		Announcement announcement = announcementService.getAnnouncement(annId);
		
		if (!UserPermissionChecker.inRoleForTopic(request, "audience", announcement.getParent())) {
			throw new UnauthorizedException();
		}
		
		model.addAttribute("announcement", announcement);		
		
		return viewNameSelector.select(request, "displayFullAnnouncement");
	}
	
	@RequestMapping("EDIT")
	public String editPreferences(Model model, RenderRequest request) throws PortletException {
		
		List<TopicSubscription> myTopics = tss.getTopicSubscriptionEdit(request);
		
		if (request.getRemoteUser() == null || 
				request.getRemoteUser().equalsIgnoreCase(GUEST_USERNAME)) {
			model.addAttribute("isGuest", Boolean.TRUE);
		} else {
			model.addAttribute("isGuest", Boolean.FALSE);
		}
		model.addAttribute("topicSubscriptions", myTopics);
		model.addAttribute("topicsToUpdate", myTopics.size());
		
		return viewNameSelector.select(request, "editDisplayPreferences");
	}
	
    @RequestMapping("EDIT")
    public void savePreferences(ActionRequest request, ActionResponse response,
            @RequestParam("topicsToUpdate") Integer topicsToUpdate) throws PortletException {
        
        List<TopicSubscription> newSubscription = new ArrayList<TopicSubscription>();
        
        for (int i=0; i<topicsToUpdate; i++) {
            Long topicId = Long.valueOf( request.getParameter("topicId_"+i) );
            
            // Will be numeric for existing, persisted TopicSubscription 
            // instances;  blank (due to null id field) otherwise
            String topicSubId = request.getParameter("topicSubId_"+i).trim();

            Boolean subscribed = Boolean.valueOf( request.getParameter("subscribed_"+i) );
            Topic topic = announcementService.getTopic(topicId);
            
            // Make sure that any pushed_forced topics weren't sneakingly removed (by tweaking the URL, for example)
            if (topic.getSubscriptionMethod() == Topic.PUSHED_FORCED) {
                subscribed = new Boolean(true);
            } 
            
            TopicSubscription ts = new TopicSubscription(request.getRemoteUser(), topic, subscribed);
            if (topicSubId.length() > 0) {
                // This TopicSubscription represents an existing, persisted entity
                try {
                    ts.setId(Long.valueOf(topicSubId));
                } catch (NumberFormatException nfe) {
                    logger.debug(nfe.getMessage(), nfe);
                }
            }
            
            newSubscription.add(ts);
        }
        
        if (newSubscription.size() > 0) {
            try {
                announcementService.addOrSaveTopicSubscription(newSubscription);
            } catch (Exception e) {
                logger.error("ERROR saving TopicSubscriptions for user "+request.getRemoteUser()+". Message: "+e.getMessage());
            }
        } 
        
        response.setPortletMode(PortletMode.VIEW);
        response.setRenderParameter("action", "displayAnnouncements");
        
    }
	
	public void setTss(ITopicSubscriptionService tss) {
		this.tss = tss;
	}

	public void setShowDate(Boolean showDate) {
		this.showDate = showDate;
	}

	public void afterPropertiesSet() throws Exception {
		guestAnnouncementCache = cm.getCache("guestAnnouncementCache");
		logger.debug("guestAnnouncementCache created.");
	}

	public void setCm(CacheManager cm) {
		this.cm = cm;
	}

	
}
