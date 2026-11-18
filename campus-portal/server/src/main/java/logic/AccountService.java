package logic;

import database.DataManager;
import database.models.Master;
import database.models.Student;
import database.models.User;
import network.Call;
import network.Captcha;
import network.Passwords;
import network.Problem;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.Duration;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AccountService {
    private static final int MAX_PHOTO_BYTES = 2 * 1024 * 1024;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, Integer> SESSIONS = new ConcurrentHashMap<>();

    private AccountService() {
    }

    public static void register(Routes routes) {
        routes.open(RequestType.CAPTCHA, AccountService::captcha);
        routes.open(RequestType.LOGIN, AccountService::login);
        routes.open(RequestType.RESUME, AccountService::resume);
        routes.on(RequestType.LOGOUT, Routes.EVERYONE, AccountService::logout);
        routes.on(RequestType.CHANGE_PASSWORD, Routes.EVERYONE, AccountService::changePassword);
        routes.on(RequestType.HOME, Routes.EVERYONE, AccountService::home);
        routes.on(RequestType.PROFILE, Routes.EVERYONE, call -> profile(call.user()));
        routes.on(RequestType.UPDATE_CONTACT, Routes.EVERYONE, AccountService::updateContact);
        routes.on(RequestType.UPDATE_PHOTO, Routes.EVERYONE, AccountService::updatePhoto);
    }

    private static Response captcha(Call call) {
        Captcha captcha = Captcha.generate();
        call.client().setCaptchaCode(Configuration.setting("portal.test.captcha", "PORTAL_TEST_CAPTCHA", captcha.code()));
        return new Response().setFile(captcha.image());
    }

    private static Response login(Call call) {
        String expected = call.client().captchaCode();
        call.client().setCaptchaCode(null);
        if (expected == null || !expected.equals(call.text(2))) {
            throw new Problem("Wrong captcha code.");
        }
        User user = DataManager.first(User.class, "from User where username = :p0", call.text(0));
        if (user == null || !Passwords.matches(call.text(1), user.getPassword())) {
            throw new Problem("Wrong username or password.");
        }
        if (!user.isCanLogin()) {
            throw new Problem("This account can no longer log in.");
        }
        LocalDateTime previous = user.getLastLogin();
        boolean expired = Duration.between(previous, LocalDateTime.now()).toHours()
                >= Configuration.number("loginTimeDifference");
        call.client().logIn(user, previous, expired);
        if (!expired) {
            user.setLastLogin(LocalDateTime.now());
        }
        Response response = identity(user).put("mustChangePassword", expired);
        return expired ? response : withSession(response, user);
    }

    private static Response withSession(Response response, User user) {
        byte[] key = new byte[32];
        RANDOM.nextBytes(key);
        String sessionKey = HexFormat.of().formatHex(key);
        SESSIONS.put(sessionKey, user.getId());
        return response.put("sessionKey", sessionKey);
    }

    /** Lets a client that lost its connection continue without logging in again. */
    private static Response resume(Call call) {
        Integer userId = SESSIONS.get(call.text(0));
        if (userId == null) {
            throw new Problem("Please log in again.");
        }
        User user = DataManager.get(User.class, userId);
        if (!user.isCanLogin()) {
            throw new Problem("This account can no longer log in.");
        }
        call.client().logIn(user, user.getLastLogin(), false);
        return identity(user);
    }

    private static Response identity(User user) {
        Response response = new Response().put("userId", user.getId()).put("role", user.getUserType())
                .put("fullName", user.getFullName());
        if (user.getMaster() != null) {
            response.put("masterType", user.getMaster().getMasterType());
        }
        if (user.getStudent() != null) {
            response.put("studentType", user.getStudent().getStudentType());
        }
        return response;
    }

    private static Response logout(Call call) {
        SESSIONS.remove(call.text(0));
        call.client().logOut();
        return null;
    }

    private static Response changePassword(Call call) {
        User user = call.user();
        if (!Passwords.matches(call.text(0), user.getPassword())) {
            throw new Problem("The current password is wrong.");
        }
        if (!call.text(1).equals(call.text(2))) {
            throw new Problem("The new passwords do not match.");
        }
        if (call.text(1).equals(call.text(0))) {
            throw new Problem("Choose a password different from the current one.");
        }
        user.setPassword(Passwords.hash(call.text(1)));
        user.setLastLogin(LocalDateTime.now());
        call.client().passwordChanged();
        return withSession(identity(user), user);
    }

    private static Response home(Call call) {
        User user = call.user();
        Response response = identity(user).put("email", user.getEmail())
                .put("now", Format.time(LocalDateTime.now()))
                .put("lastLogin", Format.time(call.client().previousLogin()))
                .setFile(user.getPhoto());
        if (user.getStudent() != null) {
            Student student = user.getStudent();
            Master supervisor = student.getSupervisor();
            response.put("state", student.getState())
                    .put("supervisor", supervisor == null ? "" : supervisor.getUser().getFullName())
                    .put("allowedToRegister", student.isAllowedToRegister())
                    .put("selectionStart", Format.time(student.getSelectionStart()))
                    .put("selectionOpen", UnitSelectionService.isOpen(student));
        }
        return response;
    }

    static Response profile(User user) {
        Response response = new Response().put("userId", user.getId()).put("fullName", user.getFullName())
                .put("nationalNumber", user.getNationalNumber()).put("phone", user.getPhone())
                .put("email", user.getEmail()).setFile(user.getPhoto());
        if (user.getStudent() != null) {
            addStudent(response, user.getStudent());
        }
        if (user.getMaster() != null) {
            Master master = user.getMaster();
            response.put("number", master.getMasterNumber())
                    .put("department", master.getDepartment().getDepartmentType().title)
                    .put("roomNumber", master.getRoomNumber()).put("grade", master.getGrade());
        }
        return response;
    }

    private static void addStudent(Response response, Student student) {
        Master supervisor = student.getSupervisor();
        response.put("number", student.getStudentNumber())
                .put("average", Format.score(People.average(student)))
                .put("department", student.getDepartment().getDepartmentType().title)
                .put("supervisor", supervisor == null ? "" : supervisor.getUser().getFullName())
                .put("entryYear", student.getEntryYear()).put("level", student.getStudentType())
                .put("state", student.getState());
    }

    private static Response updateContact(Call call) {
        String email = call.required(0, "Email");
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new Problem("That email address does not look right.");
        }
        call.user().setEmail(email);
        call.user().setPhone(call.required(1, "Phone number"));
        return null;
    }

    private static Response updatePhoto(Call call) {
        if (call.file() != null && call.file().length > MAX_PHOTO_BYTES) {
            throw new Problem("Photos can be at most 2 MB.");
        }
        call.user().setPhoto(call.file());
        return null;
    }
}
