package org.lrth.springtests.dao;

import org.lrth.springtests.model.Student;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository
    extends PagingAndSortingRepository<Student, Long> {

}
