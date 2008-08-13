package edu.uci.vcsa.portal.portlets.announcements.model;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class TopicSubscription {

	private Topic topic;
	private Boolean subscribed;
	private String owner;
	private Long id;
	
	public TopicSubscription() { }
	
	public TopicSubscription(String owner, Topic topic, Boolean subscribed) {
		this.owner = owner;
		this.topic = topic;
		this.subscribed = subscribed;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the topic
	 */
	public Topic getTopic() {
		return topic;
	}
	/**
	 * @return the subscribed
	 */
	public Boolean getSubscribed() {
		return subscribed;
	}
	/**
	 * @param topic the topic to set
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	/**
	 * @param subscribed the subscribed to set
	 */
	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		TopicSubscription ts = (TopicSubscription) obj;
		return (this.id.compareTo( ts.getId() ) == 0);
	}
	
	
	
}
