/**
 *  Copyright 2008. The Regents of the University of California. All Rights
 *  Reserved. Permission to use, copy, modify, and distribute any part of this
 *  software including any source code and documentation for educational,
 *  research, and non-profit purposes, without fee, and without a written
 *  agreement is hereby granted, provided that the above copyright notice, this
 *  paragraph and the following three paragraphs appear in all copies of the
 *  software and documentation. Those desiring to incorporate this software into
 *  commercial products or use for commercial purposes should contact Office of
 *  Technology Alliances, University of California, Irvine, 380 University
 *  Tower, Irvine, CA 92607-7700, Phone: (949) 824-7295, FAX: (949) 824-2899. IN
 *  NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING,
 *  WITHOUT LIMITATION, LOST PROFITS, CLAIMS OR DEMANDS, OR BUSINESS
 *  INTERRUPTION, ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE
 *  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 *  ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO
 *  REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
 *  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
 *  SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
 */
package edu.uci.vcsa.portal.portlets.announcements.model;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class Topic {
													/* Announcements for this topic are... */
	public static final int PUSHED_FORCED = 1;		/* ...Pushed to the audience members and they cannot unsubscribe */
	public static final int PUSHED_INITIAL = 2; 	/* ...Pushed initially, but users can unsubscribe */
	public static final int PULLED = 3;				/* ...Not pushed to anybody, but target audience members can subscribe (pull) if they want to */
	public static final int EMERGENCY = 4;			/* A topic that supercedes all other topics */
	
	private Set<Announcement> announcements;
	
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
		admins = new TreeSet<String>();
		moderators = new TreeSet<String>();
		authors = new TreeSet<String>();
		audience = new TreeSet<String>();
	}

	
	public Set<String> getGroup(String key) {
		if (key.compareTo("admins") == 0) {
			return getAdmins();
		}
		else if (key.compareTo("moderators") == 0) {
			return getModerators();
		}
		else if (key.compareTo("authors") == 0) {
			return getAuthors();
		}
		else {
			return getAudience();
		}
	}
	
	public void setGroup(String key, Set<String> members) {
		if (key.compareTo("admins") == 0) {
			setAdmins(members);
		}
		else if (key.compareTo("moderators") == 0) {
			setModerators(members);
		}
		else if (key.compareTo("authors") == 0) {
			setAuthors(members);
		}
		else {
			setAudience(members);
		}
	}

	public boolean hasId() {
		return (this.id != null);
	}
	
	/**
	 * @return the moderators
	 */
	public Set<String> getModerators() {
		return moderators;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the allowRss
	 */
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
	
	/**
	 * Returns a list of all announcements in this topic, regardless of status.
	 * @return the announcements
	 */
	public Set<Announcement> getAnnouncements() {
		return announcements;
	}

	/**
	 * Returns a list of all published announcements in this topic. For topics to be included in this
	 * list, they must also be within their specified display period.
	 * @return the announcements
	 */
	public Set<Announcement> getPublishedAnnouncements() {
		Set<Announcement> announcementsFiltered = new TreeSet<Announcement>();
		Date now = new Date();
		if (this.announcements != null) {
			for (Announcement ann: this.announcements) {
				if (ann.isPublished() && 
						ann.getStartDisplay().before(now) &&
						ann.getEndDisplay().after(now) ) {
					announcementsFiltered.add(ann);
				}
			}
		}
		return announcementsFiltered;
	}
	
	/**
	 * Get the current number of displaying announcements
	 * @return
	 */
	public int getDisplayingAnnouncementCount() {
		return getPublishedAnnouncements().size();
	}
	
	/**
	 * Get the current number of approved & scheduled announcements
	 * @return
	 */
	public int getScheduledAnnouncementCount() {
		int count = 0;
		Date now = new Date();
		if (this.announcements != null) {
			for (Announcement ann: this.announcements) {
				if (ann.isPublished() &&
						ann.getStartDisplay().after(now)) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Get the current number of pending announcements
	 * @return
	 */
	public int getPendingAnnouncementCount() {
		int count = 0;
		if (this.announcements != null) {
			for (Announcement ann: this.announcements) {
				if (!ann.isPublished()) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * @return the id
	 */
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
	public Set<String> getAdmins() {
		return admins;
	}

	/**
	 * @return the audience
	 */
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
		Topic t = (Topic) obj;
		return (t.getId().compareTo(this.id) == 0);
	}
	
	
	
}
