package logic;

import database.DataManager;
import database.models.*;
import logic.enums.*;
import network.Call;
import network.Problem;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Weekly plans, exam lists, and the requests that travel between students and staff. */
public final class EducationService {
    private static final Random RANDOM = new Random();

    private EducationService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.WEEKLY_PLAN, Routes.UNIVERSITY, EducationService::weeklyPlan);
        routes.on(RequestType.EXAMS, Routes.UNIVERSITY, EducationService::exams);
        routes.on(RequestType.MY_REQUESTS, Routes.STUDENT, EducationService::myRequests);
        routes.on(RequestType.REQUEST_RECOMMENDATION, Routes.STUDENT, EducationService::requestRecommendation);
        routes.on(RequestType.REQUEST_ENROLLMENT, Routes.STUDENT, EducationService::requestEnrollment);
        routes.on(RequestType.REQUEST_MINOR, Routes.STUDENT, EducationService::requestMinor);
        routes.on(RequestType.REQUEST_WITHDRAWAL, Routes.STUDENT, EducationService::requestWithdrawal);
        routes.on(RequestType.REQUEST_DORMITORY, Routes.STUDENT, EducationService::requestDormitory);
        routes.on(RequestType.REQUEST_DEFENSE, Routes.STUDENT, EducationService::requestDefense);
        routes.on(RequestType.INBOX, Routes.EVERYONE, EducationService::inbox);
        routes.on(RequestType.ANSWER_REQUEST, Routes.EVERYONE, EducationService::answer);
    }

    private static Response weeklyPlan(Call call) {
        Response response = new Response();
        for (Course course : People.coursesOf(call.user())) {
            for (String time : course.getClassTimes()) {
                response.row("classes").put("course", course.getName()).put("number", course.getCourseNumber())
                        .put("master", course.getMaster().getUser().getFullName()).put("time", time);
            }
        }
        return response;
    }

    private static Response exams(Call call) {
        Response response = new Response();
        List<Course> courses = new ArrayList<>(People.coursesOf(call.user()));
        courses.sort(Comparator.comparing(Course::getExamTime));
        for (Course course : courses) {
            response.row("exams").put("course", course.getName()).put("number", course.getCourseNumber())
                    .put("time", Format.time(course.getExamTime()));
        }
        return response;
    }

    private static Response myRequests(Call call) {
        Response response = new Response().put("level", People.student(call.user()).getStudentType());
        CatalogService.addDepartments(response);
        for (UniRequest request : DataManager.query(UniRequest.class,
                "from UniRequest where requester = :p0 and requestType <> :p1 order by createdAt desc",
                call.user(), UniRequestType.NOTICE)) {
            response.row("requests").put("id", request.getId()).put("type", request.getRequestType())
                    .put("result", request.getResult()).put("text", request.getText())
                    .put("time", Format.time(request.getCreatedAt()));
        }
        return response;
    }

    static UniRequest create(UniRequestType type, User requester, User responsible, String text) {
        UniRequest request = new UniRequest();
        request.setRequestType(type);
        request.setRequester(requester);
        request.setResponsible(responsible);
        request.setText(text);
        DataManager.save(request);
        return request;
    }

    static void notice(User user, String text) {
        create(UniRequestType.NOTICE, null, user, text).setResult(UniRequestResult.ACCEPTED);
    }

    static User deputyOf(Department department) {
        if (department.getDeputy() == null) {
            throw new Problem(department.getDepartmentType().title + " has no deputy at the moment.");
        }
        return department.getDeputy().getUser();
    }

    private static void requireLevel(Student student, StudentType... levels) {
        if (!List.of(levels).contains(student.getStudentType())) {
            throw new Problem("This request is not available at your level of study.");
        }
    }

    private static void requireNoOpenRequest(User user, UniRequestType type) {
        if (DataManager.first(UniRequest.class,
                "from UniRequest where requester = :p0 and requestType = :p1 and result = :p2",
                user, type, UniRequestResult.PENDING) != null) {
            throw new Problem("You already have an open request of this kind.");
        }
    }

    private static Response requestRecommendation(Call call) {
        User user = call.user();
        Student student = People.student(user);
        requireLevel(student, StudentType.UNDERGRADUATE, StudentType.MASTERS);
        Master master = DataManager.get(Master.class, call.number(0, "Professor"));
        create(UniRequestType.RECOMMENDATION, user, master.getUser(),
                Configuration.label("recommendationRequest", user.getFullName(), student.getStudentNumber()));
        return null;
    }

    private static Response requestEnrollment(Call call) {
        User user = call.user();
        Student student = People.student(user);
        String text = Configuration.label("enrollment", user.getFullName(), student.getStudentNumber(),
                student.getDepartment().getDepartmentType().title, Configuration.label("university"),
                LocalDateTime.now().plusYears(1).toLocalDate());
        create(UniRequestType.ENROLLMENT, user, null, text).setResult(UniRequestResult.ACCEPTED);
        return null;
    }

    private static Response requestMinor(Call call) {
        User user = call.user();
        Student student = People.student(user);
        requireLevel(student, StudentType.UNDERGRADUATE);
        requireNoOpenRequest(user, UniRequestType.MINOR);
        if (People.average(student) < Configuration.number("minorPassAverageScore")) {
            throw new Problem("A minor needs an average of at least " + Configuration.number("minorPassAverageScore") + ".");
        }
        Department target = DataManager.get(Department.class, call.number(0, "Department"));
        if (target == student.getDepartment()) {
            throw new Problem("Choose a department other than your own.");
        }
        UniRequest request = create(UniRequestType.MINOR, user, deputyOf(student.getDepartment()),
                Configuration.label("minorRequest", user.getFullName(), student.getStudentNumber(),
                        target.getDepartmentType().title));
        request.setSecondResponsible(deputyOf(target));
        return null;
    }

    private static Response requestWithdrawal(Call call) {
        User user = call.user();
        Student student = People.student(user);
        requireNoOpenRequest(user, UniRequestType.WITHDRAWAL);
        create(UniRequestType.WITHDRAWAL, user, deputyOf(student.getDepartment()),
                Configuration.label("withdrawalRequest", user.getFullName(), student.getStudentNumber()));
        return null;
    }

    private static Response requestDormitory(Call call) {
        requireLevel(People.student(call.user()), StudentType.MASTERS);
        boolean granted = RANDOM.nextBoolean();
        create(UniRequestType.DORMITORY, call.user(), null,
                Configuration.label(granted ? "dormitoryAccepted" : "dormitoryRejected"))
                .setResult(granted ? UniRequestResult.ACCEPTED : UniRequestResult.REJECTED);
        return null;
    }

    private static Response requestDefense(Call call) {
        requireLevel(People.student(call.user()), StudentType.PHD);
        create(UniRequestType.DEFENSE, call.user(), null,
                Configuration.label("defense", LocalDateTime.now().plusMonths(1).toLocalDate()))
                .setResult(UniRequestResult.ACCEPTED);
        return null;
    }

    private static Response inbox(Call call) {
        Response response = new Response();
        User user = call.user();
        for (UniRequest request : DataManager.query(UniRequest.class,
                "from UniRequest where responsible = :p0 or secondResponsible = :p0 order by createdAt desc", user)) {
            UniRequestResult mine = answerOf(request, user);
            response.row("items").put("id", request.getId()).put("type", request.getRequestType())
                    .put("from", request.getRequester() == null ? "System" : request.getRequester().getFullName())
                    .put("text", request.getText()).put("time", Format.time(request.getCreatedAt()))
                    .put("result", mine == null ? request.getResult() : mine)
                    .put("canAnswer", request.getRequestType() != UniRequestType.NOTICE && mine == null
                            && request.getResult() == UniRequestResult.PENDING);
        }
        return response;
    }

    private static UniRequestResult answerOf(UniRequest request, User user) {
        if (request.getResponsible() == user) {
            return request.getFirstAnswer();
        }
        return request.getSecondAnswer();
    }

    private static Response answer(Call call) {
        UniRequest request = DataManager.get(UniRequest.class, call.id(0));
        User user = call.user();
        boolean first = request.getResponsible() == user;
        if (!first && request.getSecondResponsible() != user) {
            throw new Problem("That request was not sent to you.");
        }
        if (request.getResult() != UniRequestResult.PENDING || answerOf(request, user) != null) {
            throw new Problem("That request has already been answered.");
        }
        UniRequestResult result = Boolean.parseBoolean(call.text(1)) ? UniRequestResult.ACCEPTED : UniRequestResult.REJECTED;
        if (first) {
            request.setFirstAnswer(result);
        } else {
            request.setSecondAnswer(result);
        }
        settle(request, result);
        return null;
    }

    private static void settle(UniRequest request, UniRequestResult latest) {
        boolean waiting = request.getSecondResponsible() != null
                && (request.getFirstAnswer() == null || request.getSecondAnswer() == null);
        if (latest == UniRequestResult.REJECTED) {
            request.setResult(UniRequestResult.REJECTED);
        } else if (!waiting) {
            request.setResult(UniRequestResult.ACCEPTED);
            carryOut(request);
        }
    }

    private static void carryOut(UniRequest request) {
        User requester = request.getRequester();
        switch (request.getRequestType()) {
            case CHAT -> MessengerService.chatBetween(requester, request.getResponsible());
            case WITHDRAWAL -> {
                requester.getStudent().setState(StudentState.WITHDREW);
                requester.setCanLogin(false);
            }
            case RECOMMENDATION -> request.setText(recommendation(request));
            case COURSE -> UnitSelectionService.select(requester.getStudent(),
                    DataManager.get(Course.class, request.getCourseId()));
            case GROUP_CHANGE -> UnitSelectionService.replace(requester.getStudent(),
                    DataManager.get(Course.class, request.getCourseId()),
                    DataManager.get(Course.class, request.getOtherCourseId()));
            default -> {
            }
        }
    }

    private static String recommendation(UniRequest request) {
        Student student = request.getRequester().getStudent();
        return Configuration.label("recommendation", request.getResponsible().getFullName(),
                request.getRequester().getFullName(), student.getStudentNumber(),
                orNone(CatalogService.names(People.passedCourses(student))),
                orNone(CatalogService.names(student.getAssistingCourses())));
    }

    private static String orNone(String text) {
        return text.isEmpty() ? "no courses" : text;
    }
}
