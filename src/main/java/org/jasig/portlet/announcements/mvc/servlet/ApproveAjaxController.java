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
package org.jasig.portlet.announcements.mvc.servlet;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author eolsson
 */
@Controller("ajaxApproveController")
public class ApproveAjaxController extends AbstractController implements InitializingBean {
    
    private IAnnouncementService announcementService;

	private EhCacheCacheManager cacheManager = null;

	@Autowired
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	@Autowired
	public void setCacheManager(EhCacheCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void afterPropertiesSet() throws Exception {
		if (cacheManager == null) {
			throw new BeanCreationException("Required cacheManager field was not set");
		}
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
				
		Long annId = Long.valueOf( request.getParameter("annId") );
		Boolean approval = Boolean.valueOf( request.getParameter("approval") );
		Announcement ann = announcementService.getAnnouncement(annId);
		
		Date startDisplay = ann.getStartDisplay();
		Date endDisplay = ann.getEndDisplay();
		if (endDisplay == null) {
		    // Unspecified end date means the announcement does not expire;  we 
		    // will substitute a date in the future each time this item is 
		    // evaluated.
		    long aYearFromNow = System.currentTimeMillis() + Announcement.MILLISECONDS_IN_A_YEAR;
		    endDisplay = new Date(aYearFromNow);
		}
		
		Date now = new Date();
		int status = 3;
		/**
		 * Scheduled = 0
		 * Expired   = 1
		 * Showing   = 2
		 * Pending   = 3
		 */
		if (startDisplay.after(now) && endDisplay.after(now) && approval) {
			status = 0;
		}
		else if (startDisplay.before(now) && endDisplay.after(now) && approval) {
			status = 2;
		}
		else if (endDisplay.before(now)) {
			status = 1;
		}
		
		ann.setPublished(approval);
		cacheManager.getCacheManager().getCache("guestAnnouncementCache").flush();
		
		announcementService.addOrSaveAnnouncement(ann);
		
		return new ModelAndView("ajaxApprove", "status", status);
		
	}

}
