package com.Controller;

import com.Model.CredentialValidator;
import com.Model.SharedData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.lang.*;

public class LoginController {
    @FXML
    private Label alertText;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private VBox loginView;
    @FXML
    private void closeLogin() {
        Stage stage = (Stage) loginView.getScene().getWindow();
        stage.close();
    }
    @FXML
    private static void launchHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/fxml/home.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 430, 220);
            // Create a new stage and set the new scene to it
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onLoginSubmit(){
        String U = username.getText();
        String P = password.getText();
        if (U.isBlank() || P.isBlank()) {
            alertText.setText("Incomplete Credentials!");
            return;
        }
        try {
            String alert = CredentialValidator.verifyCredentials(U, P);
            if (alert == "") {
                System.out.print("Logged In!");
                closeLogin();
                SharedData.username = U;
                launchHome();
            } else
                alertText.setText("Invalid Credentials!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onSignupClick(){
        closeLogin();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/fxml/signup.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 465, 300);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
