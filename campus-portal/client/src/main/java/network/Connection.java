package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** One socket to the server. Each request gets a future that completes with its own response. */
public final class Connection {
    private static final int TIMEOUT_MILLIS = 3000;

    private final Socket socket = new Socket();
    private final Map<Integer, CompletableFuture<Response>> pending = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final CompletableFuture<String> authToken = new CompletableFuture<>();
    private final Consumer<String> serverMessages;
    private final Runnable onClosed;
    private PrintWriter writer;
    private volatile boolean open;

    private Connection(Consumer<String> serverMessages, Runnable onClosed) {
        this.serverMessages = serverMessages;
        this.onClosed = onClosed;
    }

    public static Connection open(String host, int port, Consumer<String> serverMessages, Runnable onClosed)
            throws IOException {
        Connection connection = new Connection(serverMessages, onClosed);
        connection.start(host, port);
        return connection;
    }

    private void start(String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_MILLIS);
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        open = true;
        Thread thread = new Thread(() -> readLoop(reader), "server-responses");
        thread.setDaemon(true);
        thread.start();
        try {
            authToken.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            close();
            throw new IOException("The server did not answer.", e);
        }
    }

    private void readLoop(BufferedReader reader) {
        try {
            String line;
            while ((line = Protocol.readLine(reader)) != null) {
                deliver(Protocol.GSON.fromJson(line, Response.class));
            }
        } catch (IOException ignored) {
            // treated the same as the server closing the connection
        }
        boolean wasOpen = open;
        close();
        pending.values().forEach(future -> future.completeExceptionally(new IOException("Connection lost")));
        pending.clear();
        if (wasOpen) {
            onClosed.run();
        }
    }

    private void deliver(Response response) {
        if (response.getRequestId() != 0) {
            CompletableFuture<Response> future = pending.remove(response.getRequestId());
            if (future != null) {
                future.complete(response);
            }
        } else if (!response.get("authToken").isEmpty()) {
            authToken.complete(response.get("authToken"));
        } else if (!response.isOk()) {
            serverMessages.accept(response.getError());
        }
    }

    public boolean isOpen() {
        return open;
    }

    public CompletableFuture<Response> send(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        if (!open) {
            future.completeExceptionally(new IOException("Not connected"));
            return future;
        }
        request.setAuthToken(authToken.join());
        request.setRequestId(nextId.getAndIncrement());
        pending.put(request.getRequestId(), future);
        synchronized (this) {
            writer.print(Protocol.GSON.toJson(request));
            writer.print('\n');
            writer.flush();
        }
        return future;
    }

    public void close() {
        open = false;
        try {
            socket.close();
        } catch (IOException ignored) {
            // already closed
        }
    }
}
