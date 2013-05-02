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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

public class Importer {
    private static final String SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private static final String ANNOUNCEMENT_SVC_BEAN_NAME = "announcementService";

    public static void main(String[] args) throws Exception
    {
        String dir = args[0];
        String importExportContext = args[1];
        ApplicationContext context = PortletApplicationContextLocator.getApplicationContext(importExportContext);
        SessionFactory sessionFactory = context.getBean(SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
        IAnnouncementService announcementService = context.getBean(ANNOUNCEMENT_SVC_BEAN_NAME,IAnnouncementService.class);

        JAXBContext jc = JAXBContext.newInstance(Topic.class);

        File folder = new File(dir);
        File[] files = folder.listFiles(new ImportFileFilter());

        for(File f : files) {
            StreamSource xml = new StreamSource(f.getAbsoluteFile());
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<Topic> je1 = unmarshaller.unmarshal(xml, Topic.class);
            Topic topic = je1.getValue();
            Session session = sessionFactory.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            announcementService.addOrSaveTopic(topic);
            transaction.commit();
            System.out.println("Successfully imported topic '" + topic.getTitle() + "'");
        }
    }

    private static class ImportFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml"));
        }

    }
}
