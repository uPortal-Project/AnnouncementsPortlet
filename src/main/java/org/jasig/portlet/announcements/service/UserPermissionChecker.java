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
		
	public boolean admin;
	public boolean moderator;
	public boolean author;
	public boolean audience;
	public String userName;
	
	private static final String ADMIN_ROLE_NAME = "Portal_Administrators";
	
	/**
	 * 
	 * @param request
	 * @param topic
	 */
	public UserPermissionChecker(PortletRequest request, Topic topic) {
		String user = request.getRemoteUser();
		if (user == null) {
			user = "guest";
		}
		
		admin = UserPermissionChecker.inRoleForTopic(request, "admins", topic);
		moderator = UserPermissionChecker.inRoleForTopic(request, "moderators", topic);
		author = UserPermissionChecker.inRoleForTopic(request, "authors", topic);
		audience = UserPermissionChecker.inRoleForTopic(request, "audience", topic);
		userName = user;
		
		if (request.isUserInRole(UserPermissionChecker.ADMIN_ROLE_NAME)) {
			admin = true;
		}
		
		// Make sure that roles are properly inherited
		if (admin) {
			moderator = true;
		}
		
		if (moderator) {
			author = true;
		}
	}
	
	public static boolean isPortalAdmin(PortletRequest request) {
		return request.isUserInRole(UserPermissionChecker.ADMIN_ROLE_NAME);
	}
	
	/**
	 * 
	 * @param request
	 * @param role
	 * @param topic
	 * @return true if the user in the PortletRequest has the role specified by the Topic
	 */
	public static boolean inRoleForTopic(PortletRequest request, String role, Topic topic) {
		// automatic for portal admins
		if (UserPermissionChecker.isPortalAdmin(request)) {
			return true;
		}
		
		String user = request.getRemoteUser();
		if (user == null) {
			user = "guest";
		}
		
		Set<String> group = topic.getGroup(role);
		for (String groupMember: group) {
			if (request.isUserInRole(groupMember)) {
				return true;
			}
			if (groupMember.startsWith("USER.")) {
				String p[] = groupMember.split("\\.");
				if (p[1].equalsIgnoreCase(user)) {
					return true;
				}
			}
		}
		return false;
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
