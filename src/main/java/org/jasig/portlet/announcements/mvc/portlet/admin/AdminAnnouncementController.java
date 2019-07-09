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

import edu.emory.mathcs.backport.java.util.Collections;
import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.portlet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.validators.AnnouncementValidator;
import org.jasig.portlet.announcements.mvc.TopicEditor;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * <p>AdminAnnouncementController class.</p>
 *
 * @author eolsson
 * @version $Id: $Id
 */
@Controller
@RequestMapping("VIEW")
public class AdminAnnouncementController implements InitializingBean {

  /** Constant <code>PREFERENCE_USE_ATTACHMENTS="AdminAnnouncementController.useAttachme"{trunked}</code> */
  public static final String PREFERENCE_USE_ATTACHMENTS =
      "AdminAnnouncementController.useAttachments";
  /** Constant <code>DEFAULT_USE_ATTACHMENTS="true"</code> */
  public static final String DEFAULT_USE_ATTACHMENTS = "true";

  /** Constant <code>PREFERENCE_ALLOW_OPEN_ENDDATE="AdminAnnouncementController.allowOpenEn"{trunked}</code> */
  public static final String PREFERENCE_ALLOW_OPEN_ENDDATE =
      "AdminAnnouncementController.allowOpenEndDate";

  /** Constant <code>PREFERENCE_ALLOW_EMPTY_MESSAGE="AdminAnnouncementController.allowEmptyM"{trunked}</code> */
  public static final String PREFERENCE_ALLOW_EMPTY_MESSAGE =
      "AdminAnnouncementController.allowEmptyMessage";

  /** Constant <code>PREFERENCE_ABSTRACT_MAX_LENGTH="AdminAnnouncementController.abstractTex"{trunked}</code> */
  public static final String PREFERENCE_ABSTRACT_MAX_LENGTH =
      "AdminAnnouncementController.abstractTextMaxLength";
  /** Constant <code>DEFAULT_ABSTRACT_MAX_LENGTH="255"</code> */
  public static final String DEFAULT_ABSTRACT_MAX_LENGTH = "255";

  /** Constant <code>PREFERENCE_TINY_MCE_INITIALIZATION_OPTIONS="AdminAnnouncementController.tinyMceInit"{trunked}</code> */
  public static final String PREFERENCE_TINY_MCE_INITIALIZATION_OPTIONS =
      "AdminAnnouncementController.tinyMceInitializationOptions";
  /** Constant <code>DEFAULT_TINY_MCE_INITIALIZATION_OPTIONS="mode:\"textareas\",  editor_selector:\""{trunked}</code> */
  public static final String DEFAULT_TINY_MCE_INITIALIZATION_OPTIONS =
      "mode:\"textareas\", "
          + " editor_selector:\"mceEditor\", "
          + " theme:\"advanced\", "
          + " plugins:\"paste,preview\", "
          + " theme_advanced_buttons1:\"bold,italic,underline,strikethrough,separator,outdent,indent,blockquote,separator,fontselect,fontsizeselect\", "
          + " theme_advanced_buttons2:\"cut,copy,paste,pastetext,pasteword,separator,bullist,numlist,separator,charmap,emotions\", "
          + " theme_advanced_buttons3:\"undo,redo,separator,link,unlink,image,anchor,cleanup,help,separator,code,preview\", "
          + " theme_advanced_toolbar_location:\"top\", "
          + " theme_advanced_toolbar_align:\"left\", "
          + " extended_valid_elements:\"a[name|href|target|title|onclick],span[class|align|style]\", "
          + " theme_advanced_path:false";

  @Autowired private IAnnouncementService announcementService;

  private static final Log log = LogFactory.getLog(AdminController.class);
  private PropertyEditor topicEditor;

  @Autowired private String customDateFormat = "yyyy-MM-dd";

  /** CSS classes added to the start & end date input fields to enable the jQuery UI datepicker */
  private String datePickerFormat = "format-y-m-d divider-dash";

  @Autowired private UserPermissionCheckerFactory userPermissionCheckerFactory = null;

