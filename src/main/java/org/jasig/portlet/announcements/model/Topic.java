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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.jasig.portlet.announcements.xml.Namespaces;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *
 * $LastChangedBy$
 * $LastChangedDate$
 */
@XmlType(namespace = Namespaces.TOPIC_NAMESPACE)
@XmlRootElement(name="topic")
public class Topic {
													/* Announcements for this topic are... */
	public static final int PUSHED_FORCED = 1;		/* ...Pushed to the audience members and they cannot unsubscribe */
	public static final int PUSHED_INITIAL = 2; 	/* ...Pushed initially, but users can unsubscribe */
	public static final int PULLED = 3;				/* ...Not pushed to anybody, but target audience members can subscribe (pull) if they want to */
	public static final int EMERGENCY = 4;			/* A topic that supercedes all other topics */

	private static final org.apache.log4j.Logger logger = Logger.getLogger(Topic.class);

	@JsonManagedReference
	private Set<Announcement> announcements;

    private Set<TopicSubscription> subscriptions;

	private Set<String> admins;
	private Set<String> moderators;
	private Set<String> authors;
	private Set<String> audience;

	private String creator;
	private String title;
	private String description;
	private boolean allowRss;
	private int subscriptionMethod;
	private Long id;

	public Topic() {
        subscriptions = new HashSet<TopicSubscription>();
		admins = new TreeSet<String>();
		moderators = new TreeSet<String>();
		authors = new TreeSet<String>();
		audience = new TreeSet<String>();
	}


	public Set<String> getGroup(String key) {
		if (UserRoles.ADMIN_ROLE_NAME.equals(key)) {
			return getAdmins();
		}
		else if (UserRoles.MODERATOR_ROLE_NAME.equals(key)) {
			return getModerators();
		}
        else if (UserRoles.AUTHOR_ROLE_NAME.equals(key)) {
            return getAuthors();
        }
        else if (UserRoles.AUDIENCE_ROLE_NAME.equals(key)) {
            return getAudience();
        }
		throw new RuntimeException("Role not found:  " + key);
	}

	public void setGroup(String key, Set<String> members) {
        if (UserRoles.ADMIN_ROLE_NAME.equals(key)) {
            setAdmins(members);
        }
        else if (UserRoles.MODERATOR_ROLE_NAME.equals(key)) {
            setModerators(members);
        }
        else if (UserRoles.AUTHOR_ROLE_NAME.equals(key)) {
            setAuthors(members);
        }
        else if (UserRoles.AUDIENCE_ROLE_NAME.equals(key)) {
            setAudience(members);
        } else {
            throw new RuntimeException("Role not found:  " + key);
        }
	}

	public boolean hasId() {
		return (this.id != null);
	}

	/**
	 * @return the moderators
	 */
    @XmlElementWrapper(name="moderators")
    @XmlElement(name="moderator")
	public Set<String> getModerators() {
		return moderators;
	}
	/**
	 * @return the creator
	 */
    @XmlElement(name="creator", defaultValue = "system")
	public String getCreator() {
		return creator;
	}
	/**
	 * @return the title
	 */
    @XmlElement(name="title", required=true)
	public String getTitle() {
		return title;
	}
	/**
	 * @return the description
	 */
    @XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	/**
	 * @return the allowRss
	 */
    @XmlElement(name="allowRss")
	public boolean isAllowRss() {
		return allowRss;
	}

