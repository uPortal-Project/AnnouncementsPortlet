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
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.validators.TopicValidator;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * @author eolsson
 *
 */
@Controller
@RequestMapping("VIEW")
public class AdminTopicController {

	private static final Log log = LogFactory.getLog(AdminTopicController.class);

	@Autowired
	private IAnnouncementService announcementService;

	@Autowired
	private Boolean includeJQuery;

	@Autowired
	private UserPermissionCheckerFactory userPermissionCheckerFactory = null;
	/**
	 * Add topic view controller, creates or fetches the topic for adding or editing
	 * @param topicIdStr
	 * @param model
	 * @return
	 * @throws PortletException
	 */
	@RequestMapping(params="action=addTopic")
	public String showAddTopicForm(@RequestParam(value="edit", required=false) String topicIdStr,
			Model model) throws PortletException {

		if (!model.containsAttribute("topic")) {
			if (topicIdStr != null && !"".equals(topicIdStr)) {
				Long topicId = Long.parseLong(topicIdStr);
				Topic t = announcementService.getTopic(topicId);
				model.addAttribute("topic",t);
				if (log.isDebugEnabled())
					log.debug("Adding existing topic to model: "+t.toString());
			}
			else {
				model.addAttribute("topic",new Topic());
				if (log.isDebugEnabled())
					log.debug("Adding new topic to model");
			}
		}
		return "addTopic";
	}

	/**
	 * Saves the Topic that was submitted
	 * @param topic
	 * @param result
	 * @param status
	 * @param request
	 * @param response
	 * @throws PortletException
	 */
	@RequestMapping(params="action=addTopic")
	public void actionAddTopicForm(@ModelAttribute("topic") Topic topic,
			BindingResult result, SessionStatus status, ActionRequest request,
			ActionResponse response) throws PortletException {

		new TopicValidator().validate(topic, result);
		if (result.hasErrors()) {
			if (log.isDebugEnabled())
				log.debug("Error in form: "+ result.toString());
			response.setRenderParameter("action", "addTopic");
			return;
		}

		if (!result.hasErrors() && topic != null) {
			if (log.isDebugEnabled())
				log.debug("No errors in form");

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
			status.setComplete();

			response.setRenderParameter("action", "baseAdmin");
		}

	}

	/**
	 * Delete a specified topic
	 * @param topicId
	 * @param response
	 * @throws NumberFormatException
	 * @throws PortletException
	 */
	@RequestMapping(params="action=deleteTopic")
	public void actionDeleteTopic(@RequestParam("topicId") String topicId,
			ActionRequest request, ActionResponse response) throws NumberFormatException, PortletException {
		Topic topic = announcementService.getTopic(Long.parseLong(topicId));

		if(!UserPermissionChecker.inRoleForTopic(request, UserPermissionChecker.ADMIN_ROLE_NAME, topic)) {
			throw new UnauthorizedException("You do not have access to delete this topic!");
		}

		announcementService.deleteTopic(topic);

		response.setRenderParameter("action", "baseAdmin");
	}

	/**
	 * Show a specified topic management view
	 * @param topicId
	 * @param request
	 * @param model
	 * @return
	 * @throws NumberFormatException
	 * @throws PortletException
	 */
	@RequestMapping(params="action=showTopic")
	public String showTopic(@RequestParam("topicId") String topicId,
			RenderRequest request, Model model) throws NumberFormatException, PortletException {

		Topic topic = announcementService.getTopic(Long.parseLong(topicId));

		UserPermissionChecker upChecker = userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
		if(!upChecker.isAuthor() && !upChecker.isModerator() && !upChecker.isAdmin()) {
		    throw new UnauthorizedException("You do not have access to this topic!");
		}

		Set<Announcement> annSet = topic.getAnnouncements();
		List<Announcement> annList = new ArrayList<Announcement>();
		annList.addAll(annSet);
		if (annSet.size() < 1)
			annList = null;

		if (annList != null) {
			Collections.sort(annList);
		}

		model.addAttribute("user", upChecker);
		model.addAttribute("topic", topic);
		model.addAttribute("announcements", annList);
		model.addAttribute("now", new Date());
		model.addAttribute("includeJQuery", includeJQuery);

		return "showTopic";
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * @param includeJQuery the includeJQuery to set
	 */
	public void setIncludeJQuery(Boolean includeJQuery) {
		this.includeJQuery = includeJQuery;
	}


}