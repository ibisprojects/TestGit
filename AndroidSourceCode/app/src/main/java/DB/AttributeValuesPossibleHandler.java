package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.AttributeValuesPossible;

/**
 * Created by manojsre on 8/21/2014.
 */
public class AttributeValuesPossibleHandler extends DataBaseHandler {


    //TABLES
    private static final String TBL_ATTR_VALUES_POSSIBLE = "TBL_ATTRVALUESPOSSIBLE";

    // Project Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ATTR_ID = "attr_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";


    public AttributeValuesPossibleHandler(Context context) {
        super(context);
    }

    public void smartAdd(AttributeValuesPossible attributeValuesPossible) {

        //Log.d("MainActivity",attributeValuesPossible.getAttributeId() +" smart adding " + attributeValuesPossible.getId() + " adding "+ attributeValuesPossible.getName());
        //SQLiteDatabase db = this.getWritableDatabase();

        if (getAttrValuesPossible(CommonFunctions.IntegerSmartParse(attributeValuesPossible.getAttributeId()), CommonFunctions.IntegerSmartParse(attributeValuesPossible.getId())) != null) {
            updateAttrValuesPossible(attributeValuesPossible);
        } else {
            addAttrValuesPossible(attributeValuesPossible);
        }

    }

    // Adding new project
    public void addAttrValuesPossible(AttributeValuesPossible attributeValuesPossible) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(attributeValuesPossible.getId()));
        values.put(KEY_NAME, attributeValuesPossible.getName());
        values.put(KEY_ATTR_ID, CommonFunctions.IntegerSmartParse(attributeValuesPossible.getAttributeId()));
        values.put(KEY_DESCRIPTION, attributeValuesPossible.getDescription());
        //Log.d("AttributeValuesPossibleHandler","For "+CommonFunctions.IntegerSmartParse(attributeValuesPossible.getAttributeId())+" Attr added "+CommonFunctions.IntegerSmartParse(attributeValuesPossible.getId())+" is "+ attributeValuesPossible.getName());
        // Inserting Row
        db.insert(TBL_ATTR_VALUES_POSSIBLE, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateAttrValuesPossible(AttributeValuesPossible attributeValuesPossible) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(attributeValuesPossible.getId()));
        values.put(KEY_NAME, attributeValuesPossible.getName());
        values.put(KEY_ATTR_ID, CommonFunctions.IntegerSmartParse(attributeValuesPossible.getAttributeId()));
        values.put(KEY_DESCRIPTION, attributeValuesPossible.getDescription());
        //Log.d("AttributeValuesPossibleHandler","For "+CommonFunctions.IntegerSmartParse(attributeValuesPossible.getAttributeId())+" Attr updated "+CommonFunctions.IntegerSmartParse(attributeValuesPossible.getId())+" is "+ attributeValuesPossible.getName());
        // updating row
        int returnInt =db.update(TBL_ATTR_VALUES_POSSIBLE, values, KEY_ATTR_ID + "= ? AND " + KEY_ID + " = ?",
                new String[]{String.valueOf(attributeValuesPossible.getAttributeId()), String.valueOf(attributeValuesPossible.getId())});
        db.close();
        return returnInt;
    }


    // Getting single project
    public AttributeValuesPossible getAttrValuesPossible(int attrId, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_ATTR_VALUES_POSSIBLE, new String[]{KEY_ATTR_ID, KEY_ID, KEY_NAME, KEY_DESCRIPTION}, KEY_ATTR_ID + "= ? AND " + KEY_ID + " = ?",
                new String[]{String.valueOf(attrId), String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        AttributeValuesPossible attributeValuesPossible = new AttributeValuesPossible(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        db.close();
        return attributeValuesPossible;
    }

    public List<AttributeValuesPossible> getAttributeValuesPossibleForAttribute(String attrId) {
        //Log.d("AttributeValuesPossibleHandler","Getting for "+ attrId);
        SQLiteDatabase db = this.getReadableDatabase();
        List<AttributeValuesPossible> attributeValueList = new ArrayList<AttributeValuesPossible>();
        Cursor cursor = null;
        attributeValueList.add(new AttributeValuesPossible("","","Select" ,""));
        try {
            cursor = db.query(TBL_ATTR_VALUES_POSSIBLE, new String[]{KEY_ATTR_ID, KEY_ID, KEY_NAME, KEY_DESCRIPTION}, KEY_ATTR_ID + "=?",
                    new String[]{String.valueOf(attrId)}, null, null, null, null);

            cursor.moveToFirst();
            //Log.d("AttributeValuesPossibleHandler","Got "+cursor.getCount()+" for "+ attrId);
            while (!cursor.isAfterLast()) {
                attributeValueList.add(new AttributeValuesPossible(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                //Log.d("AttributeValuesPossibleHandler","Added  "+ cursor.getString(2));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return attributeValueList;
    }
}
