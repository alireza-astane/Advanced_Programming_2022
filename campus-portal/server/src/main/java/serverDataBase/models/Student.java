package serverDataBase.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.StudentState;
import logic.enums.StudentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Student")
public class Student {
    @Id
    @Column(name = "id")
    int id;

    @OneToOne
    @JoinColumn(name = "id",nullable = false)
    @MapsId
    private User user;

    @Column(name = "studentType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    StudentType studentType;

    @Column(name = "studentshipNumber")
    String studentshipNumber="";

    @Column(name = "entryYear",nullable = false)
    int entryYear;

    @Column(name = "averageScore")
    double averageScore=0;

    @Column(name = "studentState")
    @Enumerated(EnumType.ORDINAL)
    StudentState studentState=StudentState.ALLOWED_TO_REGISTER;

    @Column(name = "studentAllowedToRegister")
    boolean studentAllowedToRegister=true;

    @Column(name = "studentRegistrationTime")
    LocalDateTime studentRegistrationTime;

    @Column(name = "studentDepartmentId",nullable = false)
    int studentDepartmentId;



    @OneToMany
    @JoinColumn(name = "studentId")
    private java.util.List<Score> studentScores = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "Student_Course",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "courseId") }
    )
    List<Course> studentCourses = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "TA_Course",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "courseId") }
    )
    List<Course> studentAssistingCourses = new ArrayList<>();


    @Column(name = "studentSupervisorId")
    int studentSupervisorId;


    @ManyToMany
    @JoinTable(
            name = "Student_PreRegisteredCourse",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "courseId") }
    )
    List<Course> studentPreRegisteredCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Student_SelectedCourse",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "courseId") }
    )
    List<Course> studentSelectedCourse = new ArrayList<>();



    public List<Course> getStudentPreRegisteredCourses() {
        return studentPreRegisteredCourses;
    }

    public void setStudentPreRegisteredCourses(List<Course> studentPreRegisteredCourses) {
        this.studentPreRegisteredCourses = studentPreRegisteredCourses;
    }

    public List<Course> getStudentSelectedCourse() {
        return studentSelectedCourse;
    }

    public void setStudentSelectedCourse(List<Course> studentSelectedCourse) {
        this.studentSelectedCourse = studentSelectedCourse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StudentType getStudentType() {
        return studentType;
    }

    public void setStudentType(StudentType studentType) {
        this.studentType = studentType;
    }

    public String getStudentshipNumber() {
        return studentshipNumber;
    }

    public void setStudentshipNumber(String studentshipNumber) {
        this.studentshipNumber = studentshipNumber;
    }

    public int getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(int entryYear) {
        this.entryYear = entryYear;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public StudentState getStudentState() {
        return studentState;
    }

    public void setStudentState(StudentState studentState) {
        this.studentState = studentState;
    }

    public boolean isStudentAllowedToRegister() {
        return studentAllowedToRegister;
    }

    public void setStudentAllowedToRegister(boolean studentAllowedToRegister) {
        this.studentAllowedToRegister = studentAllowedToRegister;
    }

    public LocalDateTime getStudentRegistrationTime() {
        return studentRegistrationTime;
    }

    public void setStudentRegistrationTime(LocalDateTime studentRegistrationTime) {
        this.studentRegistrationTime = studentRegistrationTime;
    }

    public int getStudentDepartmentId() {
        return studentDepartmentId;
    }

    public void setStudentDepartmentId(int studentDepartmentId) {
        this.studentDepartmentId = studentDepartmentId;
    }

    public List<Score> getStudentScores() {
        return studentScores;
    }

    public void setStudentScores(List<Score> studentScores) {
        this.studentScores = studentScores;
    }

    public List<Course> getStudentCourses() {
        return studentCourses;
    }

    public void setStudentCourses(List<Course> studentCourses) {
        this.studentCourses = studentCourses;
    }

    public int getStudentSupervisorId() {
        return studentSupervisorId;
    }

    public void setStudentSupervisorId(int studentSupervisorId) {
        this.studentSupervisorId = studentSupervisorId;
    }

    public List<Course> getStudentAssistingCourses() {
        return studentAssistingCourses;
    }

    public void setStudentAssistingCourses(List<Course> studentAssistingCourses) {
        this.studentAssistingCourses = studentAssistingCourses;
    }
}
