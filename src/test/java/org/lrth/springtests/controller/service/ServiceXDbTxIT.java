package org.lrth.springtests.controller.service;

import static java.util.Optional.of;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lrth.springtests.dao.StudentRepository;
import org.lrth.springtests.dao.SubjectRepository;
import org.lrth.springtests.dao.TopicRepository;
import org.lrth.springtests.enums.DifficultyEnum;
import org.lrth.springtests.model.Student;
import org.lrth.springtests.model.Subject;
import org.lrth.springtests.model.Topic;
import org.lrth.springtests.service.ServiceX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@ActiveProfiles({"inttest"})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ServiceX.class
//    ,
//    SubjectRepository.class,
//    StudentRepository.class,
//    TopicRepository.class
})
public class ServiceXDbTxIT {
    
    // service under test
    @Autowired
    private ServiceX sut;

    @Autowired
    private SubjectRepository subjectDao;

    @Autowired
    private StudentRepository studentDao;

    @Autowired
    private TopicRepository topicDao;
    
    // START - TestContainers
    private static final DockerImageName MARIADB_IMAGE = DockerImageName
            .parse("mariadb:10.5.5");
    
    @Container
    private static final MariaDBContainer mariadb = new MariaDBContainer<>(MARIADB_IMAGE)
            .withDatabaseName("schooldb")
            .withUsername("root").withPassword("");
    
    static {
        mariadb.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }
    
    @BeforeEach
    public void setup() {
        TestTransaction.flagForCommit();
        // finish previous test transaction
        TestTransaction.end();
        // a transaction is required for next test to insert data
        TestTransaction.start();
    }
    // END - TestContainers
    
    // test-data script is not really relevant in these tests
    @Test
    @Sql({"/integration/test-db-schema.sql",
        "/integration/test-data-01-happyPath.sql"})
    public void saveAll_success_happyPath() {
        // GIVEN
        final Long subjectId = 1001l;
        final Long studentId = 1009l;
        final Long topicId = 1019l;
        
        final Subject inSubject = buildTestSubject(subjectId);
        final Student inStudent = buildTestStudent(studentId);
        final Topic inTopic = buildTestTopic(topicId);
        
        // WHEN
        sut.saveAll(of(inSubject), of(inStudent), of(inTopic));

        // THEN
        assertTrue(subjectDao.findById(subjectId).isPresent());
        assertTrue(studentDao.findById(studentId).isPresent());
        assertTrue(topicDao.findById(topicId).isPresent());
    }

    /**
     * WARNING: use different IDs among tests while figuring out a
     *  way to safely reuse them regardless the execution order.
     */
    @Test
    @Sql({"/integration/test-db-schema.sql",
        "/integration/test-data-01-happyPath.sql"})
    public void saveAll_error_rolledback() {
        // GIVEN
        final Long subjectId1 = 2001l;
        final Long studentId1 = 2009l;
        final Long subjectId2 = 2002l;
        final Long studentId2 = 2010l;
        final Long invalidSharedTopicId = 2019l;
        
        final Subject inSubject1 = buildTestSubject(subjectId1);
        final Student inStudent1 = buildTestStudent(studentId1);

        final Subject inSubject2 = buildTestSubject(subjectId2);
        final Student inStudent2 = buildTestStudent(studentId2);
        
        final Topic inTopic = buildTestTopic(invalidSharedTopicId);
        
        // WHEN
        //  first attempt will succeed like happy path
        sut.saveAll(of(inSubject1), of(inStudent1), of(inTopic));
        //  second attempt should fail because of duplicate topic
        assertThrows(RuntimeException.class,
                () -> sut.saveAll(of(inSubject2), of(inStudent2), of(inTopic)));

        // THEN 
        // only the first saveAll should have been saved
        assertTrue(subjectDao.findById(subjectId1).isPresent());
        assertTrue(studentDao.findById(studentId1).isPresent());
        assertTrue(topicDao.findById(invalidSharedTopicId).isPresent());

        // second ones should have been rolled back
        assertFalse(subjectDao.findById(subjectId2).isPresent());
        assertFalse(studentDao.findById(studentId2).isPresent());        
    }

    private Subject buildTestSubject(Long id) {
        final Subject subject = new Subject();
        subject.setId(id);
        subject.setName("Conductivity");
        subject.setIntroduction("something long...");
        subject.setDifficulty(DifficultyEnum.MODERATE);

        return subject;
    }
    
    private Student buildTestStudent(Long id) {
        final Student student = new Student();
        student.setId(id);
        student.setName("Juan");
        
        return student;
    }
    
    private Topic buildTestTopic(Long id) {
        final Topic topic = new Topic();
        topic.setId(id);
        topic.setName("Conductivity Over Copper");

        return topic;
    }
}
