package com.nrel.citsci;

import android.widget.CheckBox;

import java.io.File;

/**
 * Created by manoj on 4/1/2016.
 */
public class ImageFile {
    File file;
    CheckBox checkBox;
    boolean checked;

    public ImageFile(File file) {
        this.file = file;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
