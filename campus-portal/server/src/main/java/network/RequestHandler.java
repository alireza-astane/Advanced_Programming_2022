package network;

import com.google.gson.JsonParseException;
import logic.enums.MasterType;
import logic.enums.UserType;
import logic.pageDataClasses.ChangePassData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;
import serverDataBase.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends Thread {
    static Logger log = LogManager.getLogger(RequestHandler.class);

    private final ClientHandler clientHandler;
    private final BufferedReader reader;

    public RequestHandler(ClientHandler clientHandler) throws IOException {
        super("client-" + clientHandler.getId());
        this.clientHandler = clientHandler;
        reader = new BufferedReader(new InputStreamReader(clientHandler.socket.getInputStream(), StandardCharsets.UTF_8));
        setDaemon(true);
    }

    private boolean checkAuthToken(Request request) {
        return request.getAuthToken() != null && MessageDigest.isEqual(
                request.getAuthToken().getBytes(StandardCharsets.UTF_8),
                clientHandler.authToken.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void run() {
        try {
            String input;
            while ((input = Protocol.readLine(reader)) != null) {
                Request request = decode(input);
                if (request != null && request.getRequestType() != null && checkAuthToken(request)) {
                    process(request);
                } else {
                    log.warn("Rejected a malformed request from client " + clientHandler.getId());
                }
            }
        } catch (IOException e) {
            log.info("Connection to client " + clientHandler.getId() + " ended: " + e.getMessage());
        } finally {
            clientHandler.close();
            log.info("Client " + clientHandler.getId() + " disconnected");
        }
    }

    private static Request decode(String input) {
        try {
            return Protocol.GSON.fromJson(input, Request.class);
        } catch (JsonParseException | IllegalArgumentException e) {
            return null;
        }
    }

    void process(Request request) {
        try {
            DataManager.inTransaction(() -> {
                authorize(request);
                handle(request);
            });
        } catch (AccessDeniedException e) {
            log.warn("Client " + clientHandler.getId() + " denied " + request.getRequestType());
            clientHandler.responser.sendError(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException
                 | NullPointerException | java.time.format.DateTimeParseException e) {
            log.warn("Could not process " + request.getRequestType() + ": " + e);
            clientHandler.responser.sendError("That request could not be completed.");
        } catch (RuntimeException e) {
            log.error("Failed to process " + request.getRequestType(), e);
            clientHandler.responser.sendError("Something went wrong on the server.");
        }
    }

    private void authorize(Request request) {
        RequestType type = request.getRequestType();
        if (RequestAccess.isPublic(type)) {
            return;
        }
        User user = clientHandler.getUser();
        if (user == null) {
            throw new AccessDeniedException("Please log in first.");
        }
        if (clientHandler.mustChangePassword() && type != RequestType.CHANGE_PASSWORD
                && type != RequestType.CHANGE_PASS_DATA && type != RequestType.LOGOUT) {
            throw new AccessDeniedException("Please change your password first.");
        }
        if (!RequestAccess.allows(type, user.getUserType())) {
            throw new AccessDeniedException("You do not have access to that.");
        }
        checkOwnership(request, user);
    }

    private void checkOwnership(Request request, User user) {
        List<String> args = request.getArrayList();
        switch (request.getRequestType()) {
            case CHAT_DATA, REQUEST_SEND_MESSEGE -> requireChatMember(user, number(args, 0));
            case CW_COURSE_PAGE_DATA -> requireCourseMember(user, course(number(args, 0)));
            case CW_EDUCATIONAL_CONTEXT -> requireCourseMember(user, course(context(number(args, 0)).getEducationalContextCourseId()));
            case STUDENT_EXERCISE_DATA, UPLOAD_ANSWER -> requireCourseMember(user, course(exercise(number(args, 0)).getExerciseCourseId()));
            case CW_MASTER_EXERCISE_DATA -> requireCourseStaff(user, course(exercise(number(args, 0)).getExerciseCourseId()));
            case MASTER_TEMPORARY_SCORE_DATA, VERIFY_SCORES, ADD_EDUCATIONAL_CONTEXT, ADD_EXERCISE ->
                    requireCourseStaff(user, course(number(args, 0)));
            case ADD_STUDENT_TO_CW, ADD_TA_TO_CW -> requireCourseStaff(user, course(number(args, 1)));
            case EDIT_EDUCATIONAL_CONTEXT, DELETE_EDUCATIONAL_CONTEXT ->
                    requireCourseStaff(user, course(context(number(args, 0)).getEducationalContextCourseId()));
            case SET_SCORE_VALUE, EDIT_SCORE_VALUE, PROTESTATION_ANSWER ->
                    requireCourseStaff(user, course(score(number(args, 0)).getScoreCourseId()));
            case SCORE_PROTESTATION -> {
                if (score(number(args, 0)).getScoreStudentId() != user.getId()) {
                    throw new AccessDeniedException("That is not your score.");
                }
            }
            case SCORING_ANSWER -> {
                Answer answer = DataManager.getAnswerById(number(args, 0));
                requireCourseStaff(user, course(exercise(answer.getAnswerExerciseId()).getExerciseCourseId()));
            }
            case ANSWER_UNIREQUEST -> {
                UniRequest uniRequest = DataManager.getUniRequestById(number(args, 0));
                if (uniRequest.getUniRequestResponsibleId() != user.getId()) {
                    throw new AccessDeniedException("That request was not sent to you.");
                }
            }
            case EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA -> {
                Student student = DataManager.getUserById(number(args, 0)).getStudent();
                requireDepartment(user, student.getStudentDepartmentId());
            }
            case EDIT_COURSE_DATA, DELETE_COURSE, EDIT_COURSE ->
                    requireDepartment(user, course(number(args, 0)).getCourseDepartmentId());
            case ADD_MASTER -> requireMasterManager(user);
            case EDIT_MASTER_DATA, EDIT_MASTER, DELETE_MASTER -> {
                requireMasterManager(user);
                requireDepartment(user, DataManager.getUserById(number(args, 0)).getMaster().getMasterDepartmentId());
            }
            default -> {
            }
        }
    }

    private static int number(List<String> args, int index) {
        return Integer.parseInt(args.get(index));
    }

    private static <T> T found(T value) {
        if (value == null) {
            throw new IllegalArgumentException("No such record");
        }
        return value;
    }

    private static Course course(int id) {
        return found(DataManager.getCourseById(id));
    }

    private static Exercise exercise(int id) {
        return found(DataManager.getExerciseById(id));
    }

    private static EducationalContext context(int id) {
        return found(DataManager.getEducationalContextById(id));
    }

    private static Score score(int id) {
        return found(DataManager.getScoreById(id));
    }

    private static void requireChatMember(User user, int chatId) {
        if (found(DataManager.getChatById(chatId)).getChatUsers().stream().noneMatch(u -> u.getId() == user.getId())) {
            throw new AccessDeniedException("You are not part of that chat.");
        }
    }

    private static boolean isCourseStaff(User user, Course course) {
        if (user.getUserType() == UserType.STUDENT) {
            return course.getCourseTAs().stream().anyMatch(s -> s.getId() == user.getId());
        }
        if (course.getCourseMasterId() == user.getId()) {
            return true;
        }
        return user.getUserType() == UserType.ASSISTANT
                && user.getMaster().getMasterDepartmentId() == course.getCourseDepartmentId();
    }

    private static void requireCourseStaff(User user, Course course) {
        if (!isCourseStaff(user, course)) {
            throw new AccessDeniedException("You do not teach that course.");
        }
    }

    private static void requireCourseMember(User user, Course course) {
        boolean student = course.getCourseStudents().stream().anyMatch(s -> s.getId() == user.getId());
        if (!student && !isCourseStaff(user, course)) {
            throw new AccessDeniedException("You are not part of that course.");
        }
    }

    private static void requireDepartment(User user, int departmentId) {
        if (user.getMaster() == null || user.getMaster().getMasterDepartmentId() != departmentId) {
            throw new AccessDeniedException("That belongs to another department.");
        }
    }

    private static void requireMasterManager(User user) {
        boolean boss = user.getMaster() != null && user.getMaster().getMasterType() == MasterType.BOSS;
        if (!boss) {
            throw new AccessDeniedException("Only the head of department can do that.");
        }
    }

    private void handle(Request request) {
        Responser responser = clientHandler.responser;
        ArrayList<String> args = request.getArrayList();
        User user = clientHandler.getUser();
        switch (request.getRequestType()) {
            case LOGIN_DATA -> responser.sendLoginData();
            case LOGIN -> responser.sendLoginResult(args);
            case PING -> responser.sendOfflineData();
            case LOGOUT -> {
                clientHandler.setUser(null);
                clientHandler.setMustChangePassword(false);
                responser.sendLoginData();
            }
            case ADMIN_MENU_DATA -> responser.sendAdminMenuData();
            case MOHSENI_MENU_DATA -> responser.sendMohseniMenuData();
            case WEEKLY_PLAN -> responser.sendWeeklyPlanData();
            case EXAMS_TIME -> responser.sendExamsListData();
            case MESSENGER_MENU -> responser.sendMessengerMenuData();
            case CHAT_DATA -> responser.sendChatData(number(args, 0));
            case CW_MENU_DATA -> responser.sendCWMenuData();
            case STUDENT_PROFILE_DATA -> responser.sendStudentProfileData();
            case GENERAL_PROFILE_DATA -> responser.sendGeneralProfileData();
            case CW_COURSE_PAGE_DATA -> responser.sendCWCourseData(number(args, 0));
            case CW_EDUCATIONAL_CONTEXT -> responser.senCWEducationalContextData(number(args, 0));
            case STUDENT_MAIN_MENU -> responser.sendStudentMenuData();
            case MASTER_MAIN_MENU -> responser.sendMasterMenuData();
            case ASSISTANT_MAIN_MENU -> responser.sendAssistantMenuData();
            case MASTER_PROFILE_DATA -> responser.sendMasterProfileData();
            case MOHSENI_STUDENT_INFO -> responser.sendMohseniStudentInfoData(args.get(0));
            case ADD_CHAT_DATA -> responser.sendAddChatData();
            case UNIREQUESTS_DATA -> responser.sendUniRequestsData();
            case STUDENT_TEMPORARY_SCORE_DATA -> responser.sendStudentTemporaryScores();
            case STUDENT_GENERAL_CALENDER_DATA -> responser.sendStudentGeneralCalenderData();
            case MASTER_GENERAL_CALENDER_DATA -> responser.sendMasterGeneralCalenderData();
            case EDIT_MASTER_DATA -> responser.sendEditMasterData(number(args, 0));
            case STUDENT_EXERCISE_DATA -> responser.sendCWStudentExerciseData(number(args, 0));
            case EDIT_COURSE_DATA -> responser.sendEditCourseData(number(args, 0));
            case COURSE_LIST_DATA -> responser.sendCoursesListData(args.get(0), args.get(1), args.get(2));
            case ASSISTANT_TEMPORARY_SCORE_DATA -> responser.sendTATemporaryScores(args.get(0), args.get(1), args.get(2));
            case CW_MASTER_EXERCISE_DATA -> responser.sendCWMasterExerciseData(number(args, 0));
            case UNIT_SELECTION_DATA -> responser.sendUnitSelectionData();
            case EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA -> responser.sendEducationalSituationToAssistant(number(args, 0));
            case EDUCATIONAL_SITUATION_TO_STUDENT_DATA -> responser.sendEducationalSituationToStudent();
            case MASTER_TEMPORARY_SCORE_DATA -> responser.sendMasterTemporaryScores(number(args, 0));
            case MOHSENI_STUDENT_SEARCH_DATA -> responser.sendMohseniStudentSearchData(args.get(0), args.get(1), args.get(2));
            case MASTER_LIST_DATA -> responser.sendMastersListData(args.get(0), args.get(1), args.get(2));
            case CHANGE_PASS_DATA -> responser.sendChangePassData();
            case CHOOOSE_UNIT_SELECTION_DATA -> responser.sendChooseUnitSelectionTime();
            case DELETE_MASTER -> DataManager.deleteMaster(number(args, 0));
            case DELETE_COURSE -> DataManager.deleteCourse(number(args, 0));
            case REQUEST_RECOMMENDATION -> DataManager.addRecommendationRequest(user.getId(), number(args, 0));
            case ENROLLMENT_REQUEST -> DataManager.addEnrollmentRequest(user.getId());
            case MINOR_UNIREQUEST -> DataManager.addMinorRequest(user.getId(), number(args, 0));
            case WITHDRAW_REQUEST -> DataManager.addWithdrawRequest(user.getId());
            case DORMITPRY_REQUEST -> DataManager.addDormitoryRequest(user.getId());
            case DDD_UNIREQUEST -> DataManager.addDDDRequest(user.getId());
            case SCORE_PROTESTATION -> DataManager.addScoreProtestation(number(args, 0), args.get(1));
            case PROTESTATION_ANSWER -> DataManager.addScoreProtestationAnswer(number(args, 0), args.get(1));
            case SET_SCORE_VALUE, EDIT_SCORE_VALUE -> DataManager.setScoreValue(number(args, 0), args.get(1));
            case VERIFY_SCORES -> DataManager.verifyScores(number(args, 0));
            case CHANGE_EMAIL -> DataManager.changeEmail(user.getId(), args.get(0));
            case CHANGE_PHONE_NUMBER -> DataManager.changePhoneNumber(user.getId(), args.get(0));
            case CHANGE_PASSWORD -> changePassword(user, args);
            case REQUEST_SELCECT_UNIT_FORM_ASSISTANT -> DataManager.requestUnitSelectionFromAssistant(user.getId(), number(args, 0));
            case REQUEST_SEND_MESSEGE -> DataManager.addMessage(user.getId(), number(args, 0), args.get(1), request.getFileInByte());
            case REQUEST_CREATE_CHAT -> DataManager.addCreateChatRequest(user.getId(), args.get(0));
            case MESSAGE_TO_ADMIN -> DataManager.addMessageToAdmin(user.getId(), args.get(0));
            case ADD_TA_TO_CW -> DataManager.addTAToCourse(args.get(0), number(args, 1));
            case ADD_STUDENT_TO_CW -> DataManager.addStudentToCourse(args.get(0), number(args, 1));
            case DELETE_EDUCATIONAL_CONTEXT -> DataManager.deleteEducationalContext(number(args, 0));
            case CHANGE_GOUP -> DataManager.changeGroup(user.getId(), number(args, 0), number(args, 1));
            case SCORING_ANSWER -> DataManager.scoreAnswer(number(args, 0), args.get(1));
            case ADD_COURSE -> DataManager.addCourse(user.getMaster().getMasterDepartmentId(), args);
            case EDIT_COURSE -> DataManager.editCourse(args);
            case ADD_MASTER -> DataManager.addMaster(user.getMaster().getMasterDepartmentId(), args, request.getFileInByte());
            case EDIT_MASTER -> DataManager.editMaster(args, request.getFileInByte());
            case ADD_EXERCISE -> DataManager.addExercise(args, request.getFileInByte());
            case UPLOAD_ANSWER -> DataManager.addAnswer(user.getId(), number(args, 0), args.get(1), request.getFileInByte());
            case ANSWER_UNIREQUEST -> DataManager.answerUniRequest(number(args, 0), Boolean.parseBoolean(args.get(1)));
            case ADD_EDUCATIONAL_CONTEXT -> DataManager.addEducationalContext(number(args, 0), args.get(1), args.get(2), request.getFileInByte());
            case EDIT_EDUCATIONAL_CONTEXT -> DataManager.editEducationalContext(number(args, 0), args.get(1), args.get(2), request.getFileInByte());
            case SET_UNIT_SELECTION_TIME -> DataManager.setUnitSelectionTime(user.getMaster().getMasterDepartmentId(), args);
            case STAR_OR_DESATR_COURSE -> DataManager.starOrUnstarCourse(user.getId(), number(args, 0));
            case SELECT_COURSE -> DataManager.preRegisterCourse(user.getId(), number(args, 0));
            case DESELECT_COURSE -> DataManager.undoPreRegisterCourse(user.getId(), number(args, 0));
            default -> throw new IllegalArgumentException("Unsupported request");
        }
    }

    private void changePassword(User user, ArrayList<String> args) {
        ChangePassData result = new ChangePassData();
        if (!Passwords.matches(args.get(0), user.getUserPassword())) {
            result.setResult("WRONG_PASS");
        } else if (!args.get(1).equals(args.get(2))) {
            result.setResult("NOT_SAME");
        } else if (args.get(1).length() < 8) {
            result.setResult("TOO_SHORT");
        } else {
            DataManager.changePass(user.getId(), args.get(1));
            clientHandler.setMustChangePassword(false);
            result.setResult("CHANGED");
        }
        clientHandler.responser.sendResponse(result);
    }
}
