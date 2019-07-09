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
package org.jasig.portlet.announcements.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.model.Role;
import org.jasig.portlet.announcements.model.RoleSelection;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class attempts to read the portlet.xml file to determine what groups should be available to
 * evaluate against. Each group is a <security-role-ref> element in the portlet.xml
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 * @version $Id: $Id
 */
@Service
public class PortletXMLGroupService implements ServletContextAware, IGroupService {

  private final String PORTLET_XML_PATH = "/WEB-INF/portlet.xml";
  private Document doc;
  private List<String> roles;
  private static Log log = LogFactory.getLog(PortletXMLGroupService.class);
  private ServletContext context;

  /**
   * <p>init.</p>
   */
  @PostConstruct
  public void init() {
    parseXml();

    if (doc != null) {
      String roleNameCandidate;
      roles = new ArrayList<String>();

      // Find all the <security-role-ref> elements in the file
      NodeList roleSections = doc.getElementsByTagName("security-role-ref");
      for (int i = 0; i < roleSections.getLength(); i++) {
        // for each <security-role-ref>, get the child nodes
        if (roleSections.item(i).hasChildNodes()) {
          NodeList roleNames = roleSections.item(i).getChildNodes();
          for (int j = 0; j < roleNames.getLength(); j++) {
            // go through the child nodes of each <security-role-ref> to find the <role-name> node
            if (roleNames.item(j).getNodeName().equalsIgnoreCase("role-name")) {
              // copy the <role-name> to the roles list if it's not there already
              roleNameCandidate = roleNames.item(j).getTextContent();
              if (!roles.contains(roleNameCandidate)) {
                roles.add(roleNameCandidate);
              }
            }
          }
          roleNames = null;
        }
      }

      Collections.sort(roles);
      log.info("Successfully instantiated and found roles [" + getRolesPretty() + "]");
    } else {
      log.error("Error parsing the file: " + PORTLET_XML_PATH + ". See other messages for trace.");
    }
  }

  /**
   * <p>getAllRoles.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Role> getAllRoles() {
    List<Role> list = new ArrayList<>();

    for (String role : roles) {
      list.add(new Role(role, false));
    }

    return list;
  }

  /** {@inheritDoc} */
  public List<Role> getAllRolesFromGroupSet(Set<String> selected) {
    List<Role> list = new ArrayList<>();

    for (String role : roles) {
      if (selected.contains(role)) list.add(new Role(role, true));
      else list.add(new Role(role, false));
    }

    for (String person : selected) {
      if (person.startsWith("USER.")) {
        list.add(new Role(person, true));
      }
    }

    return list;
  }

  /** {@inheritDoc} */
  public Set<String> getSetForRoleSelection(RoleSelection roleSel) {
    Set<String> newSet = new TreeSet<>();
    newSet.addAll(roleSel.getSelectedRoles());
    return newSet;
  }

  private String getRolesPretty() {
    StringBuffer sb = new StringBuffer();
    for (String role : roles) {
      sb.append(role + ", ");
    }
    return sb.toString();
  }

  private void parseXml() {

    try {
      URL portletXmlUrl = context.getResource(PORTLET_XML_PATH);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      InputSource xmlInp = new InputSource(portletXmlUrl.openStream());
      DocumentBuilder dbl = dbf.newDocumentBuilder();
      doc = dbl.parse(xmlInp);
      log.debug("Finished parsing " + PORTLET_XML_PATH + ".");
    } catch (MalformedURLException e) {
      log.error(e.getMessage());
    } catch (ParserConfigurationException e) {
      log.error(e.getMessage());
    } catch (java.io.IOException e) {
      log.error(e.getMessage());
    } catch (org.xml.sax.SAXException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /** {@inheritDoc} */
  public void setServletContext(ServletContext context) {
    this.context = context;
  }
}
