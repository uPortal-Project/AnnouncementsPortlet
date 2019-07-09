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
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 * @version $Id: $Id
 */
public interface UserRoles {
  /** Constant <code>PORTAL_ADMIN_ROLE_NAME="Portal_Administrators"</code> */
  String PORTAL_ADMIN_ROLE_NAME = "Portal_Administrators";
  /** Constant <code>ADMIN_ROLE_NAME="admin"</code> */
  String ADMIN_ROLE_NAME = "admin";
  /** Constant <code>MODERATOR_ROLE_NAME="moderator"</code> */
  String MODERATOR_ROLE_NAME = "moderator";
  /** Constant <code>AUTHOR_ROLE_NAME="author"</code> */
  String AUTHOR_ROLE_NAME = "author";
  /** Constant <code>AUDIENCE_ROLE_NAME="audience"</code> */
  String AUDIENCE_ROLE_NAME = "audience";
}
