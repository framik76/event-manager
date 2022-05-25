package com.yanchware.eventmanager.eventmanager.repository;

import com.yanchware.eventmanager.eventmanager.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findEventByUserIdAndType(long userId, String type);
}
