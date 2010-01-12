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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.TopicSubscriptionService;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class EditDisplayPreferencesController extends AbstractController {

	private static Log log = LogFactory.getLog(EditDisplayPreferencesController.class);
	
	private TopicSubscriptionService tss;
	private IAnnouncementService announcementService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleActionRequestInternal(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		int topicsToUpdate = 0;
		
		try {
			topicsToUpdate = Integer.valueOf( request.getParameter("topicsToUpdate") );
		} catch (NumberFormatException e) {
			log.error("topicsToUpdate was not passed along from form.");
			// just log this error. 
		}
		
		List<TopicSubscription> newSubscription = new ArrayList<TopicSubscription>();
		
		for (int i=0; i<topicsToUpdate; i++) {
			Long topicId = Long.valueOf( request.getParameter("topicId_"+i) );
			Long topicSubId = Long.valueOf( request.getParameter("topicSubId_"+i) );
			Boolean subscribed = Boolean.valueOf( request.getParameter("subscribed_"+i) );
			Topic topic = announcementService.getTopic(topicId);
			
			// Make sure that any pushed_forced topics weren't sneakingly removed (by tweaking the URL, for example)
			if (topic.getSubscriptionMethod() == Topic.PUSHED_FORCED) {
				subscribed = new Boolean(true);
			} 
			
			TopicSubscription ts = new TopicSubscription(request.getRemoteUser(), topic, subscribed);
			ts.setId(topicSubId);
			
			newSubscription.add(ts);
		}
		
		if (newSubscription.size() > 0) {
			try {
				announcementService.addOrSaveTopicSubscription(newSubscription);
			} catch (Exception e) {
				log.error("ERROR saving TopicSubscriptions for user "+request.getRemoteUser()+". Message: "+e.getMessage());
			}
		} 
		
		response.setPortletMode(PortletMode.VIEW);
		response.setRenderParameter("action", "displayAnnouncements");
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		Map<String,Object> mav = new HashMap<String,Object>();
		
		List<TopicSubscription> myTopics = tss.getTopicSubscriptionEdit(request);
		
		if (request.getRemoteUser() == null || request.getRemoteUser().equalsIgnoreCase(BaseDisplayController.GUEST_USERNAME)) {
			mav.put("isGuest", new Boolean(true));
		} else {
			mav.put("isGuest", new Boolean(false));
		}
		mav.put("topicSubscriptions", myTopics);
		mav.put("topicsToUpdate", myTopics.size());
		
		return new ModelAndView("editDisplayPreferences", mav);
	}

	/**
	 * @param tss the tss to set
	 */
	public void setTss(TopicSubscriptionService tss) {
		this.tss = tss;
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}


	
	
}
