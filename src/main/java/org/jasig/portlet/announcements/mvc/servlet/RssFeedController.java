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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 */
@Controller("rssFeedController")
public class RssFeedController extends AbstractController {

	private static final String CONTENT_TYPE = "application/rss+xml";

	private IAnnouncementService announcementService;

	@Autowired
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException {
		
		response.setContentType(CONTENT_TYPE);
		
		Long topicId;
		try {
			topicId = Long.valueOf( ServletRequestUtils.getIntParameter(request, "topic") );
			if (topicId == null) {
				throw new IllegalStateException("Must specify the topic id");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Must specify topic id");
			return null;
		}
		
		Topic t = null;
		try {
			t = announcementService.getTopic(topicId);
		} catch (PortletException e) {
			e.printStackTrace();
		}
		
		if (t == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such topic");
			return null;
		}
		
		if (!t.isAllowRss()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "This topic is not available as RSS");
			return null;
		}
		
		// fetch and sort the announcements
		List<Announcement> announcements = new ArrayList<Announcement>();
		announcements.addAll(t.getPublishedAnnouncements());
		
		Collections.sort(announcements);
		
		// create the feed
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle(t.getTitle());
		feed.setLink(request.getRequestURL().append("?topic=").append(topicId.toString()).toString());
		feed.setDescription(t.getDescription());
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndEntry entry;
		SyndContent description;
		
		for (Announcement a: announcements) {
			entry = new SyndEntryImpl();
			entry.setTitle(a.getTitle());
			entry.setAuthor(a.getAuthor());
			if (a.getLink() != null)
				entry.setLink(a.getLink());
			entry.setPublishedDate(a.getStartDisplay());
			description = new SyndContentImpl();
			description.setType("text/plain");
			description.setValue(a.getMessage());
			entry.setDescription(description);
			entries.add(entry);
		}
		
		feed.setEntries(entries);
		
		SyndFeedOutput output = new SyndFeedOutput();
		String out;
		try {
			out = output.outputString(feed);
		} catch (FeedException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating feed");
			return null;
		}
		
		response.setContentLength(out.length());
		response.getOutputStream().print(out);
		response.getOutputStream().flush();
		
		return null;
		
	}

}
