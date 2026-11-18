package network;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Pinger extends Thread{
    static Logger log = LogManager.getLogger(Pinger.class);
    Client client;

    Request lastPageRequest;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Request getLastPageRequest() {
        return lastPageRequest;
    }

    public void setLastPageRequest(Request lastPageRequest) {
        this.lastPageRequest = lastPageRequest;
    }

    public Pinger(Client client){
        this.client=client;
    }
    @Override
    public void run() {
        while (client.isOnline()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                client.requester.sendRequest(lastPageRequest);
                log.info("Pinging...");
            } catch (InterruptedException ignored) {}
        }
    }

//interupt tp stop and run again //anywhere witch is necessary

}