	/**
	 * @param moderators the moderators to set
	 */
	public void setModerators(Set<String> moderators) {
		this.moderators = moderators;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param allowRss the allowRss to set
	 */
	public void setAllowRss(boolean allowRss) {
		this.allowRss = allowRss;
	}

    @XmlElementWrapper(name="subscriptions")
    @XmlElement(name="subscription")
    public Set<TopicSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<TopicSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

	/**
	 * Returns a list of all announcements in this topic, regardless of status.
	 * @return the announcements
	 */
    @XmlElementWrapper(name="announcements")
    @XmlElement(name="announcement")
	public Set<Announcement> getAnnouncements() {
		return announcements;
	}

	/**
	 * Returns a list of all published announcements in this topic. For topics to be included in this
	 * list, they must also be within their specified display period.
	 * @return the announcements
	 */
    @XmlTransient
	public Set<Announcement> getPublishedAnnouncements() {
		Set<Announcement> announcementsFiltered = new HashSet<Announcement>();  // Don't use a TreeSet here... causes lost announcements
		Date now = new Date();
		if (this.announcements != null) {
			for (Announcement ann : this.announcements) {
			    Date startDisplay = ann.getStartDisplay();
                Date endDisplay = ann.getEndDisplay();
                if (endDisplay == null) {
                    // Unspecified end date means the announcement does not expire;  we
                    // will substitute a date in the future each time this item is
                    // evaluated.
                    long aYearFromNow = System.currentTimeMillis() + Announcement.MILLISECONDS_IN_A_YEAR;
                    endDisplay = new Date(aYearFromNow);
                }
                if (ann.getPublished() && startDisplay.before(now) && endDisplay.after(now) ) {
                    announcementsFiltered.add(ann);
                }
			}
		}
		if(logger.isDebugEnabled()) {
		    logger.debug(String.format("Returning %d of %d announcements",
		            this.announcements.size(), announcementsFiltered.size()));
		}
		return announcementsFiltered;
	}

	/**
     * Returns a list of all historic announcements in this topic.
     * Non-historic announcements are simply all non-expired
     * announcements as well as announcements that expired less
     * than a day ago.
     * @return the announcements
     */
    @XmlTransient
	public Set<Announcement> getNonHistoricAnnouncements() {
	    Set<Announcement> announcementsFiltered = new HashSet<Announcement>();  // Don't use a TreeSet here... causes lost announcements
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1); // subtract 1 day from today.
        Date date = cal.getTime();
        if (this.announcements != null) {
            for (Announcement ann : this.announcements) {
                if((ann.getEndDisplay() == null) || (date.before(ann.getEndDisplay()))) {
                    announcementsFiltered.add(ann);
                }
            }
        }
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Returning %d of %d announcements",
                    this.announcements.size(), announcementsFiltered.size()));
        }
        return announcementsFiltered;
	}

	/**
	 * Returns a list of all historic announcements in this topic.
	 * @return the announcements
	 */
    @XmlTransient
	public Set<Announcement> getHistoricAnnouncements() {
		Set<Announcement> announcementsFiltered = new HashSet<Announcement>();  // Don't use a TreeSet here... causes lost announcements
		Calendar cal = Calendar.getInstance();
		Date dateStart = cal.getTime();
		if (this.announcements != null) {
			for (Announcement ann : this.announcements) {
				if (ann.getEndDisplay().before(dateStart)) {
					announcementsFiltered.add(ann);
				}
			}
		}
		if(logger.isDebugEnabled()) {
            logger.debug(String.format("Returning %d of %d announcements",
                    this.announcements.size(), announcementsFiltered.size()));
        }
		return announcementsFiltered;
	}

	/**
	 * Get the current number of displaying announcements
	 * @return
	 */
    @XmlTransient
	public int getDisplayingAnnouncementCount() {
		return getPublishedAnnouncements().size();
	}

	/**
	 * Get the current number of approved & scheduled announcements
	 * @return
	 */
    @XmlTransient
	public int getScheduledAnnouncementCount() {
		int count = 0;
		Date now = new Date();
		if (this.announcements != null) {
			for (Announcement ann: this.announcements) {
				if (ann.getPublished() &&
						ann.getStartDisplay().after(now)) {
					count++;
				}
			}
		}
		return count;
	}

    @XmlTransient
    public Set<Announcement> getPendingAnnouncements() {
        Set<Announcement> announcementsFiltered = new HashSet<Announcement>();  // Don't use a TreeSet here... causes lost announcements
        Date now = new Date();
        if (this.announcements != null) {
            for (Announcement ann : this.announcements) {
                if (!ann.getPublished() && ann.getNullSafeEndDisplay().after(now) ) {
                    announcementsFiltered.add(ann);
                }
            }
        }
        return announcementsFiltered;
    }

	/**
	 * Get the current number of pending announcements
	 * @return
	 */
    @XmlTransient
	public int getPendingAnnouncementCount() {
		int count = 0;
		if (this.announcements != null) {
			for (Announcement ann: this.announcements) {
				if (!ann.getPublished()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * @return the id
	 */
    @XmlTransient
	public Long getId() {
		return id;
	}

	/**
	 * @param announcements the announcements to set
	 */
	public void setAnnouncements(Set<Announcement> announcements) {
		this.announcements = announcements;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the authors
	 */
    @XmlElementWrapper(name="authors")
    @XmlElement(name="author")
	public Set<String> getAuthors() {
		return authors;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(Set<String> authors) {
		this.authors = authors;
	}

	/**
	 * @return the admins
	 */
    @XmlElementWrapper(name="admins")
    @XmlElement(name="admin")
	public Set<String> getAdmins() {
		return admins;
	}

	/**
	 * @return the audience
	 */
    @XmlElementWrapper(name="audience")
    @XmlElement(name="member")
	public Set<String> getAudience() {
		return audience;
	}

	/**
	 * @param admins the admins to set
	 */
	public void setAdmins(Set<String> admins) {
		this.admins = admins;
	}

	/**
	 * @param audience the audience to set
	 */
	public void setAudience(Set<String> audience) {
		this.audience = audience;
	}

	/**
	 * @return the subscriptionMethod
	 */
    @XmlElement(name="subscriptionMethod", required=true)
	public int getSubscriptionMethod() {
		return subscriptionMethod;
	}

	/**
	 * @param subscriptionMethod the subscriptionMethod to set
	 */
	public void setSubscriptionMethod(int subscriptionMethod) {
		this.subscriptionMethod = subscriptionMethod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
		Topic t = (Topic) obj;
		return (t.getId().compareTo(this.id) == 0);
	}

    @Override
    public int hashCode() {
        int code = (title != null ? title : "").hashCode();
        code += id != null && id > 0 ? id.intValue() : 0;
        return code;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString() {
		return "Topic [allowRss=" + allowRss + ", creator=" + creator
				+ ", description=" + description + ", id=" + id
				+ ", moderators=" + moderators + ", subscriptionMethod="
				+ subscriptionMethod + ", title=" + title + "]";
	}

}
