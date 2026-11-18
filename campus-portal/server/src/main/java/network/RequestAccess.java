package network;

import logic.enums.UserType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static logic.enums.UserType.*;
import static network.RequestType.*;

/** Which kinds of user may send each request. Ownership is checked by {@link RequestHandler}. */
final class RequestAccess {
    private static final Set<RequestType> PUBLIC = EnumSet.of(LOGIN, LOGIN_DATA);
    private static final Map<RequestType, Set<UserType>> ROLES = new EnumMap<>(RequestType.class);

    static {
        allow(EnumSet.allOf(UserType.class), PING, LOGOUT, CHANGE_PASS_DATA, CHANGE_PASSWORD, MESSENGER_MENU, CHAT_DATA,
                ADD_CHAT_DATA, REQUEST_SEND_MESSEGE, REQUEST_CREATE_CHAT, MESSAGE_TO_ADMIN, UNIREQUESTS_DATA,
                ANSWER_UNIREQUEST, GENERAL_PROFILE_DATA, CHANGE_EMAIL, CHANGE_PHONE_NUMBER);
        Set<UserType> university = EnumSet.of(STUDENT, MASTER, ASSISTANT);
        allow(university, COURSE_LIST_DATA, MASTER_LIST_DATA, WEEKLY_PLAN, EXAMS_TIME, CW_MENU_DATA,
                CW_COURSE_PAGE_DATA, CW_EDUCATIONAL_CONTEXT, CW_MASTER_EXERCISE_DATA, SCORING_ANSWER,
                ADD_EDUCATIONAL_CONTEXT, EDIT_EDUCATIONAL_CONTEXT);
        allow(EnumSet.of(STUDENT), STUDENT_MAIN_MENU, STUDENT_PROFILE_DATA, STUDENT_TEMPORARY_SCORE_DATA,
                STUDENT_GENERAL_CALENDER_DATA, STUDENT_EXERCISE_DATA, UNIT_SELECTION_DATA,
                EDUCATIONAL_SITUATION_TO_STUDENT_DATA, REQUEST_RECOMMENDATION, ENROLLMENT_REQUEST, MINOR_UNIREQUEST,
                WITHDRAW_REQUEST, DORMITPRY_REQUEST, DDD_UNIREQUEST, SCORE_PROTESTATION, UPLOAD_ANSWER,
                REQUEST_SELCECT_UNIT_FORM_ASSISTANT, STAR_OR_DESATR_COURSE, SELECT_COURSE, DESELECT_COURSE, CHANGE_GOUP);
        Set<UserType> staff = EnumSet.of(MASTER, ASSISTANT);
        allow(staff, MASTER_PROFILE_DATA, MASTER_GENERAL_CALENDER_DATA, MASTER_TEMPORARY_SCORE_DATA,
                PROTESTATION_ANSWER, SET_SCORE_VALUE, VERIFY_SCORES, EDIT_SCORE_VALUE, ADD_TA_TO_CW, ADD_STUDENT_TO_CW,
                DELETE_EDUCATIONAL_CONTEXT, ADD_EXERCISE, EDIT_MASTER_DATA, ADD_MASTER, EDIT_MASTER, DELETE_MASTER);
        allow(EnumSet.of(MASTER), MASTER_MAIN_MENU);
        allow(EnumSet.of(ASSISTANT), ASSISTANT_MAIN_MENU, ASSISTANT_TEMPORARY_SCORE_DATA,
                EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA, EDIT_COURSE_DATA, ADD_COURSE, EDIT_COURSE, DELETE_COURSE,
                CHOOOSE_UNIT_SELECTION_DATA, SET_UNIT_SELECTION_TIME);
        allow(EnumSet.of(ADMIN), ADMIN_MENU_DATA);
        allow(EnumSet.of(MOHSENI), MOHSENI_MENU_DATA, MOHSENI_STUDENT_INFO, MOHSENI_STUDENT_SEARCH_DATA);
    }

    private RequestAccess() {
    }

    private static void allow(Set<UserType> roles, RequestType... types) {
        for (RequestType type : types) {
            ROLES.put(type, roles);
        }
    }

    static boolean isPublic(RequestType type) {
        return PUBLIC.contains(type);
    }

    static boolean allows(RequestType type, UserType role) {
        return ROLES.getOrDefault(type, Set.of()).contains(role);
    }

    static boolean isKnown(RequestType type) {
        return PUBLIC.contains(type) || ROLES.containsKey(type);
    }
}
