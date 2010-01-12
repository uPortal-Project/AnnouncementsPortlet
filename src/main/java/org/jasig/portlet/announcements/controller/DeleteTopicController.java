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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.web.portlet.mvc.AbstractController;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class DeleteTopicController extends AbstractController {

	private IAnnouncementService announcementService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleActionRequestInternal(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		
		Long topicId = Long.valueOf( request.getParameter("topicId") );
		Topic topic = announcementService.getTopic(topicId);
		
		announcementService.deleteTopic(topic);
		
		response.setRenderParameter("action", "baseAdmin");
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

}
