package com.Controller.notes;

import com.Model.ConnectSQL;
import com.Model.Note;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateController {
    private List<ImageView> imageViews = new ArrayList<>();
    private List<File> imageFiles = new ArrayList<>();

    @FXML
    private TextField titleInput, tagsInput;
    @FXML
    private TextArea aboutInput;
    @FXML
    private HBox imageViewHBox;
    @FXML
    private Button browseButton;

    private ViewController viewController;
    public void setParentController(ViewController viewController){
        this.viewController = viewController;
    }
    @FXML
    private void onBrowseClick(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        Stage stage = (Stage) imageViewHBox.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if(selectedFiles == null || selectedFiles.isEmpty())
            return;

        if(selectedFiles.size() + imageFiles.size() > 3){
            ViewController.showWarning("Maximum 3 images allowed!");
            return;
        }

        for (File file : selectedFiles) {
                imageFiles.add(file);
                ImageView imageView = createImageView(file);
                imageViews.add(imageView);
                imageViewHBox.getChildren().add(imageView);
        }
        if(imageFiles.size() == 3){
            browseButton.setDisable(true);
        }
    }
    private ImageView createImageView(File imageFile) {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(125);
        imageView.setPreserveRatio(true);
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);

        Button deleteButton = new Button("X");
        deleteButton.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");

        HBox hBox = new HBox(deleteButton, imageView);
        imageViewHBox.getChildren().add(hBox);
        hBox.setSpacing(10);
        imageViews.add(imageView);

        deleteButton.setOnAction(event -> {
            browseButton.setDisable(false);
            imageFiles.remove(imageFile);
            imageViews.remove(imageView);
            imageViewHBox.getChildren().remove(imageView);
            imageViewHBox.getChildren().remove(deleteButton);
            imageViewHBox.getChildren().remove(hBox);
        });
        return imageView;
    }

    @FXML
    private void onClearClick(){
        titleInput.clear();
        tagsInput.clear();
        aboutInput.clear();
        imageFiles.clear();
        imageViews.clear();
        imageViewHBox.getChildren().clear();
        browseButton.setDisable(false);
    }

    @FXML
    private void onSubmitClick(){
        if(titleInput.getText().isBlank() || aboutInput.getText().isBlank()){
            ViewController.showWarning("No empty fields!");
            return;
        }

        if(ViewController.notesList.contains(titleInput.getText())){
            ViewController.showWarning("Title already exists!");
            return;
        }
        try{
            Note sendNote = new Note(titleInput.getText(), tagsInput.getText(), aboutInput.getText(), null, imageFiles);
            ConnectSQL.getInstance().insertNote(sendNote);
//            ViewController.notesList.add(titleInput.getText());
            viewController.getNotes();

        }catch (Exception e){
            e.printStackTrace();
            ViewController.showWarning("Error in inserting data!");
            return;
        }
        ViewController.showWarning("Note created successfully!");
        onClearClick();
    }

}
