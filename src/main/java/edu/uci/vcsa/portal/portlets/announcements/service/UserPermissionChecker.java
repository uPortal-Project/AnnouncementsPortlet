/**
 *  Copyright 2008. The Regents of the University of California. All Rights
 *  Reserved. Permission to use, copy, modify, and distribute any part of this
 *  software including any source code and documentation for educational,
 *  research, and non-profit purposes, without fee, and without a written
 *  agreement is hereby granted, provided that the above copyright notice, this
 *  paragraph and the following three paragraphs appear in all copies of the
 *  software and documentation. Those desiring to incorporate this software into
 *  commercial products or use for commercial purposes should contact Office of
 *  Technology Alliances, University of California, Irvine, 380 University
 *  Tower, Irvine, CA 92607-7700, Phone: (949) 824-7295, FAX: (949) 824-2899. IN
 *  NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING,
 *  WITHOUT LIMITATION, LOST PROFITS, CLAIMS OR DEMANDS, OR BUSINESS
 *  INTERRUPTION, ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE
 *  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 *  ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO
 *  REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
 *  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
 *  SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
 */
/**
 * 
 */
package edu.uci.vcsa.portal.portlets.announcements.service;

import java.util.Set;

import javax.portlet.PortletRequest;

import edu.uci.vcsa.portal.portlets.announcements.model.Topic;

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
