package Upload;

import android.content.Context;
import android.util.Log;

import com.nrel.citsci.DatasheetMainActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import CitSciClasses.SiteCharecteristics;
import DB.TempObservationImagesHandler;

/**
 * Created by Manoj on 3/23/2015.
 */
public class DataSheetObservation implements Serializable {
    String datasheetName = "";
    String datasaheetId = "";
    String projectId = "";
    ArrayList<OrganismObservation> organismObservations = new ArrayList<OrganismObservation>();
    ArrayList<SiteCharecteristics> siteCharecteristics = new ArrayList<SiteCharecteristics>();
    String observationName = "";
    String datum = "";
    String latitude = "";
    String longitude = "";
    String areaID = "";
    String accuracy = "";
    String date = "";
    String authority = "";
    String authorityId = "";
    String recorderId = "";
    String recorder = "";
    String time = "";
    String comments = "";
    byte[] locationImageArr;
    byte[] locationIconImageArr;
    String imageFileName = "";
    static DataSheetObservation dataSheetObservation;

    public DataSheetObservation() {
    }

    public static DataSheetObservation getInstance() {
        if(dataSheetObservation == null) {
            dataSheetObservation = new DataSheetObservation();
        }
        return dataSheetObservation;
    }

    public static DataSheetObservation getNewInstance() {
        dataSheetObservation = new DataSheetObservation();
        return dataSheetObservation;
    }

    public static void resetInstance() {
        dataSheetObservation = null;
    }

    public String getDatasheetName() {
        return datasheetName;
    }

    public void setDatasheetName(String datasheetName) {
        this.datasheetName = datasheetName;
    }

    public String getDatasaheetId() {
        return datasaheetId;
    }

    public void setDatasaheetId(String datasaheetId) {
        this.datasaheetId = datasaheetId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ArrayList<OrganismObservation> getOrganismObservations(Context context) {

        return  organismObservations;
        /*
        TempObservationImagesHandler tempObservationImagesHandler = new TempObservationImagesHandler(context);

        Set<String> completedOrganism = new HashSet<String>();
        ArrayList<OrganismObservation> orderedObservations = new ArrayList<OrganismObservation>();
        ArrayList<OrganismObservation> workingOrganismObservations = new ArrayList<OrganismObservation>(organismObservations);
        //Loop through observatiuons
        for (OrganismObservation obs:
                workingOrganismObservations) {

            //Check if already completed
            if (!completedOrganism.contains(obs.getId())) {
                Log.d("111", "Working on organism" + obs.getId());
                String currentOrganism = obs.getId();
                String currentOrganismName = obs.getName();
                int workingOrgMaxIndex = 0;
                int workingOrgCount =-1;
                //loop through to find max index for organism
                for (OrganismObservation workingOrgObs :
                        organismObservations) {
                    if(workingOrgObs.getId().equalsIgnoreCase(currentOrganism)){
                        if(workingOrgMaxIndex < workingOrgObs.getParentEntryIndex()){
                            workingOrgMaxIndex = workingOrgObs.getParentEntryIndex();
                        }
                        workingOrgCount++;
                    }
                }

                if(workingOrgCount < workingOrgMaxIndex){
                    workingOrgMaxIndex = workingOrgCount;
                }

                Log.d("111", "Max index for " + obs.getId() + " is " + workingOrgMaxIndex);

                //Add all organisms in order of indexes with placeholder for missing ones in between
                for (int i = 0; i < workingOrgMaxIndex; i++){
                    boolean foundIt = false;
                    int j;
                    for (j= 0; j < organismObservations.size(); j++) {
                        OrganismObservation workingOrgObs = organismObservations.get(j);
                        if(workingOrgObs.getId().equalsIgnoreCase(currentOrganism) && workingOrgObs.getParentEntryIndex() == i){
                            foundIt = true;
                            orderedObservations.add(workingOrgObs);
                            //tempObservationImagesHandler.updateEntryIndexForOrganism(workingOrgObs.getParentEntryId(),workingOrgObs.getParentEntryIndex(),j);
                            Log.d("111", "Found entry for organism " + obs.getId() + " with index " + i);
                            organismObservations.remove(j);
                            break;
                        }
                    }

                    if(!foundIt){
                        OrganismObservation placeHolder = new OrganismObservation();
                        placeHolder.setId(currentOrganism);
                        placeHolder.setParentEntryIndex(i);
                        placeHolder.setName(currentOrganismName);
                        orderedObservations.add(placeHolder);
                        Log.d("111", "Added a placeholder for " + obs.getId() + " for index " + i);
                    }
                }
            }
            completedOrganism.add(obs.getId());
        }
        return orderedObservations;*/
    }

    public void setOrganismObservations(ArrayList<OrganismObservation> organismObservations) {
        this.organismObservations = organismObservations;
    }

    public ArrayList<SiteCharecteristics> getSiteCharecteristics() {
        return siteCharecteristics;
    }

    public void addSiteCharecteristics(SiteCharecteristics siteCharecteristic) {
        this.siteCharecteristics.add(siteCharecteristic);
    }

    public void clearDatasheet(){
        this.siteCharecteristics.clear();
        this.organismObservations.clear();
    }

    public String getObservationName() {
        return observationName;
    }

    public void setObservationName(String observationName) {
        this.observationName = observationName;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public byte[] getLocationImageArr() {
        return locationImageArr;
    }

    public void setLocationImageArr(byte[] locationImageArr) {
        this.locationImageArr = locationImageArr;
    }

    public byte[] getLocationIconImageArr() {
        return locationIconImageArr;
    }

    public void setLocationIconImageArr(byte[] locationIconImageArr) {
        this.locationIconImageArr = locationIconImageArr;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }
}
