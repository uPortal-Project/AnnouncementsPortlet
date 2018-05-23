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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

public class Importer {

  private static final String ANNOUNCEMENT_SVC_BEAN_NAME = "announcementService";

  private static final Log log = LogFactory.getLog(Importer.class);

  private File dataDirectory;
  private IAnnouncementService announcementService;
  private List<String> errors = new ArrayList<String>();

  public Importer(
      File dataDirectory, /*SessionFactory sessionFactory, */
      IAnnouncementService announcementService) {
    this.dataDirectory = dataDirectory;
    this.announcementService = announcementService;
  }

  /**
   * Imports topics and announcements into the database from XML data files.
   *
   * <ul>
   *   <li>args[0] -- <b>file system directory</b> containing XML data files to import
   * </ul>
   *
   * @throws Exception Various exceptions like JAXBException
   */
  public static void main(String[] args) {

    if (args.length != 1) {
      log.error(
          "Invalid number of arguments. Command:\n  $java org.jasig.portlet.announcements.Importer <dir>");
      System.exit(1);
    }

    // dataDirectory
    String dir = args[0];
    File dataDirectory = new File(dir);
    if (!dataDirectory.exists()) {
      log.error("The specified dataDirectory does not exist:  " + dir);
      System.exit(1);
    }
    log.info(
        "Importing XML data files in the following directory and its descendants:  " + dataDirectory.getAbsolutePath());

    // announcementService
    ApplicationContext context =
        PortletApplicationContextLocator.getApplicationContext(
            PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION);
    IAnnouncementService announcementService =
        context.getBean(ANNOUNCEMENT_SVC_BEAN_NAME, IAnnouncementService.class);

    Importer importer = new Importer(dataDirectory, announcementService);
    importer.importData();

    if (importer.errors.size() > 0) {
      log.error("Import failed - see previous errors");
      System.exit(1);
    }
  }

  public void importData() {
    importTopics();
    importAnnouncements();
  }

  private void importTopics() {
    try {
      JAXBContext jc = JAXBContext.newInstance(Topic.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();

      List<File> files = listFilesRecursively(dataDirectory, new TopicImportFileFilter());

      if (files == null) {
        errors.add("Directory " + dataDirectory + " is not a valid directory");
      } else {

        for (File f : files) {
          log.info("Processing file " + f.toString());
          StreamSource xmlFile = new StreamSource(f.getAbsoluteFile());
          try {
            JAXBElement<Topic> je1 = unmarshaller.unmarshal(xmlFile, Topic.class);
            Topic topic = je1.getValue();

            if (StringUtils.isBlank(topic.getTitle())) {
              String msg =
                  "Error parsing file "
                      + f.toString()
                      + "; did not get valid record:\n"
                      + topic.toString();
              log.error(msg);
              errors.add(msg);
            } else {
              announcementService.addOrSaveTopic(topic);
              log.info("Successfully imported topic '" + topic.getTitle() + "'");
            }
          } catch (JAXBException e) {
            String msg =
                "JAXB exception " + e.getCause().getMessage() + " processing file " + f.toString();
            log.error(msg, e);
            errors.add(msg + ". See stack trace");
          } catch (HibernateException e) {
            String msg =
                "Hibernate exception "
                    + e.getCause().getMessage()
                    + " processing file "
                    + f.toString();
            log.error(msg, e);
            errors.add(msg + ". See stack trace");
          }
        }
      }
    } catch (JAXBException e) {
      String msg = "Fatal JAXBException in importTopics - no topics imported";
      log.fatal(msg, e);
      errors.add(msg + ".  See stack trace");
    }
  }

  private void importAnnouncements() {
    try {
      JAXBContext jc = JAXBContext.newInstance(Announcement.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();

      List<File> files = listFilesRecursively(dataDirectory, new AnnouncementImportFileFilter());

      if (files == null) {
        errors.add("Directory " + dataDirectory + " is not a valid directory");
      } else {

        for (File f : files) {
          log.info("Processing file " + f.toString());
          StreamSource xml = new StreamSource(f.getAbsoluteFile());
          try {
            JAXBElement<Announcement> je1 = unmarshaller.unmarshal(xml, Announcement.class);
            Announcement announcement = je1.getValue();
            if (StringUtils.isBlank(announcement.getTitle())) {
              String msg =
                  "Error parsing "
                      + f.toString()
                      + "; did not get valid record:\n"
                      + announcement.toString();
              log.error(msg);
              errors.add(msg);
            } else if (announcement.getParent() == null
                || StringUtils.isBlank(announcement.getParent().getTitle())) {
              String msg =
                  "Announcement in file "
                      + f.toString()
                      + " does not reference a topic with a title";
              log.error(msg);
              errors.add(msg);
            } else {
              Topic topic = findTopicForAnnouncement(announcement);
              announcement.setParent(topic);
              announcementService.addOrSaveAnnouncement(announcement);
              log.info("Successfully imported announcement '" + announcement.getTitle() + "'");
            }
          } catch (ImportException e) {
            log.error(e.getMessage());
            errors.add(e.getMessage());
          } catch (JAXBException e) {
            String msg =
                "JAXB exception " + e.getCause().getMessage() + " processing file " + f.toString();
            log.error(msg, e);
            errors.add(msg + ". See stack trace");
          } catch (HibernateException e) {
            String msg =
                "Hibernate exception "
                    + e.getCause().getMessage()
                    + " processing file "
                    + f.toString();
            log.error(msg, e);
            errors.add(msg + ". See stack trace");
          }
        }
      }
    } catch (JAXBException e) {
      String msg = "Fatal JAXBException in importAnnouncements - no Announcements imported";
      log.fatal(msg, e);
      errors.add(msg + ".  See stack trace");
    }
  }

  private Topic findTopicForAnnouncement(Announcement announcement) {
    Topic topic = null;
    List<Topic> topics = announcementService.getAllTopics();
    for (Topic t : topics) {
      if (t.getTitle().equals(announcement.getParent().getTitle())) {
        if (topic != null) {
          throw new ImportException(
              "Unable to import Announcement '"
                  + announcement.getTitle()
                  + "' - multiple topics exist with title '"
                  + t.getTitle()
                  + "'");
        } else {
          topic = t;
        }
      }
    }
    return topic;
  }

  private List<File> listFilesRecursively(File parent, FileFilter filter) {
    final List<File> rslt = new ArrayList<>();
    // Sub directories first
    final File[] subdirs = parent.listFiles(new FileFilter() {
      @Override
      public boolean accept(File child) {
        return child.isDirectory();
      }
    });
    for (File dir : subdirs) {
      rslt.addAll(listFilesRecursively(dir, filter));
    }
    // Now this directory
    rslt.addAll(Arrays.asList(parent.listFiles(filter)));
    return Collections.unmodifiableList(rslt);
  }

  private static class TopicImportFileFilter implements FileFilter {
    public boolean accept(File pathname) {
      return (pathname.isFile() && pathname.getName().toLowerCase().endsWith("-topic.xml"));
    }
  }

  private static class AnnouncementImportFileFilter implements FileFilter {
    public boolean accept(File pathname) {
      return (pathname.isFile() && pathname.getName().toLowerCase().endsWith("-announcement.xml"));
    }
  }

  private class ImportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImportException(String message) {
      super(message);
    }
  }
}
