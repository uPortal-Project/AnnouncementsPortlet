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

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
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
import java.util.Set;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.mvc.portlet.display.AnnouncementsViewController;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Provides the current content of a topic in RSS format, if the topic permits it.
 */
@Controller("rssFeedController")
public class RssFeedController {

    private static final String CONTENT_TYPE = "application/rss+xml";

    /**
     * For announcements that don't specify an external link, we will attempt to construct one based
     * on what we know about how uPortal constructs URLs.  It will work (until it doesn't), but this
     * behavior is a flagrant violation of the Java Portlet Specification, which mandates that URLs
     * to portlet content may only be generated within a RENDER or RESOURCE phase.
     */
    private static final String ANNOUNCEMENT_DEEP_LINK_FORMAT =
            "%s/%s/p/%s?pP_announcementId=%d&pP_action=" + AnnouncementsViewController.ACTION_DISPLAY_FULL_ANNOUNCEMENT;

    private static final String PATH_ATTRIBUTE = "path";

    private IAnnouncementService announcementService;

    @Value("${RssFeedController.portalContextName:uPortal}")
    private String portalContextName;

    @Value("${RssFeedController.announcementsPortletFname:announcements}")
    private String announcementsPortletFname;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    public void setAnnouncementService(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @RequestMapping(produces = CONTENT_TYPE)
    public void getRssFeed(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Locate the Topic specified in the request
        final Topic topic;
        try {
            topic = selectTopic(request);
        } catch (IllegalArgumentException | ServletRequestBindingException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Must specify a valid 'topic' (id) or 'topicTitle' parameter");
            logger.info("Returning SC_BAD_REQUEST because topic (id) and/or topicTitle were improperly specified in the request", e);
            return;
        }

        // Validate:  do we have a topic?
        if (topic == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such topic");
            return;
        }

        // Validate:  does our topic permit RSS?
        if (!topic.isAllowRss()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This topic is not available as RSS");
            return;
        }

        final SyndFeed feed = buildRssFeed(topic, request);

        final SyndFeedOutput output = new SyndFeedOutput();
        final String out;
        try {
            out = output.outputString(feed);
        } catch (FeedException e) {
            logger.warn(String.format("Failed to create SyndFeedOutput for topic '%s'", topic.getTitle()));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating feed");
            return;
        }

        response.setContentLength(out.length());
        response.getOutputStream().print(out);
        response.getOutputStream().flush();

    }

    private Topic selectTopic(HttpServletRequest req) throws ServletRequestBindingException {

        Topic rslt = null; // default -- not found

        /*
         * There are two options:
         *
         *   - 1: by database identifier (original method -- try first)
         *   - 2: by title (try second)
         */
        final Long topicId = ServletRequestUtils.getLongParameter(req, "topic");
        if (topicId != null) {
            try {
                rslt = announcementService.getTopic(topicId);
            } catch (PortletException pe) {
                logger.warn(String.format("Failed to obtain a Topic for the specified id of '%s'", topicId));
            }
        } else {
            final String titleParameter = ServletRequestUtils.getStringParameter(req, "topicTitle");
            if (titleParameter != null) {
                // We need to find it...
                final List<Topic> allTopics = announcementService.getAllTopics();
                for (Topic t : allTopics) {
                    final String convertedTopicTitle = t.getTitle().trim().replaceAll("\\s", "-");
                    logger.debug(String.format("Calculated convertedTopicTitle='%s' for topic with title='%s'",
                            convertedTopicTitle, t.getTitle()));
                    if (convertedTopicTitle.equalsIgnoreCase(titleParameter)) {
                        rslt = t;
                        break;
                    }
                }
                if (rslt != null) {
                    logger.debug(String.format("Found topic '%s' for titleParameter='%s'",
                            rslt.getTitle(), titleParameter));
                } else {
                    logger.warn(String.format("Failed to obtain a Topic for the specified titleParameter of '%s'", titleParameter));
                }
            } else {
                throw new IllegalArgumentException("Neither 'topic' nor 'topicTitle' parameter specified");
            }
        }

        return rslt;

    }

    private SyndFeed buildRssFeed(Topic topic, HttpServletRequest request) throws IOException {

        final String urlPrefix = calculateUrlPrefix(request);

        // fetch and sort the announcements
        final List<Announcement> announcements = new ArrayList<>();
        announcements.addAll(topic.getPublishedAnnouncements());
        Collections.sort(announcements);

        final SyndFeed rslt = new SyndFeedImpl();
        rslt.setFeedType("rss_2.0");
        rslt.setTitle(topic.getTitle());
        rslt.setLink(request.getRequestURL().append("?topic=").append(topic.getId()).toString());
        rslt.setDescription(topic.getDescription());

        final List<SyndEntry> entries = new ArrayList<>();
        for (Announcement a : announcements) {
            final SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(a.getTitle());
            entry.setAuthor(a.getAuthor());
            // Every entry really should have a link
            if (StringUtils.isNotBlank(a.getLink())) {
                // Use the external URL, if specified
                entry.setLink(a.getLink());
            } else {
                /*
                 * Deep-link to the full announcement within this portlet.  There are a number of
                 * issues with this feature (see note above).
                 */
                final String deepLink = String.format(ANNOUNCEMENT_DEEP_LINK_FORMAT, urlPrefix,
                        portalContextName, announcementsPortletFname, a.getId());
                logger.debug(String.format("Calculated the following deepLink for announcement with id=%s:  %s",
                        a.getId(), deepLink));
                entry.setLink(deepLink);
            }
            entry.setPublishedDate(a.getStartDisplay());
            final SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(a.getAbstractText());
            entry.setDescription(description);
            final Set<String> attachments = a.getAttachments();
            if (!attachments.isEmpty()) {
                List<SyndEnclosure> enclosures = new ArrayList<>();
                for (String attachment : attachments) {
                    final JsonNode json = objectMapper.readTree(attachment);
                    final SyndEnclosure se = new SyndEnclosureImpl();
                    final String enclosureUrl = urlPrefix + json.get(PATH_ATTRIBUTE).getTextValue();
                    se.setUrl(enclosureUrl);
                    enclosures.add(se);
                }
                entry.setEnclosures(enclosures);
            }
            entries.add(entry);
        }
        rslt.setEntries(entries);

        return rslt;

    }

    private String calculateUrlPrefix(HttpServletRequest req) {
        final String requestUrl = req.getRequestURL().toString();
        final String requestUri = req.getRequestURI();
        final int urlPrefixLength = requestUrl.indexOf(requestUri);
        return requestUrl.substring(0, urlPrefixLength);
    }

}
