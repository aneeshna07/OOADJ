package com;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{
    public void reset() throws IOException{
        start(new Stage());
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 250);
        stage.setTitle("Productivity App");
        stage.setScene(scene);
        stage.show();
    }
    public static void main() throws IOException {
        launch();
    }
}