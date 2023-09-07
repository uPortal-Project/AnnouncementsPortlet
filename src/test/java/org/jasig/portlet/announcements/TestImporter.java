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
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementsService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/** @author eolsson */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION})
@Transactional
public class TestImporter extends TestCase {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  IAnnouncementsService announcementsService;

  @Test
  public void testImporter() {
    File dataDirectory = new File(this.getClass().getResource("/data").getFile());

    Importer importer = new Importer(dataDirectory, announcementsService);
    importer.importData();

    // Clear the first level cache to remove the topics that are cached
    entityManager.clear();

    List<Topic> updatedTopics = announcementsService.getAllTopics();
    assertEquals(
        "topic list should have 1 item; instead had " + updatedTopics.size(),
        1,
        updatedTopics.size());

    // verify data after import.
    Topic addedTopic = updatedTopics.get(0);
    if (!"Campus Services".equals(addedTopic.getTitle())) {
      addedTopic = updatedTopics.get(0);
    }
    assert "Campus Services".equals(addedTopic.getTitle());

    Set<Announcement> announcements = addedTopic.getAnnouncements();
    assertEquals(
        "Campus Services topic has " + announcements.size() + " announcement instead of 2",
        2,
        announcements.size());
    boolean firstAnnFound = false;
    boolean secondAnnFound = false;
    for (Announcement announcement : announcements) {
      if ("New Portal Update".equals(announcement.getTitle())) {
        firstAnnFound = true;
      } else if ("New Cafeteria Option".equals(announcement.getTitle())) {
        secondAnnFound = true;
      }
    }
    assertTrue("Did not find first announcement", firstAnnFound);
    assertTrue("Did not find second announcement", secondAnnFound);
  }

}
