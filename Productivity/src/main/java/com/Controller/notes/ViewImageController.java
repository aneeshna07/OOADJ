package com.Controller.notes;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
public class ViewImageController {
    @FXML
    private ImageView imageView;
    private int currentIndex = 0;
    public void initialize() {
        imageView.setImage(ViewController.noteImagesList.get(currentIndex));
    }

    @FXML
    private void onPrevButtonClick() {
        if (currentIndex > 0) {
            imageView.setImage(ViewController.noteImagesList.get(--currentIndex));
        }
    }

    @FXML
    private void onNextButtonClick() {
        if (currentIndex < ViewController.noteImagesList.size() - 1) {
            imageView.setImage(ViewController.noteImagesList.get(++currentIndex));
        }
    }

}
