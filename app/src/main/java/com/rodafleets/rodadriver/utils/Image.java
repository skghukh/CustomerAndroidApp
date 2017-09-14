package com.rodafleets.rodadriver.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Image {


    private String fileName;
    private Bitmap bitmap;
    private File file;

    public Image(String fileName, Bitmap bitmap, File file) {
        this.fileName = fileName;
        this.bitmap = bitmap;
        this.file = file;
    }

    public Image(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getFile() { return this.file; }
}
