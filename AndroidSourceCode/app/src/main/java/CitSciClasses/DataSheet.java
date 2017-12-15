package CitSciClasses;

/**
 * Created by manojsre on 10/10/2014.
 */
public class DataSheet {
    private String projectID;
    private String datasheetID;
    private String name;
    private String areaSubtypeID;
    private String predefined;

    public DataSheet(String projectID,String datasheetID,String name,String areaSubtypeID,String predefined){
        this.datasheetID = datasheetID;
        this.projectID = projectID;
        this.name = name;
        this.areaSubtypeID = areaSubtypeID;
        this.predefined = predefined;
    }

    public String getDatasheetID() {
        return datasheetID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public void setDatasheetID(String datasheetID) {
        datasheetID = datasheetID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaSubtypeID() {
        return areaSubtypeID;
    }

    public void setAreaSubtypeID(String areaSubtypeID) {
        this.areaSubtypeID = areaSubtypeID;
    }

    public String getPredefined() {
        return predefined;
    }

    public void setPredefined(String predefined) {
        this.predefined = predefined;
    }
}
