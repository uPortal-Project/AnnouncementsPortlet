/**
 *  Copyright 2008. The Regents of the University of California. All Rights
 *  Reserved. Permission to use, copy, modify, and distribute any part of this
 *  software including any source code and documentation for educational,
 *  research, and non-profit purposes, without fee, and without a written
 *  agreement is hereby granted, provided that the above copyright notice, this
 *  paragraph and the following three paragraphs appear in all copies of the
 *  software and documentation. Those desiring to incorporate this software into
 *  commercial products or use for commercial purposes should contact Office of
 *  Technology Alliances, University of California, Irvine, 380 University
 *  Tower, Irvine, CA 92607-7700, Phone: (949) 824-7295, FAX: (949) 824-2899. IN
 *  NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING,
 *  WITHOUT LIMITATION, LOST PROFITS, CLAIMS OR DEMANDS, OR BUSINESS
 *  INTERRUPTION, ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE
 *  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 *  ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO
 *  REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
 *  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
 *  SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
 */
package edu.uci.vcsa.portal.portlets.announcements.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.uci.vcsa.portal.portlets.announcements.model.Role;
import edu.uci.vcsa.portal.portlets.announcements.model.RoleSelection;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 * 
 * This class attempts to read the portlet.xml file to determine what groups should be available to evaluate against.
 * Each group is a <security-role-ref> element in the portlet.xml
 */
public class PortletXMLGroupService implements ServletContextAware, IGroupService {
	
	final private String PORTLET_XML_PATH = "/WEB-INF/portlet.xml";
	private Document doc;
	private List<String> roles;
	private static Log log = LogFactory.getLog(PortletXMLGroupService.class);
	private ServletContext context;
	
	public void init() {
		parseXml();
		
		if (doc != null) {
			String roleNameCandidate;
			roles = new ArrayList<String>();
			
			// Find all the <security-role-ref> elements in the file
			NodeList roleSections = doc.getElementsByTagName("security-role-ref");
			for (int i=0; i<roleSections.getLength(); i++) {
				// for each <security-role-ref>, get the child nodes
				if (roleSections.item(i).hasChildNodes()) {
					NodeList roleNames = roleSections.item(i).getChildNodes();
					for (int j=0; j<roleNames.getLength(); j++) {
						// go through the child nodes of each <security-role-ref> to find the <role-name> node
						if (roleNames.item(j).getNodeName().equalsIgnoreCase("role-name")) {
							// copy the <role-name> to the roles list if it's not there already
							roleNameCandidate = roleNames.item(j).getTextContent();
							if (!roles.contains(roleNameCandidate)) {
								roles.add(roleNameCandidate);
							}
						}
					}
					roleNames = null;
				}
			}
			
			Collections.sort(roles);
			log.info("Successfully instantiated and found roles ["+getRolesPretty()+"]");
		}
		else {
			log.error("Error parsing the file: "+PORTLET_XML_PATH+". See other messages for trace.");
		}
	}
	
	
	public List<Role> getAllRoles() {
		List<Role> list = new ArrayList<Role>();
		
		for (String role: roles) {
			list.add(new Role(role, false));
		}
		
		return list;
	}
	
	public List<Role> getAllRolesFromGroupSet(Set<String> selected) {
		List<Role> list = new ArrayList<Role>();
		
		for (String role: roles) {
			if (selected.contains(role))
				list.add(new Role(role, true));
			else
				list.add(new Role(role, false));
		}
		
		for (String person: selected) {
			if (person.startsWith("USER.")) {
				list.add(new Role(person, true));
			}
		}
		
		return list;
	}
	
	public Set<String> getSetForRoleSelection(RoleSelection roleSel) {
		Set<String> newSet = new TreeSet<String>();
		newSet.addAll(roleSel.getSelectedRoles());
		return newSet;		
	}
	
	private String getRolesPretty() {
		StringBuffer sb = new StringBuffer();
		for (String role: roles) {
			sb.append(role+", ");
		}
		return sb.toString();
	}
	
	private void parseXml() {
			
		URL portletXmlUrl = null;
		try {
			portletXmlUrl = context.getResource(PORTLET_XML_PATH);
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		} finally {
			
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				InputSource xmlInp = new InputSource(portletXmlUrl.openStream());
				DocumentBuilder dbl = dbf.newDocumentBuilder();
				doc = dbl.parse(xmlInp);
			} catch (ParserConfigurationException e) {
				log.error(e.getMessage());
			} catch (java.io.IOException e) {
				log.error(e.getMessage());
	        } catch (org.xml.sax.SAXException e) {
				log.error(e.getMessage());
	        } catch (Exception e) {
	        	log.error(e.getMessage());
	        } finally {
	        	log.debug("Finished parsing "+PORTLET_XML_PATH+".");
	        }
	        
		}

	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}
	
	
}
