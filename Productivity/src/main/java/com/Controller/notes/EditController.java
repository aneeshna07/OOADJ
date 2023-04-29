package com.Controller.notes;

import com.Model.ConnectSQL;
import com.Model.Note;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.text.View;

public class EditController {
    private List<ImageView> imageViews = new ArrayList<>();
    private List<File> imageFiles = new ArrayList<>();

//    private final ViewController viewController = new ViewController();

    private ViewController viewController;
    private Boolean isModified = false;
    @FXML
    private TextField displayTitle, displayTags;
    @FXML
    private TextArea displayAbout;
    @FXML
    private HBox imageViewHBox;
    @FXML
    private Button browseButton;

    public void setParentController(ViewController viewController) {
        this.viewController = viewController;
    }
    @FXML
    private void initialize() throws SQLException {

//        SharedData.username = "user1";
//        ViewController.selectedNote = "hello";
//        ViewController.notesList = ConnectSQL.getInstance().searchNotes();

        imageViewHBox.getChildren().clear();
        try {
            Note note = ConnectSQL.getInstance().getNote(ViewController.selectedNote);
            displayTitle.setText(note.getTitle());
            displayTags.setText(note.getTags());
            displayAbout.setText(note.getAbout());

            int i = 1;
            File imageFile;
            for (Image image : note.getImages()) {
                if(image == null)
                    break;
                imageFile = imageToFile(image, "image" + i++ );
                addImageView(imageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayTitle.textProperty().addListener((observable, oldValue, newValue) -> { isModified = true; });
        displayTags.textProperty().addListener((observable, oldValue, newValue) -> { isModified = true; });
        displayAbout.textProperty().addListener((observable, oldValue, newValue) -> { isModified = true; });
        imageViewHBox.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    isModified = true;
                    break;
                }
            }
        });
    }

    @FXML
    private File imageToFile(Image image, String fileName) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File file = File.createTempFile(fileName, ".png");
        ImageIO.write(bufferedImage, "png", file);
        return file;
    }

    @FXML
    private void addImageView(File file){
        imageFiles.add(file);
        ImageView imageView = createImageView(file);
        imageViews.add(imageView);
        imageViewHBox.getChildren().add(imageView);
    }

    @FXML
    private void onBrowseClick(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) imageViewHBox.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if(selectedFiles == null || selectedFiles.isEmpty()){return;}

        if(selectedFiles.size() + imageFiles.size() > 3){
            ViewController.showWarning("Maximum 3 images allowed!");
            return;
        }

        for (File file : selectedFiles) {
            addImageView(file);
        }
        if(imageFiles.size() == 3){
            browseButton.setDisable(true);
        }
    }

    @FXML
    private ImageView createImageView(File imageFile) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
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
    private void onRefreshClick() {
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSubmitClick(){
        if(!isModified){
            ViewController.showWarning("No changes made!");
            return;
        }
        if(displayTitle.getText().isBlank() || displayAbout.getText().isBlank()){
            ViewController.showWarning("No empty fields!");
            return;
        }

        if(!displayTitle.getText().equals(ViewController.selectedNote) && ViewController.notesList.contains(displayTitle.getText())){
            ViewController.showWarning("Title already exists!");
            return;
        }
        try{
            Note sendNote = new Note(displayTitle.getText(), displayTags.getText(), displayAbout.getText(), null, imageFiles);
            ConnectSQL.getInstance().updateNote(ViewController.selectedNote, sendNote);
            if(!displayTitle.getText().equals(ViewController.selectedNote))
                ViewController.notesList.set(ViewController.notesList.indexOf(ViewController.selectedNote), displayTitle.getText());
            viewController.getNotes();
            ViewController.selectedNote = displayTitle.getText();

        }catch(Exception e){
            e.printStackTrace();
//            ViewController.selectedNote = note.getTitle();
            ViewController.showWarning("Error in inserting data!");
            return;
        }
        ViewController.showWarning("Note edited successfully!");
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
