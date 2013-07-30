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

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.portlet.RenderRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eolsson
 *
 */
@Controller
@RequestMapping("VIEW")
public class AdminController {

	@Autowired
	private IAnnouncementService announcementService;

	@Autowired
	private UserPermissionCheckerFactory userPermissionCheckerFactory = null;

	/**
	 * Base view mapping for the Admin portlet, fetches all the topics and figures out what permissions the
	 * current user has on each.
	 * @param request
	 * @param model                    .
	 * @return
	 */
	@RequestMapping
	public String showBaseView(RenderRequest request, Model model) {

		List<Topic> allTopics = announcementService.getAllTopics();
        List<Announcement> pendingAnnouncements = new ArrayList<Announcement>();

        // add all topics for the portal admin
		if (UserPermissionChecker.isPortalAdmin(request)) {
			model.addAttribute("allTopics", allTopics);
			model.addAttribute("portalAdmin", Boolean.TRUE);
            for(Topic t : allTopics) {
                pendingAnnouncements.addAll(t.getPendingAnnouncements());
            }
		}
		else {
			List<Topic> adminTopics = new ArrayList<Topic>();
			List<Topic> otherTopics = new ArrayList<Topic>();

			// cycle through all the topics and check if the current user has any permissions
			for (Topic t: allTopics) {
			    UserPermissionChecker upChecker = userPermissionCheckerFactory.createUserPermissionChecker(request, t);
			    if(upChecker.isAdmin()) {
					adminTopics.add(t);
                    pendingAnnouncements.addAll(t.getPendingAnnouncements());
				}
				else if (upChecker.isModerator()) {
					otherTopics.add(t);
                    pendingAnnouncements.addAll(t.getPendingAnnouncements());
				}
				else if (upChecker.isAuthor()) {
					otherTopics.add(t);
				}
			}

			model.addAttribute("adminTopics", adminTopics);
			model.addAttribute("otherTopics", otherTopics);
			model.addAttribute("portalAdmin", Boolean.FALSE);
		}

        model.addAttribute("pendingAnnouncements",pendingAnnouncements);
        model.addAttribute("pendingAnnouncementCount",pendingAnnouncements.size());
		return "baseAdmin";

	}


	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

}