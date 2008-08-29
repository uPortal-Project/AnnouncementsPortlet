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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;

import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class AddAnnouncementFormController extends SimpleFormController {

	private IAnnouncementService announcementService;
	private static Log log = LogFactory.getLog(AddAnnouncementFormController.class);
	private String customDateFormat = "yyyy-MM-dd";
	private String datePickerFormat = "format-y-m-d divider-dash";
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.SimpleFormController#onSubmitAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected void onSubmitAction(ActionRequest request,
			ActionResponse response, Object command, BindException errors)
			throws Exception {
		
		Long topicId = null;
		Announcement newAnn = (Announcement) command;
		
		try {
			topicId = Long.valueOf( request.getParameter("topicId") );
		} catch (NumberFormatException ex) {
		}
			
		if (topicId != null) {
			
			if (!newAnn.hasId()) {
				Topic topic = announcementService.getTopic(topicId);
				
				// add the automatic data
				newAnn.setAuthor( request.getRemoteUser() );
				newAnn.setCreated( new Date() );
				newAnn.setParent( topic );
				
				announcementService.addOrSaveAnnouncement(newAnn);
			} else {
				announcementService.mergeAnnouncement(newAnn);
			}
			
			response.setRenderParameter("topicId", topicId.toString());
			response.setRenderParameter("action", "showTopic");
		} else {
			log.error("No topicId: "+Arrays.toString(errors.getAllErrors().toArray()));
			response.setRenderParameter("action", "baseAdmin");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.SimpleFormController#referenceData(javax.portlet.PortletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(PortletRequest request, Object command,
			Errors errors) throws Exception {
		
		Map<String,Object> model = new HashMap<String,Object>();
		Announcement newAnn = (Announcement) command;
		Long topicId = null;
		
		try {
			topicId = Long.valueOf( request.getParameter("topicId") );
			Topic topic = announcementService.getTopic(topicId);
			model.put("topicId", topic.getId().toString());
			model.put("topicTitle", topic.getTitle());
		} catch (NumberFormatException ex) {
			log.debug("No topicId inside portlet request");
		}
		
		try {
			topicId = newAnn.getParent().getId();
			model.put("topicId", newAnn.getParent().getId().toString());
			model.put("topicTitle", newAnn.getParent().getTitle());
		} catch (Exception exp) {
			log.error("No topicId found by looking at parent topic id of command object: "+exp.getMessage());
		}
		
		model.put("datePickerFormat", datePickerFormat);
		
		return model;
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.BaseCommandController#initBinder(javax.portlet.PortletRequest, org.springframework.web.portlet.bind.PortletRequestDataBinder)
	 */
	@Override
	protected void initBinder(PortletRequest request,
			PortletRequestDataBinder binder) throws Exception {
	
		SimpleDateFormat dateFormat = new SimpleDateFormat(customDateFormat);
		dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.setAllowedFields(new String[] {"id","created","author","title","abstractText","message","link","startDisplay","endDisplay"});
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractFormController#formBackingObject(javax.portlet.PortletRequest)
	 */
	@Override
	protected Object formBackingObject(PortletRequest request) throws Exception {
		Announcement ann = new Announcement();
		Long topicId = null;
		Long editId = null;
		
		try {
			editId = Long.valueOf( request.getParameter("editId") );
			log.debug("formBackingObject: editId found. This is an edit request for announcement Id "+editId.toString());
			ann = announcementService.getAnnouncement(editId);
			// return immediately when we have our announcement
			return ann;
		} catch (NumberFormatException e) {
			log.debug("formBackingObject: No editId found. This is not an edit request");
		}
		
		try {
			topicId = Long.valueOf( request.getParameter("topicId") );
			Topic topic = announcementService.getTopic(topicId);
			ann.setParent(topic);
		} catch (NumberFormatException e) {
			log.error("formBackingObject: Unable to get topicId from request");
		}
		
		return ann;
	}

	@Override
	protected void handleInvalidSubmit(ActionRequest request, ActionResponse response) {
		log.error("Invalid submission. Going to baseAdmin");
		response.setRenderParameter("action","baseAdmin");
	}
	
	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * When a custom date format is set by Spring, this method converts it immediately to a string of two CSS classes
	 * required by the date picker in the view.
	 * @param customDateFormat
	 */
	public void setCustomDateFormat(String customDateFormat) {
		this.customDateFormat = customDateFormat;
		
		if (log.isDebugEnabled()) {
			log.debug("Trying to parse custom date input format: ["+customDateFormat+"]");
		}
		
		String[] finalPieces = {"", "", ""};
		String[] pieces = {"", "", ""};
		String divider = null;
		
		// Ignore any custom date format requests if the requirements are not met
		if (customDateFormat.contains("/") && !customDateFormat.contains("-") && !customDateFormat.contains(".")) {
			pieces = customDateFormat.split("/");
			divider = "slash";
		} 
		else if (customDateFormat.contains("-") && !customDateFormat.contains("/") && !customDateFormat.contains(".")) {
			pieces = customDateFormat.split("-");
			divider = "dash";
		}
		else if (customDateFormat.contains(".") && !customDateFormat.contains("/") && !customDateFormat.contains("-")) {
			pieces = customDateFormat.split("\\.");
			divider = "dot";
		}
		else {
			return;
		}
		
		// Ignore any custom date format requests if the requirements are not met
		if (pieces.length > 3) {
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Custom date input format: ["+pieces[0]+" "+divider+" "+pieces[1]+" "+divider+" "+pieces[2]+"]");
		}
		
		for (int i=0; i<pieces.length; i++) {
			if (pieces[i].equalsIgnoreCase("mm")) {
				finalPieces[i] = "m";
			}
			else if (pieces[i].equalsIgnoreCase("dd")) {
				finalPieces[i] = "d";
			}
			else if (pieces[i].equalsIgnoreCase("yyyy")) {
				finalPieces[i] = "y";
			}
		}
		
		datePickerFormat = "format-" + finalPieces[0] + "-" + finalPieces[1] + "-" + finalPieces[2] + " divider-" + divider;
		
		if (log.isDebugEnabled()) {
			log.debug("Custom date input format parsed as: ["+datePickerFormat+"]");
		}
	}




	
	
}
