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
package org.jasig.portlet.announcements.controller;

import java.beans.PropertyEditorSupport;

import javax.portlet.PortletException;

import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;

/**
 * @author eolsson
 *
 */
public class TopicEditor extends PropertyEditorSupport {

	private IAnnouncementService announcementService;
	
	public TopicEditor(IAnnouncementService service) {
		this.announcementService = service;
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
		Topic t = (Topic) super.getValue();
		if (t == null) {
			return null;
		}
		return t.getId().toString();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text != null && !"".equals(text)) {
			try {
				super.setValue(announcementService.getTopic(Long.parseLong(text)));
			} catch (PortletException e) {
				throw new IllegalArgumentException("Invalid Topic ID. Cannot convert to object.");
			}
		}
	}

	
	
}
