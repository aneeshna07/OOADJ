package com.Controller;

import com.Main;
import com.Model.SharedData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class HomeController {
    @FXML
    private Label welcomeText;

    public void initialize() {
        String username = SharedData.username;
        if(username != null)
            welcomeText.setText("Welcome " + username + "!");
    }
    @FXML
    private Button logout;

    @FXML
    private void closeHome() {
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onLogoutClick() throws IOException {
        SharedData.username = null;
        closeHome();
        Main main = new Main();
        main.reset();
    }

    @FXML
    protected void onNotesClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/notes/view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 480);
        Stage stage = new Stage();
        stage.setTitle("Notes-Home");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        closeHome();
    }
    @FXML
    protected void onTasksClick(ActionEvent actionEvent){}

}
