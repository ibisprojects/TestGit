package CitSciClasses;

/**
 * Created by manojsre on 10/10/2014.
 */
public class SiteCharecteristics {

    private String dataSheetId="";
    private String id="";
    private String picklist="";
    private String parentFormEntryID="";
    private String subplotTypeID="";
    private String orderNumber="";
    private String minValue="";
    private String maxValue="";
    private String description="";
    private String attributeName="";
    private String attributeTypeID="";
    private String attributeValueID="";
    private String organismInfoID="";
    private String organismName="";
    private String howSpecified="";
    private String unitID="";
    private String unitName="";
    private String unitAbbreviation="";
    private String valueType="";
    private String selectedValue="";

    public SiteCharecteristics(){

    }
    public SiteCharecteristics(String dataSheetId, String id, String picklist, String parentFormEntryID, String subplotTypeID, String orderNumber, String minValue, String maxValue, String description,String attributeName, String attributeTypeID, String attributeValueID, String organismInfoID, String organismName, String howSpecified, String unitID, String unitName, String unitAbbreviation, String valueType) {
        this.dataSheetId = dataSheetId;
        this.id = id;
        this.picklist = picklist;
        this.parentFormEntryID = parentFormEntryID;
        this.subplotTypeID = subplotTypeID;
        this.orderNumber = orderNumber;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.description = description;
        this.attributeName = attributeName;
        this.attributeTypeID = attributeTypeID;
        this.attributeValueID = attributeValueID;
        this.organismInfoID = organismInfoID;
        this.organismName = organismName;
        this.howSpecified = howSpecified;
        this.unitID = unitID;
        this.unitName = unitName;
        this.unitAbbreviation = unitAbbreviation;
        this.valueType = valueType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDataSheetId() {
        return dataSheetId;
    }

    public void setDataSheetId(String dataSheetId) {
        this.dataSheetId = dataSheetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicklist() {
        return picklist;
    }

    public void setPicklist(String picklist) {
        this.picklist = picklist;
    }

    public String getParentFormEntryID() {
        return parentFormEntryID;
    }

    public void setParentFormEntryID(String parentFormEntryID) {
        this.parentFormEntryID = parentFormEntryID;
    }

    public String getSubplotTypeID() {
        return subplotTypeID;
    }

    public void setSubplotTypeID(String subplotTypeID) {
        this.subplotTypeID = subplotTypeID;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttributeTypeID() {
        return attributeTypeID;
    }

    public void setAttributeTypeID(String attributeTypeID) {
        this.attributeTypeID = attributeTypeID;
    }

    public String getAttributeValueID() {
        return attributeValueID;
    }

    public void setAttributeValueID(String attributeValueID) {
        this.attributeValueID = attributeValueID;
    }

    public String getOrganismInfoID() {
        return organismInfoID;
    }

    public void setOrganismInfoID(String organismInfoID) {
        this.organismInfoID = organismInfoID;
    }

    public String getOrganismName() {
        return organismName;
    }

    public void setOrganismName(String organismName) {
        this.organismName = organismName;
    }

    public String getHowSpecified() {
        return howSpecified;
    }

    public void setHowSpecified(String howSpecified) {
        this.howSpecified = howSpecified;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitAbbreviation() {
        return unitAbbreviation;
    }

    public void setUnitAbbreviation(String unitAbbreviation) {
        this.unitAbbreviation = unitAbbreviation;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
