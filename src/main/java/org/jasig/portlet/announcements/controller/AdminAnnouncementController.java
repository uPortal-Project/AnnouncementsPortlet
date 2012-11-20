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

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.validators.AnnouncementValidator;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
public class AdminAnnouncementController implements InitializingBean {

    public static final String PREFERENCE_ALLOW_OPEN_ENDDATE = "AdminAnnouncementController.allowOpenEndDate";

	@Autowired
	private IAnnouncementService announcementService;

	private static final Log log = LogFactory.getLog(AdminController.class);
	private PropertyEditor topicEditor;

	@Autowired
	private String customDateFormat = "yyyy-MM-dd";

	/**
	 * CSS classes added to the start & end date input fields to enable the
	 * jQuery UI datepicker
	 */
	private String datePickerFormat = "format-y-m-d divider-dash";

	@Autowired
	private UserPermissionCheckerFactory userPermissionCheckerFactory = null;

	@InitBinder("announcement")
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(customDateFormat);
		dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(Topic.class, topicEditor);
	    binder.setAllowedFields(new String[] {"id","created","author","title","abstractText","message",
	    		"link","startDisplay","endDisplay", "parent", "action"});
	}


	/**
	 * Does all the prep work before showing the form
	 */
	@RequestMapping(params="action=addAnnouncement")
	public String showAddAnnouncementForm(
			@RequestParam(value="editId",required=false) Long editId,
			@RequestParam(value="topicId",required=false) Long topicId,
			Model model) throws PortletException {

		if (!model.containsAttribute("announcement")) {
			Announcement ann = new Announcement();
			Topic topic = null;

			if (editId != null) {
				try {
					log.debug("editId found. This is an edit request for announcement Id "+editId.toString());
					ann = announcementService.getAnnouncement(editId);
					// return immediately when we have our announcement

				} catch (NumberFormatException e) {
					log.debug("No editId found. This is not an edit request");
				}
			}

			if (ann != null && ann.getParent() == null) {
				try {
					topic = announcementService.getTopic(topicId);
					ann.setParent(topic);
				} catch (NumberFormatException e) {
					log.error("Unable to get topicId from request");
				}
			}

            model.addAttribute("announcement", ann);
		}

		model.addAttribute("datePickerFormat", datePickerFormat);

		return "addAnnouncement";
	}

	/**
	 * Saves the announcement
	 * @param newAnn
	 * @param topicIdStr
	 * @param request
	 * @param response
	 * @param errors
	 * @throws PortletException
	 */
	@RequestMapping(params="action=addAnnouncement")
	public void actionAddAnnouncementForm(
			ActionRequest request,
			ActionResponse response,
			@ModelAttribute("announcement") Announcement announcement,
			BindingResult result,
			SessionStatus status) throws PortletException {

		new AnnouncementValidator(getAllowOpenEndDate(request)).validate(announcement, result);
		if (result.hasErrors()) {
			response.setRenderParameter("action", "addAnnouncement");
			return;
		}

		if (!result.hasErrors()) {

			if (!announcement.hasId()) {
				// add the automatic data
				announcement.setAuthor( request.getRemoteUser() );
				announcement.setCreated( new Date() );

				announcementService.addOrSaveAnnouncement(announcement);
			} else {
				announcementService.mergeAnnouncement(announcement);
			}

			status.setComplete();
			response.setRenderParameter("topicId", announcement.getParent().getId().toString());
			response.setRenderParameter("action", "showTopic");
		}

	}

	/**
	 * Handles deletion of announcements
	 * @param topicId
	 * @param annId
	 * @param response
	 * @throws PortletException
	 */
	@RequestMapping(params="action=deleteAnnouncement")
	public void actionDeleteAnnouncement(@RequestParam("topicId") Long topicId,
			@RequestParam("annId") Long annId, ActionRequest request,
			ActionResponse response) throws PortletException {

		Topic topic = announcementService.getTopic(topicId);
		Announcement ann = announcementService.getAnnouncement(annId);

		UserPermissionChecker upChecker = userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
		if(upChecker.isAdmin() || upChecker.isModerator() || (upChecker.isAuthor() && ann.getAuthor() == request.getRemoteUser())) {
		    // the person deleting the announcement must be the author, a moderator or an admin
            announcementService.deleteAnnouncement(ann);
		} else {
			throw new UnauthorizedException("You do not have access to this topic!");
		}

		response.setRenderParameter("topicId", topicId.toString());
		response.setRenderParameter("action", "showTopic");
	}

	public boolean getAllowOpenEndDate(PortletRequest req) {
	    PortletPreferences prefs = req.getPreferences();
	    return Boolean.parseBoolean(prefs.getValue(PREFERENCE_ALLOW_OPEN_ENDDATE, "false"));
	}

	/**
	 * When a custom date format is set by Spring, this method converts it immediately to a string of two CSS classes
	 * required by the date picker in the view.
	 * @param customDateFormat
	 */
	public void setCustomDateFormat(String customDateFormat) {
		this.customDateFormat = customDateFormat;

		if (log.isDebugEnabled()) {
			log.debug("Trying to parse custom date input format: ["+customDateFormat+"]");
		}

		String[] finalPieces = {"", "", ""};
		String[] pieces = {"", "", ""};
		String divider = null;

		// Ignore any custom date format requests if the requirements are not met
		if (customDateFormat.contains("/") && !customDateFormat.contains("-") && !customDateFormat.contains(".")) {
			pieces = customDateFormat.split("/");
			divider = "slash";
		}
		else if (customDateFormat.contains("-") && !customDateFormat.contains("/") && !customDateFormat.contains(".")) {
			pieces = customDateFormat.split("-");
			divider = "dash";
		}
		else if (customDateFormat.contains(".") && !customDateFormat.contains("/") && !customDateFormat.contains("-")) {
			pieces = customDateFormat.split("\\.");
			divider = "dot";
		}
		else {
			return;
		}

		// Ignore any custom date format requests if the requirements are not met
		if (pieces.length > 3) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Custom date input format: ["+pieces[0]+" "+divider+" "+pieces[1]+" "+divider+" "+pieces[2]+"]");
		}

		for (int i=0; i<pieces.length; i++) {
			if (pieces[i].equalsIgnoreCase("mm")) {
				finalPieces[i] = "m";
			}
			else if (pieces[i].equalsIgnoreCase("dd")) {
				finalPieces[i] = "d";
			}
			else if (pieces[i].equalsIgnoreCase("yyyy")) {
				finalPieces[i] = "y";
			}
		}

		datePickerFormat = "format-" + finalPieces[0] + "-" + finalPieces[1] + "-" + finalPieces[2] + " divider-" + divider;

		if (log.isDebugEnabled()) {
			log.debug("Custom date input format parsed as: ["+datePickerFormat+"]");
		}
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}


	public void afterPropertiesSet() throws Exception {
		topicEditor = new TopicEditor(announcementService);
	}

}