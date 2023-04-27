package org.lrth.springtests.dao;

import org.lrth.springtests.model.Subject;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository
    extends PagingAndSortingRepository<Subject, Long> {

}
