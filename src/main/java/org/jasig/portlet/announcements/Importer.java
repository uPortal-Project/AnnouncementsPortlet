/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.announcements;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.jasig.portlet.announcements.model.Announcement;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

public class Importer {
    private static final String SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private static final String ANNOUNCEMENT_SVC_BEAN_NAME = "announcementService";

    private static final Logger log = Logger.getLogger(Importer.class);

    private String dir;
    private SessionFactory sessionFactory;
    private IAnnouncementService announcementService;
    private List<String> errors = new ArrayList<String>();

    public Importer(String dir, SessionFactory sessionFactory, IAnnouncementService announcementService) {
        this.dir = dir;
        this.sessionFactory = sessionFactory;
        this.announcementService = announcementService;
    }

    public void setDir(String dir) {
    	this.dir = dir;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
    	this.sessionFactory = sessionFactory;
    }
    
    public void setAnnouncementService(IAnnouncementService announcementService) {
    	this.announcementService = announcementService;
    }
    
    /**
     * Imports topics and announcements into the database.
     * arg0 - directory containing the files
     * arg1 - spring context xml file
     *
     * @param args args to program
     * @throws Exception Various exceptions like JAXBException
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2) {
            log.error("Invalid number of arguments. Command:\njava org.jasig.portlet.announcements.Importer <dir> <pathToSpringContextXmlFile");
            System.exit(1);
        }
        String dir = args[0];
        String importExportContext = args[1];
        ApplicationContext context = PortletApplicationContextLocator.getApplicationContext(importExportContext);
        SessionFactory sessionFactory = context.getBean(SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
        IAnnouncementService announcementService = context.getBean(ANNOUNCEMENT_SVC_BEAN_NAME,IAnnouncementService.class);

        Importer importer = new Importer(dir, sessionFactory, announcementService);
        importer.importData();

        if (importer.errors.size() > 0 ) {
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

            File folder = new File(dir);
            File[] files = folder.listFiles(new TopicImportFileFilter());

            if (files == null) {
                errors.add("Directory " + dir + " is not a valid directory");
            } else {

                for(File f : files) {
                    log.info("Processing file " + f.toString());
                    StreamSource xmlFile = new StreamSource(f.getAbsoluteFile());
                    try {
                        JAXBElement<Topic> je1 = unmarshaller.unmarshal(xmlFile, Topic.class);
                        Topic topic = je1.getValue();

                        if (StringUtils.isBlank(topic.getTitle())) {
                            String msg = "Error parsing file " + f.toString() + "; did not get valid record:\n" + topic.toString();
                            log.error(msg);
                            errors.add(msg);
                        } else {
                            announcementService.addOrSaveTopic(topic);
                            log.info("Successfully imported topic '" + topic.getTitle() + "'");
                        }
                    } catch (JAXBException e) {
                        String msg = "JAXB exception " + e.getCause().getMessage() + " processing file " + f.toString();
                        log.error(msg, e);
                        errors.add(msg + ". See stack trace");
                    } catch (HibernateException e) {
                        String msg = "Hibernate exception " + e.getCause().getMessage() + " processing file " + f.toString();
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

            File folder = new File(dir);
            File[] files = folder.listFiles(new AnnouncementImportFileFilter());

            if (files == null) {
                errors.add("Directory " + dir + " is not a valid directory");
            } else {

                for(File f : files) {
                    log.info("Processing file " + f.toString());
                    StreamSource xml = new StreamSource(f.getAbsoluteFile());
                    try {
                        JAXBElement<Announcement> je1 = unmarshaller.unmarshal(xml, Announcement.class);
                        Announcement announcement = je1.getValue();
                        if (StringUtils.isBlank(announcement.getTitle())) {
                            String msg = "Error parsing " + f.toString() + "; did not get valid record:\n" + announcement.toString();
                            log.error(msg);
                            errors.add(msg);
                        } else if (announcement.getParent() == null || StringUtils.isBlank(announcement.getParent().getTitle())) {
                            String msg = "Announcement in file " + f.toString() + " does not reference a topic with a title";
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
                        String msg = "JAXB exception " + e.getCause().getMessage() + " processing file " + f.toString();
                        log.error(msg, e);
                        errors.add(msg + ". See stack trace");
                    } catch (HibernateException e) {
                        String msg = "Hibernate exception " + e.getCause().getMessage() + " processing file " + f.toString();
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
                    throw new ImportException("Unable to import Announcement '" + announcement.getTitle()
                            + "' - multiple topics exist with title '" + t.getTitle() + "'");
                } else {
                    topic = t;
                }
            }
        }
        return topic;
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
        public ImportException() {
            super();
        }
        public ImportException(String message) {
            super(message);
        }
    }
}
