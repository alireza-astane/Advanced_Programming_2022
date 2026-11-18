package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Talks to a running server over a real socket, as the desktop client does. */
final class PortalClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    final String token;
    private int nextId = 1;

    PortalClient(int port) throws IOException {
        socket = new Socket("localhost", port);
        socket.setSoTimeout(15000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        token = read().get("authToken");
    }

    Response read() throws IOException {
        String line = Protocol.readLine(in);
        if (line == null) {
            throw new IOException("Connection closed");
        }
        return Protocol.GSON.fromJson(line, Response.class);
    }

    void sendRaw(Request request, String authToken) {
        request.setAuthToken(authToken);
        request.setRequestId(nextId++);
        out.print(Protocol.GSON.toJson(request));
        out.print('\n');
        out.flush();
    }

    Response ask(Request request) throws IOException {
        sendRaw(request, token);
        Response response = read();
        assertEquals(request.getRequestId(), response.getRequestId());
        return response;
    }

    Response ask(RequestType type, Object... args) throws IOException {
        String[] text = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            text[i] = String.valueOf(args[i]);
        }
        return ask(new Request(type, text));
    }

    Response ok(RequestType type, Object... args) throws IOException {
        Response response = ask(type, args);
        assertNull(response.getError(), type.name());
        return response;
    }

    void refused(String expected, RequestType type, Object... args) throws IOException {
        Response response = ask(type, args);
        assertFalse(response.isOk(), type + " should have been refused");
        assertTrue(response.getError().contains(expected), response.getError());
    }

    Response login(String username, String password) throws IOException {
        ok(RequestType.CAPTCHA);
        String code = Server.getInstance().clientWithToken(token).captchaCode();
        return ask(RequestType.LOGIN, username, password, code);
    }

    static Map<String, String> find(List<Map<String, String>> rows, String key, String value) {
        return rows.stream().filter(row -> value.equals(row.get(key))).findFirst().orElseThrow();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
