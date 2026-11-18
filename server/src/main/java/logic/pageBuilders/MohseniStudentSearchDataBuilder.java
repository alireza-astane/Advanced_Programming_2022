package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Objects;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Student;

public final class MohseniStudentSearchDataBuilder {

    private MohseniStudentSearchDataBuilder() {
    }

    public static MohseniStudentSearchData getMohseniStudentSearchData(String departmentId, String entryYear, String studentType) {
        MohseniStudentSearchData mohseniStudentSearchData = new MohseniStudentSearchData();
        mohseniStudentSearchData.setResponseType(ResponseType.MOHSENI_STUDENT_SEARCH_DATA);
        ArrayList<Student> students = new ArrayList<>(DataManager.getStudents());
        if(departmentId!=null){
            students.removeIf(student -> !String.valueOf(student.getStudentDepartmentId()).equals(departmentId));
        }
        if(entryYear!=null){
            students.removeIf(student -> !String.valueOf(student.getEntryYear()).equals(entryYear));
        }
        if(studentType!=null){
            students.removeIf(student -> !Objects.equals(student.getStudentType().toString(), studentType));
        }
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        for(Student student:students){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(student.getUser().getId()));
            arrayList.add(student.getUser().getUserFullName());
            arrayList.add(String.valueOf(student.getStudentshipNumber()));
            arrayList.add(student.getStudentType().toString());

            arrayLists.add(arrayList);
        }
        mohseniStudentSearchData.setSearchResult(arrayLists);
        return mohseniStudentSearchData;


    }
}
