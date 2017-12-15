package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import CitSciClasses.TempObservationImage;

/**
 * Created by manoj on 10/4/2017.
 */

public class TempObservationImagesHandler  extends DataBaseHandler {

    //TABLES
    private static final String TBL_TEMPOBSERVATIONIMAGES = "TBL_TEMPOBSERVATIONIMAGES";

    // Project Table Columns names
    private static final String KEY_ATTRIBUTE_ID = "attribute_id";
    private static final String KEY_ENTRY_INDEX = "entry_index";
    private static final String KEY_DATAENTERED= "data_entered";
    private static final String KEY_ORGANISM_INFO_ID = "organism_info";
    private static final String KEY_TEMPIMAGENAME = "temp_image_name";
    private static final String KEY_FINALIMAGENAME = "final_image_name";
    private static final String KEY_ATTRIBUTE_INDEX_FINALIZED = "attribute_index_finalized";


    public TempObservationImagesHandler(Context context) {
        super(context);
    }

    // Adding new project
    public void addFormAttribute(TempObservationImage tempObservationImage) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ATTRIBUTE_ID, CommonFunctions.IntegerSmartParse(tempObservationImage.getAttributeId()));
        values.put(KEY_ENTRY_INDEX, CommonFunctions.IntegerSmartParse(tempObservationImage.getEntryIndex()));
        values.put(KEY_DATAENTERED, CommonFunctions.IntegerSmartParse(tempObservationImage.getDataEntered()));
        values.put(KEY_ORGANISM_INFO_ID, CommonFunctions.IntegerSmartParse(tempObservationImage.getOrganismInfoId()));
        values.put(KEY_TEMPIMAGENAME, tempObservationImage.getTempImageName());
        values.put(KEY_FINALIMAGENAME, tempObservationImage.getFinalImageName());
        values.put(KEY_ATTRIBUTE_INDEX_FINALIZED, tempObservationImage.getAttributeIndexFinalized());

        db.insert(TBL_TEMPOBSERVATIONIMAGES, null, values);
        db.close(); // Closing database connection
    }


    public int updateOrganismForEormEntry(String attibuteID,String entryIndex, String organismInfoID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORGANISM_INFO_ID, organismInfoID);

        //Log.d("222","Updating " + attibuteID + " to" + organismInfoID + " for index " +  entryIndex);

        // updating row
        int returnInt =db.update(TBL_TEMPOBSERVATIONIMAGES, values, KEY_ATTRIBUTE_ID + " = ? AND " + KEY_ENTRY_INDEX + " = ?",
                new String[]{attibuteID, entryIndex});
        db.close();
        return returnInt;
    }

    public int updateDataEnteredForFormEntry(String attibuteID, String entryIndex) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATAENTERED, "1");

        Log.d("222","Updating " + attibuteID + " to" +  " data entered for index " + entryIndex);

        // updating row
        int returnInt =db.update(TBL_TEMPOBSERVATIONIMAGES, values, KEY_ATTRIBUTE_ID + " = ? AND " + KEY_ENTRY_INDEX + " = ?",
                new String[]{attibuteID, entryIndex});
        db.close();
        return returnInt;
    }

    public int updateEntryIndexForAttribuiteID(String attributeID, int entryIndex, int attrActualIndex) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_INDEX, attrActualIndex);
        values.put(KEY_ATTRIBUTE_INDEX_FINALIZED, "1");

        Log.d("222","Updating " + attributeID + " with index " + entryIndex +  "  to index " + attrActualIndex);
        // updating row
        int returnInt =db.update(TBL_TEMPOBSERVATIONIMAGES, values, KEY_ATTRIBUTE_ID + " = ? and " + KEY_ENTRY_INDEX + " = ?" + " and " + KEY_ATTRIBUTE_INDEX_FINALIZED + " = 0",
                new String[]{attributeID, String.valueOf(entryIndex)});
        db.close();
        Log.d("222","Updates " + returnInt + " rows while updating " + attributeID + " with index " + entryIndex +  "  to index " + attrActualIndex);
        return returnInt;
    }

    public int updateEntryIndexForOrganism(String organismID, int entryIndex, int attrActualIndex) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_INDEX, attrActualIndex);

        Log.d("222","Updating " + organismID + " with index " + entryIndex +  "  to index " + attrActualIndex);
        // updating row
        int returnInt =db.update(TBL_TEMPOBSERVATIONIMAGES, values, KEY_ORGANISM_INFO_ID + " = ? and " + KEY_ENTRY_INDEX + " = ?",
                new String[]{organismID, String.valueOf(entryIndex)});
        db.close();
        Log.d("222","Updates " + returnInt + " rows while updating " + organismID + " with index " + entryIndex +  "  to index " + attrActualIndex);
        return returnInt;
    }

    public int deleteImage(String tempImageName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // deleting image
        int returnInt =db.delete(TBL_TEMPOBSERVATIONIMAGES, KEY_TEMPIMAGENAME + " = ?",
                new String[]{tempImageName});
        Log.d("222","Deleted image count: " + returnInt);
        db.close();
        return returnInt;
    }



    public int cleanupTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // deleting image
        int returnInt =db.delete(TBL_TEMPOBSERVATIONIMAGES, null, null);
        db.close();
        return returnInt;
    }

    public List<TempObservationImage> getAllFinalTempObservationImages() {
        SQLiteDatabase db = this.getWritableDatabase();

        // deleting entries with no data
        //db.delete(TBL_TEMPOBSERVATIONIMAGES, KEY_DATAENTERED + " = ? AND " +  KEY_TEMPIMAGENAME + " is null",new String[]{"0"});
        db.delete(TBL_TEMPOBSERVATIONIMAGES, KEY_DATAENTERED + " = ?",new String[]{"0"});
        //Log.d("222","Deleted entries with no data entered: " + iCount);


        //Cleanup an reorder entries for which data is entered
        Cursor cleanupCursor = null;
        try {
            cleanupCursor = db.rawQuery("select distinct " + KEY_ORGANISM_INFO_ID + ", " + KEY_ENTRY_INDEX +"  from " + TBL_TEMPOBSERVATIONIMAGES + " order by " + KEY_ORGANISM_INFO_ID + "," + KEY_ENTRY_INDEX, null);
            cleanupCursor.moveToFirst();
            int orgActualIndex = 0;
            String currentOrganismID = "";
            while (!cleanupCursor.isAfterLast() && cleanupCursor.getCount()>0) {

                String organismID = cleanupCursor.getString(0);
                int entryIndex = cleanupCursor.getInt(1);
                Log.d("222","Unique Organism, Index" + organismID + " , " + String.valueOf(entryIndex));
                if(!currentOrganismID.equalsIgnoreCase(organismID)){
                    orgActualIndex = 0;
                    currentOrganismID = organismID;
                }
                if(entryIndex != orgActualIndex){
                    Log.d("222","Updated entry index for " + entryIndex + " to " + orgActualIndex);
                    updateEntryIndexForOrganism(organismID, entryIndex, orgActualIndex);
                }

                orgActualIndex++;

                cleanupCursor.moveToNext();

            }
        } finally {
            if (cleanupCursor != null)
                cleanupCursor.close();
        }


        db = this.getWritableDatabase();

        db.delete(TBL_TEMPOBSERVATIONIMAGES, KEY_TEMPIMAGENAME + " = ?",new String[]{""});
        db.delete(TBL_TEMPOBSERVATIONIMAGES, KEY_TEMPIMAGENAME + " IS NULL",null);


        List<TempObservationImage> tempObservationImages = new ArrayList<TempObservationImage>();
        int organismImageIndex = 0;
        String currentOrganism = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_TEMPOBSERVATIONIMAGES, new String[]{KEY_ATTRIBUTE_ID, KEY_ENTRY_INDEX, KEY_ORGANISM_INFO_ID, KEY_TEMPIMAGENAME}, null,
                    null, null, null, KEY_ORGANISM_INFO_ID + ", " + KEY_ENTRY_INDEX + " ASC");
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && cursor.getCount()>0) {

                if(currentOrganism != null && currentOrganism.equalsIgnoreCase(cursor.getString(2))) {
                    organismImageIndex++;
                }
                else{
                    currentOrganism = cursor.getString(2);
                    organismImageIndex = 0;
                }

                String finalImageName = "Datasheet_Organism_" + cursor.getString(2) + "_" + cursor.getString(1) + "_" + organismImageIndex + ".jpg";
                File imageFile = new File(cursor.getString(3));

                Log.d("222","Renaming " + cursor.getString(3) + " to" + imageFile.getParent() + "/" +  finalImageName);

                imageFile.renameTo(new File(imageFile.getParent() + "/" +  finalImageName));
                tempObservationImages.add(new TempObservationImage(cursor.getString(0), cursor.getString(1), cursor.getString(2),"1", cursor.getString(3), finalImageName, "1"));

                cursor.moveToNext();

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        db.close();
        return tempObservationImages;
    }
}
