/**
 * 
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
