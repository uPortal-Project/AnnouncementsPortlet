/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.mvc.portlet.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.mvc.IViewNameSelector;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShowHistoryController {

	private static final org.apache.log4j.Logger logger = Logger.getLogger(ShowHistoryController.class);

	@Autowired
	private IAnnouncementService announcementService;

	@Autowired
    private UserPermissionCheckerFactory userPermissionCheckerFactory = null;

    @Autowired(required=true)
    private final IViewNameSelector viewNameSelector = null;

    @RequestMapping(value="VIEW", params="action=showHistory")
    protected String showHistory(Model model, RenderRequest request,
            @RequestParam("topicId") String topicId) throws Exception {


		Topic topic = announcementService.getTopic(Long.parseLong(topicId));
		UserPermissionChecker upChecker = userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
		upChecker.validateCanEditTopic();

		Set<Announcement> annSet = topic.getAnnouncements();
		List<Announcement> annList = new ArrayList<Announcement>();
		// only add expired announcements to this list.
		for (Announcement ann : annSet) {
			if (ann.getEndDisplay().compareTo(Calendar.getInstance().getTime()) < 0) {
				annList.add(ann);
			}
		}

		if (annList != null) {
			Collections.sort(annList);
		}

		logger.info("number of announcements: " + annList.size());

		model.addAttribute("user", upChecker);
		model.addAttribute("topic", topic);
		model.addAttribute("announcements", annList);
		model.addAttribute("now", new Date());

		return viewNameSelector.select(request, "showHistory");
	}
}
