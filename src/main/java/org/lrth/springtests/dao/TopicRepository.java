package org.lrth.springtests.dao;

import org.lrth.springtests.model.Topic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository
    extends PagingAndSortingRepository<Topic, Long> {

}
