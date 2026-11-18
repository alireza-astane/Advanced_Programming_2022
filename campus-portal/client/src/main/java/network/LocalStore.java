package network;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** What the client keeps on this computer: settings, saved pages and unsent support messages. */
public final class LocalStore {
    private static final int SAVED_MESSAGES = 10;
    private static final Set<RequestType> SAVED_PAGES = Set.of(RequestType.HOME, RequestType.PROFILE,
            RequestType.WEEKLY_PLAN, RequestType.EXAMS, RequestType.TRANSCRIPT, RequestType.CHATS, RequestType.CHAT);

    private final Path root;

    public LocalStore() {
        this(Path.of(System.getProperty("portal.data", System.getProperty("user.home") + "/.campus-portal")));
    }

    public LocalStore(Path root) {
        this.root = root;
    }

    private static void write(Path file, String text) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String read(Path file) {
        try {
            return Files.exists(file) ? Files.readString(file) : null;
        } catch (IOException e) {
            return null;
        }
    }

    public String setting(String key, String defaultValue) {
        return settings().getProperty(key, defaultValue);
    }

    public void setSetting(String key, String value) {
        Properties settings = settings();
        settings.setProperty(key, value);
        StringBuilder text = new StringBuilder();
        settings.forEach((name, content) -> text.append(name).append('=').append(content).append('\n'));
        write(root.resolve("settings.properties"), text.toString());
    }

    private Properties settings() {
        Properties properties = new Properties();
        String text = read(root.resolve("settings.properties"));
        if (text != null) {
            text.lines().map(line -> line.split("=", 2)).filter(parts -> parts.length == 2)
                    .forEach(parts -> properties.setProperty(parts[0], parts[1]));
        }
        return properties;
    }

    private Path pagePath(String userId, Request request) {
        String name = request.getRequestType() + "-" + String.join("-", request.getArgs());
        return root.resolve("users").resolve(userId).resolve(name.replaceAll("[^A-Za-z0-9_-]", "_") + ".json");
    }

    public void savePage(String userId, Request request, Response response) {
        if (SAVED_PAGES.contains(request.getRequestType())) {
            if (request.getRequestType() == RequestType.CHAT) {
                List<Map<String, String>> messages = response.table("messages");
                messages.subList(0, Math.max(0, messages.size() - SAVED_MESSAGES)).clear();
            }
            write(pagePath(userId, request), Protocol.GSON.toJson(response));
        }
    }

    public Response savedPage(String userId, Request request) {
        String text = read(pagePath(userId, request));
        return text == null ? null : Protocol.GSON.fromJson(text, Response.class);
    }

    private Path queuePath(String userId) {
        return root.resolve("users").resolve(userId).resolve("support-queue.json");
    }

    public List<String> queuedMessages(String userId) {
        String text = read(queuePath(userId));
        if (text == null) {
            return new ArrayList<>();
        }
        return Protocol.GSON.fromJson(text, new TypeToken<ArrayList<String>>() { }.getType());
    }

    public void setQueuedMessages(String userId, List<String> messages) {
        write(queuePath(userId), Protocol.GSON.toJson(messages));
    }
}
