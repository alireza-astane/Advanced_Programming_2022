package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import logic.enums.CourseType;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;

public final class CourseListDataBuilder {

    private CourseListDataBuilder() {
    }

    public static CourseListData getCourseListData(String courseNumber, String unit, String departmentId) {
        CourseListData courseListData = new CourseListData();
        courseListData.setResponseType(ResponseType.COURSE_LIST_DATA);

        ArrayList<Course> courses = new ArrayList<>(DataManager.getCourses());
        //filter


        if(courseNumber!=null){
            courses.removeIf(course -> !Objects.equals(course.getCourseNumber(), courseNumber));
        }
        if(unit!=null){
            courses.removeIf(course -> !String.valueOf(course.getCourseUnits()).equals(unit));
        }
        if(departmentId!=null){
            courses.removeIf(course -> !String.valueOf(course.getCourseDepartmentId()).equals(departmentId));
        }
    


        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:courses){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(course.getCourseNumber());
        
            arrayList.add(course.getCourseName());
        
        
            arrayList.add(DataManager.getUserById(course.getCourseMasterId()).getUserFullName());
        
            arrayList.add(String.valueOf(course.getCourseUnits()));
        
            arrayList.add(DataManager.getDepartmentById(course.getCourseDepartmentId()).getDepartmentType().name);
        
            arrayList.add(course.getCourseWeaklyPlan().toString());
        
            try {
                arrayList.add(course.getCourseExamTime().toString());
            } catch (Exception e) {

            }

            arrayLists.add(arrayList);
        }
        courseListData.setCourseList(arrayLists);

        return courseListData;



    }
}
