/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.mvc.servlet;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 */
@Controller("rssFeedController")
public class RssFeedController {

    private static final String CONTENT_TYPE = "application/rss+xml";

    private IAnnouncementService announcementService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public void setAnnouncementService(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @RequestMapping(produces = CONTENT_TYPE)
    public void getRssFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {


        // Locate the Topic specified in the request
        final Long topicId;
        final Topic topic;
        try {
            topicId = Long.valueOf(ServletRequestUtils.getIntParameter(request, "topic"));
            if (topicId == null) {
                throw new ServletRequestBindingException("Parameter 'topic' not specified");
            }
            try {
                topic = announcementService.getTopic(topicId);
            } catch (PortletException pe) {
                logger.warn("Failed to obtain a Topic for the specified id of '{}'", topicId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such topic");
                return;
            }
        } catch (ServletRequestBindingException srbe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Must specify a valid topic id");
            logger.info("Returning SC_BAD_REQUEST because topicId was improperly specified in the request", srbe);
            return;
        }

        // Validate that this topic permits RSS
        if (!topic.isAllowRss()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This topic is not available as RSS");
            return;
        }

        // fetch and sort the announcements
        final List<Announcement> announcements = new ArrayList<>();
        announcements.addAll(topic.getPublishedAnnouncements());
        Collections.sort(announcements);

        // create the feed
        final SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(topic.getTitle());
        feed.setLink(request.getRequestURL().append("?topic=").append(topicId.toString()).toString());
        feed.setDescription(topic.getDescription());

        final List<SyndEntry> entries = new ArrayList<>();
        for (Announcement a : announcements) {
            final SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(a.getTitle());
            entry.setAuthor(a.getAuthor());
            if (a.getLink() != null) entry.setLink(a.getLink());
            entry.setPublishedDate(a.getStartDisplay());
            final SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(a.getAbstractText());
            entry.setDescription(description);
            entries.add(entry);
        }
        feed.setEntries(entries);

        final SyndFeedOutput output = new SyndFeedOutput();
        final String out;
        try {
            out = output.outputString(feed);
        } catch (FeedException e) {
            logger.warn("Failed to create SyndFeedOutput for topic '{}'", topic.getTitle());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating feed");
            return;
        }

        response.setContentLength(out.length());
        response.getOutputStream().print(out);
        response.getOutputStream().flush();
    }

}
