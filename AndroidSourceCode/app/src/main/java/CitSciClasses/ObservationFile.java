package CitSciClasses;

import android.widget.CheckBox;

/**
 * Created by Manoj on 4/9/2015.
 */
public class ObservationFile {
    String fileName;
    String displayName;
    String fileDate;
    CheckBox checkBox;
    Boolean isChecked= false;

    public ObservationFile(String fileName,String displayName, String fileDate) {
        this.fileName = fileName;
        this.fileDate = fileDate;
        this.displayName = displayName;
    }

    public Boolean isChecked() {
        return isChecked;
    }
    public void toggleChecked()
    {
        isChecked = !isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}
