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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.AnnouncementConfiguration;
import org.jasig.portlet.announcements.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Chris Waymire (chris@waymire.net) */
@Controller
@RequestMapping("CONFIG")
public class AnnouncementConfigurationController {
  protected final Log log = LogFactory.getLog(getClass());

  private IConfigService configService;

  @Autowired(required = true)
  public void setConfigService(IConfigService configService) {
    this.configService = configService;
  }

  @RequestMapping
  public String getConfigFormView() {
    return "config";
  }

  @RequestMapping(params = "action=updateConfiguration")
  public void saveConfiguration(
      ActionRequest request,
      ActionResponse response,
      @ModelAttribute("config") AnnouncementConfiguration config,
      @RequestParam(value = "save", required = false) String save)
      throws PortletModeException {

    if (StringUtils.isNotBlank(save)) {
      log.debug("Saving announcement configuration: {" + config.toString() + "}");
      configService.saveConfiguration(request, config);
    }

    response.setPortletMode(PortletMode.VIEW);
  }

  @ModelAttribute("config")
  public AnnouncementConfiguration announcementConfiguration(final PortletRequest request) {
    final AnnouncementConfiguration config = configService.getConfiguration(request);
    return config;
  }
}
