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
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 */
public interface IAnnouncementService {

  public List<Topic> getAllTopics();

  public Topic getEmergencyTopic();

  public void addOrSaveTopic(Topic topic);

  public void persistTopic(Topic topic);

  public void mergeTopic(Topic topic);

  public void addOrSaveAnnouncement(Announcement ann);

  public void mergeAnnouncement(Announcement ann);

  public Topic getTopic(Long id) throws PortletException;

  public Announcement getAnnouncement(Long id) throws PortletException;

  public void deleteAnnouncementsPastCurrentTime();

  public void deleteAnnouncementsPastExpirationThreshold(int numDays);

  public List<TopicSubscription> getTopicSubscriptionFor(PortletRequest request)
      throws PortletException;

  public void addOrSaveTopicSubscription(List<TopicSubscription> subs);

  public void persistTopicSubscription(List<TopicSubscription> subs);

  public void deleteTopic(Topic topic);

  public void deleteAnnouncement(Announcement ann);

  public void deleteTopicSubscription(TopicSubscription sub);
}
