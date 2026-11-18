package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.CourseType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue
    @Column(name = "courseId")
    int courseId;

    @Column(name = "courseName",nullable = false)
    String courseName;

    @Column(name = "courseNumber",nullable = false)
    String courseNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "courseId")
    private List<Score> scores = new ArrayList<>();

    @Column(name = "courseDepartmentId",nullable = false)
    int courseDepartmentId;

    @Column(name = "courseMasterId",nullable = false)
    int courseMasterId;

    @ElementCollection
    @CollectionTable(name = "CourseWeeklyPlan", joinColumns = @JoinColumn(name = "courseId"))
    @Column(name = "classTime")
    List<String> courseWeaklyPlan = new ArrayList<>();


    @Column(name = "courseExamTime",nullable = false)
    LocalDateTime courseExamTime;


    @Column(name = "courseCapacity",nullable = false)
    int courseCapacity;

    @Column(name = "courseType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    CourseType courseType;

    @Column(name = "courseUnits",nullable = false)
    int courseUnits;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "courseId")
    private List<EducationalContext> courseEducationalContexts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "courseId")
    private List<Exercise> courseExercises = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "Course_Prerequisite",
            joinColumns = @JoinColumn(name = "courseId"),
            inverseJoinColumns = @JoinColumn(name = "relatedCourseId"))
    private List<Course> coursePrerequisites = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "Course_Corequisite",
            joinColumns = @JoinColumn(name = "courseId"),
            inverseJoinColumns = @JoinColumn(name = "relatedCourseId"))
    private List<Course> courseCorequisites = new ArrayList<>();


    @ManyToMany
    @JoinTable(name = "Course_Group",
            joinColumns = @JoinColumn(name = "courseId"),
            inverseJoinColumns = @JoinColumn(name = "relatedCourseId"))
    private List<Course> courseGroup = new ArrayList<>();



    @ManyToMany(mappedBy = "studentCourses")
    private List<Student> courseStudents = new ArrayList<>();



    @ManyToMany(mappedBy = "studentAssistingCourses")
    private List<Student> courseTAs = new ArrayList<>();

    @ManyToMany(mappedBy = "studentPreRegisteredCourses")
    private List<Student> coursePreregisteredStudents = new ArrayList<>();


    @Column(name = "courseTerm",nullable = false)
    String term;


    public void setCourseCorequisites(List<Course> courseCorequisites) {
        this.courseCorequisites = courseCorequisites;
    }

    public List<Course> getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(List<Course> courseGroup) {
        this.courseGroup = courseGroup;
    }

    public List<Student> getCoursePreregisteredStudents() {
        return coursePreregisteredStudents;
    }

    public void setCoursePreregisteredStudents(List<Student> coursePreregisteredStudents) {
        this.coursePreregisteredStudents = coursePreregisteredStudents;
    }




    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public int getCourseDepartmentId() {
        return courseDepartmentId;
    }

    public void setCourseDepartmentId(int courseDepartmentId) {
        this.courseDepartmentId = courseDepartmentId;
    }

    public int getCourseMasterId() {
        return courseMasterId;
    }

    public void setCourseMasterId(int courseMasterId) {
        this.courseMasterId = courseMasterId;
    }

    public List<Student> getCourseTAs() {
        return courseTAs;
    }
    public List<String> getCourseWeaklyPlan() {
        return courseWeaklyPlan;
    }

    public void setCourseWeaklyPlan(List<String> courseWeaklyPlan) {
        this.courseWeaklyPlan = courseWeaklyPlan;
    }

    public LocalDateTime getCourseExamTime() {
        return courseExamTime;
    }

    public void setCourseExamTime(LocalDateTime courseExamTime) {
        this.courseExamTime = courseExamTime;
    }

    public int getCourseCapacity() {
        return courseCapacity;
    }

    public void setCourseCapacity(int courseCapacity) {
        this.courseCapacity = courseCapacity;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public int getCourseUnits() {
        return courseUnits;
    }

    public void setCourseUnits(int courseUnits) {
        this.courseUnits = courseUnits;
    }

    public List<EducationalContext> getCourseEducationalContexts() {
        return courseEducationalContexts;
    }

    public void setCourseEducationalContexts(List<EducationalContext> courseEducationalContexts) {
        this.courseEducationalContexts = courseEducationalContexts;
    }

    public List<Exercise> getCourseExercises() {
        return courseExercises;
    }

    public void setCourseTAs(List<Student> courseTAs) {
        this.courseTAs = courseTAs;
    }

    public void setCourseExercises(List<Exercise> courseExercises) {
        this.courseExercises = courseExercises;
    }

    public List<Course> getCoursePrerequisites() {
        return coursePrerequisites;
    }

    public void setCoursePrerequisites(List<Course> coursePrerequisiteId) {
        this.coursePrerequisites = coursePrerequisiteId;
    }

    public List<Course> getCourseCorequisites() {
        return courseCorequisites;
    }

    public void setCourseCorequisite(List<Course> courseCorequisites) {
        this.courseCorequisites = courseCorequisites;
    }

    public List<Student> getCourseStudents() {
        return courseStudents;
    }

    public void setCourseStudents(List<Student> courseStudents) {
        this.courseStudents = courseStudents;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
