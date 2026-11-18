package logic.pageDataClasses;

import logic.enums.CourseType;
import logic.enums.ResponseType;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
public class CourseListData extends Response {
    ArrayList<ArrayList<String>> courseList;

    public static CourseListData getCourseListData(String courseNumber, String unit, String departmentId) {
        CourseListData courseListData = new CourseListData();
        courseListData.setResponseType(ResponseType.COURSE_LIST_DATA);

        ArrayList<Course> courses = DataManager.getCourses();
        //filter


        try {
            if(courseNumber!=null){
                courses.removeIf(course -> !Objects.equals(course.getCourseNumber(), courseNumber));
            }
            if(unit!=null){
                courses.removeIf(course -> !String.valueOf(course.getCourseUnits()).equals(unit));
            }
            if(departmentId!=null){
                courses.removeIf(course -> !String.valueOf(course.getCourseDepartmentId()).equals(departmentId));
            }
        } catch (Exception ignored) {
        }


        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:courses){
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                arrayList.add(course.getCourseNumber());
            } catch (Exception ignored) {
            }
            try {
                try {
                    arrayList.add(course.getCourseName());
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
            try {
                arrayList.add(DataManager.getUserById(course.getCourseMasterId()).getUserFullName());
            } catch (Exception ignored) {

            }
            try {
                arrayList.add(String.valueOf(course.getCourseUnits()));
            } catch (Exception ignored) {

            }
            try {
                arrayList.add(DataManager.getDepartmentById(course.getCourseDepartmentId()).getDepartmentType().name);
            } catch (Exception ignored) {

            }
            try {
                arrayList.add(course.getCourseWeaklyPlan().toString());
            } catch (Exception ignored) {
            }
            try {
                arrayList.add(course.getCourseExamTime().toString());
            } catch (Exception e) {

            }

            arrayLists.add(arrayList);
        }
        courseListData.setCourseList(arrayLists);

        return courseListData;



    }


    public ArrayList<ArrayList<String>> getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList<ArrayList<String>> courseList) {
        this.courseList = courseList;
    }
//arrayList :
    //num
    //            name
    //            masterName
    //            units
    //            departmentName
    //            plan?
    //            examTime


    public ArrayList<Course> sortByExamTime(ArrayList<Course> inputList){
        inputList.sort(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.getCourseExamTime().compareTo(o2.getCourseExamTime());
            }
        });
        return inputList;
    }
    public ArrayList<Course> sortByAlphabet(ArrayList<Course> inputList){
        inputList.sort(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.getCourseName().compareTo(o2.getCourseName());
            }
        });
        return inputList;

    }
    public ArrayList<Course> sortByGrade(ArrayList<Course> inputList){
        ArrayList<Course> outPut = new ArrayList<>();
        ArrayList<Course> arrayList1 = new ArrayList<>(inputList);
        arrayList1.removeIf(course -> course.getCourseType()!=CourseType.UNDER_GRADUATION);
        ArrayList<Course> arrayList2 = new ArrayList<>(inputList);
        arrayList2.removeIf(course -> course.getCourseType()!=CourseType.MASTERY);
        ArrayList<Course> arrayList3 = new ArrayList<>(inputList);
        arrayList3.removeIf(course -> course.getCourseType()!=CourseType.PHD);
        outPut.addAll(arrayList1);
        outPut.addAll(arrayList2);
        outPut.addAll(arrayList3);
        return outPut;


    }


}
