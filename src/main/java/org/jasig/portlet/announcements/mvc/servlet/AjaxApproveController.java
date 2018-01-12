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

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Responsible for receiving and processing the publish/unpublish messages sent from the Admin
 * portlet via AJaX.
 */
@Controller("ajaxApproveController")
public class AjaxApproveController {

    private static final int STATUS_SCHEDULED = 0;
    private static final int STATUS_EXPIRED = 1;
    private static final int STATUS_SHOWING = 2;
    private static final int STATUS_PENDING = 3;

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

    @RequestMapping
    public ModelAndView toggleApprove(HttpServletRequest request) throws PortletException {

        final Long annId = Long.valueOf(request.getParameter("annId"));
        final Boolean approval = Boolean.valueOf(request.getParameter("approval"));
        final Announcement ann = announcementService.getAnnouncement(annId);

        final Date startDisplay = ann.getStartDisplay();
        Date endDisplay = ann.getEndDisplay();
        if (endDisplay == null) {
            // Unspecified end date means the announcement does not expire;  we
            // will substitute a date in the future each time this item is
            // evaluated.
            final long aYearFromNow = System.currentTimeMillis() + Announcement.MILLISECONDS_IN_A_YEAR;
            endDisplay = new Date(aYearFromNow);
        }

        final Date now = new Date();
        int status = STATUS_PENDING; // default
        /* Scheduled = 0 Expired = 1 Showing = 2 Pending = 3 */
        if (startDisplay.after(now) && endDisplay.after(now) && approval) {
            status = STATUS_SCHEDULED;
        } else if (startDisplay.before(now) && endDisplay.after(now) && approval) {
            status = STATUS_SHOWING;
        } else if (endDisplay.before(now)) {
            status = STATUS_EXPIRED;
        }

        ann.setPublished(approval);

        announcementService.addOrSaveAnnouncement(ann);

        return new ModelAndView("ajaxApprove", "status", status);
    }

}
