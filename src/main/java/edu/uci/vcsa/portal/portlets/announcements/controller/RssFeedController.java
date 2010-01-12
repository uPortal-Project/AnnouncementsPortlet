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
package edu.uci.vcsa.portal.portlets.announcements.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 *
 */
public class RssFeedController extends AbstractController {

	private IAnnouncementService announcementService;
	
	private static final String CONTENT_TYPE = "application/rss+xml";
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
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

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

}
