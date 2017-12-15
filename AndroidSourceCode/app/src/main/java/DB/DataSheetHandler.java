package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.DataSheet;


/**
 * Created by manojsre on 8/21/2014.
 */
public class DataSheetHandler extends DataBaseHandler {


    //TABLES
    private static final String TBL_DATASHEETS = "TBL_DATASHEETS";

    // Project Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AREASUBTYPEID = "area_subtype_id";
    private static final String KEY_PREDEFINED = "datasheet_predefined";


    public DataSheetHandler(Context context) {
        super(context);
    }


    public void smartAdd(DataSheet datasheet) {
        if (getDatasheet(CommonFunctions.IntegerSmartParse(datasheet.getDatasheetID())) != null) {
            updateDatasheet(datasheet);
        } else {
            addDatasheet(datasheet);
        }

    }

    // Adding new project
    public void addDatasheet(DataSheet datasheet) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(datasheet.getDatasheetID()));
        values.put(KEY_PROJECT_ID, CommonFunctions.IntegerSmartParse(datasheet.getProjectID()));
        values.put(KEY_NAME, datasheet.getName());
        values.put(KEY_AREASUBTYPEID, datasheet.getAreaSubtypeID());
        values.put(KEY_PREDEFINED, datasheet.getPredefined());

        // Inserting Row
        db.insert(TBL_DATASHEETS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateDatasheet(DataSheet datasheet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(datasheet.getDatasheetID()));
        values.put(KEY_PROJECT_ID, CommonFunctions.IntegerSmartParse(datasheet.getProjectID()));
        values.put(KEY_NAME, datasheet.getName());
        values.put(KEY_AREASUBTYPEID, datasheet.getAreaSubtypeID());
        values.put(KEY_PREDEFINED, datasheet.getPredefined());


        // updating row
        int returnInt = db.update(TBL_DATASHEETS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(datasheet.getDatasheetID())});
        db.close();
        return returnInt;
    }


    // Getting single project
    public DataSheet getDatasheet(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_DATASHEETS, new String[]{KEY_PROJECT_ID, KEY_ID, KEY_NAME, KEY_AREASUBTYPEID, KEY_PREDEFINED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d("DatasheetHandler:Count", String.valueOf(cursor.getCount()));
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        DataSheet datasheet = new DataSheet(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return datasheet;
    }

    public List getDataSheetList(int projectId) {
        List returnList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_DATASHEETS, new String[]{KEY_PROJECT_ID, KEY_ID, KEY_NAME, KEY_AREASUBTYPEID, KEY_PREDEFINED}, KEY_PROJECT_ID + "=?",
                    new String[]{String.valueOf(projectId)}, null, null, null, null);
            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    DataSheet datasheet = new DataSheet(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    returnList.add(datasheet);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return returnList;
    }
}
