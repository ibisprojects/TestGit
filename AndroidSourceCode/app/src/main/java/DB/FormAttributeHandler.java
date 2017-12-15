package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.FormAttribute;

/**
 * Created by manojsre on 11/1/2014.
 */
public class FormAttributeHandler extends DataBaseHandler {

    //TABLES
    private static final String TBL_FORMATTRIBUTES = "TBL_FormAttributes";

    // Form Attribute Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATASHEET_ID = "datasheet_id";
    private static final String KEY_PICKLIST = "pricklist";
    private static final String KEY_SUBPLOTTYPE_ID = "subplotTypeID";
    private static final String KEY_PARENT_FORMENTRY_ID = "parent_form_entry";
    private static final String KEY_ORDER_NUMBER = "area_order_number";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ATTRIBUTE_NAME = "attribute_name";
    private static final String KEY_ATTRIBUTETYPE_ID = "attribute_type";
    private static final String KEY_ATTRIBUTEVALUE_ID = "attribute_value";
    private static final String KEY_ORGANISM_INFO_ID = "organism_info";
    private static final String KEY_ORGANISM_NAME = "organism_name";
    private static final String KEY_ORGANISM_TYPE = "organism_type";
    private static final String KEY_HOWSPECIFIED = "how_specified";
    private static final String KEY_UNIT_ID = "unit";
    private static final String KEY_UNIT_NAME = "unit_name";
    private static final String KEY_UNIT_ABBR = "unit_abbre";
    private static final String KEY_VALUE_TYPE = "value_type";


    public FormAttributeHandler(Context context) {
        super(context);
    }


    public void smartAdd(FormAttribute formAttribute) {
        //SQLiteDatabase db = this.getWritableDatabase();
        if (getFormAttribute(CommonFunctions.IntegerSmartParse(formAttribute.getId())) != null) {
            updateFormAttribute(formAttribute);
        } else {
            addFormAttribute(formAttribute);
        }

    }

