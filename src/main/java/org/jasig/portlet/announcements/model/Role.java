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

/**
 * <p>Role class.</p>
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 * @version $Id: $Id
 */
public class Role {

  private String name;
  private Boolean selected;

  // No arg default constructor needed to get rid of error with jaxb2-maven-plugin:schemaGen
  /**
   * <p>Constructor for Role.</p>
   */
  public Role() {}

  /**
   * <p>Constructor for Role.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @param selected a boolean.
   */
  public Role(String name, boolean selected) {
    this.name = name;
    this.selected = new Boolean(selected);
  }

  /**
   * <p>getPerson.</p>
   *
   * @return a boolean.
   */
  public boolean getPerson() {
    if (name != null) {
      return name.startsWith("USER.");
    }
    return false;
  }

  /**
   * <p>getPersonName.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getPersonName() {
    if (name != null && name.startsWith("USER.")) {
      String[] p = name.split("\\.");
      return p[1];
    }
    return name;
  }

  /**
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Setter for the field <code>name</code>.</p>
   *
   * @param name a {@link java.lang.String} object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * <p>Getter for the field <code>selected</code>.</p>
   *
   * @return a {@link java.lang.Boolean} object.
   */
  public Boolean getSelected() {
    return selected;
  }

  /**
   * <p>Setter for the field <code>selected</code>.</p>
   *
   * @param selected a {@link java.lang.Boolean} object.
   */
  public void setSelected(Boolean selected) {
    this.selected = selected;
  }
}
