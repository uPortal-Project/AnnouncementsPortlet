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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.jasig.portlet.announcements.model.RoleSelection;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.IGroupService;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class AddMembersFormController extends SimpleFormController {

	private IAnnouncementService announcementService;
	private IGroupService groupService;
	
	public AddMembersFormController() {
		super();
		setCommandClass(RoleSelection.class);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.SimpleFormController#onSubmitAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected void onSubmitAction(ActionRequest request,
			ActionResponse response, Object command, BindException errors)
			throws Exception {
		if (!errors.hasErrors()) {
			RoleSelection rolesSelected = (RoleSelection) command;
			Long topicId = Long.parseLong((String) request.getParameter("topicId"));
			String groupKey = (String) request.getParameter("groupKey");
			Topic topic = announcementService.getTopic(topicId);
			
			// Extract and save the USER members
			Set<String> oldGroupList = topic.getGroup(groupKey);
			Set<String> newList = new TreeSet<String>();
			
			// look for members that are users and add them to the list
			for (String member: oldGroupList) {
				if (member.startsWith("USER."))
					newList.add(member);
			}
			
			// update the members of the selected group
			if (rolesSelected != null && 
					rolesSelected.getSelectedRoles() != null && 
					rolesSelected.getSelectedRoles().size() > 0) {
				newList.addAll(groupService.getSetForRoleSelection(rolesSelected));
			}
			
			// if nothing was selected, then just save the users, or if it was, save the changes now
			topic.setGroup(groupKey, newList );
			
			// save the topic to the database
			announcementService.addOrSaveTopic(topic);
			
			response.setRenderParameter("topicId", topicId.toString());
			response.setRenderParameter("action", "showTopic");
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(PortletRequest request) throws Exception {

		Map<String,Object> mav = new HashMap<String,Object>();
		String topicId = (String) request.getParameter("topicId");
		Topic topic = announcementService.getTopic(Long.parseLong(topicId));
		
		mav.put("groupKey", (String) request.getParameter("groupKey"));
		mav.put("topic", topic);
		mav.put("roles", groupService.getAllRolesFromGroupSet( topic.getGroup( (String) request.getParameter("groupKey") ) ));
		
		return mav;
	}

	@Override
	protected Object formBackingObject(PortletRequest request) throws Exception {
		Long topicId = Long.valueOf(request.getParameter("topicId"));
		String groupKey = request.getParameter("groupKey");
		Topic topic = announcementService.getTopic(topicId);
		Set<String> group = topic.getGroup(groupKey);
		
		RoleSelection rs = new RoleSelection(group);
		return rs;
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * @param groupService the groupService to set
	 */
	public void setGroupService(IGroupService groupService) {
		this.groupService = groupService;
	}

}
