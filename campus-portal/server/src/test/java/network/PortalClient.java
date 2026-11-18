package network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import logic.enums.ResponseType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/** Talks to a running server over a real socket, as the desktop client does. */
final class PortalClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    final String token;

    record Reply(ResponseType type, String json, JsonObject body) {
    }

    PortalClient(int port) throws IOException {
        socket = new Socket("localhost", port);
        socket.setSoTimeout(10000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        token = read().body().get("authToken").getAsString();
    }

    Reply read() throws IOException {
        String line = Protocol.readLine(in);
        if (line == null) {
            throw new IOException("Connection closed");
        }
        JsonObject body = JsonParser.parseString(line).getAsJsonObject();
        return new Reply(ResponseType.valueOf(body.get("responseType").getAsString()), line, body);
    }

    void send(RequestType type, String... args) {
        sendAs(token, type, null, args);
    }

    void sendFile(RequestType type, byte[] file, String... args) {
        sendAs(token, type, file, args);
    }

    void sendAs(String authToken, RequestType type, byte[] file, String... args) {
        Request request = new Request(type);
        request.setAuthToken(authToken);
        request.setFileInByte(file);
        request.setArrayList(new ArrayList<>(Arrays.asList(args)));
        out.print(Protocol.GSON.toJson(request));
        out.print('\n');
        out.flush();
    }

    Reply ask(RequestType type, String... args) throws IOException {
        send(type, args);
        return read();
    }

    String currentCaptcha() {
        return Server.getInstance().clients().stream()
                .filter(client -> client.authToken.equals(token)).findFirst().orElseThrow().captchaCode;
    }

    Reply login(String username, String password) throws IOException {
        ask(RequestType.LOGIN_DATA);
        return ask(RequestType.LOGIN, username, password, currentCaptcha());
    }

    Reply loginWithCaptcha(String username, String password, String code) throws IOException {
        ask(RequestType.LOGIN_DATA);
        return ask(RequestType.LOGIN, username, password, code);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
