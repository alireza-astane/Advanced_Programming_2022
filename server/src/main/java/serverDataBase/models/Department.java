package serverDataBase.models;

import jakarta.persistence.*;
import logic.enums.DepartmentType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue
    @Column(name = "departmentId")
    int departmentId;

    @Column(name = "departmentType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    DepartmentType departmentType;

    @Column(name = "departmentBossId",nullable = false)
    int departmentBossId;

    public DepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(DepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    @Column(name = "departmentAssistantId")
    int departmentAssistantId;

    @OneToMany
    @JoinColumn(name = "departmentId")
    private List<Course> departmentCourses = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "departmentId")
    private List<Master> departmentMasters = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "departmentId")
    private List<Student> departmentStudents = new ArrayList<>();

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getDepartmentBossId() {
        return departmentBossId;
    }

    public void setDepartmentBossId(int departmentBossId) {
        this.departmentBossId = departmentBossId;
    }

    public int getDepartmentAssistantId() {
        return departmentAssistantId;
    }

    public void setDepartmentAssistantId(int departmentAssistantId) {
        this.departmentAssistantId = departmentAssistantId;
    }

    public List<Course> getDepartmentCourses() {
        return departmentCourses;
    }

    public void setDepartmentCourses(List<Course> departmentCourses) {
        this.departmentCourses = departmentCourses;
    }

    public List<Master> getDepartmentMasters() {
        return departmentMasters;
    }

    public void setDepartmentMasters(List<Master> departmentMasters) {
        this.departmentMasters = departmentMasters;
    }

    public List<Student> getDepartmentStudents() {
        return departmentStudents;
    }

    public void setDepartmentStudents(List<Student> departmentStudents) {
        this.departmentStudents = departmentStudents;
    }
}
