/**
 * 
 */
package edu.uci.vcsa.portal.portlets.announcements.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;

/**
 * @author eolsson
 *
 */
public class ApproveAjaxController extends AbstractController {

	private IAnnouncementService announcementService;
	private BaseDisplayController displayController;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
				
		Long annId = Long.valueOf( request.getParameter("annId") );
		Boolean approval = Boolean.valueOf( request.getParameter("approval") );
		Announcement ann = announcementService.getAnnouncement(annId);
		
		Date now = new Date();
		int status = 3;
		/**
		 * Scheduled = 0
		 * Expired   = 1
		 * Showing   = 2
		 * Pending   = 3
		 */
		if (ann.getStartDisplay().after(now) && ann.getEndDisplay().after(now) && approval) {
			status = 0;
		}
		else if (ann.getStartDisplay().before(now) && ann.getEndDisplay().after(now) && approval) {
			status = 2;
		}
		else if (ann.getEndDisplay().before(now)) {
			status = 1;
		}
		
		ann.setPublished(approval);
		displayController.invalidateGuestCache();
		
		announcementService.addOrSaveAnnouncement(ann);
		
		return new ModelAndView("/ajaxApprove", "status", status);
		
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * @param displayController the displayController to set
	 */
	public void setDisplayController(BaseDisplayController displayController) {
		this.displayController = displayController;
	}
	
}
