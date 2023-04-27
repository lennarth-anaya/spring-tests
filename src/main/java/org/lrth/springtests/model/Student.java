package org.lrth.springtests.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Student {
    @Id
    private Long id;
    
    @Column
    private String name;
    
    @ManyToMany(mappedBy = "students")
    private Set<Subject> subjects;
}
