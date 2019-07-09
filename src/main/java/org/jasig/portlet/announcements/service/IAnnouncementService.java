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

import java.util.List;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.model.TopicSubscription;

/**
 * <p>IAnnouncementService interface.</p>
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 * @version $Id: $Id
 */
public interface IAnnouncementService {

  /**
   * <p>getAllTopics.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Topic> getAllTopics();

  /**
   * <p>getEmergencyTopic.</p>
   *
   * @return a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public Topic getEmergencyTopic();

  /**
   * <p>addOrSaveTopic.</p>
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public void addOrSaveTopic(Topic topic);

  /**
   * <p>persistTopic.</p>
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public void persistTopic(Topic topic);

  /**
   * <p>mergeTopic.</p>
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public void mergeTopic(Topic topic);

  /**
   * <p>addOrSaveAnnouncement.</p>
   *
   * @param ann a {@link org.jasig.portlet.announcements.model.Announcement} object.
   */
  public void addOrSaveAnnouncement(Announcement ann);

  /**
   * <p>mergeAnnouncement.</p>
   *
   * @param ann a {@link org.jasig.portlet.announcements.model.Announcement} object.
   */
  public void mergeAnnouncement(Announcement ann);

  /**
   * <p>getTopic.</p>
   *
   * @param id a {@link java.lang.Long} object.
   * @return a {@link org.jasig.portlet.announcements.model.Topic} object.
   * @throws javax.portlet.PortletException if any.
   */
  public Topic getTopic(Long id) throws PortletException;

  /**
   * <p>getAnnouncement.</p>
   *
   * @param id a {@link java.lang.Long} object.
   * @return a {@link org.jasig.portlet.announcements.model.Announcement} object.
   * @throws javax.portlet.PortletException if any.
   */
  public Announcement getAnnouncement(Long id) throws PortletException;

  /**
   * <p>deleteAnnouncementsPastCurrentTime.</p>
   */
  public void deleteAnnouncementsPastCurrentTime();

  /**
   * <p>deleteAnnouncementsPastExpirationThreshold.</p>
   *
   * @param numDays a int.
   */
  public void deleteAnnouncementsPastExpirationThreshold(int numDays);

  /**
   * <p>getTopicSubscriptionFor.</p>
   *
   * @param request a {@link javax.portlet.PortletRequest} object.
   * @return a {@link java.util.List} object.
   * @throws javax.portlet.PortletException if any.
   */
  public List<TopicSubscription> getTopicSubscriptionFor(PortletRequest request)
      throws PortletException;

  /**
   * <p>addOrSaveTopicSubscription.</p>
   *
   * @param subs a {@link java.util.List} object.
   */
  public void addOrSaveTopicSubscription(List<TopicSubscription> subs);

  /**
   * <p>persistTopicSubscription.</p>
   *
   * @param subs a {@link java.util.List} object.
   */
  public void persistTopicSubscription(List<TopicSubscription> subs);

  /**
   * <p>deleteTopic.</p>
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public void deleteTopic(Topic topic);

  /**
   * <p>deleteAnnouncement.</p>
   *
   * @param ann a {@link org.jasig.portlet.announcements.model.Announcement} object.
   */
  public void deleteAnnouncement(Announcement ann);

  /**
   * <p>deleteTopicSubscription.</p>
   *
   * @param sub a {@link org.jasig.portlet.announcements.model.TopicSubscription} object.
   */
  public void deleteTopicSubscription(TopicSubscription sub);
}
