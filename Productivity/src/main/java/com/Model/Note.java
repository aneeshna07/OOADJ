package com.Model;

import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class Note{
    private String title, tags, about;
    private List<Image> images;
    private List<File> files;
    public Note(String title, String tags, String about, List<Image> images, List<File> files) {
        this.title = title;
        this.tags = tags;
        this.about = about;
        this.images = images;
        this.files = files;
    }

    public String getTitle() {
        return title;
    }
    public String getTags() {
        return tags;
    }
    public String getAbout() {
        return about;
    }

    public List<Image> getImages() {
        return images;
    }
    public List<File> getFiles() { return files; }
}
