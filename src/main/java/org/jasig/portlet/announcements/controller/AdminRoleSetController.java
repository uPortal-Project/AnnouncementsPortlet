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

import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.jasig.portlet.announcements.model.RoleSelection;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author eolsson
 *
 */
@Controller
@RequestMapping("VIEW")
public class AdminRoleSetController {

	@Autowired
	private IAnnouncementService announcementService;
	@Autowired
	private IGroupService groupService;

	
	@RequestMapping(params="action=addMembers")
	public void processSubmit(@ModelAttribute("selection") RoleSelection rolesSelected,
			BindingResult errors, 
			@RequestParam("topicId") Long topicId,
			@RequestParam("groupKey") String groupKey,
			ActionResponse response) throws PortletException {

		if (!errors.hasErrors()) {
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
	
	@RequestMapping(params="action=addMembers")
	public String showForm(@RequestParam("topicId") Long topicId, 
			@RequestParam("groupKey") String groupKey,
			Model model) throws PortletException {
		
		if (!model.containsAttribute("selection")) {
			Topic topic = announcementService.getTopic(topicId);
			Set<String> group = topic.getGroup(groupKey);
			
			model.addAttribute("selection", new RoleSelection(group));
			model.addAttribute("roles", groupService.getAllRolesFromGroupSet( topic.getGroup( groupKey ) ));
			model.addAttribute(topic);
			
		}
		
		model.addAttribute("groupKey", groupKey);
		
		return "addMembers";
	}
	
	/**
	 * Handler for adding individual users to a topic's permission set
	 * @param response
	 * @param topicId
	 * @param groupKey
	 * @param userAdd
	 * @throws PortletException
	 */
	@RequestMapping(params="action=addUser")
	public void processAddUser(ActionResponse response, 
			@RequestParam("topicId") Long topicId,
			@RequestParam("groupKey") String groupKey,
			@RequestParam("userAdd") String userAdd) throws PortletException {
		
		Topic topic = announcementService.getTopic(topicId);
		Set<String> updateGroup = topic.getGroup(groupKey);
		
		updateGroup.add("USER."+userAdd);
		
		announcementService.addOrSaveTopic(topic);
		
		response.setRenderParameter("topicId", topicId.toString());
		response.setRenderParameter("groupKey", groupKey);
		response.setRenderParameter("action", "addMembers");
		
	}
	
	@RequestMapping(params="action=deleteUser")
	public void processDeleteUser(ActionResponse response, 
			@RequestParam("topicId") Long topicId,
			@RequestParam("groupKey") String groupKey,
			@RequestParam("userKey") String userKey) throws PortletException {
		
		Topic topic = announcementService.getTopic(topicId);
		Set<String> updateGroup = topic.getGroup(groupKey);
		
		updateGroup.remove(userKey);
		
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

	/**
	 * @param groupService the groupService to set
	 */
	public void setGroupService(IGroupService groupService) {
		this.groupService = groupService;
	}
	
}
