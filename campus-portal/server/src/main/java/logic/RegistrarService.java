package logic;

import database.DataManager;
import database.models.Student;
import network.RequestType;
import network.Response;
import network.Routes;

public final class RegistrarService {
    private RegistrarService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.ALL_STUDENTS, Routes.REGISTRAR, call -> allStudents());
        routes.on(RequestType.STUDENT_DETAILS, Routes.REGISTRAR, call ->
                AccountService.profile(DataManager.get(Student.class, call.id(0)).getUser()));
    }

    private static Response allStudents() {
        Response response = new Response();
        for (Student student : DataManager.query(Student.class, "from Student order by entryYear, studentNumber")) {
            response.row("students").put("id", student.getId()).put("name", student.getUser().getFullName())
                    .put("number", student.getStudentNumber()).put("level", student.getStudentType())
                    .put("entryYear", student.getEntryYear());
        }
        return response;
    }
}
