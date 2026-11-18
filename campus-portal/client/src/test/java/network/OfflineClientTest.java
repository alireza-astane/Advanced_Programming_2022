package network;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfflineClientTest {

    @AfterEach
    void clearPort() {
        System.clearProperty("portal.port");
    }

    @Test
    void reportsAMissingServerAndConnectsOnceItIsUp() throws Exception {
        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        System.setProperty("portal.port", String.valueOf(port));
        List<String> messages = new ArrayList<>();
        Client client = new Client();
        client.notifier = messages::add;

        assertFalse(client.connect());
        client.getRequester().requestLogin("student.sara", "demo1234", "1234");
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("Cannot reach the portal server"));
        assertFalse(client.isOnline());

        try (ServerSocket server = new ServerSocket(port)) {
            client.getRequester().requestLogin("student.sara", "demo1234", "1234");
            assertTrue(client.isOnline());
            assertEquals(1, messages.size());
        }
    }
}
