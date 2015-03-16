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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.xml.namespace.QName;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.AnnouncementSortStrategy;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.model.UserRoles;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.ITopicSubscriptionService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationQuery;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationResult;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * @author eolsson
 *
 */
@Controller
@RequestMapping("VIEW")
public class AnnouncementsViewController implements InitializingBean {

    private static final String GUEST_USERNAME = "guest";
    private static final Logger logger = Logger.getLogger(AnnouncementsViewController.class);
    private Cache guestAnnouncementCache = null;

    @Autowired
    private ITopicSubscriptionService tss = null;

    @Autowired
    private final IAnnouncementService announcementService = null;

    @Autowired
    private EhCacheCacheManager cm = null;

    @Autowired(required=true)
    private final IViewNameSelector viewNameSelector = null;

    @Autowired
    private final UserPermissionCheckerFactory userPermissionCheckerFactory = null;

    public static final String PREFERENCE_DISPLAY_STARTDATE = "AnnouncementsViewController.displayPublishDate";
    public static final String PREFERENCE_DISABLE_EDIT = "AnnouncementsViewController.PREFERENCE_DISABLE_EDIT";
    public static final String PREFERENCE_PAGE_SIZE = "AnnouncementsViewController.PAGE_SIZE";
    public static final String PREFERENCE_SORT_STRATEGY = "AnnouncementsViewController.AnnouncementSortStrategy";
    public static final String PREFERENCE_USE_SCROLLING_DISPLAY = "AnnouncementsViewController.useScrollingDisplay";
    public static final String PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS = "AnnouncementsViewController.scrollingDisplayHeightPixels";
    public static final String PREFERENCE_HIDE_ABSTRACT = "AnnouncementsViewController.hideAbstract";
    public static final String PREFERENCE_SYNDICATE_TOPICS_AS_NOTIFICATIONS = "AnnouncementsViewController.syndicateTopicsAsNotifications";
    public static final String PREFERENCE_SYNDICATE_TOPICS_ANNOUNCEMENTS_DISPLAY_FNAME = "AnnouncementsViewController.syndicateTopicsAnnouncementsDisplayFName";
    public static final String DEFAULT_SORT_STRATEGY = "START_DISPLAY_DATE_ASCENDING";
    
