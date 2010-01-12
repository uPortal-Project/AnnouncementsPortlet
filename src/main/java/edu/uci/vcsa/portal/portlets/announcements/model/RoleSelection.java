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
package edu.uci.vcsa.portal.portlets.announcements.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class RoleSelection implements Serializable {

	private static final long serialVersionUID = -4522351577889716059L;
	List<String> selectedRoles;

	public RoleSelection() {
		selectedRoles = new ArrayList<String>();
	}
	
	public RoleSelection(Set<String> selection) {
		selectedRoles = new ArrayList<String>();
		selectedRoles.addAll(selection);
	}
	
	public List<String> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(List<String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
	
}
