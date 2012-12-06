package org.jasig.portlet.announcements.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("HELP")
public class AdminHelpController {

    @RequestMapping
    public String getHelpView() {
        return "help";
    }

}