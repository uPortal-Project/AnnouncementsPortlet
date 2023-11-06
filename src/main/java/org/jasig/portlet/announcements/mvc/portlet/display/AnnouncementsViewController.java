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
package org.jasig.portlet.announcements.mvc.portlet.display;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.AnnouncementSortStrategy;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.jasig.portlet.announcements.model.UserRoles;
import org.jasig.portlet.announcements.mvc.IViewNameSelector;
import org.jasig.portlet.announcements.service.IAnnouncementsService;
import org.jasig.portlet.announcements.service.ITopicSubscriptionService;
import org.jasig.portlet.announcements.service.UserIdService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationQuery;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * <p>AnnouncementsViewController class.</p>
 *
 * @author Unknown
 * @version $Id: $Id
 */
@Controller
@RequestMapping("VIEW")
public class AnnouncementsViewController {

  /** Constant <code>ACTION_DISPLAY_FULL_ANNOUNCEMENT="displayFullAnnouncement"</code> */
  public static final String ACTION_DISPLAY_FULL_ANNOUNCEMENT = "displayFullAnnouncement";

  /** Constant <code>PREFERENCE_DISPLAY_STARTDATE="AnnouncementsViewController.displayPubl"{trunked}</code> */
  public static final String PREFERENCE_DISPLAY_STARTDATE =
      "AnnouncementsViewController.displayPublishDate";
  /** Constant <code>PREFERENCE_DISABLE_EDIT="AnnouncementsViewController.PREFERENCE_"{trunked}</code> */
  public static final String PREFERENCE_DISABLE_EDIT =
      "AnnouncementsViewController.PREFERENCE_DISABLE_EDIT";
  /** Constant <code>PREFERENCE_PAGE_SIZE="AnnouncementsViewController.PAGE_SIZE"</code> */
  public static final String PREFERENCE_PAGE_SIZE = "AnnouncementsViewController.PAGE_SIZE";
  /** Constant <code>PREFERENCE_SORT_STRATEGY="AnnouncementsViewController.Announcemen"{trunked}</code> */
  public static final String PREFERENCE_SORT_STRATEGY =
      "AnnouncementsViewController.AnnouncementSortStrategy";
  /** Constant <code>PREFERENCE_USE_SCROLLING_DISPLAY="AnnouncementsViewController.useScrollin"{trunked}</code> */
  public static final String PREFERENCE_USE_SCROLLING_DISPLAY =
      "AnnouncementsViewController.useScrollingDisplay";
  /** Constant <code>PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS="AnnouncementsViewController.scrollingDi"{trunked}</code> */
  public static final String PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS =
      "AnnouncementsViewController.scrollingDisplayHeightPixels";
  /** Constant <code>PREFERENCE_HIDE_ABSTRACT="AnnouncementsViewController.hideAbstrac"{trunked}</code> */
  public static final String PREFERENCE_HIDE_ABSTRACT = "AnnouncementsViewController.hideAbstract";
  /** Constant <code>PREFERENCE_SYNDICATE_TOPICS_AS_NOTIFICATIONS="AnnouncementsViewController.syndicateTo"{trunked}</code> */
  public static final String PREFERENCE_SYNDICATE_TOPICS_AS_NOTIFICATIONS =
      "AnnouncementsViewController.syndicateTopicsAsNotifications";
  /** Constant <code>PREFERENCE_SYNDICATE_TOPICS_ANNOUNCEMENTS_DISPLAY_FNAME="AnnouncementsViewController.syndicateTo"{trunked}</code> */
  public static final String PREFERENCE_SYNDICATE_TOPICS_ANNOUNCEMENTS_DISPLAY_FNAME =
      "AnnouncementsViewController.syndicateTopicsAnnouncementsDisplayFName";
  /** Constant <code>DEFAULT_SORT_STRATEGY="START_DISPLAY_DATE_ASCENDING"</code> */
  public static final String DEFAULT_SORT_STRATEGY = "START_DISPLAY_DATE_ASCENDING";

