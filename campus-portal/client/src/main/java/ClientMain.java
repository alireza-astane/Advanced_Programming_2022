import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import network.Client;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ClientMain extends Application {
    static Logger log = LogManager.getLogger(ClientMain.class);
    public static void main(String[] args){
        Configuration.load();
        log.info("Application triggered");
        Thread thread = new Thread(Client.getInstance());
        thread.start();
        launch(args);
        log.info("Application Finished");
    }

    @Override
    public void start(Stage primaryStage){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlFiles.START.path));

                Parent root = null;
        try {
            root = loader.load();
            //log.info("Application Started");
        } catch (IOException e) {
            //log.error("Application Failed");
        }
        assert root != null;
        Client.getInstance().loader=loader;
        Client.getInstance().controller = loader.getController();

        Scene scene = new Scene(root);
        scene.setUserData(loader);
        Client.getInstance().scene=scene;
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
