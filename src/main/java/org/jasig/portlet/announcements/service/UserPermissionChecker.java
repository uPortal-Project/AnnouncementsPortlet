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
package org.jasig.portlet.announcements.service;

import java.util.Set;

import javax.portlet.PortletRequest;

import org.jasig.portlet.announcements.model.Topic;


/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *
 * $LastChangedBy$
 * $LastChangedDate$
 *
 * Utility class that can be used statically to check role membership of a Topic, or when
 * instantiated, can be used inside a JSP for example to check role membership as it relates to
 * a specific Topic.
 *
 */
public class UserPermissionChecker {
    private static final String PORTAL_ADMIN_ROLE_NAME = "Portal_Administrators";
    private static final String ADMIN_ROLE_NAME = "admins";
    private static final String MODERATOR_ROLE_NAME = "moderator";
    private static final String AUTHOR_ROLE_NAME = "author";
    private static final String AUDIENCE_ROLE_NAME = "audience";

    private boolean admin = false;
    private boolean moderator = false;
    private boolean author = false;
    private boolean audience = false;
    private String userName = null;
    private boolean guest = true;

    /**
     *
     * @param request
     * @param topic
     */
    public UserPermissionChecker(PortletRequest request, Topic topic) {
        guest = (request.getRemoteUser() == null);
        userName = guest ? " guest" : request.getRemoteUser();

        if(request.isUserInRole(UserPermissionChecker.PORTAL_ADMIN_ROLE_NAME) || this.inRoleForTopic(request, ADMIN_ROLE_NAME, topic)) {
            admin = moderator = author = true;
        } else if(this.inRoleForTopic(request, MODERATOR_ROLE_NAME, topic)) {
            moderator = author = true;
        } else if(this.inRoleForTopic(request, AUTHOR_ROLE_NAME, topic)) {
            author = true;
        }
        audience = this.inRoleForTopic(request, AUDIENCE_ROLE_NAME, topic);
    }

    /**
     *
     * @param request
     * @param role
     * @param topic
     * @return true if the user in the PortletRequest has the role specified by the Topic
     */
    private boolean inRoleForTopic(PortletRequest request, String role, Topic topic) {
        // automatic for portal admins, only check if not a guest
        if (!guest && UserPermissionChecker.isPortalAdmin(request)) {
            return true;
        }

        Set<String> group = topic.getGroup(role);
        for (String groupMember: group) {
            if (groupMember.startsWith("USER.")) {
                String p[] = groupMember.split("\\.");
                if (p[1].equalsIgnoreCase(userName)) {
                    return true;
                }
            } else if (request.isUserInRole(groupMember)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPortalAdmin(PortletRequest request) {
        return request.isUserInRole(UserPermissionChecker.PORTAL_ADMIN_ROLE_NAME);
    }

    public boolean isGuest() {
        return guest;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @return the moderator
     */
    public boolean isModerator() {
        return moderator;
    }

    /**
     * @return the author
     */
    public boolean isAuthor() {
        return author;
    }

    /**
     * @return the audience
     */
    public boolean isAudience() {
        return audience;
    }

}
