package GuiControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXDatePicker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.LoginData;
import network.Client;
import network.Request;
import network.RequestType;
import tornadofx.control.DateTimePicker;


import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class LoginGui extends Gui implements Initializable{
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    public LoginData loginData;
    public AnchorPane BackGround;
    public Button LoginButton;
    public TextField NameField;
    public TextField PassField;
    public TextField CaptchaField;
    public Button CaptchaButton;
    public ImageView CaptchaIcon;
    public Label CaptchaImage;
    public Label ErrorLabel;
    public Button ExitButton;








    public void login(ActionEvent actionEvent) {
        Client.getInstance().getRequester().requestLogin(NameField.getText(),PassField.getText(),CaptchaField.getText());
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void SetCaptcha(MouseEvent mouseEvent) {
        Client.getInstance().getRequester().requestPageData(RequestType.LOGIN_DATA);
    }

    public void setLoginData(LoginData loginData){
        this.loginData = loginData;
        Platform.runLater(this::setCaptcha);
    }

    public LoginData getLoginData() {
        return loginData;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }


    public void setCaptcha(){
        try {
            if(loginData.getCaptchaImageFile()!=null){
                File outputFile = new File("src//main//resources//Image//Captcha//captcha.jpg");
                FileOutputStream fos = new FileOutputStream(outputFile) ;
                fos.write(loginData.getCaptchaImageFile());
                fos.flush();
                fos.close();
                InputStream inputStream = new FileInputStream(outputFile);
                ImageView imageView = new ImageView(new Image(inputStream));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        CaptchaImage.setGraphic(imageView);
                    }
                });
            }

            if(loginData.getResult()!=null) {
                switch (loginData.getResult()) {
                    case "AdminMenu" -> {
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage, FxmlFiles.ADMIN_MENU);
                            }
                        });
                    }
                    case "MohseniMenu" -> {
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage, FxmlFiles.MOHSENI_MENU);
                            }
                        });
                    }
                    case "StudentMenu" -> {
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
                            }
                        });
                    }
                    case "MasterMenu" ->{
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage, FxmlFiles.MASTER_MENU);
                            }
                        });
                    }
                    case "AssistantMenu" ->{
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage, FxmlFiles.ASSISTANT_MENU);
                            }
                        });
                    }
                    case "ChangePass" -> {
                        Stage stage = (Stage) LoginButton.getScene().getWindow();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Gui.setStage(stage,FxmlFiles.CHANGE_PASSWORD);
                            }
                        });

                    }
                    default ->Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ErrorLabel.setText(loginData.getResult());
                        }
                    });

                }

                }


        } catch (IOException ignored) {
        }
    }


    @Override
    public void update(String input) {
        setLoginData(gson.fromJson(input, LoginData.class));
        setCaptcha();
    }
}

