package com.Controller.notes;

import java.io.IOException;
import java.sql.SQLException;

import java.util.*;
import java.util.List;

import com.Main;
import com.Model.Note;
import com.Model.SharedData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ViewController {

    @FXML
    private Label welcomeText;
    @FXML
    private TextField searchNotes;
    @FXML
    private ListView<String> notesListView;

    @FXML
    private EditController editController;
    @FXML
    private CreateController createController;

    protected static List<Image> noteImagesList = new ArrayList<>();
    protected static String selectedNote;
    protected static List<String> notesList = new ArrayList<>(), selectedNotesList = new ArrayList<>();

    @FXML
    private VBox displayNoteVBox;
    @FXML
    private Label displayTitle, displayTags, displayAbout;
    @FXML
    private Button viewImagesButton;
    @FXML
    private ImageView image1, image2, image3;
    @FXML
    private Button deleteButton;

    public void initialize(){
//        SharedData.username = "user1";
        notesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        notesListView.setOnMouseClicked((MouseEvent mouseEvent) -> {
            deleteButton.setDisable(false);
            selectedNotesList = notesListView.getSelectionModel().getSelectedItems();
            selectedNote = selectedNotesList.get(0);
            displayNote();
        });

        welcomeText.setText("User : " + SharedData.username);
        getNotes();
    }
    @FXML
    static void showWarning(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(warningMessage);
        alert.showAndWait();
    }
    @FXML
    private void closeView(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        SharedData.username = null;
        stage.close();
    }
    @FXML
    private void onLogoutClick(ActionEvent event) {
        try {
            closeView(event);
            Main main = new Main();
            main.reset();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onTasksClick(){
        return;
    }
    @FXML
    private void onNewNoteClick() {
        SharedData.primaryStage = (Stage) notesListView.getScene().getWindow();
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/fxml/notes/create.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notes/create.fxml"));
            Parent root = loader.load();
            CreateController createController = loader.getController();
            createController.setParentController(this);
            Scene scene = new Scene(root, 640, 500);

            // Create a new stage for the new window
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(SharedData.primaryStage);
            newStage.setTitle("Create Note");
            // Set the scene on the new stage
            newStage.setScene(scene);
            newStage.setResizable(false);

            // Show the new stage and wait for it to be closed
            newStage.showAndWait();
//            newStage.close();
            SharedData.primaryStage.show();
            onRefreshClick();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void onEditNoteClick() {
        SharedData.primaryStage = (Stage) notesListView.getScene().getWindow();
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/fxml/notes/edit.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notes/edit.fxml"));
            Parent root = loader.load();
            EditController editController = loader.getController();
            editController.setParentController(this);
            Scene scene = new Scene(root, 640, 500);
            // Create a new stage for the new window
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(SharedData.primaryStage);
            newStage.setTitle("Edit Note");
            // Set the scene on the new stage
            newStage.setScene(scene);
            newStage.setResizable(false);
            // Show the new stage and wait for it to be closed
            newStage.showAndWait();
//            newStage.close();
            SharedData.primaryStage.show();
            onRefreshClick();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteNoteClick() {
        if(selectedNotesList.isEmpty()){
            showWarning("No Note Selected!");
            return;
        }
        try {
            com.Model.ConnectSQL.getInstance().deleteNote(selectedNotesList);
            selectedNotesList = new ArrayList<>();

            onRefreshClick();
        } catch (SQLException e) {
            e.printStackTrace();
            showWarning("Connection error!");
        }
    }

    @FXML
    private void onViewImagesClick() {
        SharedData.primaryStage = (Stage) notesListView.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/notes/viewImage.fxml"));
            Scene scene = new Scene(root, 450, 360);
            // Create a new stage for the new window
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(SharedData.primaryStage);
            newStage.setTitle("View Images");
            // Set the scene on the new stage
            newStage.setScene(scene);
            newStage.setResizable(false);
            // Show the new stage and wait for it to be closed
            newStage.showAndWait();
            newStage.close();
            SharedData.primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onSearchByTitleClick() {
        if(searchNotes.getText().isBlank()){
            showWarning("Empty search field!");
            return;
        }
        if(notesList.isEmpty()){
            showWarning("No notes found!");
            return;
        }
        try{
            List<String> resultList = com.Model.ConnectSQL.getInstance().searchNotesByTitle(searchNotes.getText());
            if(resultList.isEmpty()){
                notesListView.getItems().clear();
                showWarning("No notes found!");
                onRefreshClick();
            }
            else {
                notesListView.getItems().clear();
                notesListView.getItems().addAll(resultList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showWarning("Connection error!");
            onRefreshClick();
        }
    }
    @FXML
    private void onSearchByTagClick() {
        if(searchNotes.getText().isBlank()){
            showWarning("Empty search field!");
            return;
        }
        if(notesList.isEmpty()){
            showWarning("No notes found!");
            return;
        }
        try{
            List<String> resultList = com.Model.ConnectSQL.getInstance().searchNotesByTag(searchNotes.getText());
            if(resultList.isEmpty()){
                notesListView.getItems().clear();
                showWarning("No results found!");
                onRefreshClick();
            }
            else {
                notesListView.getItems().clear();
                notesListView.getItems().addAll(resultList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showWarning("Connection Error!");
            onRefreshClick();
        }
    }

    @FXML
    private void onRefreshClick() {
        selectedNotesList = new ArrayList<>();
        searchNotes.clear();
        getNotes();
    }

    @FXML
    void getNotes(){
        try {
            selectedNote = null;
            notesList = com.Model.ConnectSQL.getInstance().searchNotes();
            notesListView.getItems().clear();

            displayNoteVBox.setVisible(false);
            deleteButton.setDisable(true);

            if(notesList.isEmpty()) {
                showWarning("No notes found!");
            }
            else {
                notesListView.getItems().addAll(notesList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showWarning("Connection error!");
        }
    }

    @FXML
    private void displayNote(){
        displayNoteVBox.setVisible(true);
        try {
            Note note = com.Model.ConnectSQL.getInstance().getNote(selectedNote);
            displayTitle.setText(note.getTitle());
            displayTags.setText(note.getTags());
            displayAbout.setText(note.getAbout());

            noteImagesList = note.getImages();
            image1.setImage(noteImagesList.get(0));
            image2.setImage(noteImagesList.get(1));
            image3.setImage(noteImagesList.get(2));

            if(noteImagesList.get(0) == null)
                viewImagesButton.setDisable(true);
            else
                viewImagesButton.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
