package network;

import logic.enums.UserType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/** Connects each request type to its handler and to the roles allowed to send it. */
public final class Routes {
    public static final Set<UserType> STUDENT = Set.of(UserType.STUDENT);
    public static final Set<UserType> DEPUTY = Set.of(UserType.ASSISTANT);
    public static final Set<UserType> STAFF = Set.of(UserType.MASTER, UserType.ASSISTANT);
    public static final Set<UserType> UNIVERSITY = Set.of(UserType.STUDENT, UserType.MASTER, UserType.ASSISTANT);
    public static final Set<UserType> REGISTRAR = Set.of(UserType.REGISTRAR);
    public static final Set<UserType> EVERYONE = Set.of(UserType.values());

    public interface Handler {
        Response handle(Call call);
    }

    record Route(Set<UserType> roles, Handler handler) {
    }

    private final Map<RequestType, Route> routes = new EnumMap<>(RequestType.class);

    public void open(RequestType type, Handler handler) {
        routes.put(type, new Route(null, handler));
    }

    public void on(RequestType type, Set<UserType> roles, Handler handler) {
        routes.put(type, new Route(roles, handler));
    }

    Route get(RequestType type) {
        return routes.get(type);
    }
}
