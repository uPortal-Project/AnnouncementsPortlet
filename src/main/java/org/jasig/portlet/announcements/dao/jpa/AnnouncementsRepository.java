package org.jasig.portlet.announcements.dao.jpa;

import org.jasig.portlet.announcements.model.Announcement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Calendar;

public interface AnnouncementsRepository extends CrudRepository<Announcement, Long> {

    @Query(
            value = "delete from Announcement where END_DISPLAY < current_timestamp()",
            nativeQuery = true)
    long deleteWhereEndDisplayPastCurrentTime();

    @Query(
            value = "delete from Announcement where END_DISPLAY < :date",
            nativeQuery = true)
    long deleteWhereEndDisplayPastDate(Calendar date);

}
