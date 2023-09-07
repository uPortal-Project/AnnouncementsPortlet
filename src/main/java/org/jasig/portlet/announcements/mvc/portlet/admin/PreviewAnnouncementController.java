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

import javax.portlet.RenderRequest;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.mvc.IViewNameSelector;
import org.jasig.portlet.announcements.service.IAnnouncementsService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>PreviewAnnouncementController class.</p>
 *
 * @author Unknown
 * @version $Id: $Id
 */
@Controller
public class PreviewAnnouncementController {

  @Autowired private IAnnouncementsService announcementsService = null;

  @Autowired(required = true)
  private final IViewNameSelector viewNameSelector = null;

  @Autowired private final UserPermissionCheckerFactory userPermissionCheckerFactory = null;

  /**
   * <p>previewAnnouncement.</p>
   *
   * @param model a {@link org.springframework.ui.Model} object.
   * @param request a {@link javax.portlet.RenderRequest} object.
   * @param annId a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   * @throws java.lang.Exception if any.
   */
  @RequestMapping(value = "VIEW", params = "action=previewAnnouncement")
  protected String previewAnnouncement(
      Model model, RenderRequest request, @RequestParam("annId") String annId) throws Exception {

    Announcement ann = announcementsService.getAnnouncement(Long.parseLong(annId));
    Topic topic = ann.getParent();
    UserPermissionChecker upChecker =
        userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
    upChecker.validateCanEditTopic();

    model.addAttribute("announcement", ann);
    model.addAttribute("user", upChecker);
    return viewNameSelector.select(request, "previewAnnouncement");
  }
}
