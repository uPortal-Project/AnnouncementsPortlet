package org.jasig.portlet.announcements.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;

import org.springframework.beans.factory.InitializingBean;

public class UserAgentViewNameSelector implements IViewNameSelector, InitializingBean {
    
    private Map<String,String> userAgentMappings;
    private final Map<Pattern,String> patterns = new HashMap<Pattern,String>();

    public void afterPropertiesSet() {
        // Compile our patterns
        for (Map.Entry<String,String> y : userAgentMappings.entrySet()) {
            patterns.put(Pattern.compile(y.getKey()), y.getValue());
        }
    }

    public String select(PortletRequest req, String baseViewName) {
        
        // Assertions.
        if (req == null) {
            String msg = "Argument 'req' cannot be null";
            throw new IllegalArgumentException(msg);
        }
        
        String rslt = baseViewName;
        
        String userAgent = req.getProperty("user-agent");
        if (userAgent != null && patterns.size() != 0) {
            for (Map.Entry<Pattern,String> y : patterns.entrySet()) {
                if (y.getKey().matcher(userAgent).matches()) {
                    rslt += y.getValue();
                    break;
                }
            }
        }

        return rslt;

    }
    
    public void setUserAgentMappings(Map<String,String> userAgentMappings) {
        this.userAgentMappings = Collections.unmodifiableMap(userAgentMappings);
    }

}
