/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jasig.portlet.announcements.service;

import java.util.Set;
import javax.portlet.PortletRequest;
import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.UserRoles;

/**
 * Utility class that can be used statically to check role membership of a Topic, or when
 * instantiated, can be used inside a JSP for example to check role membership as it relates to a
 * specific Topic.
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 */
public final class UserPermissionChecker implements UserRoles {

  /**
   * This approach to unauthenticated users does not support multiples. In this case, thankfully, it
   * only applies to circumstances where the username 'guest' has been explicitly added to the
   * audience of a topic (which is uncommon). It doesn't interfere with general support for multiple
   * guest users.
   */
  private static final String GUEST_USERNAME = "guest";

  private final boolean admin;
  private final boolean moderator;
  private final boolean author;
  private final boolean audience;
  private final String userName;
  private final boolean guest;

  /**
   * Used by {@link UserPermissionCheckerFactory}. A fully-constructed instance can tell you
   * anything about a user's permissions, but is more expensive to create. If you only need to know
   * if the user is in the audience for a topic, use the static method.
   */
  /* package-private */ UserPermissionChecker(PortletRequest request, Topic topic) {
    guest = (request.getRemoteUser() == null);
    userName = guest ? GUEST_USERNAME : request.getRemoteUser();

    if (request.isUserInRole(UserRoles.PORTAL_ADMIN_ROLE_NAME)
        || inRoleForTopic(request, ADMIN_ROLE_NAME, topic)) {
      admin = moderator = author = true;
    } else if (inRoleForTopic(request, MODERATOR_ROLE_NAME, topic)) {
      admin = false;
      moderator = author = true;
    } else if (inRoleForTopic(request, AUTHOR_ROLE_NAME, topic)) {
      admin = moderator = false;
      author = true;
    } else {
      admin = moderator = author = false;
    }

    audience = inRoleForTopic(request, AUDIENCE_ROLE_NAME, topic);
  }

  /** @return true if the user in the PortletRequest has the role specified by the Topic */
  public static boolean inRoleForTopic(PortletRequest request, String role, Topic topic) {
    boolean isGuest = (request.getRemoteUser() == null);

    // automatic for portal admins, only check if not a guest
    if (!isGuest && UserPermissionChecker.isPortalAdmin(request)) {
      return true;
    }

    String userName = isGuest ? GUEST_USERNAME : request.getRemoteUser();

    Set<String> group = topic.getGroup(role);
    for (String groupMember : group) {
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
    return request.isUserInRole(UserRoles.PORTAL_ADMIN_ROLE_NAME);
  }

  public boolean isGuest() {
    return guest;
  }
  /**
   * Used in JSPs.
   *
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Used in JSPs.
   *
   * @return the admin
   */
  public boolean isAdmin() {
    return admin;
  }

  /** @return the moderator */
  public boolean isModerator() {
    return moderator;
  }

  /** @return the author */
  public boolean isAuthor() {
    return author;
  }

  /** @return the audience */
  public boolean isAudience() {
    return audience;
  }

  public boolean canEditTopic() {
    return (isAuthor() || isModerator() || isAdmin());
  }

  public void validateCanEditTopic() {
    if (!canEditTopic()) throw new UnauthorizedException("You do not have access to this topic!");
  }
}
