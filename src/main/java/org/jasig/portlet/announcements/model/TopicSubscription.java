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
package org.jasig.portlet.announcements.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.jasig.portlet.announcements.xml.Namespaces;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 */
@XmlType(namespace = Namespaces.TOPIC_SUBSCRIPTION_NAMESPACE)
@XmlRootElement(name = "topicSubscription")
public class TopicSubscription {

  private Topic topic;
  private Boolean subscribed;
  private String owner;
  private Long id;

  public TopicSubscription() {}

  public TopicSubscription(String owner, Topic topic, Boolean subscribed) {
    this.owner = owner;
    this.topic = topic;
    this.subscribed = subscribed;
  }

  /** @return the id */
  @XmlTransient
  public Long getId() {
    return id;
  }

  /** @param id the id to set */
  public void setId(Long id) {
    this.id = id;
  }

  /** @return the owner */
  @XmlElement(name = "owner")
  public String getOwner() {
    return owner;
  }

  /** @param owner the owner to set */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /** @return the topic */
  @XmlTransient
  public Topic getTopic() {
    return topic;
  }
  /** @return the subscribed */
  @XmlElement(name = "subscribed")
  public Boolean getSubscribed() {
    return subscribed;
  }
  /** @param topic the topic to set */
  public void setTopic(Topic topic) {
    this.topic = topic;
  }
  /** @param subscribed the subscribed to set */
  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    TopicSubscription ts = (TopicSubscription) obj;
    return (this.id.compareTo(ts.getId()) == 0);
  }
}