  /** Constant <code>NOTIFICATION_NAMESPACE="https://source.jasig.org/schemas/portle"{trunked}</code> */
  public static final String NOTIFICATION_NAMESPACE =
      "https://source.jasig.org/schemas/portlet/notification";
  /** Constant <code>NOTIFICATION_QUERY_LOCAL_NAME="NotificationQuery"</code> */
  public static final String NOTIFICATION_QUERY_LOCAL_NAME = "NotificationQuery";
  /** Constant <code>NOTIFICATION_QUERY_QNAME</code> */
  public static final QName NOTIFICATION_QUERY_QNAME =
      new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_QUERY_LOCAL_NAME);
  /** Constant <code>NOTIFICATION_QUERY_QNAME_STRING="{ + NOTIFICATION_NAMESPACE + } + NOTIFI"{trunked}</code> */
  public static final String NOTIFICATION_QUERY_QNAME_STRING =
      "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_QUERY_LOCAL_NAME;
  /** Constant <code>NOTIFICATION_RESULT_LOCAL_NAME="NotificationResult"</code> */
  public static final String NOTIFICATION_RESULT_LOCAL_NAME = "NotificationResult";
  /** Constant <code>NOTIFICATION_RESULT_QNAME</code> */
  public static final QName NOTIFICATION_RESULT_QNAME =
      new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_RESULT_LOCAL_NAME);
  /** Constant <code>NOTIFICATION_RESULT_QNAME_STRING="{ + NOTIFICATION_NAMESPACE + } + NOTIFI"{trunked}</code> */
  public static final String NOTIFICATION_RESULT_QNAME_STRING =
      "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_RESULT_LOCAL_NAME;
  /** Constant <code>USER_TOPICS_CACHE="userTopicLists"</code> */
  public static final String USER_TOPICS_CACHE = "userTopicLists";
  /** Constant <code>TOPICS_CACHE="topicLists"</code> */
  public static final String TOPICS_CACHE = "topicLists";

  @Autowired
  private final IAnnouncementsService announcementsService = null;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Log logger = LogFactory.getLog(getClass());
  @Autowired
  private ITopicSubscriptionService tss = null;
  @Autowired
  private EhCacheCacheManager cm = null;
  @Autowired(required = true)
  private IViewNameSelector viewNameSelector;
  @Autowired
  private UserPermissionCheckerFactory userPermissionCheckerFactory;
  @Autowired
  private UserIdService userIdService;

  /**
   * Main method of this display controller. Calculates which topics should be shown to this user
   * and which announcements to show from those topics.
   *
   * @param model a {@link org.springframework.ui.Model} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param from a {@link java.lang.Integer} object.
   * @param to a {@link java.lang.Integer} object.
   * @return a {@link java.lang.String} object.
   * @throws javax.portlet.PortletException if any.
   */
  @SuppressWarnings("unchecked")
  @RenderMapping()
  public String mainView(
      Model model,
      RenderRequest request,
      @RequestParam(value = "from", required = false) Integer from,
      @RequestParam(value = "to", required = false) Integer to)
      throws PortletException {

    if (from == null || to == null) {
      from = 0;
      to = (Integer) model.asMap().get("increment");
    }

    final PortletPreferences prefs = request.getPreferences();

    List[] lists = getLists(request);

    List<Announcement> announcements = lists[0];
    List<Announcement> emergencyAnnouncements = lists[1];

    // sort the list (since they are not sorted from the database)
    Comparator<Announcement> sortStrategy =
        AnnouncementSortStrategy.getStrategy(
            prefs.getValue(PREFERENCE_SORT_STRATEGY, DEFAULT_SORT_STRATEGY));
    Collections.sort(announcements, sortStrategy);
    Collections.sort(emergencyAnnouncements, sortStrategy);

    // create a shortened list
    final Boolean useScrollingDisplay = (Boolean) model.asMap().get("useScrollingDisplay");
    final List<Announcement> announcementsShort =
        useScrollingDisplay ? announcements : paginateAnnouncements(announcements, from, to, model);

    // Disable the edit link where appropriate
    Boolean disableEdit = Boolean.valueOf(prefs.getValue(PREFERENCE_DISABLE_EDIT, "false"));
    model.addAttribute("disableEdit", disableEdit);

    model.addAttribute("from", new Integer(from));
    model.addAttribute("to", new Integer(to));
    model.addAttribute("hasMore", (!useScrollingDisplay && announcements.size() > to));
    model.addAttribute("announcements", announcementsShort);
    model.addAttribute("emergency", emergencyAnnouncements);
    model.addAttribute(
        "hideAbstract", Boolean.valueOf(prefs.getValue(PREFERENCE_HIDE_ABSTRACT, "false")));
    return viewNameSelector.select(request, "displayAnnouncements");
  }

  private List[] getLists(PortletRequest request) throws PortletException {
    final String userId = userIdService.getUserId(request);
    List<TopicSubscription> myTopics = null;

    final Cache userTopicsCache = cm.getCache(USER_TOPICS_CACHE);
    String topicsHash = userTopicsCache.get(userId, String.class);
    logger.debug("topicHash: " + topicsHash + " for " + userId);
    if (topicsHash == null) {

      // fetch the user's topic subscription from the database
      myTopics = tss.getTopicSubscription(request);

      final String topicNames = myTopics.stream()
          .map(t -> t.getTopic().getTitle())
          .sorted()
          .collect(Collectors.joining("#"));
      logger.debug(topicNames);

      topicsHash = DigestUtils.md5DigestAsHex(topicNames.getBytes());
      userTopicsCache.put(userId, topicsHash);
      logger.debug("new topicHash: " + topicsHash + " for " + userId);
    }

    final Cache topicsCache = cm.getCache(TOPICS_CACHE);
    Map<Boolean, List<Announcement>> topicLists = topicsCache.get(topicsHash, Map.class);

    if (topicLists == null) {
      logger.debug("Caching topic list for hash: " + topicsHash);
      if (myTopics == null) {
        // fetch the user's topic subscription from the database if not done above
        myTopics = tss.getTopicSubscription(request);
      }

      topicLists = myTopics
          .stream()
          .filter(TopicSubscription::getSubscribed)
          .map(ts -> ts.getTopic().getPublishedAnnouncements())
          .flatMap(Collection::stream)
          .collect(Collectors.partitioningBy(
              (Announcement a) -> a.getParent().getSubscriptionMethod() == Topic.EMERGENCY));

      topicsCache.put(topicsHash, topicLists);
    }

    return new List[]{topicLists.get(false), topicLists.get(true)};
  }

  /**
   * <p>emergenciesResource.</p>
   *
   * @param request a {@link javax.portlet.ResourceRequest} object.
   * @param response a {@link javax.portlet.ResourceResponse} object.
   * @throws java.io.IOException if any.
   * @throws javax.portlet.PortletException if any.
   */
  @ResourceMapping(value = "emergencies")
  public void emergenciesResource(ResourceRequest request, ResourceResponse response)
      throws IOException, PortletException {
    logger.debug("Processing AJAX resource request for emergency alerts");
    List[] lists = getLists(request);
    final String json = mapper.writeValueAsString(lists[1]);
    response.getWriter().write(json != null && !json.isEmpty() ? json : "[]");
  }

  /**
   * <p>announcementsResource.</p>
   *
   * @param request a {@link javax.portlet.ResourceRequest} object.
   * @param response a {@link javax.portlet.ResourceResponse} object.
   * @throws java.io.IOException if any.
   * @throws javax.portlet.PortletException if any.
   */
  @ResourceMapping(value = "announcements")
  public void announcementsResource(ResourceRequest request, ResourceResponse response)
      throws IOException, PortletException {
    logger.debug("Processing AJAX resource request for announcements");
    List[] lists = getLists(request);
    final String json = mapper.writeValueAsString(lists[0]);
    response.getWriter().write(json != null && !json.isEmpty() ? json : "[]");
  }

  /**
   * <p>displayFullAnnouncement.</p>
   *
   * @param model a {@link org.springframework.ui.Model} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param announcementId a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   * @throws java.lang.Exception if any.
   */
  @RenderMapping(params = "action=" + ACTION_DISPLAY_FULL_ANNOUNCEMENT)
  public String displayFullAnnouncement(
      Model model, RenderRequest request, @RequestParam("announcementId") String announcementId)
      throws Exception {

    Announcement announcement = getAnnouncementById(request, announcementId);
    model.addAttribute("announcement", announcement);

    return viewNameSelector.select(request, "displayFullAnnouncement");
  }

  /**
   * <p>syndicateAnnouncementsAsNotifications.</p>
   *
   * @param req a {@link javax.portlet.EventRequest} object.
   * @param res a {@link javax.portlet.EventResponse} object.
   * @throws javax.portlet.PortletException if any.
   */
  @EventMapping(NOTIFICATION_QUERY_QNAME_STRING)
  public void syndicateAnnouncementsAsNotifications(final EventRequest req, final EventResponse res)
      throws PortletException {

    final NotificationQuery query = (NotificationQuery) req.getEvent().getValue();
    logger.debug(
        "Syndicating announcements for Notification portlet with windowId="
            + query.getQueryWindowId());

    final PortletPreferences prefs = req.getPreferences();
    final List<String> topicTitlesToSyndicate =
        Arrays.asList(prefs.getValues(PREFERENCE_SYNDICATE_TOPICS_AS_NOTIFICATIONS, new String[0]));

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
    final String announcementsDisplayFName =
        prefs.getValue(PREFERENCE_SYNDICATE_TOPICS_ANNOUNCEMENTS_DISPLAY_FNAME, "announcements");
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
      logger.debug(
          "Considering topic '" + topic.getTitle() + "' for remoteUser=" + req.getRemoteUser());

      final Set<Announcement> announcements = topic.getPublishedAnnouncements();

      // Ignore any that are empty...
      if (announcements.isEmpty()) {
        continue;
      }

      final List<NotificationEntry> entries = new ArrayList<NotificationEntry>();
      for (Announcement ann : announcements) {
        final NotificationEntry entry = new NotificationEntry();
        entry.setTitle(ann.getTitle());
        entry.setBody(
            ann
                .getAbstractText()); // Use abstract for body b/c notifications are intended to be smaller
        entry.setSource("Announcements"); // TODO:  Don't hard-code
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
        url.append("/uPortal") // TODO:  Don't hard-code
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

    logger.debug(
        "Found the following categories for remoteUser '"
            + req.getRemoteUser()
            + "':  "
            + categories);

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

  /**
   * <p>getDisplayPublishDate.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  @ModelAttribute("displayPublishDate")
  public boolean getDisplayPublishDate(PortletRequest req) {
    PortletPreferences prefs = req.getPreferences();
    return Boolean.parseBoolean(prefs.getValue(PREFERENCE_DISPLAY_STARTDATE, "false"));
  }

  /**
   * <p>Setter for the field <code>tss</code>.</p>
   *
   * @param tss a {@link org.jasig.portlet.announcements.service.ITopicSubscriptionService} object.
   */
  public void setTss(ITopicSubscriptionService tss) {
    this.tss = tss;
  }

  /**
   * <p>Setter for the field <code>cm</code>.</p>
   *
   * @param cm a {@link org.springframework.cache.ehcache.EhCacheCacheManager} object.
   */
  public void setCm(EhCacheCacheManager cm) {
    this.cm = cm;
  }

  /**
   * <p>displayFullAnnouncementHistory.</p>
   *
   * @param model a {@link org.springframework.ui.Model} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param announcementId a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   * @throws java.lang.Exception if any.
   */
  @RenderMapping(params = "action=displayFullAnnouncementHistory")
  public String displayFullAnnouncementHistory(
      Model model, RenderRequest request, @RequestParam("announcementId") String announcementId)
      throws Exception {

    Announcement announcement = getAnnouncementById(request, announcementId);
    model.addAttribute("announcement", announcement);

    return viewNameSelector.select(request, "displayFullAnnouncementHistory");
  }

  /**
   * <p>displayHistory.</p>
   *
   * @param model a {@link org.springframework.ui.Model} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @return a {@link java.lang.String} object.
   * @throws java.lang.Exception if any.
   */
  @RenderMapping(params = "action=displayHistory")
  public String displayHistory(Model model, RenderRequest request) throws Exception {

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
    Collections.sort(
        announcements,
        new Comparator<Announcement>() {
          @Override
          public int compare(Announcement s, Announcement s2) {
            return s2.getEndDisplay().compareTo(s.getEndDisplay());
          }
        });

    model.addAttribute("announcements", announcements);

    return viewNameSelector.select(request, "displayHistory");
  }

  /**
   * <p>getPageSize.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a int.
   */
  @ModelAttribute("increment")
  public int getPageSize(PortletRequest req) {
    final PortletPreferences prefs = req.getPreferences();
    int rslt = 5; // default
    try {
      rslt = Integer.parseInt(prefs.getValue(PREFERENCE_PAGE_SIZE, "5"));
    } catch (NumberFormatException nfe) {
      // Log it, but roll on...
      logger.warn(
          "Non-integer value encountered for "
              + PREFERENCE_PAGE_SIZE
              + ": "
              + prefs.getValue(PREFERENCE_PAGE_SIZE, null));
    }
    return rslt;
  }

  /**
   * <p>isGuest.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  @ModelAttribute("isGuest")
  public boolean isGuest(PortletRequest req) {
    boolean rslt = (req.getRemoteUser() == null);
    logger.debug("isGuest is: " + Boolean.toString(rslt));
    logger.debug("remoteUser is: " + req.getRemoteUser());
    return rslt;
  }

  /**
   * <p>getUseScrollingDisplay.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  @ModelAttribute("useScrollingDisplay")
  public boolean getUseScrollingDisplay(PortletRequest req) {
    final PortletPreferences prefs = req.getPreferences();
    return Boolean.valueOf(
        prefs.getValue(PREFERENCE_USE_SCROLLING_DISPLAY, "false")); // default is false
  }

  /**
   * <p>getScrollingDisplayHeightPixels.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a int.
   */
  @ModelAttribute("scrollingDisplayHeightPixels")
  public int getScrollingDisplayHeightPixels(PortletRequest req) {
    final PortletPreferences prefs = req.getPreferences();
    int rslt = 500; // default
    try {
      rslt = Integer.parseInt(prefs.getValue(PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS, "500"));
    } catch (NumberFormatException nfe) {
      // Log it, but roll on...
      logger.warn(
          "Non-integer value encountered for "
              + PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS
              + ": "
              + prefs.getValue(PREFERENCE_SCROLLING_DISPLAY_HEIGHT_PIXELS, null));
    }
    return rslt;
  }

  /*
   * Implementation
   */

  private List<Announcement> paginateAnnouncements(
      final List<Announcement> announcements, Integer from, Integer to, Model model) {
    List<Announcement> rslt;
    // if the announcement list is already short, then just reference it
    if (announcements.size() < to - from) {
      rslt = announcements;
    }
    // otherwise, just take the range requested and pass it along to the view
    else {
      rslt = new ArrayList<Announcement>();
      for (int i = from; i < to && announcements.size() > i; i++) {
        if (announcements.get(i) != null) {
          rslt.add(announcements.get(i));
        }
      }
    }
    return rslt;
  }

  private Announcement getAnnouncementById(PortletRequest request, String announcementId)
      throws Exception {
    Long annId = Long.valueOf(announcementId);
    Announcement announcement = announcementsService.getAnnouncement(annId);

    if (!UserPermissionChecker.inRoleForTopic(
        request, UserRoles.AUDIENCE_ROLE_NAME, announcement.getParent())) {
      throw new UnauthorizedException();
    }

    return announcement;
  }
}
