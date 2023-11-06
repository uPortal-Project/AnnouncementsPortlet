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
import java.util.EnumSet;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.boot.MetadataSources;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jasig.portlet.announcements.spring.PortletApplicationContextLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * This tool is responsible for creating the Announcements portlet database schema (and dropping
 * it first, if necessary).  It leverages the org.hibernate:hibernate-tools library, but integrates
 * with Announcements' Spring-managed ORM strategy and Announcements' configuration features (esp.
 * encrypted properties).  It is invokable from the command line with '$ java', but designed to be
 * integrated with build tools like Gradle.
 */
public class SchemaCreator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        // There will be an instance of this class in the ApplicationContent
        ApplicationContext context =
                PortletApplicationContextLocator.getApplicationContext(
                        PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION);
        final SchemaCreator schemaCreator = context.getBean("schemaCreator", SchemaCreator.class);
        System.exit(schemaCreator.create());
    }

    private int create() {
        try {
            SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
            ServiceRegistry serviceRegistry = sessionFactory.getServiceRegistry();

            MetadataSources metadata = new MetadataSources(serviceRegistry.getParentServiceRegistry());
            metadata.addDirectory(new File("src/main/resources/hibernate-mappings"));

            EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.execute(enumSet, SchemaExport.Action.BOTH, metadata.buildMetadata());

            final List<Exception> exceptions = schemaExport.getExceptions();
            if (!exceptions.isEmpty()) {
                logger.error("Schema Create Failed;  see below for details");
                for (Exception e : exceptions) {
                    logger.error("Exception from Hibernate Tools SchemaExport", e);
                }
                return 1;
            }
        } catch (Exception e) {
            logger.error("Failed to initialize & invoke the SchemaExport tool", e);
            return 1;
        }

        return 0;
    }

}
