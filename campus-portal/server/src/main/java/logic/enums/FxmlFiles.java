package logic.enums;

public enum FxmlFiles {
    ADD_NEW_MASTER("AddNewMaster.fxml"),
    ADD_NEW_STUDENT("AddNewStudent.fxml"),
    CHANGE_PASSWORD("ChangePassword.fxml"),
    DOCTORAL_DISSERTATION_DEFENSE("DoctoralDissertationDefense.fxml"),
    DORMITORY("Dormitory.fxml"),
    ENROLLMENT_CERTIFICATE("EnrollmentCertificate.fxml"),
    LOGIN("Login.fxml"),
    MINOR("Minor.fxml"),
    PHD_REQUESTS("PhdRequests.fxml"),
    RECOMMENDATION("Recommendation.fxml"),
    REGISTRATION("Registration.fxml"),
    UG_REQUESTS("UGRequests.fxml"),
    M_REQUESTS("MRequests.fxml"),
    WEAKLY_PLAN("WeaklyPlan.fxml"),
    WITHDRAWAL("Withdrawal.fxml"),
    STUDENT_MENU("StudentMenu.fxml"),
    MASTER_MENU("MasterMenu.fxml"),
    ASSISTANT_MENU("AssistantMenu.fxml"),
    EDUCATION("Education.fxml"),
    MASTER_PROFILE("MasterProfile.fxml"),
    STUDENT_PROFILE("StudentProfile.fxml"),
    RECORD("Record.fxml"),
    STUDENT_COURSES_LIST("SCoursesList.fxml"),
    STUDENT_MASTERS_LIST("SMastersList.fxml"),
    ADD_NEW_USER("AddNewUser.fxml"),
    EDIT_MASTER("BEditMaster.fxml"),
    ADD_MASTER("BAddMaster.fxml"),
    EDIT_COURSE("EditCourse.fxml"),
    ADD_COURSE("AddCourse.fxml"),
    ASSISTANT_COURSES_LIST("ACourseList.fxml"),
    BOSS_MASTERS_LIST("BMastersList.fxml");

    public final String path;
    FxmlFiles(String path){
        this.path=path;
    }

}
