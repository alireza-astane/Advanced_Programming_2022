package network;

import database.DataManager;
import database.DemoData;
import database.models.*;
import logic.Configuration;
import logic.UnitSelectionService;
import logic.enums.ScoreType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import static network.PortalClient.find;
import static network.RequestType.*;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private static final String PASSWORD = DemoData.PASSWORD;
    private static Server server;
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("portal.db.url", "jdbc:h2:mem:portal-test;DB_CLOSE_DELAY=-1");
        Configuration.load();
        server = Server.getInstance();
        server.start(0);
        port = server.getPort();
        new Thread(server::acceptClients).start();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
        DataManager.close();
    }

    private static PortalClient as(String username) throws IOException {
        PortalClient client = new PortalClient(port);
        Response response = client.login(username, PASSWORD);
        assertNull(response.getError(), username);
        return client;
    }

    private static <T> T db(Supplier<T> query) {
        return DataManager.inTransaction(query);
    }

    private static int userId(String username) {
        return db(() -> DataManager.first(User.class, "from User where username = :p0", username).getId());
    }

    private static int courseId(String number, int group) {
        return db(() -> DataManager.first(Course.class, "from Course where courseNumber = :p0 and groupNumber = :p1",
                number, group).getId());
    }

    private static int courseId(String number) {
        return courseId(number, 1);
    }

    private static String future(int days) {
        return LocalDateTime.now().plusDays(days).withNano(0).withSecond(0).toString();
    }

    @Test
    void everyRoleLogsInWithItsOwnIdentity() throws IOException {
        Map<String, String> roles = Map.of("student.sara", "STUDENT", "prof.karimi", "MASTER",
                "deputy.cs", "ASSISTANT", "admin", "ADMIN", "registrar", "REGISTRAR");
        for (var entry : roles.entrySet()) {
            try (PortalClient client = new PortalClient(port)) {
                assertEquals(entry.getValue(), client.login(entry.getKey(), PASSWORD).get("role"));
            }
        }
        assertEquals(1, userId("admin"));
    }

    @Test
    void wrongCredentialsAndCaptchasAreRejected() throws IOException {
        try (PortalClient client = new PortalClient(port)) {
            assertEquals("Wrong username or password.", client.login("student.sara", "not-the-password").getError());
            assertEquals("Wrong username or password.", client.login("nobody", PASSWORD).getError());
            client.ok(CAPTCHA);
            client.refused("captcha", LOGIN, "student.sara", PASSWORD, "x");
            client.refused("captcha", LOGIN, "student.sara", PASSWORD, "x");
            client.refused("log in", HOME);
        }
    }

    @Test
    void aCaptchaWorksOnlyOnce() throws IOException {
        try (PortalClient client = new PortalClient(port)) {
            client.ok(CAPTCHA);
            String code = Server.getInstance().clientWithToken(client.token).captchaCode();
            client.refused("username", LOGIN, "student.sara", "wrong", code);
            client.refused("captcha", LOGIN, "student.sara", PASSWORD, code);
        }
    }

    @Test
    void forgedTokensAreIgnored() throws IOException {
        try (PortalClient victim = as("student.sara"); PortalClient attacker = new PortalClient(port)) {
            attacker.sendRaw(new Request(HOME), victim.token);
            Response next = attacker.ask(CAPTCHA);
            assertTrue(next.isOk());
            assertNotNull(next.getFile());
        }
    }

    @Test
    void passwordsAreHashedAndNeverSent() throws IOException {
        String stored = db(() -> DataManager.get(User.class, userId("student.sara")).getPassword());
        assertTrue(stored.startsWith("$2"));
        try (PortalClient registrar = as("registrar")) {
            String json = Protocol.GSON.toJson(registrar.ok(STUDENT_DETAILS, userId("student.sara")));
            assertFalse(json.contains("$2a$"));
        }
    }

    @Test
    void passwordExpiresAfterThreeHoursAndBlocksEverythingElse() throws IOException {
        int id = userId("student.yasi");
        DataManager.inTransaction(() -> DataManager.get(User.class, id).setLastLogin(LocalDateTime.now().minusHours(4)));
        try (PortalClient client = new PortalClient(port)) {
            assertEquals("true", client.login("student.yasi", PASSWORD).get("mustChangePassword"));
            client.refused("change your password", HOME);
            client.refused("current password", CHANGE_PASSWORD, "bad", "newpass123", "newpass123");
            client.refused("match", CHANGE_PASSWORD, PASSWORD, "newpass123", "other");
            client.refused("at least", CHANGE_PASSWORD, PASSWORD, "short", "short");
            client.ok(CHANGE_PASSWORD, PASSWORD, "newpass123", "newpass123");
            client.ok(HOME);
            client.ok(CHANGE_PASSWORD, "newpass123", PASSWORD, PASSWORD);
        }
    }

    @Test
    void rolesCannotUseEachOthersRequests() throws IOException {
        try (PortalClient student = as("student.omid")) {
            student.refused("access", SAVE_COURSE, "");
            student.refused("access", SET_SCORE, 1, 20);
            student.refused("access", ALL_STUDENTS);
        }
        try (PortalClient master = as("prof.karimi")) {
            master.refused("access", UNIT_SELECTION);
            master.refused("access", SET_SELECTION_WINDOW, "ALL", 0, future(1), future(2));
            master.refused("head", SAVE_MASTER, "", "X", "x.user", "password1");
        }
    }

    @Test
    void everyPageOpensForItsRole() throws IOException {
        pages("student.sara", HOME, PROFILE, COURSES, MASTERS, WEEKLY_PLAN, EXAMS, MY_REQUESTS, INBOX,
                MY_TEMPORARY_SCORES, TRANSCRIPT, UNIT_SELECTION, CHATS, CONTACTS, CW_COURSES, CW_CALENDAR);
        pages("prof.karimi", HOME, PROFILE, COURSES, MASTERS, WEEKLY_PLAN, EXAMS, INBOX, TEACHING_COURSES, CHATS,
                CONTACTS, CW_COURSES, CW_CALENDAR);
        pages("deputy.cs", HOME, PROFILE, COURSES, MASTERS, WEEKLY_PLAN, EXAMS, INBOX, TEACHING_COURSES,
                DEPARTMENT_SCORES, DEPARTMENT_STUDENTS, CHATS, CONTACTS, CW_COURSES, CW_CALENDAR);
        pages("registrar", HOME, PROFILE, ALL_STUDENTS, INBOX, CHATS, CONTACTS);
        pages("admin", HOME, PROFILE, INBOX, CHATS, CONTACTS);
    }

    private static void pages(String username, RequestType... types) throws IOException {
        try (PortalClient client = as(username)) {
            for (RequestType type : types) {
                client.ok(type);
            }
        }
    }

    @Test
    void homeShowsWhatTheBriefAsksFor() throws IOException {
        try (PortalClient student = as("student.sara")) {
            Response home = student.ok(HOME);
            assertEquals("Kaveh Karimi", home.get("supervisor"));
            assertEquals("STUDYING", home.get("state"));
            assertEquals("true", home.get("selectionOpen"));
            assertFalse(home.get("lastLogin").isEmpty());
        }
    }

    @Test
    void gradesMoveFromEntryToTemporaryToFinal() throws IOException {
        int course = courseId("MA201");
        try (PortalClient teacher = as("head.math"); PortalClient student = as("student.omid")) {
            Response page = teacher.ok(COURSE_SCORES, course);
            int omid = Integer.parseInt(find(page.table("scores"), "student", "Omid Rostami").get("id"));
            int yasi = Integer.parseInt(find(page.table("scores"), "student", "Yasaman Ebadi").get("id"));
            teacher.refused("between 0 and 20", SET_SCORE, omid, 21);
            teacher.ok(SET_SCORE, omid, 17.3);
            teacher.refused("every student", PUBLISH_SCORES, course);
            teacher.refused("Publish", FINALIZE_SCORES, course);
            assertTrue(student.ok(MY_TEMPORARY_SCORES).table("scores").isEmpty());

            teacher.ok(SET_SCORE, yasi, 8);
            teacher.ok(PUBLISH_SCORES, course);
            Map<String, String> mine = student.ok(MY_TEMPORARY_SCORES).table("scores").get(0);
            assertEquals("17.25", mine.get("value"));

            student.ok(PROTEST_SCORE, omid, "Please check question three.");
            assertEquals("Please check question three.",
                    find(teacher.ok(COURSE_SCORES, course).table("scores"), "student", "Omid Rostami").get("protest"));
            teacher.ok(ANSWER_PROTEST, omid, "Checked, one more point.", 18.25);
            assertEquals("18.25", student.ok(MY_TEMPORARY_SCORES).table("scores").get(0).get("value"));

            teacher.ok(FINALIZE_SCORES, course);
            teacher.refused("Final grades", SET_SCORE, omid, 20);
            assertTrue(student.ok(MY_TEMPORARY_SCORES).table("scores").isEmpty());
            Response record = student.ok(TRANSCRIPT);
            assertEquals("18.25", find(record.table("courses"), "course", "Linear Algebra").get("value"));
            assertEquals("N/A", find(record.table("courses"), "course", "Data Structures").get("value"));
            assertEquals("7", record.get("passedUnits"));
            assertEquals("16.68", record.get("average"));
        }
        try (PortalClient deputy = as("deputy.math")) {
            Map<String, String> summary = find(deputy.ok(DEPARTMENT_SCORES).table("summaries"), "course", "Linear Algebra");
            assertEquals("1", summary.get("passed"));
            assertEquals("1", summary.get("failed"));
            assertEquals("13.13", summary.get("average"));
            assertEquals("18.25", summary.get("averageOfPassed"));
        }
    }

    @Test
    void teachersReachOnlyTheirOwnCourses() throws IOException {
        int databases = courseId("CS320");
        try (PortalClient other = as("prof.karimi")) {
            other.refused("do not teach", COURSE_SCORES, databases);
            other.refused("do not teach", PUBLISH_SCORES, databases);
        }
        try (PortalClient otherDeputy = as("deputy.math")) {
            otherDeputy.refused("another department", DELETE_COURSE, databases);
            otherDeputy.refused("another department", STUDENT_TRANSCRIPT, userId("student.sara"));
        }
    }

    @Test
    void unitSelectionEnforcesTheRules() throws IOException {
        try (PortalClient sara = as("student.sara")) {
            sara.refused("already have", TAKE_COURSE, courseId("CS201"));
            sara.refused("already passed", TAKE_COURSE, db(() -> DataManager.first(Course.class,
                    "from Course where courseNumber = 'CS101'").getId()));
            sara.refused("prerequisites", TAKE_COURSE, courseId("CS301"));
            sara.refused("level", TAKE_COURSE, courseId("CS510"));
            sara.refused("class time", TAKE_COURSE, courseId("CS330"));
            sara.ok(TAKE_COURSE, courseId("CS320"));
            sara.refused("already have", TAKE_COURSE, courseId("CS320"));
            sara.ok(TAKE_COURSE, courseId("GS101"));
            sara.refused("general", TAKE_COURSE, courseId("GS201"));
            sara.ok(STAR_COURSE, courseId("MA201"));
            Map<String, String> row = find(sara.ok(UNIT_SELECTION).table("courses"), "number", "MA201");
            assertEquals("true", row.get("starred"));
            assertEquals("true", row.get("suggested"));
            sara.ok(DROP_COURSE, courseId("GS101"));

            sara.ok(ASK_DEPUTY_FOR_COURSE, courseId("CH101"));
        }
        try (PortalClient deputy = as("deputy.cs")) {
            Map<String, String> item = deputy.ok(INBOX).table("items").stream()
                    .filter(row -> row.get("type").equals("COURSE")).findFirst().orElseThrow();
            deputy.ok(ANSWER_REQUEST, item.get("id"), true);
        }
        int sara = userId("student.sara");
        DataManager.inTransaction(() -> DataManager.get(Student.class, sara).setSelectionEnd(LocalDateTime.now().minusMinutes(1)));
        DataManager.inTransaction(UnitSelectionService::finishExpiredSelections);
        try (PortalClient client = as("student.sara")) {
            List<String> courses = client.ok(CW_COURSES).table("courses").stream().map(row -> row.get("number")).toList();
            assertTrue(courses.contains("CH101"));
            assertFalse(courses.contains("CS320"), "its corequisite was not taken, so it must be removed");
            assertTrue(client.ok(INBOX).table("items").stream().anyMatch(row -> row.get("text").contains("Databases was removed")));
            client.refused("not open", UNIT_SELECTION);
        }
        DataManager.inTransaction(() -> DataManager.get(Student.class, sara).setSelectionEnd(LocalDateTime.now().plusDays(1)));
    }

    @Test
    void deputySetsSelectionWindows() throws IOException {
        try (PortalClient deputy = as("deputy.math")) {
            assertEquals("Window set for 1 students.", deputy.ok(SET_SELECTION_WINDOW, "PHD", 2024,
                    LocalDateTime.now().minusHours(1).withNano(0).withSecond(0), future(1)).get("message"));
            deputy.refused("after", SET_SELECTION_WINDOW, "ALL", 0, future(2), future(1));
        }
        try (PortalClient yasi = as("student.yasi")) {
            assertEquals("true", yasi.ok(HOME).get("selectionOpen"));
        }
    }

    @Test
    void requestsFollowTheirOwnRules() throws IOException {
        try (PortalClient lena = as("student.lena")) {
            lena.refused("level", REQUEST_DORMITORY);
            lena.refused("level", REQUEST_DEFENSE);
            lena.refused("average", REQUEST_MINOR, 1);
            lena.ok(REQUEST_ENROLLMENT);
            assertTrue(lena.ok(MY_REQUESTS).table("requests").get(0).get("text").contains("24201001"));
        }
        try (PortalClient nika = as("student.nika")) {
            nika.ok(REQUEST_DORMITORY);
            assertNotEquals("PENDING", nika.ok(MY_REQUESTS).table("requests").get(0).get("result"));
            nika.refused("level", REQUEST_MINOR, 1);
        }
        try (PortalClient reza = as("student.reza")) {
            reza.ok(REQUEST_DEFENSE);
            assertTrue(reza.ok(MY_REQUESTS).table("requests").get(0).get("text").contains("defend"));
            reza.refused("level", REQUEST_RECOMMENDATION, userId("prof.karimi"));
        }
    }

    @Test
    void minorNeedsBothDeputies() throws IOException {
        int math = db(() -> DataManager.get(Master.class, userId("deputy.math")).getDepartment().getId());
        try (PortalClient sara = as("student.sara")) {
            sara.ok(REQUEST_MINOR, math);
            sara.refused("open request", REQUEST_MINOR, math);
        }
        String id;
        try (PortalClient source = as("deputy.cs")) {
            id = source.ok(INBOX).table("items").stream().filter(row -> row.get("type").equals("MINOR"))
                    .findFirst().orElseThrow().get("id");
            source.ok(ANSWER_REQUEST, id, true);
            source.refused("already", ANSWER_REQUEST, id, true);
        }
        assertEquals("PENDING", result("student.sara", id));
        try (PortalClient target = as("deputy.math")) {
            target.ok(ANSWER_REQUEST, id, true);
        }
        assertEquals("ACCEPTED", result("student.sara", id));
    }

    private static String result(String username, String id) throws IOException {
        try (PortalClient client = as(username)) {
            return find(client.ok(MY_REQUESTS).table("requests"), "id", id).get("result");
        }
    }

    @Test
    void acceptedWithdrawalBlocksLogin() throws IOException {
        try (PortalClient arman = as("student.arman")) {
            arman.ok(REQUEST_WITHDRAWAL);
        }
        try (PortalClient other = as("deputy.math")) {
            String id = db(() -> String.valueOf(DataManager.first(UniRequest.class,
                    "from UniRequest where requester.username = 'student.arman'").getId()));
            other.refused("not sent to you", ANSWER_REQUEST, id, true);
        }
        try (PortalClient deputy = as("deputy.cs")) {
            String id = deputy.ok(INBOX).table("items").stream().filter(row -> row.get("type").equals("WITHDRAWAL"))
                    .findFirst().orElseThrow().get("id");
            deputy.ok(ANSWER_REQUEST, id, true);
        }
        try (PortalClient arman = new PortalClient(port)) {
            assertEquals("This account can no longer log in.", arman.login("student.arman", PASSWORD).getError());
        }
    }

    @Test
    void recommendationIsWrittenWhenAccepted() throws IOException {
        try (PortalClient master = as("prof.karimi")) {
            Map<String, String> item = master.ok(INBOX).table("items").stream()
                    .filter(row -> row.get("type").equals("RECOMMENDATION")).findFirst().orElseThrow();
            master.ok(ANSWER_REQUEST, item.get("id"), true);
            assertEquals("ACCEPTED", result("student.sara", item.get("id")));
        }
        try (PortalClient sara = as("student.sara")) {
            String text = sara.ok(MY_REQUESTS).table("requests").stream()
                    .filter(row -> row.get("type").equals("RECOMMENDATION")).findFirst().orElseThrow().get("text");
            assertTrue(text.contains("Programming Fundamentals"), text);
        }
    }

    @Test
    void messengerKeepsConversationsPrivate() throws IOException {
        byte[] file = {1, 2, 3, 4};
        String chat;
        try (PortalClient sara = as("student.sara")) {
            chat = find(sara.ok(CHATS).table("chats"), "name", "Omid Rostami").get("id");
            sara.ask(new Request(SEND_MESSAGE, chat, "notes attached", "notes.pdf").withFile(file));
            assertEquals("notes attached", sara.ok(CHATS).table("chats").get(0).get("lastMessage"));
            List<Map<String, String>> messages = sara.ok(CHAT, chat).table("messages");
            Map<String, String> last = messages.get(messages.size() - 1);
            assertEquals("true", last.get("mine"));
            assertArrayEquals(file, sara.ok(MESSAGE_FILE, last.get("id")).getFile());
            assertTrue(sara.ok(CONTACTS).table("contacts").stream().anyMatch(row -> row.get("name").equals("Kaveh Karimi")));
            assertFalse(sara.ok(CONTACTS).table("contacts").stream().anyMatch(row -> row.get("name").equals("Lena Ahmadi")));
            sara.refused("directly", BROADCAST, userId("student.lena"), "hello", "");
        }
        try (PortalClient outsider = as("student.reza")) {
            outsider.refused("not part", CHAT, chat);
            outsider.refused("not part", SEND_MESSAGE, chat, "hi", "");
            outsider.ok(REQUEST_CHAT, "24201001");
            outsider.ok(MESSAGE_ADMIN, "I need help");
        }
        try (PortalClient lena = as("student.lena")) {
            String id = lena.ok(INBOX).table("items").stream().filter(row -> row.get("type").equals("CHAT"))
                    .findFirst().orElseThrow().get("id");
            lena.ok(ANSWER_REQUEST, id, true);
            assertEquals("Reza Kamali", lena.ok(CHATS).table("chats").get(0).get("name"));
        }
        try (PortalClient admin = as("admin")) {
            assertTrue(admin.ok(CHATS).table("chats").stream().anyMatch(row -> row.get("lastMessage").equals("I need help")));
        }
        try (PortalClient registrar = as("registrar")) {
            int everyone = registrar.ok(CONTACTS).table("contacts").size();
            assertEquals(registrar.ok(ALL_STUDENTS).table("students").size(), everyone);
            registrar.ok(BROADCAST, userId("student.lena") + "," + userId("student.reza"), "Term starts soon", "");
            assertEquals(2, registrar.ok(CHATS).table("chats").size());
        }
    }

    @Test
    void coursewareRespectsCourseRoles() throws IOException {
        int course = courseId("CS201");
        String exercise;
        String content;
        try (PortalClient teacher = as("prof.karimi")) {
            Response page = teacher.ok(CW_COURSE, course);
            assertEquals("MASTER", page.get("role"));
            exercise = page.table("exercises").get(0).get("id");
            content = teacher.ok(CW_ADD_CONTENT, course, "Week 2").get("id");
            for (int i = 0; i < 5; i++) {
                teacher.ok(CW_SAVE_ITEM, content, "", "Item " + i, "");
            }
            teacher.refused("at most 5", CW_SAVE_ITEM, content, "", "Too many", "");
            String answer = teacher.ok(CW_EXERCISE, exercise).table("answers").get(0).get("student");
            assertEquals("Omid Rostami", answer);
        }
        try (PortalClient assistant = as("student.nika")) {
            assertEquals("ASSISTANT", assistant.ok(CW_COURSE, course).get("role"));
            assistant.refused("access", CW_ADD_CONTENT, course, "Not allowed");
            assistant.refused("access", CW_DELETE_ITEM, 1);
            String item = assistant.ok(CW_CONTENT, content).table("items").get(0).get("id");
            assistant.ok(CW_SAVE_ITEM, content, item, "Edited by the assistant", "");
            Map<String, String> answer = assistant.ok(CW_EXERCISE, exercise).table("answers").get(0);
            assertEquals("*".repeat("Omid Rostami".length()), answer.get("student"));
            assertEquals("********", answer.get("number"));
            assistant.ok(CW_GRADE_ANSWER, answer.get("id"), 19);
        }
        try (PortalClient student = as("student.omid")) {
            Response page = student.ok(CW_EXERCISE, exercise);
            assertEquals("19.00", page.get("grade"));
            student.refused("written answer", CW_SUBMIT_ANSWER, exercise, "", "");
            student.ok(CW_SUBMIT_ANSWER, exercise, "A better answer", "");
            student.refused("staff", CW_GRADE_ANSWER, page.get("answerId"), 20);
            assertEquals("Edited by the assistant", student.ok(CW_CONTENT, content).table("items").get(0).get("text"));
        }
        try (PortalClient outsider = as("student.lena")) {
            outsider.refused("not part", CW_COURSE, course);
            outsider.refused("not part", CW_EXERCISE, exercise);
        }
    }

    @Test
    void professorsAddMembersWhoAreNotified() throws IOException {
        int course = courseId("CS510");
        try (PortalClient teacher = as("head.cs")) {
            teacher.ok(CW_ADD_MEMBER, course, "24103001", true);
            teacher.refused("No student", CW_ADD_MEMBER, course, "000", false);
        }
        try (PortalClient reza = as("student.reza")) {
            assertEquals("ASSISTANT", reza.ok(CW_COURSE, course).get("role"));
            assertTrue(reza.ok(INBOX).table("items").stream().anyMatch(row -> row.get("text").contains("teaching assistant")));
        }
    }

    @Test
    void deputyManagesCoursesAndStudents() throws IOException {
        int karimi = userId("prof.karimi");
        try (PortalClient deputy = as("deputy.cs")) {
            deputy.refused("Class times", SAVE_COURSE, "", "Compilers", "CS410", 1, karimi, "UNDERGRADUATE", 3, 20, false, future(40), "someday", "", "", "");
            String id = deputy.ok(SAVE_COURSE, "", "Compilers", "CS410", 1, karimi, "UNDERGRADUATE", 3, 20, false, future(40),
                    "Thu 08:00-09:30", "", "", "").get("id");
            assertEquals("Compilers", find(deputy.ok(COURSES).table("courses"), "id", id).get("name"));
            deputy.ok(DELETE_COURSE, id);
            assertTrue(deputy.ok(COURSES).table("courses").stream().noneMatch(row -> row.get("id").equals(id)));
            deputy.ok(ADD_STUDENT, "New Person", "student.new", "password1", "new@portal.example", "0900", "123", "26101001",
                    "UNDERGRADUATE", 2026, karimi);
            deputy.refused("taken", ADD_STUDENT, "Other", "student.new", "password1", "", "", "", "26101002", "UNDERGRADUATE", 2026, "");
        }
        try (PortalClient created = new PortalClient(port)) {
            assertNull(created.login("student.new", "password1").getError());
        }
    }

    @Test
    void headManagesProfessorsAndTheDeputy() throws IOException {
        try (PortalClient head = as("head.physics")) {
            String id = head.ok(SAVE_MASTER, "", "New Professor", "prof.new", "password1", "p@portal.example", "0900", "1",
                    "M9001", "12", "PROFESSOR").get("id");
            head.refused("current deputy", SET_DEPUTY, id);
            int deputy = userId("deputy.physics");
            head.ok(REMOVE_DEPUTY, deputy);
            head.ok(SET_DEPUTY, id);
            head.refused("position", DELETE_MASTER, id);
            head.ok(REMOVE_DEPUTY, id);
            head.ok(SET_DEPUTY, deputy);
            head.ok(DELETE_MASTER, id);
            head.refused("another department", DELETE_MASTER, userId("prof.karimi"));
        }
        try (PortalClient removed = new PortalClient(port)) {
            assertEquals("This account can no longer log in.", removed.login("prof.new", "password1").getError());
        }
    }

    @Test
    void demoDataMeetsTheBrief() {
        assertTrue(db(() -> DataManager.all(Department.class).size()) >= 5);
        assertTrue(db(() -> DataManager.all(Department.class).stream().allMatch(department ->
                DataManager.query(Course.class, "from Course where department = :p0", department).size() >= 2)));
        assertEquals(ScoreType.FINAL, db(() -> DataManager.first(Score.class,
                "from Score where course.courseNumber = 'CS101'").getScoreType()));
    }

    @Test
    void manyClientsCanWorkAtTheSameTime() throws Exception {
        String[] users = {"student.sara", "student.omid", "prof.karimi", "prof.moradi", "deputy.cs", "registrar"};
        var failures = new ConcurrentLinkedQueue<Throwable>();
        Thread[] threads = new Thread[users.length];
        for (int i = 0; i < users.length; i++) {
            String user = users[i];
            threads[i] = new Thread(() -> {
                try (PortalClient client = as(user)) {
                    for (int n = 0; n < 20; n++) {
                        client.ok(HOME);
                        client.ok(CHATS);
                    }
                } catch (Throwable e) {
                    failures.add(e);
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertTrue(failures.isEmpty(), failures.toString());
    }
}
