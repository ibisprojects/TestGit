package CitSciClasses;

/**
 * Created by manojsre on 10/10/2014.
 */
public class Location {

    private String dataSheetId;
    private String areadId;
    private String areaName;

    public Location(String dataSheetId, String areadId, String areaName) {
        this.dataSheetId = dataSheetId;
        this.areadId = areadId;
        this.areaName = areaName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDataSheetId() {
        return dataSheetId;
    }

    public void setDataSheetId(String dataSheetId) {
        this.dataSheetId = dataSheetId;
    }

    public String getAreadId() {
        return areadId;
    }

    public void setAreadId(String areadId) {
        this.areadId = areadId;
    }
}
