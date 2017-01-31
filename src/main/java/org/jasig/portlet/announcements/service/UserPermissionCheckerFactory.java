/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.service;

import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.jasig.portlet.announcements.model.Topic;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionCheckerFactory implements InitializingBean{
    private static final Logger logger = Logger.getLogger(UserPermissionCheckerFactory.class);
    private static final String CACHE_KEY_DELIM = "|";
    private static final String CACHE_NAME = "userPermissionCheckerCache";

    @Autowired
    private EhCacheCacheManager cacheManager = null;

    @Autowired
    private UserIdService userIdService;

    private Cache cache = null;

    public UserPermissionChecker createUserPermissionChecker(PortletRequest request, Topic topic) {

        String key = getCacheKey(request, topic);
        Element element = cache.get(key);
        if(element == null) {
            if(logger.isTraceEnabled()) {
                logger.trace("Creating cache entry for " + key);
            }
            UserPermissionChecker value = new UserPermissionChecker(request, topic);
            cache.put(new Element(key, value));
            return value;

        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Successfully retrieved cache entry for " + key);
            }
            return (UserPermissionChecker) element.getObjectValue();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = cacheManager.getCacheManager().getCache(CACHE_NAME);
        if (cache == null) {
            throw new BeanCreationException("Required " + CACHE_NAME + " could not be loaded.");
        }
        else {
            if(logger.isDebugEnabled()) {
                logger.debug(CACHE_NAME + " created.");
            }
        }
    }

    private String getCacheKey(PortletRequest request, Topic topic) {
        String userId = userIdService.getUserId(request);
        return new StringBuilder(userId).append(CACHE_KEY_DELIM).append(topic.getTitle()).toString();
    }

}
