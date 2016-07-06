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
package org.jasig.portlet.announcements.model;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.jasig.portlet.announcements.xml.Namespaces;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *
 */
@XmlType(namespace = Namespaces.ANNOUNCEMENT_NAMESPACE,
        propOrder = {"startDisplay", "endDisplay", "title", "abstractText", "message", "link", "published",
                "created", "author", "parent", "attachments"})
@XmlRootElement(name="announcement")
public class Announcement implements Comparable<Announcement> {

    /**
     * Useful for announcements with open-ended display periods.
     */
    public static final long MILLISECONDS_IN_A_YEAR = 1000L   // Milliseconds in a second
                                                * 60L   // Seconds in a minute
                                                * 60L   // Minutes in an hour
                                                * 24L   // Hours in a day
                                                * 365L; // Days in a year (basically)

    private String title;
	private String abstractText;
	private Date created;
	private Date startDisplay;
	private Date endDisplay;
	private String message;
	private String author;
	private String link;
	private Boolean published = false;
	@JsonBackReference
	private Topic parent;
	private Long id;
    private Set<String> attachments;

	/**
	 * @return the published
	 */
    @XmlElement(name="published")
	public Boolean getPublished() {
		return published;
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
    @XmlTransient
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
    @XmlElement(name="parent")
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
    @XmlElement(name="title", required=true)
	public String getTitle() {
		return title;
	}

	/**
	 * @return the created
	 */
    @XmlElement(name="created")
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the startDisplay
	 */
    @XmlElement(name="startDisplay", required=true)
	public Date getStartDisplay() {
		return startDisplay;
	}
	/**
	 * @return the endDisplay
	 */
    @XmlElement(name="endDisplay", required=true)
	public Date getEndDisplay() {
	    return endDisplay;
	}

    @XmlTransient
	public Date getNullSafeEndDisplay() {
	    // Unspecified end date means the announcement does not expire;  we
	    // will substitute a date in the future each time this item is
        // evaluated.
	    return endDisplay != null ? endDisplay : new Date(System.currentTimeMillis() + Announcement.MILLISECONDS_IN_A_YEAR);
	}

	/**
	 * @return the message
	 */
    @XmlElement(name="message",required=true)
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
	 * @param created date created
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
    @XmlElement(name="abstract", required=true)
	public String getAbstractText() {
		return abstractText;
	}
	/**
	 * @return the author
	 */
    @XmlElement(name="author", defaultValue = "system")
	public String getAuthor() {
		return author;
	}
	/**
	 * @return the link
	 */
    @XmlElement(name="link")
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

    @XmlElementWrapper(name="attachments")
    @XmlElement(name="attachment")
    public Set<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<String> attachments) {
        this.attachments = attachments;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
		Announcement t = (Announcement) obj;
		return (t.getId().compareTo(this.id) == 0);
	}

    @Override
    public int hashCode() {
        int code = (title != null ? title : "").hashCode();
        code += (id != null && id > 0 ? id.intValue() : 0);
        return code;
    }

    // manipulate the sorting so that start date, then title, then id,
	// are used to determine order of display appearance
	public int compareTo(Announcement otherAnn) {
	    return AnnouncementSortStrategy.START_DISPLAY_DATE_DESCENDING.getComparator().compare(this, otherAnn);
	}

}
