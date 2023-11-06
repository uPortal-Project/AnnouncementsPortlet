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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.announcements.dao.jpa.AnnouncementsRepository;
import org.jasig.portlet.announcements.dao.jpa.TopicSubscriptionsRepository;
import org.jasig.portlet.announcements.dao.jpa.TopicsRepository;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>AnnouncementsService class.</p>
 */
@Service
@Transactional
public class AnnouncementsService implements IAnnouncementsService {

  private static final Log log = LogFactory.getLog(AnnouncementsService.class);

  @Autowired
  private AnnouncementsRepository announcementsRepository;
  @Autowired
  private TopicsRepository topicsRepository;
  @Autowired
  private TopicSubscriptionsRepository topicSubscriptionsRepository;

    /** {@inheritDoc} */
    public List<Topic> getAllTopics() {
        return IterableUtils.toList(this.topicsRepository.findAll());
    }

    /** {@inheritDoc} */
    public Topic getEmergencyTopic() {
        Iterable<Topic> iterable = this.topicsRepository.findBySubscriptionMethod(4);
        return iterable.iterator().next();
    }

    /** {@inheritDoc} */
    public void addOrSaveTopic(Topic topic) {
        log.debug(
          "Insert or save topic: [topicId: "
              + (topic.getId() != null ? topic.getId().toString() : "NEW")
              + "]");
        this.topicsRepository.save(topic);
    }

    /** {@inheritDoc} */
    public void persistTopic(Topic topic) {
        log.debug("Persisting topic: [topicId: " + topic.getId().toString() + "]");
        this.topicsRepository.save(topic);
    }

    /** {@inheritDoc} */
    public void addOrSaveAnnouncement(Announcement ann) {
        log.debug(
              "Save announcement: [annId: "
                      + (ann.getId() != null ? ann.getId().toString() : "NEW")
                      + "]");
        if (ann.getCreated() == null) {
            ann.setCreated(new Date());
        }
        this.announcementsRepository.save(ann);
    }

    /** {@inheritDoc} */
    public Topic getTopic(Long id) throws PortletException {
        if (id == null) {
            throw new PortletException("Programming error: getTopic called with null parameter");
        }
        final Topic result = this.topicsRepository.findOne(id);
        if (result == null) {
            throw new PortletException("The requested topic [" + id.toString() + "] does not exist.");
        }
        return result;
        // TODO: use the following instead of the above when we upgrade to Spring 5.x and use a newer version of
        //  Spring Data JPA ('findOne' has been deprecated in favor of 'findById')
        //return this.topicsRepository.findById(id)
        //        .orElseThrow(() -> new PortletException("The requested topic [" + id.toString() + "] does not exist."));
    }

    /** {@inheritDoc} */
    public Announcement getAnnouncement(Long id) throws PortletException {
        if (id == null) {
            throw new PortletException("Programming error: getAnnouncement called with null parameter");
        }
        final Announcement result = this.announcementsRepository.findOne(id);
        if (result == null) {
            throw new PortletException("The requested announcement [" + id.toString() + "] does not exist.");
        }
        return result;
        // TODO: use the following instead of the above when we upgrade to Spring 5.x and use a newer version of
        //  Spring Data JPA ('findOne' has been deprecated in favor of 'findById')
        //return this.announcementsRepository.findById(id)
        //        .orElseThrow(() -> new PortletException("The requested announcement [" + id.toString() + "] does not exist."));
  }

    /** {@inheritDoc} */
    public void deleteAnnouncementsPastCurrentTime() {
        long count = this.announcementsRepository.deleteWhereEndDisplayPastCurrentTime();
        log.info("Deleted " + count + " expired announcements that stopped displaying prior to now.");
    }

    /** {@inheritDoc} */
    public void deleteAnnouncementsPastExpirationThreshold(int numDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (numDays * -1));
        long count = this.announcementsRepository.deleteWhereEndDisplayPastDate(cal);
        log.info(
          "Deleted "
              + count
              + " expired announcements that stopped displaying prior to "
              + cal.getTime());
    }

    /** {@inheritDoc} */
    public List<TopicSubscription> getTopicSubscriptionFor(PortletRequest request) throws PortletException {
        return IterableUtils.toList(this.topicSubscriptionsRepository.findByOwner(request.getRemoteUser()));
    }

    /** {@inheritDoc} */
    public void addOrSaveTopicSubscription(List<TopicSubscription> subs) {
        //this.topicSubscriptionsRepository.saveAll(subs);
        for (TopicSubscription sub : subs) {
            this.topicSubscriptionsRepository.save(sub);
        }
    }

    /** {@inheritDoc} */
    public void deleteTopic(Topic topic) {
        // any topic subscriptions with this id should be trashed first (since the topic is not aware of
        // what topic subscriptions exist for it)
        Long topicId = topic.getId();
        List<TopicSubscription> result = IterableUtils.toList(this.topicSubscriptionsRepository.findByTopicId(topicId));
        for (TopicSubscription ts : result) {
            this.topicSubscriptionsRepository.delete(ts);
        }
        // then delete the topic itself (announcements get deleted by hibernate)
        this.topicsRepository.delete(topic);
    }

    /** {@inheritDoc} */
    public void deleteAnnouncement(Announcement ann) {
        this.announcementsRepository.delete(ann);
    }

    /** {@inheritDoc} */
    public void deleteTopicSubscription(TopicSubscription sub) {
        this.topicSubscriptionsRepository.delete(sub);
    }

}
