package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.Location;

/**
 * Created by manojsre on 11/1/2014.
 */
public class LocationHandler extends DataBaseHandler {
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "projectManager";

    //TABLES
    private static final String TBL_LOCATIONS = "TBL_Locations";

    // Project Table Columns names
    private static final String KEY_AREAID = "area_id";
    private static final String KEY_DATASHEET_ID = "datasheet_id";
    private static final String KEY_AREA_NAME = "area_name";


    public LocationHandler(Context context) {
        super(context);
    }

    public void smartAdd(Location location) {
        //SQLiteDatabase db = this.getWritableDatabase();
        if (getLocation(CommonFunctions.IntegerSmartParse(location.getAreadId())) != null) {
            updateLocation(location);
        } else {
            addLocation(location);
        }

    }

    // Adding new project
    public void addLocation(Location location) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AREAID, CommonFunctions.IntegerSmartParse(location.getAreadId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(location.getDataSheetId()));
        values.put(KEY_AREA_NAME, location.getAreaName());

        // Inserting Row
        db.insert(TBL_LOCATIONS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AREAID, CommonFunctions.IntegerSmartParse(location.getAreadId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(location.getDataSheetId()));
        values.put(KEY_AREA_NAME, location.getAreaName());

        // updating row
        int returnInt = db.update(TBL_LOCATIONS, values, KEY_AREAID + " = ?",
                new String[]{String.valueOf(location.getAreadId())});
        db.close();
        return returnInt;
    }


    // Getting single project
    public Location getLocation(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_LOCATIONS, new String[]{KEY_DATASHEET_ID, KEY_AREAID, KEY_AREA_NAME}, KEY_AREAID + "=?",

                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        Location location = new Location(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return location;
    }

    public List<Location> getLocationsForDatasheet(String datasheetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Location> locationList = new ArrayList<Location>();
        locationList.add(new Location("","","Select"));
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_LOCATIONS, new String[]{KEY_DATASHEET_ID, KEY_AREAID, KEY_AREA_NAME}, KEY_DATASHEET_ID + "=?",
                    new String[]{String.valueOf(datasheetId)}, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locationList.add(new Location(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return locationList;
    }
}

