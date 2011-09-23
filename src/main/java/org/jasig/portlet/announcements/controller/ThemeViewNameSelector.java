package org.jasig.portlet.announcements.controller;

import javax.portlet.PortletRequest;

public class ThemeViewNameSelector implements IViewNameSelector {

    protected static final String THEME_NAME_PROPERTY = "themeName";
    protected static final String MOBILE_THEMES_KEY = "mobileThemes";
    protected static final String[] MOBILE_THEMES_DEFAULT = new String[]{ "UniversalityMobile" };

    private String mobileKey = "jqm";
    
    public void setMobileKey(String mobileKey) {
        this.mobileKey = mobileKey;
    }
    
    public String select(PortletRequest req, String baseViewName) {
        
        // Assertions.
        if (req == null) {
            String msg = "Argument 'req' cannot be null";
            throw new IllegalArgumentException(msg);
        }
        
        if (isMobile(req)) {
            return baseViewName.concat(".").concat(mobileKey);
        } else {
            return baseViewName;            
        }
        
    }
    
    protected boolean isMobile(PortletRequest request) {
        String[] mobileThemes = request.getPreferences().getValues(MOBILE_THEMES_KEY, MOBILE_THEMES_DEFAULT);
        String themeName = request.getProperty(THEME_NAME_PROPERTY);
        if (themeName == null) {
            return false;
        }
        
        for (String theme : mobileThemes) {
            if (themeName.equals(theme)) {
                return true;
            }
        }
        
        return false;
    }


}
