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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.model.TopicSubscription;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;
import edu.uci.vcsa.portal.portlets.announcements.service.TopicSubscriptionService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class EditDisplayPreferencesController extends AbstractController {

	private static Log log = LogFactory.getLog(EditDisplayPreferencesController.class);
	
	private TopicSubscriptionService tss;
	private IAnnouncementService announcementService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleActionRequestInternal(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		int topicsToUpdate = 0;
		
		try {
			topicsToUpdate = Integer.valueOf( request.getParameter("topicsToUpdate") );
		} catch (NumberFormatException e) {
			log.error("topicsToUpdate was not passed along from form.");
			// just log this error. 
		}
		
		List<TopicSubscription> newSubscription = new ArrayList<TopicSubscription>();
		
		for (int i=0; i<topicsToUpdate; i++) {
			Long topicId = Long.valueOf( request.getParameter("topicId_"+i) );
			Long topicSubId = Long.valueOf( request.getParameter("topicSubId_"+i) );
			Boolean subscribed = Boolean.valueOf( request.getParameter("subscribed_"+i) );
			Topic topic = announcementService.getTopic(topicId);
			
			// Make sure that any pushed_forced topics weren't sneakingly removed (by tweaking the URL, for example)
			if (topic.getSubscriptionMethod() == Topic.PUSHED_FORCED) {
				subscribed = new Boolean(true);
			} 
			
			TopicSubscription ts = new TopicSubscription(request.getRemoteUser(), topic, subscribed);
			ts.setId(topicSubId);
			
			newSubscription.add(ts);
		}
		
		if (newSubscription.size() > 0) {
			try {
				announcementService.addOrSaveTopicSubscription(newSubscription);
			} catch (Exception e) {
				log.error("ERROR saving TopicSubscriptions for user "+request.getRemoteUser()+". Message: "+e.getMessage());
			}
		} 
		
		response.setPortletMode(PortletMode.VIEW);
		response.setRenderParameter("action", "displayAnnouncements");
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		Map<String,Object> mav = new HashMap<String,Object>();
		
		List<TopicSubscription> myTopics = tss.getTopicSubscriptionEdit(request);
		
		if (request.getRemoteUser() == null || request.getRemoteUser().equalsIgnoreCase(BaseDisplayController.GUEST_USERNAME)) {
			mav.put("isGuest", new Boolean(true));
		} else {
			mav.put("isGuest", new Boolean(false));
		}
		mav.put("topicSubscriptions", myTopics);
		mav.put("topicsToUpdate", myTopics.size());
		
		return new ModelAndView("editDisplayPreferences", mav);
	}

	/**
	 * @param tss the tss to set
	 */
	public void setTss(TopicSubscriptionService tss) {
		this.tss = tss;
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}


	
	
}
