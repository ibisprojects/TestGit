package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by manojsre on 8/21/2014.
 */
public class DataBaseHandler extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 18;

    // Database Name
    private static final String DATABASE_NAME = "projectManager";

    //TABLES
    private static final String TBL_DATASHEETS = "TBL_DATASHEETS";
    private static final String TBL_PROJECTS = "TBL_PROJECTS";
    private static final String TBL_FORMATTRIBUTES = "TBL_FormAttributes";
    private static final String TBL_SITECHARACTERISTICS = "TBL_SiteCharacteristics";
    private static final String TBL_LOCATIONS = "TBL_Locations";
    private static final String TBL_ATTR_VALUES_POSSIBLE = "TBL_ATTRVALUESPOSSIBLE";
    private static final String TBL_ORGANISMLIST = "TBL_ORGANISMLIST";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";

    // Project Table Columns names

    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_AREASUBTYPEID = "area_subtype_id";
    private static final String KEY_PREDEFINED = "datasheet_predefined";

    // Project Table Columns names
    private static final String KEY_LOGIN = "login";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";

    // Form Attribute Table Columns names
    private static final String KEY_DATASHEET_ID = "datasheet_id";
    private static final String KEY_PICKLIST = "pricklist";
    private static final String KEY_SUBPLOTTYPE_ID = "subplotTypeID";
    private static final String KEY_PARENT_FORMENTRY_ID = "parent_form_entry";
    private static final String KEY_ORDER_NUMBER = "area_order_number";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String  KEY_ATTRIBUTE_NAME= "attribute_name";
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

    //TBL_Areas
    private static final String KEY_AREAID = "area_id";
    private static final String KEY_AREA_NAME = "area_name";

    //TBL_AttributeValuesPossible
    private static final String KEY_ATTR_ID = "attr_id";

    //TBL_TEMPOBSERVATIONIMAGES
    private static final String TBL_TEMPOBSERVATIONIMAGES = "TBL_TEMPOBSERVATIONIMAGES";
    private static final String KEY_ATTRIBUTE_ID = "attribute_id";
    private static final String KEY_ENTRY_INDEX = "entry_index";
    private static final String KEY_DATAENTERED= "data_entered";
    private static final String KEY_TEMPIMAGENAME = "temp_image_name";
    private static final String KEY_FINALIMAGENAME = "final_image_name";
    private static final String KEY_ATTRIBUTE_INDEX_FINALIZED = "attribute_index_finalized";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //TBL_PROJECTS
        String CREATE_DATASHEET_TABLE = "CREATE TABLE " + TBL_DATASHEETS + "("
                + KEY_ID + " INTEGER,"
                + KEY_PROJECT_ID + " INTEGER,"
                + KEY_NAME + " TEXT, " + KEY_AREASUBTYPEID + " INTEGER, "
                + KEY_PREDEFINED + " INETGER " + ")";
        String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TBL_PROJECTS + "("
                + KEY_ID + " INTEGER," + KEY_NAME + " TEXT, " + KEY_LOGIN + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, " + KEY_STATUS + " INTEGER, "
                + KEY_LAT + " FLOAT, " + KEY_LONG + " FLOAT" + ")";

        //TBL_FormAttr
        String CREATE_FORMATTRIBUTE_TABLE = "CREATE TABLE " + TBL_FORMATTRIBUTES + "("
                + KEY_ID + " INTEGER,"
                + KEY_DATASHEET_ID + " INTEGER,"
                + KEY_PICKLIST + " INTEGER,"
                + KEY_ORDER_NUMBER + " INTEGER,"
                + KEY_SUBPLOTTYPE_ID + " INTEGER,"
                + KEY_PARENT_FORMENTRY_ID + " INTEGER,"
                + KEY_MIN_VALUE + " INTEGER,"
                + KEY_MAX_VALUE + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_ATTRIBUTE_NAME + " TEXT, "
                + KEY_ATTRIBUTETYPE_ID + " INTEGER, "
                + KEY_ATTRIBUTEVALUE_ID + " INTEGER, "
                + KEY_ORGANISM_INFO_ID + " INTEGER, "
                + KEY_ORGANISM_NAME + " TEXT, "
                + KEY_ORGANISM_TYPE + " TEXT, "
                + KEY_HOWSPECIFIED + " TEXT, "
                + KEY_UNIT_ID + " INTEGER,"
                + KEY_VALUE_TYPE + " INTEGER,"
                + KEY_UNIT_NAME + " TEXT, "
                + KEY_UNIT_ABBR + " TEXT " + ")";

        //TBL_SiteChar
        String CREATE_SITECHARACTERISTICS_TABLE = "CREATE TABLE " + TBL_SITECHARACTERISTICS + "("
                + KEY_ID + " INTEGER,"
                + KEY_DATASHEET_ID + " INTEGER,"
                + KEY_PICKLIST + " INTEGER,"
                + KEY_ORDER_NUMBER + " INTEGER,"
                + KEY_SUBPLOTTYPE_ID + " INTEGER,"
                + KEY_PARENT_FORMENTRY_ID + " INTEGER,"
                + KEY_MIN_VALUE + " INTEGER,"
                + KEY_MAX_VALUE + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_ATTRIBUTE_NAME + " TEXT, "
                + KEY_ATTRIBUTETYPE_ID + " INTEGER, "
                + KEY_ATTRIBUTEVALUE_ID + " INTEGER, "
                + KEY_ORGANISM_INFO_ID + " INTEGER, "
                + KEY_ORGANISM_NAME + " TEXT, "
                + KEY_HOWSPECIFIED + " TEXT, "
                + KEY_UNIT_ID + " INTEGER,"
                + KEY_VALUE_TYPE + " INTEGER,"
                + KEY_UNIT_NAME + " TEXT, "
                + KEY_UNIT_ABBR + " TEXT " + ")";

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TBL_LOCATIONS + "("
                + KEY_DATASHEET_ID + " INTEGER,"
                + KEY_AREAID + " INTEGER,"
                + KEY_AREA_NAME + " TEXT " + ")";

        String CREATE_ATTRBUTEVALUESPOSSIBLE_TABLE = "CREATE TABLE " + TBL_ATTR_VALUES_POSSIBLE + "("
                + KEY_ID + " INTEGER,"
                + KEY_ATTR_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT " + ")";

        String CREATE_ORGANISMLIST_TABLE = "CREATE TABLE " + TBL_ORGANISMLIST + "("
                + KEY_ID + " INTEGER,"
                + KEY_ATTR_ID + " INTEGER,"
                + KEY_NAME + " TEXT " + ")";

        String CREATE_TEMPOBSERVATIONIMAGES_TABLE = "CREATE TABLE " + TBL_TEMPOBSERVATIONIMAGES + "("
                + KEY_ATTRIBUTE_ID + " INTEGER,"
                + KEY_ENTRY_INDEX + " INTEGER,"
                + KEY_DATAENTERED + " INTEGER,"
                + KEY_ORGANISM_INFO_ID + " INTEGER,"
                + KEY_TEMPIMAGENAME + " TEXT,"
                + KEY_FINALIMAGENAME + " TEXT,"
                + KEY_ATTRIBUTE_INDEX_FINALIZED + " INTEGER"
                + ")";

        db.execSQL(CREATE_ORGANISMLIST_TABLE);
        db.execSQL(CREATE_ATTRBUTEVALUESPOSSIBLE_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_FORMATTRIBUTE_TABLE);
        db.execSQL(CREATE_SITECHARACTERISTICS_TABLE);
        db.execSQL(CREATE_PROJECTS_TABLE);
        db.execSQL(CREATE_DATASHEET_TABLE);
        db.execSQL(CREATE_TEMPOBSERVATIONIMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TBL_DATASHEETS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_FORMATTRIBUTES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SITECHARACTERISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_ATTR_VALUES_POSSIBLE);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_ORGANISMLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TEMPOBSERVATIONIMAGES);
        // Create tables again
        onCreate(db);
    }

    public static  void cleanUpDatabase(SQLiteDatabase db){
        // Drop older table if existed
        db.execSQL("DELETE FROM " + TBL_DATASHEETS);
        db.execSQL("DELETE FROM " + TBL_PROJECTS);
        db.execSQL("DELETE FROM " + TBL_FORMATTRIBUTES);
        db.execSQL("DELETE FROM " + TBL_SITECHARACTERISTICS);
        db.execSQL("DELETE FROM " + TBL_LOCATIONS);
        db.execSQL("DELETE FROM " + TBL_ATTR_VALUES_POSSIBLE);
        db.execSQL("DELETE FROM " + TBL_ORGANISMLIST);
        db.execSQL("DELETE FROM " + TBL_TEMPOBSERVATIONIMAGES);
    }

}
