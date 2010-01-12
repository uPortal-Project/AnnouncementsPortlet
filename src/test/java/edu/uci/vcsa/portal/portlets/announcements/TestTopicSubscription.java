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
package edu.uci.vcsa.portal.portlets.announcements;

import edu.uci.vcsa.portal.portlets.announcements.model.TopicSubscription;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import junit.framework.TestCase;

/**
 * @author eolsson
 *
 */
public class TestTopicSubscription extends TestCase {

	private TopicSubscription ts;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		
	}

	public void testTopicSubscription() {
		Topic t = new Topic();
		t.setId(new Long(5L));
		
		ts = new TopicSubscription("junit",t,true);
		assertEquals(Long.valueOf(5L), Long.valueOf(ts.getTopic().getId()));
	}
	
	
}
