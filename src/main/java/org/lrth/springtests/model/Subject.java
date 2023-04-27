package org.lrth.springtests.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.lrth.springtests.enums.DifficultyEnum;

import lombok.Data;

@Entity
@Data
public class Subject {

    @Id
    private Long id;

    @Column
    private String name;

    @Lob
    private String introduction;
    
    @ManyToMany
    @JoinTable(name = "subject_student",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students = new HashSet<>();

    // avoid cascading just for tx example
    @OneToMany
    (cascade = CascadeType.ALL,
            mappedBy = "subject")
    private Set<Topic> topics = new HashSet<>();
    
    @Enumerated(value = EnumType.STRING)
    private DifficultyEnum difficulty;
}
