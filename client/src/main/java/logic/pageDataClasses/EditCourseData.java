package logic.pageDataClasses;

import logic.enums.CourseType;
import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;


public class EditCourseData extends Response {
    int courseId;
    String courseName;
    String courseNumber;
    int courseDepartmentId;

    int courseMasterId;
    ArrayList<String> courseWeaklyPlan;
    String courseExamTime;
    int courseCapacity;
    CourseType courseType;
    int courseUnits;
    int coursePrerequisiteId;
    int courseCorequisiteId;


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

    public ArrayList<String> getCourseWeaklyPlan() {
        return courseWeaklyPlan;
    }

    public void setCourseWeaklyPlan(ArrayList<String> courseWeaklyPlan) {
        this.courseWeaklyPlan = courseWeaklyPlan;
    }

    public String getCourseExamTime() {
        return courseExamTime;
    }

    public void setCourseExamTime(String courseExamTime) {
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

    public int getCoursePrerequisiteId() {
        return coursePrerequisiteId;
    }

    public void setCoursePrerequisiteId(int coursePrerequisiteId) {
        this.coursePrerequisiteId = coursePrerequisiteId;
    }

    public int getCourseCorequisiteId() {
        return courseCorequisiteId;
    }

    public void setCourseCorequisiteId(int courseCorequisiteId) {
        this.courseCorequisiteId = courseCorequisiteId;
    }
}


