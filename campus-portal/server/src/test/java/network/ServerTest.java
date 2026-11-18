package network;

import logic.Configuration;
import logic.enums.ResponseType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serverDataBase.DataManager;
import serverDataBase.DemoData;
import serverDataBase.models.Message;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

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
        assertNotEquals("Wrong username or password", result(client.login(username, PASSWORD)));
        return client;
    }

    private static String result(PortalClient.Reply reply) {
        return reply.body().has("result") ? reply.body().get("result").getAsString() : null;
    }

    private static <T> T db(Function<Void, T> query) {
        return DataManager.read(() -> query.apply(null));
    }

    private static int userId(String username) {
        return db(v -> DataManager.getUserByUserName(username).getId());
    }

    private static int courseId(String number) {
        return db(v -> DataManager.getCourses().stream()
                .filter(c -> c.getCourseNumber().equals(number)).findFirst().orElseThrow().getCourseId());
    }

    private static void assertDenied(PortalClient client, RequestType type, String... args) throws IOException {
        assertEquals(ResponseType.ERROR, client.ask(type, args).type(), type.name());
    }

    @Test
    void correctLoginOpensTheRightMenu() throws IOException {
        Map<String, String> menus = Map.of("student.sara", "StudentMenu", "prof.karimi", "MasterMenu",
                "deputy.cs", "AssistantMenu", "admin", "AdminMenu", "registrar", "MohseniMenu");
        for (var entry : menus.entrySet()) {
            try (PortalClient client = new PortalClient(port)) {
                assertEquals(entry.getValue(), result(client.login(entry.getKey(), PASSWORD)));
            }
        }
    }

    @Test
    void wrongPasswordAndUnknownUserAreRejected() throws IOException {
        try (PortalClient client = new PortalClient(port)) {
            assertEquals("Wrong username or password", result(client.login("student.sara", "not-the-password")));
            assertEquals("Wrong username or password", result(client.login("nobody", PASSWORD)));
            assertDenied(client, STUDENT_MAIN_MENU);
        }
    }

    @Test
    void wrongCaptchaIsRejectedAndEachCodeWorksOnce() throws IOException {
        try (PortalClient client = new PortalClient(port)) {
            assertEquals("Wrong captcha code", result(client.loginWithCaptcha("student.sara", PASSWORD, "x")));
            assertDenied(client, STUDENT_MAIN_MENU);
            client.ask(LOGIN_DATA);
            String used = client.currentCaptcha();
            assertEquals("Wrong username or password", result(client.ask(LOGIN, "student.sara", "wrong", used)));
            assertEquals("Wrong captcha code", result(client.ask(LOGIN, "student.sara", PASSWORD, used)));
        }
    }

    @Test
    void forgedTokenIsIgnored() throws IOException {
        try (PortalClient victim = as("student.sara"); PortalClient attacker = new PortalClient(port)) {
            attacker.sendAs(victim.token, STUDENT_MAIN_MENU, null);
            assertEquals(ResponseType.LOGIN_DATA, attacker.ask(LOGIN_DATA).type());
        }
    }

    @Test
    void passwordsAreStoredHashedAndNeverSent() throws IOException {
        String stored = db(v -> DataManager.getUserByUserName("student.sara").getUserPassword());
        assertTrue(stored.startsWith("$2"));
        try (PortalClient client = as("registrar")) {
            String json = client.ask(MOHSENI_STUDENT_INFO, String.valueOf(userId("student.sara"))).json();
            assertFalse(json.contains(stored));
            assertFalse(json.contains("$2a$"));
        }
    }

    @Test
    void passwordCanBeChanged() throws IOException {
        try (PortalClient client = as("student.lena")) {
            assertEquals("WRONG_PASS", result(client.ask(CHANGE_PASSWORD, "nope", "newpass123", "newpass123")));
            assertEquals("NOT_SAME", result(client.ask(CHANGE_PASSWORD, PASSWORD, "newpass123", "other")));
            assertEquals("CHANGED", result(client.ask(CHANGE_PASSWORD, PASSWORD, "newpass123", "newpass123")));
            assertEquals("CHANGED", result(client.ask(CHANGE_PASSWORD, "newpass123", PASSWORD, PASSWORD)));
        }
    }

    @Test
    void rolesCannotUseEachOthersRequests() throws IOException {
        try (PortalClient student = as("student.omid")) {
            assertDenied(student, ADD_COURSE, "a");
            assertDenied(student, SET_SCORE_VALUE, "1", "20");
            assertDenied(student, MOHSENI_STUDENT_SEARCH_DATA, "", "", "");
            assertDenied(student, ADMIN_MENU_DATA);
        }
        try (PortalClient master = as("prof.karimi")) {
            assertDenied(master, STUDENT_MAIN_MENU);
            assertDenied(master, SET_UNIT_SELECTION_TIME, "ALL", "0", "2030-01-01T10:00");
        }
    }

    @Test
    void usersCannotReachOtherPeoplesRecords() throws IOException {
        int structures = courseId("CS201");
        int databases = courseId("CS320");
        int chat = db(v -> DataManager.getUserByUserName("student.sara").getUserChats().get(0).getChatId());
        int omidScore = db(v -> DataManager.getUserByUserName("student.omid").getStudent().getStudentScores().get(0).getScoreId());
        try (PortalClient outsider = as("student.reza")) {
            assertDenied(outsider, CHAT_DATA, String.valueOf(chat));
            assertDenied(outsider, CW_COURSE_PAGE_DATA, String.valueOf(structures));
            assertDenied(outsider, SCORE_PROTESTATION, String.valueOf(omidScore), "change it");
        }
        try (PortalClient otherTeacher = as("prof.karimi")) {
            assertDenied(otherTeacher, VERIFY_SCORES, String.valueOf(databases));
            assertDenied(otherTeacher, MASTER_TEMPORARY_SCORE_DATA, String.valueOf(databases));
        }
        try (PortalClient otherDeputy = as("deputy.math")) {
            assertDenied(otherDeputy, DELETE_COURSE, String.valueOf(structures));
        }
    }

    @Test
    void everyPageOpensForItsRole() throws IOException {
        int structures = courseId("CS201");
        int sara = userId("student.sara");
        int exercise = db(v -> DataManager.getCourseById(structures).getCourseExercises().get(0).getExerciseId());
        int notes = db(v -> DataManager.getCourseById(structures).getCourseEducationalContexts().get(0).getEducationalContextId());
        int chat = db(v -> DataManager.getUserById(sara).getUserChats().get(0).getChatId());
        try (PortalClient c = as("student.sara")) {
            for (RequestType type : new RequestType[]{STUDENT_MAIN_MENU, STUDENT_PROFILE_DATA, GENERAL_PROFILE_DATA,
                    WEEKLY_PLAN, EXAMS_TIME, MESSENGER_MENU, ADD_CHAT_DATA, UNIREQUESTS_DATA, CW_MENU_DATA,
                    STUDENT_TEMPORARY_SCORE_DATA, STUDENT_GENERAL_CALENDER_DATA, UNIT_SELECTION_DATA,
                    EDUCATIONAL_SITUATION_TO_STUDENT_DATA, CHANGE_PASS_DATA, PING}) {
                assertPage(c, type);
            }
            assertPage(c, CHAT_DATA, String.valueOf(chat));
            assertPage(c, CW_COURSE_PAGE_DATA, String.valueOf(structures));
            assertPage(c, CW_EDUCATIONAL_CONTEXT, String.valueOf(notes));
            assertPage(c, STUDENT_EXERCISE_DATA, String.valueOf(exercise));
            assertPage(c, COURSE_LIST_DATA, "", "", "");
            assertPage(c, MASTER_LIST_DATA, "", "", "");
        }
        try (PortalClient c = as("prof.karimi")) {
            for (RequestType type : new RequestType[]{MASTER_MAIN_MENU, MASTER_PROFILE_DATA, WEEKLY_PLAN, EXAMS_TIME,
                    MESSENGER_MENU, ADD_CHAT_DATA, UNIREQUESTS_DATA, CW_MENU_DATA, MASTER_GENERAL_CALENDER_DATA, PING}) {
                assertPage(c, type);
            }
            assertPage(c, MASTER_TEMPORARY_SCORE_DATA, String.valueOf(structures));
            assertPage(c, CW_MASTER_EXERCISE_DATA, String.valueOf(exercise));
        }
        try (PortalClient c = as("deputy.cs")) {
            for (RequestType type : new RequestType[]{ASSISTANT_MAIN_MENU, MASTER_PROFILE_DATA, UNIREQUESTS_DATA,
                    CHOOOSE_UNIT_SELECTION_DATA, PING}) {
                assertPage(c, type);
            }
            assertPage(c, EDIT_COURSE_DATA, String.valueOf(structures));
            assertPage(c, ASSISTANT_TEMPORARY_SCORE_DATA, "", "", "");
            assertPage(c, EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA, String.valueOf(sara));
        }
        try (PortalClient c = as("head.cs")) {
            assertPage(c, EDIT_MASTER_DATA, String.valueOf(userId("prof.karimi")));
        }
        try (PortalClient c = as("registrar")) {
            assertPage(c, MOHSENI_MENU_DATA);
            assertPage(c, MOHSENI_STUDENT_SEARCH_DATA, "", "", "");
            assertPage(c, MOHSENI_STUDENT_INFO, String.valueOf(sara));
        }
        try (PortalClient c = as("admin")) {
            assertPage(c, ADMIN_MENU_DATA);
            assertPage(c, MESSENGER_MENU);
        }
    }

    @Test
    void openingPagesChangesNothing() throws IOException {
        int chat = db(v -> DataManager.getUserByUserName("student.sara").getUserChats().get(0).getChatId());
        try (PortalClient client = as("student.sara")) {
            assertPage(client, MESSENGER_MENU);
            assertPage(client, CHAT_DATA, String.valueOf(chat));
        }
        assertEquals(2, (int) db(v -> DataManager.getChatById(chat).getChatUsers().size()));
    }

    private static void assertPage(PortalClient client, RequestType type, String... args) throws IOException {
        PortalClient.Reply reply = client.ask(type, args);
        assertNotEquals(ResponseType.ERROR, reply.type(), type + ": " + reply.json());
    }

    @Test
    void scoresFlowFromTeacherToStudent() throws IOException {
        int structures = courseId("CS201");
        int score = db(v -> DataManager.getCourseById(structures).getScores().stream()
                .filter(s -> s.getScoreStudentId() == userId("student.sara")).findFirst().orElseThrow().getScoreId());
        try (PortalClient teacher = as("prof.karimi")) {
            teacher.send(SET_SCORE_VALUE, String.valueOf(score), "17.3");
            teacher.send(VERIFY_SCORES, String.valueOf(structures));
            assertPage(teacher, MASTER_MAIN_MENU);
        }
        assertEquals(17.25, db(v -> DataManager.getScoreById(score).getScoreValue()));
        assertEquals("VERIFIED", db(v -> DataManager.getScoreById(score).getScoreType().name()));
    }

    @Test
    void requestsReachTheResponsibleUserAndCanBeAnswered() throws IOException {
        int deputy = userId("deputy.cs");
        try (PortalClient student = as("student.omid")) {
            student.send(WITHDRAW_REQUEST);
            assertPage(student, STUDENT_MAIN_MENU);
        }
        int request = db(v -> DataManager.getUserById(deputy).getInUniRequests().get(0).getUniRequestId());
        try (PortalClient other = as("deputy.math")) {
            assertDenied(other, ANSWER_UNIREQUEST, String.valueOf(request), "true");
        }
        try (PortalClient answerer = as("deputy.cs")) {
            answerer.send(ANSWER_UNIREQUEST, String.valueOf(request), "false");
            assertPage(answerer, ASSISTANT_MAIN_MENU);
        }
        assertEquals("REJECTED", db(v -> DataManager.getUniRequestById(request).getUniRequestResult().name()));
    }

    @Test
    void messagesAndFilesAreStored() throws IOException {
        int chat = db(v -> DataManager.getUserByUserName("student.sara").getUserChats().get(0).getChatId());
        byte[] file = {1, 2, 3, 4};
        try (PortalClient sara = as("student.sara")) {
            sara.sendFile(REQUEST_SEND_MESSEGE, file, String.valueOf(chat), "notes attached");
            assertPage(sara, MESSENGER_MENU);
        }
        Message message = db(v -> {
            var messages = DataManager.getChatById(chat).getMessages();
            return messages.get(messages.size() - 1);
        });
        assertEquals("notes attached", message.getMessageContent());
        assertArrayEquals(file, message.getMessageFile());
    }

    @Test
    void twoClientsCanWorkAtTheSameTime() throws Exception {
        Thread[] threads = new Thread[6];
        var failures = new java.util.concurrent.ConcurrentLinkedQueue<Throwable>();
        String[] users = {"student.sara", "student.omid", "prof.karimi", "prof.moradi", "deputy.cs", "registrar"};
        RequestType[] pages = {MESSENGER_MENU, MESSENGER_MENU, MESSENGER_MENU, MESSENGER_MENU, MESSENGER_MENU, MESSENGER_MENU};
        for (int i = 0; i < threads.length; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                try (PortalClient client = as(users[index])) {
                    for (int n = 0; n < 15; n++) {
                        assertPage(client, pages[index]);
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
