package com.Controller;

import com.Main;
import com.Model.ConnectSQL;
import com.Model.CredentialValidator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupController {
    @FXML
    private VBox signupView;
    @FXML
    private Label alertText;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;

    @FXML
    private void closeSignup() {
        Stage stage = (Stage) signupView.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onLoginClick(){
        closeSignup();
        try {
            Main main = new Main();
            main.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onSignupSubmit() {
        String U = username.getText();
        String P = password.getText();
        String CP = confirmPassword.getText();
        if(U.isBlank() || P.isBlank() || CP.isBlank()){
            alertText.setText("Incomplete Credentials!");
            return;
        }
        if(!P.equals(CP)){
            alertText.setText("Passwords do not match!");
            return;
        }
        String alert = "";
        try{
            alert = CredentialValidator.validateUsername(U);
            if(alert != ""){
                alertText.setText(alert);
                return;
            }
            alert = CredentialValidator.validatePassword(P);
            if(alert != ""){
                alertText.setText(alert);
                return;
            }
            P = CredentialValidator.encryptPassword(P);
            ConnectSQL.insertCredentials(U, P);
            alertText.setText("User registered. Proceed to Login!");

        }catch (Exception e){
            alertText.setText("Error in Database Connection!");
            e.printStackTrace();
        }
    }
}