package com;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import com.Model.SharedData;

public class TestMain extends Application{
    public void reset() throws IOException{
        start(new Stage());
    }
    @Override
    public void start(Stage stage) throws IOException {
        SharedData.username = "user1";
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/notes/view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 480);
        stage.setTitle("Productivity App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public static void main() throws IOException {
        launch();
    }
}