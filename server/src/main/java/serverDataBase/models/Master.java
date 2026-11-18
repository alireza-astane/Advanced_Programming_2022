package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.MasterGrade;
import logic.enums.MasterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "Master")
public class Master {
    @Id
    @GeneratedValue
    @Column(name = "id")
    int id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    @Column(name = "masterType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    MasterType masterType;

    @Column(name = "masterGrade")
    @Enumerated(EnumType.ORDINAL)
    MasterGrade masterGrade=MasterGrade.PROFESSOR;

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

    public MasterType getMasterType() {
        return masterType;
    }

    public void setMasterType(MasterType masterType) {
        this.masterType = masterType;
    }

    public MasterGrade getMasterGrade() {
        return masterGrade;
    }

    public void setMasterGrade(MasterGrade masterGrade) {
        this.masterGrade = masterGrade;
    }
    public String getMasterRoomNumber() {
        return masterRoomNumber;
    }

    public void setMasterRoomNumber(String masterRoomNumber) {
        this.masterRoomNumber = masterRoomNumber;
    }

    public List<Course> getMasterCourses() {
        return masterCourses;
    }

    public void setMasterCourses(List<Course> masterCourses) {
        this.masterCourses = masterCourses;
    }

    public String getMasterShipNumber() {
        return masterShipNumber;
    }

    public void setMasterShipNumber(String masterShipNumber) {
        this.masterShipNumber = masterShipNumber;
    }

    @Column(name = "masterRoomNumber")
    String masterRoomNumber="";


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "masterId")
    private List<Course> masterCourses= new ArrayList<>();

    public int getMasterDepartmentId() {
        return masterDepartmentId;
    }

    public void setMasterDepartmentId(int masterDepartmentId) {
        this.masterDepartmentId = masterDepartmentId;
    }

    @Column(name = "masterDepartmentId",nullable = false)
    int  masterDepartmentId;


    @Column(name = "masterShipNumber")
    String masterShipNumber="";



}
