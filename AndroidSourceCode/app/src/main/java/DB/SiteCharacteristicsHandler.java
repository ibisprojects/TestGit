package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CitSciClasses.FormAttribute;
import CitSciClasses.SiteCharecteristics;

/**
 * Created by manojsre on 11/1/2014.
 */
public class SiteCharacteristicsHandler extends DataBaseHandler {

    //TABLES
    private static final String TBL_SITECHARACTERISTICS = "TBL_SiteCharacteristics";

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
    private static final String KEY_HOWSPECIFIED = "how_specified";
    private static final String KEY_UNIT_ID = "unit";
    private static final String KEY_UNIT_NAME = "unit_name";
    private static final String KEY_UNIT_ABBR = "unit_abbre";
    private static final String KEY_VALUE_TYPE = "value_type";


    public SiteCharacteristicsHandler(Context context) {
        super(context);
    }


    public void smartAdd(SiteCharecteristics siteCharacteristics) {
        //SQLiteDatabase db = this.getWritableDatabase();
        if (getSiteChar(CommonFunctions.IntegerSmartParse(siteCharacteristics.getId())) != null) {
            updateSiteChar(siteCharacteristics);
        } else {
            addSiteChar(siteCharacteristics);
        }

    }

    // Adding new project
    public void addSiteChar(SiteCharecteristics siteCharacteristic) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getDataSheetId()));
        values.put(KEY_PICKLIST, CommonFunctions.IntegerSmartParse(siteCharacteristic.getPicklist()));
        values.put(KEY_PARENT_FORMENTRY_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getParentFormEntryID()));
        values.put(KEY_SUBPLOTTYPE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getSubplotTypeID()));
        values.put(KEY_ORDER_NUMBER, CommonFunctions.IntegerSmartParse(siteCharacteristic.getOrderNumber()));
        values.put(KEY_MIN_VALUE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getMinValue()));
        values.put(KEY_MAX_VALUE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getMaxValue()));
        values.put(KEY_DESCRIPTION, siteCharacteristic.getDescription());
        values.put(KEY_ATTRIBUTE_NAME, siteCharacteristic.getAttributeName());
        values.put(KEY_ATTRIBUTETYPE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getAttributeTypeID()));
        values.put(KEY_ATTRIBUTEVALUE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getAttributeValueID()));
        values.put(KEY_ORGANISM_INFO_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getOrganismInfoID()));
        values.put(KEY_ORGANISM_NAME, siteCharacteristic.getOrganismName());
        values.put(KEY_HOWSPECIFIED, siteCharacteristic.getHowSpecified());
        values.put(KEY_UNIT_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getUnitID()));
        values.put(KEY_UNIT_NAME, siteCharacteristic.getUnitName());
        values.put(KEY_UNIT_ABBR, siteCharacteristic.getUnitAbbreviation());
        values.put(KEY_VALUE_TYPE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getValueType()));
        // Inserting Row
        db.insert(TBL_SITECHARACTERISTICS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateSiteChar(SiteCharecteristics siteCharacteristic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getId()));
        values.put(KEY_DATASHEET_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getDataSheetId()));
        values.put(KEY_PICKLIST, CommonFunctions.IntegerSmartParse(siteCharacteristic.getPicklist()));
        values.put(KEY_PARENT_FORMENTRY_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getParentFormEntryID()));
        values.put(KEY_SUBPLOTTYPE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getSubplotTypeID()));
        values.put(KEY_ORDER_NUMBER, CommonFunctions.IntegerSmartParse(siteCharacteristic.getOrderNumber()));
        values.put(KEY_MIN_VALUE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getMinValue()));
        values.put(KEY_MAX_VALUE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getMaxValue()));
        values.put(KEY_DESCRIPTION, siteCharacteristic.getDescription());
        values.put(KEY_ATTRIBUTE_NAME, siteCharacteristic.getAttributeName());
        values.put(KEY_ATTRIBUTETYPE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getAttributeTypeID()));
        values.put(KEY_ATTRIBUTEVALUE_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getAttributeValueID()));
        values.put(KEY_ORGANISM_INFO_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getOrganismInfoID()));
        values.put(KEY_ORGANISM_NAME, siteCharacteristic.getOrganismName());
        values.put(KEY_HOWSPECIFIED, siteCharacteristic.getHowSpecified());
        values.put(KEY_UNIT_ID, CommonFunctions.IntegerSmartParse(siteCharacteristic.getUnitID()));
        values.put(KEY_UNIT_NAME, siteCharacteristic.getUnitName());
        values.put(KEY_UNIT_ABBR, siteCharacteristic.getUnitAbbreviation());
        values.put(KEY_VALUE_TYPE, CommonFunctions.IntegerSmartParse(siteCharacteristic.getValueType()));
        // updating row
        int returnInt = db.update(TBL_SITECHARACTERISTICS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(siteCharacteristic.getId())});
        db.close();
        return returnInt;
    }


    // Getting single project
    public SiteCharecteristics getSiteChar(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TBL_SITECHARACTERISTICS, new String[]{KEY_DATASHEET_ID, KEY_ID, KEY_PICKLIST, KEY_PARENT_FORMENTRY_ID, KEY_SUBPLOTTYPE_ID, KEY_ORDER_NUMBER, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_DESCRIPTION, KEY_ATTRIBUTE_NAME, KEY_ATTRIBUTETYPE_ID, KEY_ATTRIBUTEVALUE_ID, KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME, KEY_HOWSPECIFIED, KEY_UNIT_ID, KEY_UNIT_NAME, KEY_UNIT_ABBR, KEY_VALUE_TYPE}, KEY_ID + "=?",

                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        SiteCharecteristics siteChar = new SiteCharecteristics(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return siteChar;
    }

    public List<SiteCharecteristics> getSiteCharacteristics(String datasheetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SiteCharecteristics> siteChars = new ArrayList<SiteCharecteristics>();
        Cursor cursor = null;
        try {
            cursor = db.query(TBL_SITECHARACTERISTICS, new String[]{KEY_DATASHEET_ID, KEY_ID, KEY_PICKLIST, KEY_PARENT_FORMENTRY_ID, KEY_SUBPLOTTYPE_ID, KEY_ORDER_NUMBER, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_DESCRIPTION, KEY_ATTRIBUTE_NAME, KEY_ATTRIBUTETYPE_ID, KEY_ATTRIBUTEVALUE_ID, KEY_ORGANISM_INFO_ID, KEY_ORGANISM_NAME, KEY_HOWSPECIFIED, KEY_UNIT_ID, KEY_UNIT_NAME, KEY_UNIT_ABBR, KEY_VALUE_TYPE}, KEY_DATASHEET_ID + "=?",
                    new String[]{String.valueOf(datasheetId)}, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                siteChars.add(new SiteCharecteristics(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18)));
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return siteChars;
    }
}

