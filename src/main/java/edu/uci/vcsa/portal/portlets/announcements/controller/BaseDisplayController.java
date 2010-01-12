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
package edu.uci.vcsa.portal.portlets.announcements.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.model.TopicSubscription;
import edu.uci.vcsa.portal.portlets.announcements.service.TopicSubscriptionService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 * 
 */
public class BaseDisplayController extends AbstractController {

	public static final String GUEST_USERNAME = "guest";
	
	private TopicSubscriptionService tss;
	private final int INCREMENT = 5;
	private List<Announcement> guestAnnouncementCache = null;
	private List<Announcement> guestAnnouncementCacheEmergency = null;
	private int guestAnnouncementCacheTimeout = 60;	// in minutes
	private long guestAnnouncementCacheSaveTime;	// in milliseconds since epoch 
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		
		Map<String,Object> mav = new HashMap<String,Object>();
		
		int from = 0;
		int to = 5;
		boolean isGuest = (request.getRemoteUser() == null || request.getRemoteUser().equalsIgnoreCase(GUEST_USERNAME));
		
		// get page parameters from the request
		try {
			from = Integer.valueOf( request.getParameter("from") );
		} catch (NumberFormatException e) {}
		
		try {
			to = Integer.valueOf( request.getParameter("to") );
		} catch (NumberFormatException e) {}
			
		List<Announcement> announcements;
		List<Announcement> emergencyAnnouncements;
		
		if (!isGuest || 								// we're not a guest
				guestAnnouncementCache == null || 		// or the guest cache is empty... OR if the current time has passed save time plus the cache timeout period
				(System.currentTimeMillis() > guestAnnouncementCacheSaveTime + (long)(guestAnnouncementCacheTimeout * 60 * 1000) )) {
			
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
					logger.debug("Guest cache expired "+(new Date(guestAnnouncementCacheSaveTime+(guestAnnouncementCacheTimeout * 60 * 1000) )).toString()+". Regenerating guest cache.");
				guestAnnouncementCache = announcements;
				guestAnnouncementCacheEmergency = emergencyAnnouncements;
				guestAnnouncementCacheSaveTime = System.currentTimeMillis();
			}
		} 
		else {
			// we're a guest and we're within the cache timeout period, so return the cached announcements
			if (logger.isDebugEnabled())
				logger.debug("Guest cache valid until "+(new Date(guestAnnouncementCacheSaveTime+(guestAnnouncementCacheTimeout * 60 * 1000) )).toString()+". Using guest cache.");
			announcements = guestAnnouncementCache;
			emergencyAnnouncements = guestAnnouncementCacheEmergency;
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
			mav.put("isGuest", new Boolean(true));
		} else {
			mav.put("isGuest", new Boolean(false));
		}
		
		mav.put("from", new Integer(from));
		mav.put("to", new Integer(to));
		mav.put("hasMore", (announcements.size() > to));
		mav.put("increment", new Integer(INCREMENT));
		mav.put("announcements", announcementsShort);
		mav.put("emergency", emergencyAnnouncements);
		
		return new ModelAndView("displayAnnouncements", mav);
	}

	/**
	 * This method causes the guest announcement cache to immediately be considered too old, thus causing the 
	 * next call to this controller to trigger a database update.
	 */
	public void invalidateGuestCache() {
		guestAnnouncementCacheSaveTime -= (guestAnnouncementCacheTimeout * 60 * 1000);
	}
	
	/**
	 * @param tss the tss to set
	 */
	public void setTss(TopicSubscriptionService tss) {
		this.tss = tss;
	}

	/**
	 * @param guestAnnouncementCacheTimeout the guestAnnouncementCacheTimeout to set
	 */
	public void setGuestAnnouncementCacheTimeout(int guestAnnouncementCacheTimeout) {
		this.guestAnnouncementCacheTimeout = guestAnnouncementCacheTimeout;
	}


}
