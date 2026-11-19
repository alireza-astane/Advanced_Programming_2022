package network;

import database.DataManager;
import logic.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseLossTest {

    @Test
    void clientsAreWarnedAndDisconnectedWhenTheDatabaseGoesAway() throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("portal.db.url", "jdbc:h2:mem:portal-loss;DB_CLOSE_DELAY=-1");
        Configuration.load();
        Server server = Server.getInstance();
        server.start(0);
        new Thread(server::acceptClients).start();
        try (PortalClient client = new PortalClient(server.getPort())) {
            DataManager.close();
            assertEquals("The server lost its database connection.", client.read().getError());
            assertThrows(java.io.IOException.class, client::read);
        }
    }
}
