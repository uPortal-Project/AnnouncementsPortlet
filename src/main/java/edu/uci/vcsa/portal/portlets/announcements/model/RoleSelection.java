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
