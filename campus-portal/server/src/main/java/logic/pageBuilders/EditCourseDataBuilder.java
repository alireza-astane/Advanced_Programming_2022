package logic.pageBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import logic.enums.CourseType;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;

public final class EditCourseDataBuilder {

    private EditCourseDataBuilder() {
    }

    public static EditCourseData getEditCourseData(int courseId) {

        EditCourseData editCourseData = new EditCourseData();
        editCourseData.setResponseType(ResponseType.EDIT_COURSE_DATA);
        Course course = DataManager.getCourseById(courseId);

        editCourseData.setCourseId(courseId);
        editCourseData.setCourseCapacity(course.getCourseCapacity());
        //editCourseData.setCourseCorequisiteId(course.getCourseCorequisites());
        editCourseData.setCourseDepartmentId(course.getCourseDepartmentId());
        editCourseData.setCourseMasterId(course.getCourseMasterId());
        editCourseData.setCourseName(course.getCourseName());
        editCourseData.setCourseNumber(course.getCourseNumber());
        editCourseData.setCourseType(course.getCourseType());
        //editCourseData.setCoursePrerequisiteId(course.getCoursePrerequisites());
        editCourseData.setCourseUnits(course.getCourseUnits());
        editCourseData.setCourseWeaklyPlan(new ArrayList<>(course.getCourseWeaklyPlan()));
        editCourseData.setCourseExamTime(course.getCourseExamTime().toString());
        return editCourseData;
    }
}
