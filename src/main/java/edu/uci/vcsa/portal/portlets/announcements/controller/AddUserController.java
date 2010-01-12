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

import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.web.portlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *
 * $LastChangedBy$
 * $LastChangedDate$
 *
 */
public class AddUserController extends AbstractController {

	private IAnnouncementService announcementService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleActionRequestInternal(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		
		Long topicId = Long.valueOf( request.getParameter("topicId") );
		String groupKey = request.getParameter("groupKey");
		String userAdd = request.getParameter("userAdd");
		
		Topic topic = announcementService.getTopic(topicId);
		Set<String> updateGroup = topic.getGroup(groupKey);
		
		updateGroup.add("USER."+userAdd);
		
		announcementService.addOrSaveTopic(topic);
		
		response.setRenderParameter("topicId", topicId.toString());
		response.setRenderParameter("groupKey", groupKey);
		response.setRenderParameter("action", "addMembers");
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}
	
}
