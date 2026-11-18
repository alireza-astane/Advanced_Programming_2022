package network;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;

import java.io.IOException;

public class DatabaseConnectionChecker extends Thread{
    static Logger log = LogManager.getLogger(DatabaseConnectionChecker.class);

    @Override
    public void run() {
        while (true){
            if (!DataManager.sessionFactory.isOpen()){
                try {
                    log.info("Database disconnected. Shutting server down...");
                    Server.getInstance().serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                }
        }
    }
}
