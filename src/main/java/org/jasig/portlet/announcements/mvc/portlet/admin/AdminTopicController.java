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
package org.jasig.portlet.announcements.mvc.portlet.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.AnnouncementSortStrategy;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.UserRoles;
import org.jasig.portlet.announcements.model.validators.TopicValidator;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * <p>AdminTopicController class.</p>
 *
 * @author eolsson
 * @version $Id: $Id
 */
@Controller
@RequestMapping("VIEW")
public class AdminTopicController {

  /** Constant <code>PREFERENCE_SORT_STRATEGY="AdminTopicController.AnnouncementSortSt"{trunked}</code> */
  public static final String PREFERENCE_SORT_STRATEGY =
      "AdminTopicController.AnnouncementSortStrategy";
  /** Constant <code>DEFAULT_SORT_STRATEGY="START_DISPLAY_DATE_ASCENDING"</code> */
  public static final String DEFAULT_SORT_STRATEGY = "START_DISPLAY_DATE_ASCENDING";

  private static final Log log = LogFactory.getLog(AdminTopicController.class);

  @Autowired private IAnnouncementService announcementService;

  @Autowired private UserPermissionCheckerFactory userPermissionCheckerFactory = null;

  /**
   * Add topic view controller, creates or fetches the topic for adding or editing
   *
   * @param topicIdStr a {@link java.lang.String} object.
   * @param model a {@link org.springframework.ui.Model} object.
   * @throws javax.portlet.PortletException if any.
   * @return a {@link java.lang.String} object.
   */
  @RequestMapping(params = "action=addTopic")
  public String showAddTopicForm(
      @RequestParam(value = "edit", required = false) String topicIdStr, Model model)
      throws PortletException {

    if (!model.containsAttribute("topic")) {
      if (topicIdStr != null && !"".equals(topicIdStr)) {
        Long topicId = Long.parseLong(topicIdStr);
        Topic t = announcementService.getTopic(topicId);
        model.addAttribute("topic", t);
        if (log.isDebugEnabled()) log.debug("Adding existing topic to model: " + t.toString());
      } else {
        model.addAttribute("topic", new Topic());
        if (log.isDebugEnabled()) log.debug("Adding new topic to model");
      }
    }
    return "addTopic";
  }

  /**
   * Saves the Topic that was submitted
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   * @param result a {@link org.springframework.validation.BindingResult} object.
   * @param status a {@link org.springframework.web.bind.support.SessionStatus} object.
   * @param request a {@link javax.portlet.ActionRequest} object.
   * @param response a {@link javax.portlet.ActionResponse} object.
   * @throws javax.portlet.PortletException if any.
   */
  @RequestMapping(params = "action=addTopic")
  public void actionAddTopicForm(
      @ModelAttribute("topic") Topic topic,
      BindingResult result,
      SessionStatus status,
      ActionRequest request,
      ActionResponse response)
      throws PortletException {

    if (!UserPermissionChecker.isPortalAdmin(request)) {
      throw new UnauthorizedException("You do not have access to create a topic");
    }

    new TopicValidator().validate(topic, result);
    if (result.hasErrors()) {
      if (log.isDebugEnabled()) log.debug("Error in form: " + result.toString());
      response.setRenderParameter("action", "addTopic");
      return;
    }

    if (!result.hasErrors() && topic != null) {
      if (log.isDebugEnabled()) log.debug("No errors in form");

      // no id has been assigned by hibernate, so this must be a new topic
      if (!topic.hasId()) {
        topic.setCreator(request.getRemoteUser());
        announcementService.addOrSaveTopic(topic);
      } else {
        Long id = topic.getId();
        Topic oldTopic = announcementService.getTopic(id);

        oldTopic.setTitle(topic.getTitle());
        oldTopic.setDescription(topic.getDescription());
        oldTopic.setAllowRss(topic.isAllowRss());
        oldTopic.setSubscriptionMethod(topic.getSubscriptionMethod());
        announcementService.addOrSaveTopic(oldTopic);
      }
      status.setComplete();

      response.setRenderParameter("action", "baseAdmin");
    }
  }

  /**
   * Delete a specified topic
   *
   * @param topicId a {@link java.lang.String} object.
   * @param response a {@link javax.portlet.ActionResponse} object.
   * @throws java.lang.NumberFormatException if the topicId is not a valid {@code Long}
   * @throws javax.portlet.PortletException if any.
   * @param request a {@link javax.portlet.ActionRequest} object.
   */
  @RequestMapping(params = "action=deleteTopic")
  public void actionDeleteTopic(
      @RequestParam("topicId") String topicId, ActionRequest request, ActionResponse response)
      throws NumberFormatException, PortletException {
    Topic topic = announcementService.getTopic(Long.parseLong(topicId));

    if (!UserPermissionChecker.inRoleForTopic(request, UserRoles.ADMIN_ROLE_NAME, topic)) {
      throw new UnauthorizedException("You do not have access to delete this topic!");
    }

    announcementService.deleteTopic(topic);

    response.setRenderParameter("action", "baseAdmin");
  }

  /**
   * Show a specified topic management view
   *
   * @param topicId a {@link java.lang.String} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param model a {@link org.springframework.ui.Model} object.
   * @throws java.lang.NumberFormatException if the topicId is not a valid {@code Long}
   * @throws javax.portlet.PortletException if any.
   * @return a {@link java.lang.String} object.
   */
  @RequestMapping(params = "action=showTopic")
  public String showTopic(
      @RequestParam("topicId") String topicId, RenderRequest request, Model model)
      throws NumberFormatException, PortletException {

    PortletPreferences prefs = request.getPreferences();
    Topic topic = announcementService.getTopic(Long.parseLong(topicId));

    UserPermissionChecker upChecker =
        userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
    upChecker.validateCanEditTopic();

    Set<Announcement> annSet = topic.getNonHistoricAnnouncements();
    List<Announcement> annList = new ArrayList<Announcement>();
    annList.addAll(annSet);
    if (annSet.size() < 1) annList = null;

    if (annList != null) {
      Collections.sort(
          annList,
          AnnouncementSortStrategy.getStrategy(
              prefs.getValue(PREFERENCE_SORT_STRATEGY, DEFAULT_SORT_STRATEGY)));
    }

    model.addAttribute("user", upChecker);
    model.addAttribute("topic", topic);
    model.addAttribute("announcements", annList);
    model.addAttribute("now", new Date());

    return "showTopic";
  }

  /** @param announcementService the announcementService to set */
  /**
   * <p>Setter for the field <code>announcementService</code>.</p>
   *
   * @param announcementService a {@link org.jasig.portlet.announcements.service.IAnnouncementService} object.
   */
  public void setAnnouncementService(IAnnouncementService announcementService) {
    this.announcementService = announcementService;
  }
}
