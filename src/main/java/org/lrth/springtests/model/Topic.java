package org.lrth.springtests.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Topic {
    @Id
    private Long id;
    
    @Column
    private String name;
    
    @ManyToOne
    // nullable is true just for our testing purposes
    @JoinColumn(name="subject_id", nullable=true)
    private Subject subject;
}
