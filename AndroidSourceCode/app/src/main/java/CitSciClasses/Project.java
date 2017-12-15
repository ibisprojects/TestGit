package CitSciClasses;

/**
 * Created by manojsre on 8/19/2014.
 */
public class Project {

    private String projectID;
    private String userID;
    private String projectName;
    private String status;
    private String description;
    private String pinLatitude;
    private String pinLongitude;

    public Project(String projectID, String projectName, String status, String description, String pinLatitude, String pinLongitude, String userID) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.status = status;
        this.description = description;
        this.pinLatitude = pinLatitude;
        this.pinLongitude = pinLongitude;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPinLatitude() {
        if(this.pinLatitude==null  || this.pinLatitude=="" || this.pinLatitude=="null"){
            return "0.0";
        }
        return pinLatitude;
    }

    public void setPinLatitude(String pinLatitude) {
        this.pinLatitude = pinLatitude;
    }

    public String getPinLongitude() {
        if(this.pinLongitude==null || this.pinLongitude=="" || this.pinLatitude=="null"){
            return "0.0";
        }
        return pinLongitude;
    }

    public void setPinLongitude(String pinLongitude) {
        this.pinLongitude = pinLongitude;
    }
}

