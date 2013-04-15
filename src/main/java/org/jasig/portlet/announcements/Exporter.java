package org.jasig.portlet.announcements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.jasig.portlet.announcements.model.Topic;
import org.jasig.portlet.announcements.service.IAnnouncementService;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

public class Exporter {
    private static final String SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private static final String ANNOUNCEMENT_SVC_BEAN_NAME = "announcementService";

    public static void main(String[] args) throws Exception
    {
        String dir = args[0];
        String importExportContext = args[1];
        ApplicationContext context = PortletApplicationContextLocator.getApplicationContext(importExportContext);
        SessionFactory sessionFactory = context.getBean(SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
        IAnnouncementService announcementService = context.getBean(ANNOUNCEMENT_SVC_BEAN_NAME,IAnnouncementService.class);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        JAXBContext jc = JAXBContext.newInstance(Topic.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        List<Topic> topics = announcementService.getAllTopics();
        for(Topic topic : topics)
        {
            if(topic.getSubscriptionMethod() == 4)
            {
                continue;
            }

            session.lock(topic, LockMode.NONE);
            JAXBElement<Topic> je2 = new JAXBElement<Topic>(new QName("topic"), Topic.class, topic);
            String output = dir + File.separator + UUID.randomUUID().toString() + ".xml";
            System.out.println("Exporting Topic " + topic.getId() + " to file " + output);
            try {
                marshaller.marshal(je2,new FileOutputStream(output));
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        transaction.commit();
    }
}
