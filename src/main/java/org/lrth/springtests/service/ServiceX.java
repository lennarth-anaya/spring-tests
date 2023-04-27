package org.lrth.springtests.service;

import java.util.Optional;

import org.lrth.springtests.dao.StudentRepository;
import org.lrth.springtests.dao.SubjectRepository;
import org.lrth.springtests.dao.TopicRepository;
import org.lrth.springtests.model.Student;
import org.lrth.springtests.model.Subject;
import org.lrth.springtests.model.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ServiceX {

    private final SubjectRepository subjectDao;
    private final StudentRepository studentDao;
    private final TopicRepository topicDao;

    /**
     * This all-in one maintenance service method is
     * obviously wrong, but suits a quick transactional
     * integration test purpose of supposedly independent
     * operations.
     * 
     * @param subject
     * @param student
     * @param topic
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(Optional<Subject> subject,
            Optional<Student> student,
            Optional<Topic> topic) {
        log.info("saveAll() start");
        student.ifPresent(studentDao::save);
        subject.ifPresent(subjectDao::save);
        
        // just an excuse to throw exceptions if topic already exists
        topic.filter(t -> !topicDao.existsById(t.getId()))
                .orElseThrow(RuntimeException::new);
        
        topic.ifPresent(topicDao::save);

        log.info("saveAll() end");
    }

}
