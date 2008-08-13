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
package edu.uci.vcsa.portal.portlets.announcements.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;

import edu.uci.vcsa.portal.portlets.announcements.model.RoleSelection;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;
import edu.uci.vcsa.portal.portlets.announcements.service.IGroupService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class AddMembersFormController extends SimpleFormController {

	private IAnnouncementService announcementService;
	private IGroupService groupService;
	
	public AddMembersFormController() {
		super();
		setCommandClass(RoleSelection.class);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.SimpleFormController#onSubmitAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected void onSubmitAction(ActionRequest request,
			ActionResponse response, Object command, BindException errors)
			throws Exception {
		if (!errors.hasErrors()) {
			RoleSelection rolesSelected = (RoleSelection) command;
			Long topicId = Long.parseLong((String) request.getParameter("topicId"));
			String groupKey = (String) request.getParameter("groupKey");
			Topic topic = announcementService.getTopic(topicId);
			
			// Extract and save the USER members
			Set<String> oldGroupList = topic.getGroup(groupKey);
			Set<String> newList = new TreeSet<String>();
			
			// look for members that are users and add them to the list
			for (String member: oldGroupList) {
				if (member.startsWith("USER."))
					newList.add(member);
			}
			
			// update the members of the selected group
			if (rolesSelected != null && 
					rolesSelected.getSelectedRoles() != null && 
					rolesSelected.getSelectedRoles().size() > 0) {
				newList.addAll(groupService.getSetForRoleSelection(rolesSelected));
			}
			
			// if nothing was selected, then just save the users, or if it was, save the changes now
			topic.setGroup(groupKey, newList );
			
			// save the topic to the database
			announcementService.addOrSaveTopic(topic);
			
			response.setRenderParameter("topicId", topicId.toString());
			response.setRenderParameter("action", "showTopic");
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(PortletRequest request) throws Exception {

		Map<String,Object> mav = new HashMap<String,Object>();
		String topicId = (String) request.getParameter("topicId");
		Topic topic = announcementService.getTopic(Long.parseLong(topicId));
		
		mav.put("groupKey", (String) request.getParameter("groupKey"));
		mav.put("topic", topic);
		mav.put("roles", groupService.getAllRolesFromGroupSet( topic.getGroup( (String) request.getParameter("groupKey") ) ));
		
		return mav;
	}

	@Override
	protected Object formBackingObject(PortletRequest request) throws Exception {
		Long topicId = Long.valueOf(request.getParameter("topicId"));
		String groupKey = request.getParameter("groupKey");
		Topic topic = announcementService.getTopic(topicId);
		Set<String> group = topic.getGroup(groupKey);
		
		RoleSelection rs = new RoleSelection(group);
		return rs;
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * @param groupService the groupService to set
	 */
	public void setGroupService(IGroupService groupService) {
		this.groupService = groupService;
	}

}
