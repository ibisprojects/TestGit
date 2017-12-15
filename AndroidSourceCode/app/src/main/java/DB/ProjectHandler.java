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
import CitSciClasses.Project;

/**
 * Created by manojsre on 8/21/2014.
 */
public class ProjectHandler extends DataBaseHandler {


    //TABLES
    private static final String TBL_PROJECTS = "TBL_PROJECTS";

    // Project Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";


    public ProjectHandler(Context context) {
        super(context);
    }

    public void smartAdd(Project project) {
        //SQLiteDatabase db = this.getWritableDatabase();
        if (getProject(CommonFunctions.IntegerSmartParse(project.getProjectID())) != null) {
            updateProject(project);
        } else {
            addProject(project);
        }

    }

    public void cleanUpDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        DataBaseHandler.cleanUpDatabase(db);
    }

    // Adding new project
    public void addProject(Project project) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(project.getProjectID()));
        values.put(KEY_NAME, project.getProjectName());
        values.put(KEY_LOGIN, project.getUserID());
        values.put(KEY_STATUS, project.getProjectName());
        values.put(KEY_DESCRIPTION, project.getDescription());
        values.put(KEY_LAT, Double.parseDouble(project.getPinLatitude()));
        values.put(KEY_LONG, Double.parseDouble(project.getPinLongitude()));
        // Inserting Row
        db.insert(TBL_PROJECTS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(project.getProjectID()));
        values.put(KEY_NAME, project.getProjectName());
        values.put(KEY_LOGIN, project.getUserID());
        values.put(KEY_STATUS, project.getProjectName());
        values.put(KEY_DESCRIPTION, project.getDescription());
        values.put(KEY_LAT, Double.parseDouble(project.getPinLatitude()));
        values.put(KEY_LONG, Double.parseDouble(project.getPinLongitude()));

        // updating row
        int returnInt = db.update(TBL_PROJECTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(project.getProjectID())});
        db.close();
        return returnInt;
    }


    // Getting single project
    public Project getProject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_PROJECTS, new String[]{KEY_ID, KEY_NAME, KEY_STATUS, KEY_DESCRIPTION, KEY_LAT, KEY_LONG,KEY_LOGIN}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d("ProjectHandler:Count", String.valueOf(cursor.getCount()));
        }
        if (cursor.getCount()<1) {
            return null;
        }
        Project project = new Project(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(6));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return project;
    }

    public List getAllProjects() {
        List returnList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_PROJECTS, new String[]{KEY_ID, KEY_NAME, KEY_STATUS, KEY_DESCRIPTION, KEY_LAT, KEY_LONG,KEY_LOGIN}, null,
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    Project project = new Project(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(6));
                    returnList.add(project);
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
