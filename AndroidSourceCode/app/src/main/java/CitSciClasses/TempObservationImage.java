package CitSciClasses;

/**
 * Created by manoj on 10/4/2017.
 */

public class TempObservationImage {

    private String attributeId="";
    private String entryIndex="";
    private String organismInfoId="";
    private String dataEntered="";
    private String tempImageName="";
    private String finalImageName="";
    private String attributeIndexFinalized="0";


    public String getAttributeIndexFinalized() {
        return attributeIndexFinalized;
    }

    public void setAttributeIndexFinalized(String attributeIndexFinalized) {
        this.attributeIndexFinalized = attributeIndexFinalized;
    }

    public TempObservationImage(String attributeId, String entryIndex, String organismInfoId, String dataEntered, String tempImageName, String finalImageName, String attributeIndexFinalized) {
        this.attributeId = attributeId;
        this.entryIndex = entryIndex;
        this.organismInfoId = organismInfoId;
        this.tempImageName = tempImageName;
        this.finalImageName = finalImageName;
        this.dataEntered = dataEntered;
        this.attributeIndexFinalized = attributeIndexFinalized;
    }


    public String getDataEntered() {
        return dataEntered;
    }

    public void setDataEntered(String dataEntered) {
        this.dataEntered = dataEntered;
    }

    public String getFinalImageName() {
        return finalImageName;
    }

    public void setFinalImageName(String finalImageName) {
        this.finalImageName = finalImageName;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getEntryIndex() {
        return entryIndex;
    }

    public void setEntryIndex(String entryIndex) {
        this.entryIndex = entryIndex;
    }

    public String getOrganismInfoId() {
        return organismInfoId;
    }

    public void setOrganismInfoId(String organismInfoId) {
        this.organismInfoId = organismInfoId;
    }

    public String getTempImageName() {
        return tempImageName;
    }

    public void setTempImageName(String tempImageName) {
        this.tempImageName = tempImageName;
    }
}
