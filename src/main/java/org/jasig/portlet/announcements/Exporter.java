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
package org.jasig.portlet.announcements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementsService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

/**
 * <p>Exporter class.</p>
 */
public class Exporter {
  private static final String ANNOUNCEMENTS_SVC_BEAN_NAME = "announcementsService";

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   * @throws java.lang.Exception if any.
   */
  public static void main(String[] args) throws Exception {
    String dir = args[0];
    ApplicationContext context =
        PortletApplicationContextLocator.getApplicationContext(
            PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION);
    IAnnouncementsService announcementsService =
        context.getBean(ANNOUNCEMENTS_SVC_BEAN_NAME, IAnnouncementsService.class);

    JAXBContext jc = JAXBContext.newInstance(Topic.class);
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    List<Topic> topics = announcementsService.getAllTopics();
    for (Topic topic : topics) {
      if (topic.getSubscriptionMethod() == 4) {
        continue;
      }

      updateTopicAnnouncementsToAvoidInfiniteCycleIssueWithJaxb(topic);

      JAXBElement<Topic> je2 = new JAXBElement<>(new QName("topic"), Topic.class, topic);
      String output = dir + File.separator + UUID.randomUUID().toString() + ".xml";
      System.out.println("Exporting Topic " + topic.getId() + " to file " + output);
      try {
        marshaller.marshal(je2, new FileOutputStream(output));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static void updateTopicAnnouncementsToAvoidInfiniteCycleIssueWithJaxb(Topic topic) {
    topic.getAnnouncements().forEach(announcement -> {
      Topic topicWithNameOnly = new Topic();
      topicWithNameOnly.setTitle(announcement.getParent().getTitle());
      announcement.setParent(topicWithNameOnly);
    });
  }

}
