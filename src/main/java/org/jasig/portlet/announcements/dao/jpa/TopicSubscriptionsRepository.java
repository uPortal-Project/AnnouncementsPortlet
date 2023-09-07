package org.jasig.portlet.announcements.dao.jpa;

import org.jasig.portlet.announcements.model.TopicSubscription;
import org.springframework.data.repository.CrudRepository;

public interface TopicSubscriptionsRepository extends CrudRepository<TopicSubscription, Long> {

    Iterable<TopicSubscription> findByTopicId(Long topicId);

    Iterable<TopicSubscription> findByOwner(String ownerId);

}
