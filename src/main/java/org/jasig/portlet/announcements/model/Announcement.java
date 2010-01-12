/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.model;

import java.util.Date;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class Announcement implements Comparable<Announcement> {

	private String title;
	private String abstractText;
	private Date created;
	private Date startDisplay;
	private Date endDisplay;
	private String message;
	private String author;
	private String link;
	private Boolean published = false;
	private Topic parent;
	private Long id;
	
	
	/**
	 * @return the published
	 */
	public Boolean isPublished() {
		return published;
	}
	
	/**
	 * @return the published
	 */
	public Boolean getPublished() {
		return isPublished();
	}

	/**
	 * @param published the published to set
	 */
	public void setPublished(Boolean published) {
		this.published = published;
	}
	
	public boolean hasId() {
		return (this.id != null);
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the parent
	 */
	public Topic getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Topic parent) {
		this.parent = parent;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the published
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @return the startDisplay
	 */
	public Date getStartDisplay() {
		return startDisplay;
	}
	/**
	 * @return the endDisplay
	 */
	public Date getEndDisplay() {
		return endDisplay;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param published the published to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * @param startDisplay the startDisplay to set
	 */
	public void setStartDisplay(Date startDisplay) {
		this.startDisplay = startDisplay;
	}
	/**
	 * @param endDisplay the endDisplay to set
	 */
	public void setEndDisplay(Date endDisplay) {
		this.endDisplay = endDisplay;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the abstractText
	 */
	public String getAbstractText() {
		return abstractText;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param abstractText the abstractText to set
	 */
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Announcement t = (Announcement) obj;
		return (t.getId().compareTo(this.id) == 0);
	}

	// manipulate the sorting to that newest = first
	public int compareTo(Announcement otherAnn) {
		int val = startDisplay.compareTo(otherAnn.getStartDisplay());
		if (val == 0) {
			val = title.compareTo(otherAnn.getTitle());
		} else {
			val = (val * -1);
		}
		return val;
	}
	
}
