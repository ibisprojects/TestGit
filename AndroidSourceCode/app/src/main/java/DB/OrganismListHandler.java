package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.OrganismList;

/**
 * Created by manojsre on 8/21/2014.
 */
public class OrganismListHandler extends DataBaseHandler {


    //TABLES
    private static final String TBL_ORGANISMLIST = "TBL_ORGANISMLIST";

    // Project Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ATTR_ID = "attr_id";
    private static final String KEY_NAME = "name";


    public OrganismListHandler(Context context) {
        super(context);
    }

    public void smartAdd(OrganismList organismList) {
        //SQLiteDatabase db = this.getWritableDatabase();
        if (getOrganismList(Integer.parseInt(organismList.getId()),Integer.parseInt(organismList.getAttributeId())) != null) {
            updateOrganismList(organismList);
        } else {
            addOrganismList(organismList);
        }

    }

    // Adding new organism
    public void addOrganismList(OrganismList organismList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, Integer.parseInt(organismList.getId()));
        values.put(KEY_NAME, organismList.getName());
        values.put(KEY_ATTR_ID, Integer.parseInt(organismList.getAttributeId()));
        //Log.d("OrganismListHandler"," Added in DB " + Integer.parseInt(organismList.getId()) + " Name: "+organismList.getName() + " for " + Integer.parseInt(organismList.getAttributeId()));
        // Inserting Row
        db.insert(TBL_ORGANISMLIST, null, values);
        db.close(); // Closing database connection
    }

    // Updating organism
    public int updateOrganismList(OrganismList organismList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, Integer.parseInt(organismList.getId()));
        values.put(KEY_NAME, organismList.getName());
        values.put(KEY_ATTR_ID, Integer.parseInt(organismList.getAttributeId()));

        // updating row
        int returnInt =  db.update(TBL_ORGANISMLIST, values, KEY_ID + "=? AND " + KEY_ATTR_ID + "=?",
                new String[]{String.valueOf(organismList.getId()),String.valueOf(organismList.getAttributeId())});
        //Log.d("OrganismListHandler"," Updated in DB " + Integer.parseInt(organismList.getId()) + " Name: "+organismList.getName() + " for " + Integer.parseInt(organismList.getAttributeId()) + " with return " + returnInt);
        db.close();
        return returnInt;
    }


    // Getting single project
    public OrganismList getOrganismList(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_ORGANISMLIST, new String[]{KEY_ATTR_ID, KEY_ID, KEY_NAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        OrganismList organismList = new OrganismList(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return organismList;
    }

    public OrganismList getOrganismList(int id, int attrId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_ORGANISMLIST, new String[]{KEY_ATTR_ID, KEY_ID, KEY_NAME}, KEY_ID + "=? AND " + KEY_ATTR_ID + "=?",
                new String[]{String.valueOf(id),String.valueOf(attrId)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        OrganismList organismList = new OrganismList(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return organismList;
    }

    public List<OrganismList> getOrganismEntriesForAttribute(String attrId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<OrganismList> organismLists = new ArrayList<OrganismList>();
        Cursor cursor = null;
        organismLists.add(new OrganismList("","","Select Species"));
        try {
            cursor = db.query(TBL_ORGANISMLIST, new String[]{KEY_ATTR_ID, KEY_ID, KEY_NAME}, KEY_ATTR_ID + "=?",
                    new String[]{String.valueOf(attrId)}, null, null, null, null);
            //Log.d("OrganismListHandler",cursor.getCount()+ " organsims in the DB for this attribute for " + attrId);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                organismLists.add(new OrganismList(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return organismLists;
    }
}
