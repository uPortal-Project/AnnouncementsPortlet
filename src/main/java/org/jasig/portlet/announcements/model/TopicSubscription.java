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
 * <p>TopicSubscription class.</p>
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 * @version $Id: $Id
 */
@XmlType(namespace = Namespaces.TOPIC_SUBSCRIPTION_NAMESPACE)
@XmlRootElement(name = "topicSubscription")
public class TopicSubscription {

  private Topic topic;
  private Boolean subscribed;
  private String owner;
  private Long id;

  /**
   * <p>Constructor for TopicSubscription.</p>
   */
  public TopicSubscription() {}

  /**
   * <p>Constructor for TopicSubscription.</p>
   *
   * @param owner a {@link java.lang.String} object.
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   * @param subscribed a {@link java.lang.Boolean} object.
   */
  public TopicSubscription(String owner, Topic topic, Boolean subscribed) {
    this.owner = owner;
    this.topic = topic;
    this.subscribed = subscribed;
  }

  /** @return the id */
  /**
   * <p>Getter for the field <code>id</code>.</p>
   *
   * @return a {@link java.lang.Long} object.
   */
  @XmlTransient
  public Long getId() {
    return id;
  }

  /** @param id the id to set */
  /**
   * <p>Setter for the field <code>id</code>.</p>
   *
   * @param id a {@link java.lang.Long} object.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /** @return the owner */
  /**
   * <p>Getter for the field <code>owner</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  @XmlElement(name = "owner")
  public String getOwner() {
    return owner;
  }

  /** @param owner the owner to set */
  /**
   * <p>Setter for the field <code>owner</code>.</p>
   *
   * @param owner a {@link java.lang.String} object.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /** @return the topic */
  /**
   * <p>Getter for the field <code>topic</code>.</p>
   *
   * @return a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  @XmlTransient
  public Topic getTopic() {
    return topic;
  }
  /** @return the subscribed */
  /**
   * <p>Getter for the field <code>subscribed</code>.</p>
   *
   * @return a {@link java.lang.Boolean} object.
   */
  @XmlElement(name = "subscribed")
  public Boolean getSubscribed() {
    return subscribed;
  }
  /** @param topic the topic to set */
  /**
   * <p>Setter for the field <code>topic</code>.</p>
   *
   * @param topic a {@link org.jasig.portlet.announcements.model.Topic} object.
   */
  public void setTopic(Topic topic) {
    this.topic = topic;
  }
  /** @param subscribed the subscribed to set */
  /**
   * <p>Setter for the field <code>subscribed</code>.</p>
   *
   * @param subscribed a {@link java.lang.Boolean} object.
   */
  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    TopicSubscription ts = (TopicSubscription) obj;
    return (this.id.compareTo(ts.getId()) == 0);
  }
}
