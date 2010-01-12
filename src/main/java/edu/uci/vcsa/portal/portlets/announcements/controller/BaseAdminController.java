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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;
import edu.uci.vcsa.portal.portlets.announcements.service.UserPermissionChecker;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class BaseAdminController extends AbstractController {

	private IAnnouncementService announcementService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		
		Map<String,Object> mav = new HashMap<String,Object>();
		List<Topic> allTopics = announcementService.getAllTopics();
		
		// add all topics for the portal admin
		if (UserPermissionChecker.isPortalAdmin(request)) {
			mav.put("allTopics", announcementService.getAllTopics());
			mav.put("portalAdmin", new Boolean(true));
		}
		else {
			List<Topic> adminTopics = new ArrayList<Topic>();
			List<Topic> otherTopics = new ArrayList<Topic>();
			
			// cycle through all the topics and check if the current user has any permissions 
			for (Topic t: allTopics) {
				if (UserPermissionChecker.inRoleForTopic(request, "admins", t)) {
					adminTopics.add(t);
				}
				else if (UserPermissionChecker.inRoleForTopic(request, "moderators", t)) {
					otherTopics.add(t);
				}
				else if (UserPermissionChecker.inRoleForTopic(request, "authors", t)) {
					otherTopics.add(t);
				}
			}
			
			mav.put("adminTopics", adminTopics);
			mav.put("otherTopics", otherTopics);
			mav.put("portalAdmin", new Boolean(false));
		}
		
		return new ModelAndView("baseAdmin", mav);
		
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	
	
}
