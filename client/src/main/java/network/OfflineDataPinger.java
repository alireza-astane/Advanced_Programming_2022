package network;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class OfflineDataPinger extends Thread{
    static Logger log = LogManager.getLogger(network.Pinger.class);
    Client client;

    Request lastPageRequest;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public OfflineDataPinger(Client client){
        this.client=client;
    }
    @Override
    public void run() {
        while (client.isOnline()) {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
                client.requester.sendRequest(new Request(RequestType.PING));
                log.info("Offline data Pinging...");
            } catch (InterruptedException ignored) {}
        }
    }



    }
