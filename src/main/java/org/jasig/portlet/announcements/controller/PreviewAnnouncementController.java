package org.jasig.portlet.announcements.controller;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.announcements.UnauthorizedException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

/**
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
public class PreviewAnnouncementController extends AbstractController {

    private IAnnouncementService announcementService = null;
    private Boolean includeJQuery;
    
    /*
     * (non-Javadoc)
     * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    protected ModelAndView handleRenderRequestInternal(RenderRequest request,
            RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        
        Long annId = Long.parseLong(request.getParameter("annId"));
        Announcement ann = announcementService.getAnnouncement(annId);
        
        Topic topic = ann.getParent();
        
        if (!UserPermissionChecker.inRoleForTopic(request, "authors", topic) &&
                !UserPermissionChecker.inRoleForTopic(request, "moderators", topic) &&
                !UserPermissionChecker.inRoleForTopic(request, "admins", topic)) {
            throw new UnauthorizedException("You do not have access to this topic!");
        }

        model.put("announcement", ann);
        model.put("user", new UserPermissionChecker(request, topic));
        model.put("includeJQuery", includeJQuery);
        
        return new ModelAndView("previewAnnouncement", model);

    }

    public void setAnnouncementService(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }
    
    public void setIncludeJQuery(Boolean includeJQuery) {
        this.includeJQuery = includeJQuery;
    }

}
