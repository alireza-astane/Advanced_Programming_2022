package logic.enums;

import logic.Configuration;

public enum FxmlFiles {
    START(Configuration.addresses.get("START").toString()),
    ADD_NEW_MASTER(Configuration.addresses.get("ADD_NEW_MASTER").toString()),
    ADD_NEW_STUDENT(Configuration.addresses.get("ADD_NEW_STUDENT").toString()),
    CHANGE_PASSWORD(Configuration.addresses.get("CHANGE_PASSWORD").toString()),
    DOCTORAL_DISSERTATION_DEFENSE(Configuration.addresses.get("DOCTORAL_DISSERTATION_DEFENSE").toString()),
    DORMITORY(Configuration.addresses.get("DORMITORY").toString()),
    ENROLLMENT_CERTIFICATE(Configuration.addresses.get("ENROLLMENT_CERTIFICATE").toString()),
    LOGIN(Configuration.addresses.get("LOGIN").toString()),
    MINOR(Configuration.addresses.get("MINOR").toString()),
    PHD_REQUESTS(Configuration.addresses.get("PHD_REQUESTS").toString()),
    RECOMMENDATION(Configuration.addresses.get("RECOMMENDATION").toString()),
    REGISTRATION(Configuration.addresses.get("REGISTRATION").toString()),
    UG_REQUESTS(Configuration.addresses.get("UG_REQUESTS").toString()),
    M_REQUESTS(Configuration.addresses.get("M_REQUESTS").toString()),
    WEAKLY_PLAN(Configuration.addresses.get("WEAKLY_PLAN").toString()),
    WITHDRAWAL(Configuration.addresses.get("WITHDRAWAL").toString()),
    STUDENT_MENU(Configuration.addresses.get("STUDENT_MENU").toString()),
    MASTER_MENU(Configuration.addresses.get("MASTER_MENU").toString()),
    ASSISTANT_MENU(Configuration.addresses.get("ASSISTANT_MENU").toString()),
    EDUCATION(Configuration.addresses.get("EDUCATION").toString()),
    MASTER_PROFILE(Configuration.addresses.get("MASTER_PROFILE").toString()),
    STUDENT_PROFILE(Configuration.addresses.get("STUDENT_PROFILE").toString()),
    RECORD(Configuration.addresses.get("RECORD").toString()),
    STUDENT_COURSES_LIST(Configuration.addresses.get("STUDENT_COURSES_LIST").toString()),
    STUDENT_MASTERS_LIST(Configuration.addresses.get("STUDENT_MASTERS_LIST").toString()),
    ADD_NEW_USER(Configuration.addresses.get("ADD_NEW_USER").toString()),
    EDIT_MASTER(Configuration.addresses.get("EDIT_MASTER").toString()),
    ADD_MASTER(Configuration.addresses.get("ADD_MASTER").toString()),
    EDIT_COURSE(Configuration.addresses.get("EDIT_COURSE").toString()),
    ADD_COURSE(Configuration.addresses.get("ADD_COURSE").toString()),
    ASSISTANT_COURSES_LIST(Configuration.addresses.get("ASSISTANT_COURSES_LIST").toString()),
    BOSS_MASTERS_LIST(Configuration.addresses.get("BOSS_MASTERS_LIST").toString()),
    GENERAL_MENU(Configuration.addresses.get("GENERAL_MENU").toString()),
    MOHSENI_MENU(""),
    ADMIN_MENU(""),
    GENERAL_PROFILE(""), ASSISTANT_TEMPORARY_SCORES(""), MESSENGER_MENU(""), CW_MENU(""), UNI_REQUESTS(""), EXAMS_LIST(""), ADD_NEW_CHAT(""), MOHSENI_MESSENGER(""), MOHSENI_SEARCH_ENGINE(""), STUDENT_EDUCATIONAL_SITUATION(""), ASSISTANT_EDUCATIONAL_SITUATION(""), STUDENT_TEMPORARY_SCORES(""), MASTER_TEMPORARY_SCORES(""), SUGGESTED_COURSES_LIST("");

    public final String path;
    FxmlFiles(String path){
        this.path=path;
    }

}
