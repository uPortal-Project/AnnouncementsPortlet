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
package edu.uci.vcsa.portal.portlets.announcements.service;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.model.TopicSubscription;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class HibernateAnnouncementService extends HibernateDaoSupport implements IAnnouncementService {

	private static Log log = LogFactory.getLog(HibernateAnnouncementService.class);
	
	/**
	 * Fetch all the Topics from the database and return them as a list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getAllTopics() {
		
		List<Topic> result;
		
		try {
			result = getHibernateTemplate().find("from Topic");
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Topic getEmergencyTopic() {
		Topic t = null;
		List<Topic> result;
		
		try {
			result = getHibernateTemplate().find("from Topic where SUB_METHOD = 4");
			t = result.get(0);
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		return t;
	}
	
	public void addOrSaveTopic(Topic topic) {
		try {
			log.debug("Insert or save topic: [topicId: "+(topic.getId()!=null ? topic.getId().toString() : "NEW")+"]");
			getHibernateTemplate().saveOrUpdate(topic);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	public void persistTopic(Topic topic) {
		try {
			log.debug("Persisting topic: [topicId: "+topic.getId().toString()+"]");
			getHibernateTemplate().persist(topic);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	public void mergeTopic(Topic topic) {
		try {
			log.debug("Merging topic: [topicId: "+topic.getId().toString()+"]");
			getHibernateTemplate().merge(topic);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	public void addOrSaveAnnouncement(Announcement ann) {
		try {
			log.debug("Insert or save announcement: [annId: "+(ann.getId()!=null ? ann.getId().toString() : "NEW")+"]");
			getHibernateTemplate().saveOrUpdate(ann);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	public void mergeAnnouncement(Announcement ann) {
		try {
			log.debug("Merge announcement: [annId: "+(ann.getId()!=null ? ann.getId().toString() : "NEW")+"]");
			getHibernateTemplate().merge(ann);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	
	/**
	 * Lookup the specified topic id and return it from the database
	 * @param id
	 * @return the requested Topic
	 * @throws PortletException if called with a null parameter or if the requested topic is invalid
	 */
	@SuppressWarnings("unchecked")
	public Topic getTopic(Long id) throws PortletException {
		List<Topic> result;
		
		if (id == null) {
			throw new PortletException("Programming error: getTopic called with null parameter");
		}
		
		try {
			result = getHibernateTemplate().find("from Topic where id = '"+id.toString()+"'");
			if (result.size() != 1) {
				throw new PortletException("The requested topic ["+id.toString()+"] does not exist.");
			}
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		return result.get(0);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public Announcement getAnnouncement(Long id) throws PortletException {
		List<Announcement> result = null;
		
		if (id == null) {
			throw new PortletException("Programming error: getAnnouncement called with null parameter");
		}
		
		try {
			result = getHibernateTemplate().find("from Announcement where id = '"+id.toString()+"'");
			if (result.size() != 1) {
				throw new PortletException("The requested announcement ["+id.toString()+"] does not exist.");
			}
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		return result.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAnnouncementsPastCurrentTime() {
		List<Announcement> result = null;
		
		try {
			result = getHibernateTemplate().find("from Announcement where END_DISPLAY < current_timestamp()");
			if (result == null || result.size() == 0) {
				return;
			}
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		if (result.size() > 0) {
			log.info("Deleting "+result.size()+" expired announcements.");
			
			try {
				for (Announcement a: result) {
					getHibernateTemplate().delete(a);
				}
				getHibernateTemplate().flush();
			} catch (HibernateException ex) {
				throw convertHibernateAccessException(ex);
			}
		}
		
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws PortletException
	 */
	@SuppressWarnings("unchecked")
	public List<TopicSubscription> getTopicSubscriptionFor(RenderRequest request) throws PortletException {
		List<TopicSubscription> result = null;
		
		try {
			result = getHibernateTemplate().find("from TopicSubscription where OWNER_ID = '"+request.getRemoteUser()+"'");
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		return result;
	}
	
	public void addOrSaveTopicSubscription(List<TopicSubscription> subs) {
		
		try {
			for (TopicSubscription ts: subs) {
				getHibernateTemplate().saveOrUpdate(ts);
			}
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
	}
	
	public void persistTopicSubscription(List<TopicSubscription> subs) {
		
		try {
			for (TopicSubscription ts: subs) {
				getHibernateTemplate().persist(ts);
			}
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void deleteTopic(Topic topic) {
		try {
			// any topic subscriptions with this id should be trashed first (since the topic is not aware of 
			// what topic subscriptions exist for it)
			Long topicId = topic.getId();
			List<TopicSubscription> result = getHibernateTemplate().find("from TopicSubscription where TOPIC_ID = "+topicId.toString());
			for (TopicSubscription ts: result) {
				getHibernateTemplate().delete(ts);
			}
			// then delete the topic itself (announcements get deleted by hibernate)
			getHibernateTemplate().delete(topic);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}

	public void deleteAnnouncement(Announcement ann) {
		try {
			getHibernateTemplate().delete(ann);
			getHibernateTemplate().flush();
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
}