    // Adding new project
    public void addFormAttribute(FormAttribute formAttribute) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(formAttribute.getId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(formAttribute.getDataSheetId()));
        values.put(KEY_PICKLIST, CommonFunctions.IntegerSmartParse(formAttribute.getPicklist()));
        values.put(KEY_PARENT_FORMENTRY_ID, CommonFunctions.IntegerSmartParse(formAttribute.getParentFormEntryID()));
        values.put(KEY_SUBPLOTTYPE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getSubplotTypeID()));
        values.put(KEY_ORDER_NUMBER, CommonFunctions.IntegerSmartParse(formAttribute.getOrderNumber()));
        values.put(KEY_MIN_VALUE, CommonFunctions.IntegerSmartParse(formAttribute.getMinValue()));
        values.put(KEY_MAX_VALUE, CommonFunctions.IntegerSmartParse(formAttribute.getMaxValue()));
        values.put(KEY_DESCRIPTION, formAttribute.getDescription());
        values.put(KEY_ATTRIBUTE_NAME, formAttribute.getAttributeName());
        values.put(KEY_ATTRIBUTETYPE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getAttributeTypeID()));
        values.put(KEY_ATTRIBUTEVALUE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getAttributeValueID()));
        values.put(KEY_ORGANISM_INFO_ID, CommonFunctions.IntegerSmartParse(formAttribute.getOrganismInfoID()));
        values.put(KEY_ORGANISM_NAME, formAttribute.getOrganismName());
        values.put(KEY_ORGANISM_TYPE, formAttribute.getOrganismType());
        values.put(KEY_HOWSPECIFIED, formAttribute.getHowSpecified());
        values.put(KEY_UNIT_ID, CommonFunctions.IntegerSmartParse(formAttribute.getUnitID()));
        values.put(KEY_UNIT_NAME, formAttribute.getUnitName());
        values.put(KEY_UNIT_ABBR, formAttribute.getUnitAbbreviation());
        values.put(KEY_VALUE_TYPE, CommonFunctions.IntegerSmartParse(formAttribute.getValueType()));
        // Inserting Row
        db.insert(TBL_FORMATTRIBUTES, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateFormAttribute(FormAttribute formAttribute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(formAttribute.getId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(formAttribute.getDataSheetId()));
        values.put(KEY_PICKLIST, CommonFunctions.IntegerSmartParse(formAttribute.getPicklist()));
        values.put(KEY_PARENT_FORMENTRY_ID, CommonFunctions.IntegerSmartParse(formAttribute.getParentFormEntryID()));
        values.put(KEY_SUBPLOTTYPE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getSubplotTypeID()));
        values.put(KEY_ORDER_NUMBER, CommonFunctions.IntegerSmartParse(formAttribute.getOrderNumber()));
        values.put(KEY_MIN_VALUE, CommonFunctions.IntegerSmartParse(formAttribute.getMinValue()));
        values.put(KEY_MAX_VALUE, CommonFunctions.IntegerSmartParse(formAttribute.getMaxValue()));
        values.put(KEY_DESCRIPTION, formAttribute.getDescription());
        values.put(KEY_ATTRIBUTE_NAME, formAttribute.getAttributeName());
        values.put(KEY_ATTRIBUTETYPE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getAttributeTypeID()));
        values.put(KEY_ATTRIBUTEVALUE_ID, CommonFunctions.IntegerSmartParse(formAttribute.getAttributeValueID()));
        values.put(KEY_ORGANISM_INFO_ID, CommonFunctions.IntegerSmartParse(formAttribute.getOrganismInfoID()));
        values.put(KEY_ORGANISM_NAME, formAttribute.getOrganismName());
        values.put(KEY_ORGANISM_TYPE, formAttribute.getOrganismType());
        values.put(KEY_HOWSPECIFIED, formAttribute.getHowSpecified());
        values.put(KEY_UNIT_ID, CommonFunctions.IntegerSmartParse(formAttribute.getUnitID()));
        values.put(KEY_UNIT_NAME, formAttribute.getUnitName());
        values.put(KEY_UNIT_ABBR, formAttribute.getUnitAbbreviation());
        values.put(KEY_VALUE_TYPE, CommonFunctions.IntegerSmartParse(formAttribute.getValueType()));
        // updating row
        int returnInt =db.update(TBL_FORMATTRIBUTES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(formAttribute.getId())});
        db.close();
        return returnInt;
    }

    public String[] getOrganismInfo(String parentFormEntryID){
        String returnArray[] = new String[2];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_FORMATTRIBUTES, new String[]{KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME}, KEY_ID + "=?",

                new String[]{String.valueOf(parentFormEntryID)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        returnArray[0] = cursor.getString(0);
        returnArray[1] = cursor.getString(1);
        db.close();

        return returnArray;
    }

    // Getting single project
    public FormAttribute getFormAttribute(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_FORMATTRIBUTES, new String[]{KEY_DATASHEET_ID, KEY_ID, KEY_PICKLIST, KEY_PARENT_FORMENTRY_ID, KEY_SUBPLOTTYPE_ID, KEY_ORDER_NUMBER, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_DESCRIPTION, KEY_ATTRIBUTE_NAME, KEY_ATTRIBUTETYPE_ID, KEY_ATTRIBUTEVALUE_ID, KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME, KEY_HOWSPECIFIED, KEY_UNIT_ID, KEY_UNIT_NAME, KEY_UNIT_ABBR, KEY_VALUE_TYPE, KEY_ORGANISM_TYPE}, KEY_ID + "=?",

                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        FormAttribute formAttribute = new FormAttribute(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19));
        db.close();
        return formAttribute;
    }

    public List<FormAttribute> getFormEntries(String datasheetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<FormAttribute> formAttributes = new ArrayList<FormAttribute>();
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_FORMATTRIBUTES, new String[]{KEY_DATASHEET_ID, KEY_ID, KEY_PICKLIST, KEY_PARENT_FORMENTRY_ID, KEY_SUBPLOTTYPE_ID, KEY_ORDER_NUMBER, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_DESCRIPTION, KEY_ATTRIBUTE_NAME, KEY_ATTRIBUTETYPE_ID, KEY_ATTRIBUTEVALUE_ID, KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME, KEY_HOWSPECIFIED, KEY_UNIT_ID, KEY_UNIT_NAME, KEY_UNIT_ABBR, KEY_VALUE_TYPE, KEY_ORGANISM_TYPE}, KEY_DATASHEET_ID + "=?",
                    new String[]{String.valueOf(datasheetId)}, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                formAttributes.add(new FormAttribute(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19)));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        List<FormAttribute> orderedFormAttributes = new ArrayList<FormAttribute>();
        List<FormAttribute> parentFormAttributes = new ArrayList<FormAttribute>();

        //Reorder attribute output.
        for (FormAttribute formEntry : formAttributes) {
            if (formEntry.getParentFormEntryID() == null || formEntry.getParentFormEntryID().equalsIgnoreCase("") || formEntry.getParentFormEntryID().equalsIgnoreCase("null")) {
                parentFormAttributes.add(formEntry);
            }
        }

        //Order attributes so that Parent form entries are followed by child form entries
        for (FormAttribute parentFormEntry : parentFormAttributes) {
            orderedFormAttributes.add(parentFormEntry);
            for (FormAttribute childFormEntry : formAttributes) {
                if (childFormEntry.getParentFormEntryID() != null && !childFormEntry.getParentFormEntryID().equalsIgnoreCase("") && childFormEntry.getParentFormEntryID().equalsIgnoreCase(parentFormEntry.getId())) {
                    orderedFormAttributes.add(childFormEntry);
                }
            }

        }
        db.close();
        return orderedFormAttributes;
    }

    public List<FormAttribute> getFormEntriesForParent(String parentFormEntryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<FormAttribute> formAttributes = new ArrayList<FormAttribute>();
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_FORMATTRIBUTES, new String[]{KEY_DATASHEET_ID, KEY_ID, KEY_PICKLIST, KEY_PARENT_FORMENTRY_ID, KEY_SUBPLOTTYPE_ID, KEY_ORDER_NUMBER, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_DESCRIPTION, KEY_ATTRIBUTE_NAME, KEY_ATTRIBUTETYPE_ID, KEY_ATTRIBUTEVALUE_ID, KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME, KEY_HOWSPECIFIED, KEY_UNIT_ID, KEY_UNIT_NAME, KEY_UNIT_ABBR, KEY_VALUE_TYPE, KEY_ORGANISM_TYPE}, KEY_PARENT_FORMENTRY_ID + "=? OR " + KEY_ID + "=?",
                    new String[]{String.valueOf(parentFormEntryId),String.valueOf(parentFormEntryId)}, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                formAttributes.add(new FormAttribute(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19)));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        List<FormAttribute> orderedFormAttributes = new ArrayList<FormAttribute>();
        List<FormAttribute> parentFormAttributes = new ArrayList<FormAttribute>();

        //Reorder attribute output.
        for (FormAttribute formEntry : formAttributes) {
            if (formEntry.getParentFormEntryID() == null || formEntry.getParentFormEntryID().equalsIgnoreCase("") || formEntry.getParentFormEntryID().equalsIgnoreCase("null")) {
                parentFormAttributes.add(formEntry);
            }
        }

        //Order attributes so that Parent form entries are followed by child form entries
        for (FormAttribute parentFormEntry : parentFormAttributes) {
            orderedFormAttributes.add(parentFormEntry);
            for (FormAttribute childFormEntry : formAttributes) {
                if (childFormEntry.getParentFormEntryID() != null && !childFormEntry.getParentFormEntryID().equalsIgnoreCase("") && childFormEntry.getParentFormEntryID().equalsIgnoreCase(parentFormEntry.getId())) {
                    orderedFormAttributes.add(childFormEntry);
                }
            }

        }
        db.close();
        return orderedFormAttributes;
    }
}

