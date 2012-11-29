package org.jasig.portlet.announcements.controller;

import javax.portlet.RenderRequest;

import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.service.UserPermissionChecker;
import org.jasig.portlet.announcements.service.UserPermissionCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PreviewAnnouncementController {

    @Autowired
    private IAnnouncementService announcementService = null;

    @Autowired
    private Boolean includeJQuery;

    @Autowired(required=true)
    private final IViewNameSelector viewNameSelector = null;

    @Autowired
    private final UserPermissionCheckerFactory userPermissionCheckerFactory = null;

    @RequestMapping(value="VIEW", params="action=previewAnnouncement")
    protected String previewAnnouncement(Model model, RenderRequest request,
            @RequestParam("annId") String annId) throws Exception {

        Announcement ann = announcementService.getAnnouncement(Long.parseLong(annId));
        Topic topic = ann.getParent();
        UserPermissionChecker upChecker = userPermissionCheckerFactory.createUserPermissionChecker(request, topic);
        upChecker.validateCanEditTopic();

        model.addAttribute("announcement", ann);
        model.addAttribute("user", upChecker);
        model.addAttribute("includeJQuery", includeJQuery);
        return viewNameSelector.select(request, "previewAnnouncement");
    }
}