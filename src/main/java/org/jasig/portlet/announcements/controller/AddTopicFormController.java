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
import javax.portlet.PortletRequest;

import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class AddTopicFormController extends SimpleFormController {

	private IAnnouncementService announcementService;
	

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractFormController#formBackingObject(javax.portlet.PortletRequest)
	 */
	@Override
	protected Object formBackingObject(PortletRequest request) throws Exception {
		String topicIdStr = request.getParameter("edit");
		if (topicIdStr != null && !"".equals(topicIdStr)) {
			Long topicId = Long.parseLong(topicIdStr);
			Topic t = announcementService.getTopic(topicId);
			return t;
		}
		else {
			return new Topic();
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.SimpleFormController#onSubmitAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected void onSubmitAction(ActionRequest request,
			ActionResponse response, Object command, BindException errors)
			throws Exception {

		if (!errors.hasErrors()) {
			Topic topic = (Topic) command;
			
			// no id has been assigned by hibernate, so this must be a new topic
			if ( !topic.hasId() ) {
				topic.setCreator(request.getRemoteUser());
			} else {
				Long id = topic.getId();
				Topic oldTopic = announcementService.getTopic(id);
				
				topic.setCreator(oldTopic.getCreator());
				topic.setAdmins(oldTopic.getAdmins());
				topic.setAudience(oldTopic.getAudience());
				topic.setModerators(oldTopic.getModerators());
				topic.setAuthors(oldTopic.getAuthors());				
			}
			announcementService.addOrSaveTopic(topic);
		}
		
		response.setRenderParameter("action", "baseAdmin");
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

}
