package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.StudentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String courseNumber;

    private int groupNumber = 1;

    @ManyToOne(optional = false)
    private Department department;

    @ManyToOne(optional = false)
    private Master master;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentType level;

    private int units;
    private int capacity;
    private boolean general;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private LocalDateTime examTime;

    @ElementCollection
    @CollectionTable(name = "course_class_time")
    @Column(name = "class_time")
    private List<String> classTimes = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_prerequisite", inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
    private List<Course> prerequisites = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_corequisite", inverseJoinColumns = @JoinColumn(name = "corequisite_id"))
    private List<Course> corequisites = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "assistingCourses")
    private List<Student> assistants = new ArrayList<>();

    @ManyToMany(mappedBy = "selectedCourses")
    private List<Student> selectingStudents = new ArrayList<>();
}
