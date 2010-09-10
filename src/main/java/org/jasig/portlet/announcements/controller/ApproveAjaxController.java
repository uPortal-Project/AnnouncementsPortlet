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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * @author eolsson
 *
 */
public class ApproveAjaxController extends AbstractController implements InitializingBean {

	private IAnnouncementService announcementService;

	@Autowired
	private CacheManager cm = null;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
				
		Long annId = Long.valueOf( request.getParameter("annId") );
		Boolean approval = Boolean.valueOf( request.getParameter("approval") );
		Announcement ann = announcementService.getAnnouncement(annId);
		
		Date now = new Date();
		int status = 3;
		/**
		 * Scheduled = 0
		 * Expired   = 1
		 * Showing   = 2
		 * Pending   = 3
		 */
		if (ann.getStartDisplay().after(now) && ann.getEndDisplay().after(now) && approval) {
			status = 0;
		}
		else if (ann.getStartDisplay().before(now) && ann.getEndDisplay().after(now) && approval) {
			status = 2;
		}
		else if (ann.getEndDisplay().before(now)) {
			status = 1;
		}
		
		ann.setPublished(approval);
		cm.getCache("guestAnnouncementCache").flush();
		
		announcementService.addOrSaveAnnouncement(ann);
		
		return new ModelAndView("ajaxApprove", "status", status);
		
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	public void setCm(CacheManager cm) {
		this.cm = cm;
	}

	public void afterPropertiesSet() throws Exception {
		if (cm == null) {
			throw new BeanCreationException("Required cacheManager field was not set");
		}
	}
	
}
