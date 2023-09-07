package org.jasig.portlet.announcements.dao.jpa;

import org.jasig.portlet.announcements.model.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicsRepository extends CrudRepository<Topic, Long> {

    Iterable<Topic> findBySubscriptionMethod(Integer subMethod);

}
