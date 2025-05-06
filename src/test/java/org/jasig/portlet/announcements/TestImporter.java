import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jasig.portlet.announcements.Importer;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementsService;
import org.junit.Before;
import org.junit.Test;

public class TestImporter {

  private IAnnouncementsService announcementsService;
  private Importer importer;

  @Before
  public void setUp() {
    // Mock the announcementsService
    announcementsService = mock(IAnnouncementsService.class);

    // Mock the void methods
    doNothing().when(announcementsService).addOrSaveTopic(any(Topic.class));
    doNothing().when(announcementsService).addOrSaveAnnouncement(any(Announcement.class));

    // Create the Importer instance with mocks
//    File mockDataDirectory = new File("mock/data/directory");
    File mockDataDirectory = new File(this.getClass().getResource("/data").getFile());
    importer = new Importer(mockDataDirectory, announcementsService);
  }

  @Test
  public void testImporter() {
    // Mock the behavior of announcementsService
    List<Topic> mockTopics = new ArrayList<>();
    Topic mockTopic = new Topic();
    mockTopic.setTitle("Campus Services");

    Set<Announcement> mockAnnouncements = new HashSet<>();
    Announcement announcement1 = new Announcement();
    announcement1.setTitle("New Portal Update");
    Announcement announcement2 = new Announcement();
    announcement2.setTitle("New Cafeteria Option");
    mockAnnouncements.add(announcement1);
    mockAnnouncements.add(announcement2);

    mockTopic.setAnnouncements(mockAnnouncements);
    mockTopics.add(mockTopic);

    when(announcementsService.getAllTopics()).thenReturn(mockTopics);

    // Call the method under test
    importer.importData();

    // Verify the results
    List<Topic> updatedTopics = announcementsService.getAllTopics();
    assertEquals(
        "topic list should have 1 item; instead had " + updatedTopics.size(),
        1,
        updatedTopics.size());

    Topic addedTopic = updatedTopics.get(0);
    assertEquals("Campus Services", addedTopic.getTitle());

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

    // Verify interactions with the mock
    verify(announcementsService, times(2)).getAllTopics();
  }
}