    public static final String NOTIFICATION_NAMESPACE = "https://source.jasig.org/schemas/portlet/notification";
    public static final String NOTIFICATION_QUERY_LOCAL_NAME = "NotificationQuery";
    public static final QName NOTIFICATION_QUERY_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_QUERY_LOCAL_NAME);
    public static final String NOTIFICATION_QUERY_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_QUERY_LOCAL_NAME;
    public static final String NOTIFICATION_RESULT_LOCAL_NAME = "NotificationResult";
    public static final QName NOTIFICATION_RESULT_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_RESULT_LOCAL_NAME);
    public static final String NOTIFICATION_RESULT_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_RESULT_LOCAL_NAME;
        
    
    /**
     * Main method of this display controller. Calculates which topics should be shown to
     * this user and which announcements to show from those topics.
     * @param model
     * @param request
     * @param from
     * @return
     * @throws PortletException
     */
    @SuppressWarnings("unchecked")
    @RenderMapping()
    public String mainView(Model model, RenderRequest request,
            @RequestParam(value="from",required=false) Integer from,
            @RequestParam(value="to",required=false) Integer to)
        throws PortletException {

        if (from == null || to == null) {
            from = 0;
            to = (Integer) model.asMap().get("increment");
        }

        PortletPreferences prefs = request.getPreferences();

        List<Announcement> announcements;
        List<Announcement> emergencyAnnouncements;

        Element guestCacheElement = null;
        Element emergCacheElement = null;
        guestCacheElement = guestAnnouncementCache.get("guest");
        emergCacheElement = guestAnnouncementCache.get("emergency");

        final Boolean isGuest = (Boolean) model.asMap().get("isGuest");
        if (!isGuest || (guestCacheElement == null || emergCacheElement == null)) {

            // create a new announcement list
            announcements = new ArrayList<Announcement>();
            emergencyAnnouncements = new ArrayList<Announcement>();

            // fetch the user's topic subscription from the database
            List<TopicSubscription> myTopics = tss.getTopicSubscription(request);

            // add all the published announcements of each subscribed topic to the announcement list
            // to emergency announcements into their own list
            for (TopicSubscription ts: myTopics) {
                if (ts.getSubscribed() && ts.getTopic().getSubscriptionMethod() != Topic.EMERGENCY) {
                    announcements.addAll(ts.getTopic().getPublishedAnnouncements());
                }
                else if (ts.getSubscribed() && ts.getTopic().getSubscriptionMethod() == Topic.EMERGENCY) {
                    emergencyAnnouncements.addAll(ts.getTopic().getPublishedAnnouncements());
                }
            }

            // sort the list (since they are not sorted from the database)
            Comparator<Announcement> sortStrategy = AnnouncementSortStrategy.getStrategy(prefs.getValue(PREFERENCE_SORT_STRATEGY,DEFAULT_SORT_STRATEGY));
            Collections.sort(announcements,sortStrategy);
            Collections.sort(emergencyAnnouncements,sortStrategy);

            if (isGuest) {
                if (logger.isDebugEnabled())
                    logger.debug("Guest cache expired. Regenerating guest cache.");

                guestAnnouncementCache.put(new Element("guest", announcements));
                guestAnnouncementCache.put(new Element("emergency", emergencyAnnouncements));
            }
        }
        else {
            // we're a guest and we're within the cache timeout period, so return the cached announcements
            if (logger.isDebugEnabled())
                logger.debug("Guest cache valid. Using guest cache.");
            announcements = (List<Announcement>) guestCacheElement.getObjectValue();
            emergencyAnnouncements = (List<Announcement>) emergCacheElement.getObjectValue();
        }

        // create a shortened list
        final Boolean useScrollingDisplay = (Boolean) model.asMap().get("useScrollingDisplay");
        final List<Announcement> announcementsShort = useScrollingDisplay 
                ? announcements
                : paginateAnnouncements(announcements, from, to, model);

        // Disable the edit link where appropriate
        Boolean disableEdit = Boolean.valueOf(prefs.getValue(PREFERENCE_DISABLE_EDIT, "false"));
        model.addAttribute("disableEdit", disableEdit);

        model.addAttribute("from", new Integer(from));
        model.addAttribute("to", new Integer(to));
        model.addAttribute("hasMore", (!useScrollingDisplay && announcements.size() > to));
        model.addAttribute("announcements", announcementsShort);
        model.addAttribute("emergency", emergencyAnnouncements);
        model.addAttribute("hideAbstract", Boolean.valueOf(prefs.getValue(PREFERENCE_HIDE_ABSTRACT,"false")));
        return viewNameSelector.select(request, "displayAnnouncements");
    }

    @RenderMapping(params="action=displayFullAnnouncement")
    public String displayFullAnnouncement(Model model, RenderRequest request,
            @RequestParam("announcementId") String announcementId) throws Exception {

        Announcement announcement = getAnnouncementById(request,announcementId);
        model.addAttribute("announcement", announcement);

        return viewNameSelector.select(request, "displayFullAnnouncement");
    }

    @EventMapping(NOTIFICATION_QUERY_QNAME_STRING)
    public void syndicateAnnouncementsAsNotifications(final EventRequest req, final EventResponse res) throws PortletException {

        final NotificationQuery query = (NotificationQuery) req.getEvent().getValue();
        logger.debug("Syndicating announcements for Notification portlet with windowId=" + query.getQueryWindowId());

        final PortletPreferences prefs = req.getPreferences();
        final List<String> topicTitlesToSyndicate = Arrays.asList(
                prefs.getValues(
                        PREFERENCE_SYNDICATE_TOPICS_AS_NOTIFICATIONS, 
                        new String[0]
                )
        );

        // Get out if we know there's nothing to do...
        if (topicTitlesToSyndicate.isEmpty()) {
            logger.debug("No topics are defined for syndication with the Notification portlet");
            return;
        }

        /*
         *  Obtain the FName of the targeted AnnouncementsDisplay portlet for 
         *  building links.  TODO:  This logic needs to be moved to a pluggable 
         *  link-building strategy.
         */
        final String announcementsDisplayFName = prefs.getValue(PREFERENCE_SYNDICATE_TOPICS_ANNOUNCEMENTS_DISPLAY_FNAME, "announcements");
        logger.debug("Using announcementsDisplayFName=" + announcementsDisplayFName);

        final List<NotificationCategory> categories = new ArrayList<NotificationCategory>();

        // fetch the user's topic subscription from the database
        final List<TopicSubscription> myTopics = tss.getTopicSubscription(req);
        for (TopicSubscription topicSub : myTopics) {

            final Topic topic = topicSub.getTopic();

            // We only want the white-listed ones...
            if (!topicTitlesToSyndicate.contains(topic.getTitle())) {
                continue;
            }
            logger.debug("Considering topic '" + topic.getTitle() + "' for remoteUser=" + req.getRemoteUser());

            final Set<Announcement> announcements = topic.getPublishedAnnouncements();

            // Ignore any that are empty...
            if (announcements.isEmpty()) {
                continue;
            }

            final List<NotificationEntry> entries = new ArrayList<NotificationEntry>();
            for (Announcement ann : announcements) {
                final NotificationEntry entry = new NotificationEntry();
                entry.setTitle(ann.getTitle());
                entry.setBody(ann.getAbstractText());  // Use abstract for body b/c notifications are intended to be smaller
                entry.setSource("Announcements");  // TODO:  Don't hard-code
                /*
                 * TODO:  This area (building a URL) needs to be "factored out" 
                 * to an interface-based approach that supports pluggable, 
                 * configurable strategies.  Ideally, furthermore, the strategy 
                 * for uPortal would leverage features that are not yet written -- 
                 * like the ability to get at uP's context name and the portlet's 
                 * fname -- or simply leverage a URL-generating API (which could 
                 * be added to the uPortal Platform API).
                 * 
                 * EXAMPLE=/uPortal/p/AnnouncementsDisplay/max/render.uP?pP_action=displayFullAnnouncement&pP_announcementId=2
                 */
                final StringBuilder url = new StringBuilder();  
                url.append("/uPortal")  // TODO:  Don't hard-code
                        .append("/p/")
                        .append(announcementsDisplayFName)
                        .append("/max/render.uP?pP_action=displayFullAnnouncement&pP_announcementId=")
                        .append(ann.getId());
                entry.setUrl(url.toString());
                entries.add(entry);
            }

            final NotificationCategory category = new NotificationCategory();
            category.setTitle(topic.getTitle());
            category.setEntries(entries);

            categories.add(category);

        }

        logger.debug("Found the following categories for remoteUser '" + req.getRemoteUser() + "':  " + categories);

        // We can bail if we haven't collected anything to share at this point...
        if (categories.isEmpty()) {
            return;
        }

        final NotificationResponse response = new NotificationResponse();
        response.setCategories(categories);

        final NotificationResult result = new NotificationResult();
        result.setQueryWindowId(query.getQueryWindowId());
        result.setResultWindowId(req.getWindowID());
        result.setNotificationResponse(response);

        res.setEvent(NOTIFICATION_RESULT_QNAME, result);

    }

    @ModelAttribute("displayPublishDate")
    public boolean getDisplayPublishDate(PortletRequest req) {
        PortletPreferences prefs = req.getPreferences();
        return Boolean.parseBoolean(prefs.getValue(PREFERENCE_DISPLAY_STARTDATE, "false"));
    }

    public void setTss(ITopicSubscriptionService tss) {
        this.tss = tss;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        guestAnnouncementCache = cm.getCacheManager().getCache("guestAnnouncementCache");
        if (guestAnnouncementCache == null) {
            throw new BeanCreationException("Required guestAnnouncementCache could not be loaded.");
        }
        else {
            logger.debug("guestAnnouncementCache created.");
        }
    }

    public void setCm(EhCacheCacheManager cm) {
        this.cm = cm;
    }

  	@RenderMapping(params = "action=displayFullAnnouncementHistory")
  	public String displayFullAnnouncementHistory(Model model,
  			RenderRequest request,
  			@RequestParam("announcementId") String announcementId)
  			throws Exception {

  		Announcement announcement = getAnnouncementById(request,announcementId);
  		model.addAttribute("announcement", announcement);

  		return viewNameSelector.select(request, "displayFullAnnouncementHistory");
  	}

  	@RenderMapping(params = "action=displayHistory")
  	public String displayHistory(Model model, RenderRequest request)
  			throws Exception {

  		List<Announcement> announcements = new ArrayList<Announcement>();

  		// fetch the user's topic subscription from the database
  		List<TopicSubscription> myTopics = tss.getTopicSubscription(request);

  		// add all the published announcements of each subscribed topic to the
  		// announcement list
  		for (TopicSubscription ts : myTopics) {
  			if (ts.getSubscribed() && ts.getTopic().getSubscriptionMethod() != Topic.EMERGENCY) {
  				announcements.addAll(ts.getTopic().getHistoricAnnouncements());
  			}
  		}

  		// sort the list by end display date descending (since they are not
  		// sorted from the database)
  		Collections.sort(announcements, new Comparator<Announcement>() {
  			@Override
            public int compare(Announcement s, Announcement s2) {
  				return s2.getEndDisplay().compareTo(s.getEndDisplay());
  			}

  		});

  		model.addAttribute("announcements", announcements);

  		return viewNameSelector.select(request, "displayHistory");
  	}
  	
  	@ModelAttribute("increment")
  	public int getPageSize(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        int rslt = 5;  // default
        try {
            rslt = Integer.parseInt(prefs.getValue(PREFERENCE_PAGE_SIZE, "5"));
        } catch (NumberFormatException nfe) {
            // Log it, but roll on...
            logger.warn("Non-integer value encountered for " 
                    + PREFERENCE_PAGE_SIZE + ": " 
                    + prefs.getValue(PREFERENCE_PAGE_SIZE, null));
        }
        return rslt;
  	}
  	
  	@ModelAttribute("isGuest")
  	public boolean isGuest(PortletRequest req) {
        boolean rslt = (req.getRemoteUser() == null || req.getRemoteUser().equalsIgnoreCase(GUEST_USERNAME));
        logger.debug("isGuest is: "+Boolean.toString(rslt));
        logger.debug("remoteUser is: "+req.getRemoteUser());
        return rslt;
  	}
  	
  	@ModelAttribute("useScrollingDisplay")
  	public boolean getUseScrollingDisplay(PortletRequest req) {
  	  final PortletPreferences prefs = req.getPreferences();
  	    return Boolean.valueOf(prefs.getValue(PREFERENCE_USE_SCROLLING_DISPLAY, "false"));  // default is false
  	}
  	
    @ModelAttribute("scrollingDisplayHeightPixels")
    public int getScrollingDisplayHeightPixels(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        int rslt = 500;  // default
        try {
            rslt = Integer.parseInt(prefs.getValue(PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS, "500"));
        } catch (NumberFormatException nfe) {
            // Log it, but roll on...
            logger.warn("Non-integer value encountered for " 
                    + PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS + ": " 
                    + prefs.getValue(PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS, null));
        }
        return rslt;
    }

    /*
  	 * Implementation
  	 */
    
    private List<Announcement> paginateAnnouncements(final List<Announcement> announcements, Integer from, Integer to, Model model) {
        List<Announcement> rslt;
        // if the announcement list is already short, then just reference it
        if (announcements.size() < to - from) {
            rslt = announcements;
        }
        // otherwise, just take the range requested and pass it along to the view
        else {
            rslt = new ArrayList<Announcement>();
            for (int i=from; i<to && announcements.size() > i; i++) {
                if (announcements.get(i) != null) {
                    rslt.add(announcements.get(i));
                }
            }
        }
        return rslt;
    }

    private Announcement getAnnouncementById(PortletRequest request, String announcementId) throws Exception {
        Long annId = Long.valueOf(announcementId);
        Announcement announcement = announcementService.getAnnouncement(annId);

        if (!UserPermissionChecker.inRoleForTopic(request, UserRoles.AUDIENCE_ROLE_NAME, announcement.getParent())) {
            throw new UnauthorizedException();
        }

        return announcement;
    }
}
