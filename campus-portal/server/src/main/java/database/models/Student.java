package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.StudentState;
import logic.enums.StudentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "student")
public class Student {
    @Id
    private int id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(optional = false)
    private Department department;

    @ManyToOne
    private Master supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentType studentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentState state = StudentState.STUDYING;

    @Column(nullable = false, unique = true)
    private String studentNumber;

    private int entryYear;
    private boolean allowedToRegister = true;
    private LocalDateTime selectionStart;
    private LocalDateTime selectionEnd;

    @ManyToMany
    @JoinTable(name = "student_course")
    private List<Course> courses = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "teaching_assistant_course")
    private List<Course> assistingCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "selected_course")
    private List<Course> selectedCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "starred_course")
    private List<Course> starredCourses = new ArrayList<>();
}
