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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class DisplayFullAnnouncementController extends AbstractController {

	private IAnnouncementService announcementService;
	
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		
		Map<String,Object> mav = new HashMap<String,Object>();
		Long annId = Long.valueOf( request.getParameter("announcementId") );
		
		Announcement announcement = announcementService.getAnnouncement(annId);
		
		if (!UserPermissionChecker.inRoleForTopic(request, "audience", announcement.getParent())) {
			throw new UnauthorizedException();
		}
		
		mav.put("announcement", announcement);
		
		return new ModelAndView("displayFullAnnouncement", mav);
		
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

}