  /**
   * <p>initBinder.</p>
   *
   * @param binder a {@link org.springframework.web.bind.WebDataBinder} object.
   */
  @InitBinder("announcement")
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(customDateFormat);
    dateFormat.setLenient(false);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    binder.registerCustomEditor(Topic.class, topicEditor);
    binder.setAllowedFields(
        new String[] {
          "id",
          "created",
          "author",
          "title",
          "abstractText",
          "message",
          "link",
          "startDisplay",
          "endDisplay",
          "parent",
          "action",
          "attachments"
        });
  }

  /**
   * Does all the prep work before showing the form
   *
   * @param editId a {@link java.lang.Long} object.
   * @param topicId a {@link java.lang.Long} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param model a {@link org.springframework.ui.Model} object.
   * @return a {@link java.lang.String} object.
   * @throws javax.portlet.PortletException if any.
   */
  @RequestMapping(params = "action=addAnnouncement")
  public String showAddAnnouncementForm(
      @RequestParam(value = "editId", required = false) Long editId,
      @RequestParam(value = "topicId", required = false) Long topicId,
      RenderRequest request,
      Model model)
      throws PortletException {

    PortletPreferences prefs = request.getPreferences();

    if (!model.containsAttribute("announcement")) {
      Announcement ann = new Announcement();
      Topic topic = null;

      if (editId != null) {
        try {
          log.debug(
              "editId found. This is an edit request for announcement Id " + editId.toString());
          ann = announcementService.getAnnouncement(editId);
          // return immediately when we have our announcement

        } catch (NumberFormatException e) {
          log.debug("No editId found. This is not an edit request");
        }
      }

      if (ann != null && ann.getParent() == null) {
        try {
          topic = announcementService.getTopic(topicId);
          ann.setParent(topic);
        } catch (NumberFormatException e) {
          log.error("Unable to get topicId from request");
        }
      }

      model.addAttribute("announcement", ann);
    }

    model.addAttribute("datePickerFormat", datePickerFormat);
    model.addAttribute(
        "abstractMaxLength",
        prefs.getValue(PREFERENCE_ABSTRACT_MAX_LENGTH, DEFAULT_ABSTRACT_MAX_LENGTH));
    model.addAttribute(
        "tinyMceInitializationOptions",
        prefs.getValue(
            PREFERENCE_TINY_MCE_INITIALIZATION_OPTIONS, DEFAULT_TINY_MCE_INITIALIZATION_OPTIONS));
    model.addAttribute(
        "useAttachments",
        Boolean.valueOf(prefs.getValue(PREFERENCE_USE_ATTACHMENTS, DEFAULT_USE_ATTACHMENTS)));
    return "addAnnouncement";
  }

  /**
   * Saves the announcement
   *
   * @param req a {@link javax.portlet.ActionRequest} object.
   * @param res a {@link javax.portlet.ActionResponse} object.
   * @throws javax.portlet.PortletException
   * @param announcement a {@link org.jasig.portlet.announcements.model.Announcement} object.
   * @param result a {@link org.springframework.validation.BindingResult} object.
   * @param status a {@link org.springframework.web.bind.support.SessionStatus} object.
   */
  @RequestMapping(params = "action=addAnnouncement")
  public void actionAddAnnouncementForm(
      @ModelAttribute("announcement") Announcement announcement,
      BindingResult result,
      SessionStatus status,
      ActionRequest req,
      ActionResponse res)
      throws PortletException {

    // First verify the user has AUTHOR permission for this topic
    UserPermissionChecker upChecker =
        userPermissionCheckerFactory.createUserPermissionChecker(req, announcement.getParent());
    if (!(upChecker.isAdmin() || upChecker.isModerator() || upChecker.isAuthor())) {
      throw new UnauthorizedException(
          "You do not have permission to create an announcement in this topic");
    }

    // Next validate the announcement
    new AnnouncementValidator(getAllowOpenEndDate(req), getAllowEmptyMessage(req))
        .validate(announcement, result);
    if (result.hasErrors()) {
      res.setRenderParameter("action", "addAnnouncement");
      return;
    }

    // Before we save, be sure the user isn't sneaking in a disallowed attachment through clever request hacking...
    PortletPreferences prefs = req.getPreferences();
    final boolean useAttachments =
        Boolean.valueOf(prefs.getValue(PREFERENCE_USE_ATTACHMENTS, DEFAULT_USE_ATTACHMENTS));
    if (!useAttachments) {
      announcement.setAttachments(Collections.emptySet());
    }

    if (!result.hasErrors()) {
      if (!announcement.hasId()) {
        // add the automatic data
        announcement.setAuthor(req.getRemoteUser());
        announcement.setCreated(new Date());
        announcementService.addOrSaveAnnouncement(announcement);
      } else {
        announcementService.mergeAnnouncement(announcement);
      }

      status.setComplete();
      res.setRenderParameter("topicId", announcement.getParent().getId().toString());
      res.setRenderParameter("action", "showTopic");
    }
  }

  /**
   * Handles deletion of announcements
   *
   * @param topicId a {@link java.lang.Long} object.
   * @param annId a {@link java.lang.Long} object.
   * @param response a {@link javax.portlet.ActionResponse} object.
   * @throws javax.portlet.PortletException
   * @param request a {@link javax.portlet.ActionRequest} object.
   */
  @RequestMapping(params = "action=deleteAnnouncement")
  public void actionDeleteAnnouncement(
      @RequestParam("topicId") Long topicId,
      @RequestParam("annId") Long annId,
      ActionRequest request,
      ActionResponse response)
      throws PortletException {

    Topic topic = announcementService.getTopic(topicId);
    Announcement ann = announcementService.getAnnouncement(annId);

    UserPermissionChecker upChecker =
        userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
    if (upChecker.isAdmin()
        || upChecker.isModerator()
        || (upChecker.isAuthor()
            && ann.getAuthor() != null
            && ann.getAuthor().equals(request.getRemoteUser()))) {
      // the person deleting the announcement must be the author, a moderator or an admin
      announcementService.deleteAnnouncement(ann);
    } else {
      throw new UnauthorizedException("You do not have permission to delete this announcement");
    }

    response.setRenderParameter("topicId", topicId.toString());
    response.setRenderParameter("action", "showTopic");
  }

  /**
   * <p>getAllowOpenEndDate.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  public boolean getAllowOpenEndDate(PortletRequest req) {
    PortletPreferences prefs = req.getPreferences();
    return Boolean.parseBoolean(prefs.getValue(PREFERENCE_ALLOW_OPEN_ENDDATE, "false"));
  }

  /**
   * <p>getAllowEmptyMessage.</p>
   *
   * @param req a {@link javax.portlet.PortletRequest} object.
   * @return a boolean.
   */
  public boolean getAllowEmptyMessage(PortletRequest req) {
    PortletPreferences prefs = req.getPreferences();
    return Boolean.parseBoolean(prefs.getValue(PREFERENCE_ALLOW_EMPTY_MESSAGE, "false"));
  }

  /**
   * When a custom date format is set by Spring, this method converts it immediately to a string of
   * two CSS classes required by the date picker in the view.
   *
   * @param customDateFormat a {@link java.lang.String} object.
   */
  public void setCustomDateFormat(String customDateFormat) {
    this.customDateFormat = customDateFormat;

    if (log.isDebugEnabled()) {
      log.debug("Trying to parse custom date input format: [" + customDateFormat + "]");
    }

    String[] finalPieces = {"", "", ""};
    String[] pieces = {"", "", ""};
    String divider = null;

    // Ignore any custom date format requests if the requirements are not met
    if (customDateFormat.contains("/")
        && !customDateFormat.contains("-")
        && !customDateFormat.contains(".")) {
      pieces = customDateFormat.split("/");
      divider = "slash";
    } else if (customDateFormat.contains("-")
        && !customDateFormat.contains("/")
        && !customDateFormat.contains(".")) {
      pieces = customDateFormat.split("-");
      divider = "dash";
    } else if (customDateFormat.contains(".")
        && !customDateFormat.contains("/")
        && !customDateFormat.contains("-")) {
      pieces = customDateFormat.split("\\.");
      divider = "dot";
    } else {
      return;
    }

    // Ignore any custom date format requests if the requirements are not met
    if (pieces.length > 3) {
      return;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "Custom date input format: ["
              + pieces[0]
              + " "
              + divider
              + " "
              + pieces[1]
              + " "
              + divider
              + " "
              + pieces[2]
              + "]");
    }

    for (int i = 0; i < pieces.length; i++) {
      if (pieces[i].equalsIgnoreCase("mm")) {
        finalPieces[i] = "m";
      } else if (pieces[i].equalsIgnoreCase("dd")) {
        finalPieces[i] = "d";
      } else if (pieces[i].equalsIgnoreCase("yyyy")) {
        finalPieces[i] = "y";
      }
    }

    datePickerFormat =
        "format-"
            + finalPieces[0]
            + "-"
            + finalPieces[1]
            + "-"
            + finalPieces[2]
            + " divider-"
            + divider;

    if (log.isDebugEnabled()) {
      log.debug("Custom date input format parsed as: [" + datePickerFormat + "]");
    }
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

  /**
   * <p>afterPropertiesSet.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public void afterPropertiesSet() throws Exception {
    topicEditor = new TopicEditor(announcementService);
  }
}
