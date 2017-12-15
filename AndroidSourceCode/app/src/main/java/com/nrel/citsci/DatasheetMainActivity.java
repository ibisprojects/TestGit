package com.nrel.citsci;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import CitSciClasses.AttributeValuesPossible;
import CitSciClasses.DataSheet;
import CitSciClasses.FormAttribute;
import CitSciClasses.GPSTracker;
import CitSciClasses.Location;
import CitSciClasses.OrganismList;
import CitSciClasses.SiteCharecteristics;
import CitSciClasses.TempObservationImage;
import DB.AttributeValuesPossibleHandler;
import DB.DataSheetHandler;
import DB.FormAttributeHandler;
import DB.LocationHandler;
import DB.OrganismListHandler;
import DB.SiteCharacteristicsHandler;
import DB.TempObservationImagesHandler;
import Upload.DataSheetObservation;
import Upload.OrganismObservation;
import Upload.XMLPreparer;

public class DatasheetMainActivity extends Activity {

    private String datasheetId;

    public static String imageTempPath =  XMLPreparer.XML_ROOT_DIR+"temp/";
    private DataSheet dataSheet;
    private DataSheetHandler dataSheetHandler;
    private TableLayout datasheetTableLayout;
    private TabHost formTabs;
    private static EditText currentDateText;
    private TextView dateText;
    private LinearLayout dataSheetMainLayout;
    private static String[] requiredFieldNames= {"DatasheetDate","LocationName","Latitude","Longitude","Accuracy" };
    private static List<String> supportedOrganismTypes = Arrays.asList("0","1");
    private static List<String> unsupportedParentFormEntries= new ArrayList<String>();
    private static String tagDelimiter ="##";
    private DataSheetObservation dataSheetObservation = new DataSheetObservation();
    private ArrayList<OrganismObservation> organismObservations = new ArrayList<OrganismObservation>();
    private ImageView observationCameraButton;
    private TextView dataSheetTitleTextView;
    private ImageView observationImageListButton;
    private Button getGPSLocationImageButton;
    private TextView editLabel;
    private ImageView cameraButton;
    private ImageView imageListButton;
    private ImageView divider;
    private String organismType = "";

    public Button logoutButton;
    public Button homeButton;
    public Button aboutButton;

    private FormAttributeHandler formAttributeHandler;
    private OrganismListHandler organismListHandler;
    private AttributeValuesPossibleHandler attributeValuesPossibleHandler;

    private TempObservationImagesHandler tempObservationImagesHandler;

    //Tracks the current index of entries for an organism for tracking entries added after form creation using report another
    private HashMap<String,Integer> currentFormEntryIndexMap = new HashMap<>();

    //Tracks the current index of entries for an organism for tracking entries added after form creation using report another

    private HashMap<String,Integer> currentOrganismIndexMap = new HashMap<>();

    private HashMap<String,String> activityData = new HashMap<>();
    private String CURRENTCAMERAIMAGEPROPERTY = "CURRENTCAMERAIMAGEPROPERTY";
    private String CURRENTCAMERAATTRIBUTEID = "CURRENTCAMERAATTRIBUTEID";
    private String CURRENTCAMERAATTRIBUTEINDEX = "CURRENTCAMERAATTRIBUTEINDEX";
    private String CURRENTCAMERAORGANISMINFOID = "CURRENTCAMERAORGANISMINFOID";

    final int LAUNCH_CAMERA_REQ = 222;
    float scale;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (null != intent) {

            //Get selected project and datasheet
            pref = getSharedPreferences("AppPref", MODE_PRIVATE);
            String ProjectID = pref.getString("SelectedProject", "0");

            if (Integer.parseInt(ProjectID) > 0) {
                dataSheetObservation.setProjectId(ProjectID);
            }else{
                loadMainMenu();
            }
            datasheetId = intent.getStringExtra("SelectedDatasheetID");

            //Get details of the selected datasheet
            dataSheetHandler =  new DataSheetHandler(this);
            dataSheet = dataSheetHandler.getDatasheet(Integer.parseInt(datasheetId));
            dataSheetObservation.setDatasheetName(dataSheet.getName());
            dataSheetObservation.setDatasaheetId(datasheetId);
            setContentView(R.layout.datasheet_main);

            //Setup up the navigation buttons and its actions
            logoutButton = (Button) findViewById(R.id.logout);
            homeButton = (Button) findViewById(R.id.home);
            aboutButton = (Button) findViewById(R.id.about);

            homeButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    loadMainActivity();
                }
            });
            logoutButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    logout();
                }
            });
            aboutButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    loadAboutActivity();
                }
            });

            //Setup internal datasheet main page button
            observationCameraButton = (ImageView) findViewById(R.id.observationsImageButton);
            observationCameraButton.setOnClickListener(new CameraButtonListener("Datasheet_"+dataSheet.getDatasheetID()));

            dataSheetTitleTextView = (TextView) findViewById(R.id.dataSheetTitle);
            dataSheetTitleTextView.setText(dataSheet.getName());

            observationImageListButton = (ImageView) findViewById(R.id.observationsImageListButton);
            observationImageListButton.setOnClickListener(new ImageListButtonListener());

            dateText = (TextView) findViewById(R.id.dateText);
            dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        showDatePickerDialog(v);
                    }
                }
            });

            dataSheetMainLayout = (LinearLayout)  findViewById(R.id.dataSheetLayout);

            scale = this.getResources().getDisplayMetrics().density;
            formAttributeHandler = new FormAttributeHandler(this);
            organismListHandler = new OrganismListHandler(this);
            attributeValuesPossibleHandler = new AttributeValuesPossibleHandler(this);
            tempObservationImagesHandler = new TempObservationImagesHandler(this);
            loadDatasheetToLayout();
        }
    }

    //Clears out temporary images in the temp folder
    public static void clearTempImages(Context context){
        //Log.d("DatasheetMainActivity","Start: clearTempImages");
        TempObservationImagesHandler tempObservationImagesHandler = new TempObservationImagesHandler(context);
        File dir = new File(Environment.getExternalStorageDirectory()+imageTempPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        tempObservationImagesHandler.cleanupTable();
        //Log.d("DatasheetMainActivity","Done: clearTempImages");
    }

    private boolean loadDatasheetToLayout(){

        //Log.d("DatasheetMainActivity","Start: loadDatasheetToLayout");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        //Setup the tabs
        formTabs = (TabHost) findViewById(R.id.tabHost);
        formTabs.setup();
        TabHost.TabSpec ts = formTabs.newTabSpec("mainTab");
        ts.setContent(R.id.tab1);
        ts.setIndicator("Main");
        formTabs.addTab(ts);

        ts = formTabs.newTabSpec("orgTab");
        ts.setContent(R.id.tab2);
        ts.setIndicator("Species");
        formTabs.addTab(ts);
        ts= formTabs.newTabSpec("siteTab");
        ts.setContent(R.id.tab3);
        ts.setIndicator("Site Characteristics");
        formTabs.addTab(ts);

        for(int i=0;i<formTabs.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) formTabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#ffffff"));
        }

        //Setup the main tab
        TableLayout mainTable = (TableLayout) findViewById(R.id.mainFormTable);
        TableRow tRow = new TableRow(this);

        Button saveButton=(Button)findViewById(R.id.submitButton);
        saveButton.setOnClickListener(saveObservationListener);

        //Check if locations are predefined
        if(dataSheet.getPredefined().equalsIgnoreCase("1")){
            //Set up location pick list
            LocationHandler locationHandler = new LocationHandler(this);
            Spinner locationSpinner = new Spinner(this);
            locationSpinner.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            List<Location> LocationList = locationHandler.getLocationsForDatasheet(datasheetId);
            LocationListAdapter spinnerArrayAdapter = new LocationListAdapter(this, R.layout.entry_list_item, LocationList);
            locationSpinner.setAdapter(spinnerArrayAdapter);
            locationSpinner.setTag("LocationSelect");
            editLabel = new TextView(this);
            editLabel.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            editLabel.setText("Location");
            tRow.addView(editLabel);
            tRow.addView(locationSpinner);
            mainTable.addView(tRow);
        }
        else{
            //Set up location input for lat long values
            TextView nameLabelView = new TextView(this);
            nameLabelView.setText("Name");
            TextView latitudeLabelView = new TextView(this);
            latitudeLabelView.setText("Latitude");
            TextView longitudeLabelView = new TextView(this);
            longitudeLabelView.setText("Longitude");
            TextView accuracyLabelView = new TextView(this);
            accuracyLabelView.setText("Accuracy");

            TextView gpsLabelView = new TextView(this);
            gpsLabelView.setText(" ");

            EditText nameEditView = new EditText(this);
            nameEditView.setTag("LocationName");
            nameEditView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            nameEditView.setMaxLines(1);
            nameEditView.setSingleLine(true);
            final EditText latitudeEditView = new EditText(this);
            //latitudeEditView.setKeyListener(SignedDecimalKeyListener.getInstance());
            latitudeEditView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            latitudeEditView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            latitudeEditView.setMaxLines(1);
            latitudeEditView.setSingleLine(true);
            latitudeEditView.setTag("Latitude");
            latitudeEditView.setInputType(getInputType("2"));
            final EditText longitudeEditView = new EditText(this);
            longitudeEditView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            longitudeEditView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            longitudeEditView.setMaxLines(1);
            longitudeEditView.setSingleLine(true);
            longitudeEditView.setTag("Longitude");
            longitudeEditView .setInputType(getInputType("2"));

            //Create GPS location button
            getGPSLocationImageButton = new Button(this);
            getGPSLocationImageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //getGPSLocationImageButton.setScaleType(ImageButton.ScaleType.FIT_START);
            getGPSLocationImageButton.setBackgroundColor(Color.TRANSPARENT);
            Drawable locationIcon = this.getResources().getDrawable( R.mipmap.ic_my_location );
            getGPSLocationImageButton.setCompoundDrawablesWithIntrinsicBounds(locationIcon, null, null, null);
            getGPSLocationImageButton.setTextAppearance(this,android.R.style.TextAppearance_Small);
            getGPSLocationImageButton.setTextColor(this.getResources().getColor(R.color.button_color_2));
            getGPSLocationImageButton.setText("Get Current Location");

            getGPSLocationImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GPSTracker  gpsTracker = new GPSTracker(DatasheetMainActivity.this);
                    if (gpsTracker.canGetLocation()) {

                        String stringLatitude = String.valueOf(gpsTracker.getLatitude());
                        latitudeEditView.setText(stringLatitude);

                        String stringLongitude = String.valueOf(gpsTracker.getLongitude());
                        longitudeEditView.setText(stringLongitude);
                    }
                    else{
                        gpsTracker.showSettingsAlert();
                    }
                }
            });

            tRow = new TableRow(this);
            tRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tRow.addView(nameLabelView);
            tRow.addView(nameEditView);
            mainTable.addView(tRow);

            TableRow tRow1 = new TableRow(this);
            tRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tRow1.addView(latitudeLabelView);
            tRow1.addView(latitudeEditView);
            mainTable.addView(tRow1);

            TableRow tRow2 = new TableRow(this);
            tRow2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tRow2.addView(longitudeLabelView);
            tRow2.addView(longitudeEditView);
            mainTable.addView(tRow2);


            TableRow tRow3 = new TableRow(this);
            tRow3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tRow3.addView(gpsLabelView);
            tRow3.addView(getGPSLocationImageButton);
            mainTable.addView(tRow3);
        }

        //Setup the organism attr tab
        //Log.d("DataSheetMainActivity","Passing data sheet Id "+datasheetId+"to datasheet handler");
        List<FormAttribute> formEntries = formAttributeHandler.getFormEntries(datasheetId);

        //Add all form entries
        addFormEntriesToDatasheet(formEntries, 0, 0);

        //Set up the site char tab

        datasheetTableLayout = (TableLayout)findViewById(R.id.siteCharLayout);
        SiteCharacteristicsHandler siteCharectrisitcsHandler = new SiteCharacteristicsHandler(this);
        List<SiteCharecteristics> siteChars = siteCharectrisitcsHandler.getSiteCharacteristics(datasheetId);
        for(SiteCharecteristics siteChar:siteChars){
            TableRow entryRow = new TableRow(this);
            entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


                editLabel = new TextView(this);
                editLabel.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                String labelText = siteChar.getAttributeName();

                if(siteChar.getUnitID()!=null && !siteChar.getUnitID().equalsIgnoreCase("null") && !siteChar.getUnitID().equalsIgnoreCase("")){
                    labelText += " ("+siteChar.getUnitAbbreviation()+")";
                }


                editLabel.setText(labelText);
                if(siteChar.getHowSpecified() !=null && !siteChar.getHowSpecified().equalsIgnoreCase("2")) {
                    EditText editText = new EditText(this);
                    editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                    editText.setMaxLines(1);
                    editText.setSingleLine(true);
                    editText.setInputType(getInputType(siteChar.getValueType()));

                    if(siteChar.getValueType()!= null && siteChar.getValueType().equalsIgnoreCase("6")){
                        editText.setOnClickListener(new DateTimePickClickListener());
                        editText.setOnFocusChangeListener(new DateTimePickClickListener());
                    }
                    //editText = setInputType(editText,siteChar.getValueType());
                    editText.setTag("SiteChar"+tagDelimiter+siteChar.getAttributeTypeID()+tagDelimiter+siteChar.getAttributeName()+ tagDelimiter + siteChar.getUnitID()+ tagDelimiter + siteChar.getId());
                    entryRow.addView(editLabel);
                    entryRow.addView(editText);
                }
                else if (siteChar.getHowSpecified() !=null && siteChar.getHowSpecified().equalsIgnoreCase("2")){
                    Spinner attributeValueSpinner = new Spinner(this);
                    attributeValueSpinner.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,(int) (1 * scale + 0.5f)));
                    List<AttributeValuesPossible> attributeValuesPossibleList = attributeValuesPossibleHandler.getAttributeValuesPossibleForAttribute(siteChar.getId());
                    AttributeValuesPossibleAdapter spinnerArrayAdapter = new AttributeValuesPossibleAdapter(this, R.layout.entry_list_item, attributeValuesPossibleList);
                    attributeValueSpinner.setAdapter(spinnerArrayAdapter);
                    attributeValueSpinner.setTag("SiteChar"+tagDelimiter+siteChar.getAttributeTypeID()+tagDelimiter+siteChar.getAttributeName()+ tagDelimiter + siteChar.getUnitID()+ tagDelimiter + siteChar.getId());
                    entryRow.addView(editLabel);
                    entryRow.addView(attributeValueSpinner);
                }

            datasheetTableLayout.addView(entryRow);
        }
        //Log.d("DatasheetMainActivity","Done: loadDataSheetToLayout");
        return true;
    }

    private void addFormEntriesToDatasheet(List<FormAttribute> formEntries, int orgCount, int indexOfRow){

        datasheetTableLayout = (TableLayout)findViewById(R.id.attibutesLayout);
        String currentOrganismID = "0";
        int currentEntry = 0;
        int currentEntryIndex = indexOfRow;
        for(final FormAttribute formEntry:formEntries){

            currentEntry++;



            TableRow entryRow = new TableRow(this);
            entryRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            // Check if it is a normal attribute attribute
            if(formEntry.getParentFormEntryID()!=null && !formEntry.getParentFormEntryID().equalsIgnoreCase("")  && !formEntry.getParentFormEntryID().equalsIgnoreCase("null")) {

                //Check if parent form entry is supported type. If so add its attributes to the form
                if(!unsupportedParentFormEntries.contains(formEntry.getParentFormEntryID())) {

                    //get the current index of this parent


                    editLabel = new TextView(this);
                    editLabel.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                    String labelText = formEntry.getAttributeName();

                    if (formEntry.getUnitID() != null && !formEntry.getUnitID().equalsIgnoreCase("null") && !formEntry.getUnitID().equalsIgnoreCase("")) {
                        labelText += " (" + formEntry.getUnitAbbreviation() + ")";
                    }
                    editLabel.setText(labelText);
                    if (formEntry.getHowSpecified() != null && !formEntry.getHowSpecified().equalsIgnoreCase("2")) {
                        //This is a text entry attribute
                        EditText editText = new EditText(this);
                        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                        editText.setMaxLines(1);
                        editText.setSingleLine(true);
                        editText.setInputType(getInputType(formEntry.getValueType()));

                        if (formEntry.getValueType() != null && formEntry.getValueType().equalsIgnoreCase("6")) {
                            editText.setOnClickListener(new DateTimePickClickListener());
                            editText.setOnFocusChangeListener(new DateTimePickClickListener());
                        }
                        //editText = setInputType(editText,formEntry.getValueType());
                        editText.setTag("Organism" + tagDelimiter + formEntry.getParentFormEntryID() + tagDelimiter + formEntry.getAttributeTypeID() + tagDelimiter + formEntry.getAttributeName() + tagDelimiter + formEntry.getUnitID() + tagDelimiter + formEntry.getId() + tagDelimiter + getCurrentIndexForParent(formEntry.getParentFormEntryID()));
                        entryRow.addView(editLabel);
                        entryRow.addView(editText);
                    } else if (formEntry.getHowSpecified() != null && formEntry.getHowSpecified().equalsIgnoreCase("2")) {
                        //This is a list attribute
                        Spinner attributeValueSpinner = new Spinner(this);
                        attributeValueSpinner.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                        List<AttributeValuesPossible> attributeValuesPossibleList = attributeValuesPossibleHandler.getAttributeValuesPossibleForAttribute(formEntry.getId());
                        AttributeValuesPossibleAdapter spinnerArrayAdapter = new AttributeValuesPossibleAdapter(this, R.layout.entry_list_item, attributeValuesPossibleList);
                        attributeValueSpinner.setAdapter(spinnerArrayAdapter);
                        attributeValueSpinner.setTag("Organism" + tagDelimiter + formEntry.getParentFormEntryID() + tagDelimiter + formEntry.getAttributeTypeID() + tagDelimiter + formEntry.getAttributeName() + tagDelimiter + formEntry.getUnitID() + tagDelimiter + formEntry.getId() + tagDelimiter + getCurrentIndexForParent(formEntry.getParentFormEntryID()));
                        entryRow.addView(editLabel);
                        entryRow.addView(attributeValueSpinner);
                    }

                    datasheetTableLayout.addView(entryRow, currentEntryIndex);
                    currentEntryIndex++;



                    //If this is the last entry or the next entry is a parent entry add a report another button
                    if(currentEntry==formEntries.size() || (formEntries.get(currentEntry).getParentFormEntryID()==null || formEntries.get(currentEntry).getParentFormEntryID().equalsIgnoreCase("") || formEntries.get(currentEntry).getParentFormEntryID().equalsIgnoreCase("null")) ){
                        TableRow reportAnotherRow = new TableRow(this);
                        TableRow.LayoutParams layoutParams= new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        reportAnotherRow.setLayoutParams(layoutParams);

                        TableRow addMoreContents = new TableRow(this);
                        TableRow.LayoutParams rlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
                        addMoreContents.setLayoutParams(rlp);

                        ImageView addMore = new ImageView(this);
                        addMore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        addMore.setImageResource(R.drawable.ic_report_another_grey_36dp);
                        addMore.setOnClickListener(new ReportAnotherButtonListener(currentOrganismID, reportAnotherRow));

                        TextView addMoreText = new TextView(this);
                        addMoreText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        addMoreText.setText("Report Another");
                        addMoreText.setTextColor(Color.parseColor("#4B4B4B"));


                        addMoreContents.addView(addMore);
                        addMoreContents.addView(addMoreText);
                        addMoreContents.setOnClickListener(new ReportAnotherButtonListener(currentOrganismID, reportAnotherRow));

                        reportAnotherRow.addView(addMoreContents);

                        datasheetTableLayout.addView(reportAnotherRow, currentEntryIndex);
                        currentEntryIndex++;
                    }
                }
            }
            else{// This is a parent form entry
                organismType = formEntry.getOrganismType();
                currentOrganismID = formEntry.getId();
                //Check if the organism type is supported by the app
                if(supportedOrganismTypes.contains(organismType)) {
                    // Check if the item is a picklist
                    orgCount++;
                    if (organismType.equalsIgnoreCase("1")) {
                        TableRow addRow = new TableRow(this);
                        addRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                        Spinner organismSpinner = new Spinner(this);
                        organismSpinner.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                        List<OrganismList> organismEntryList = organismListHandler.getOrganismEntriesForAttribute(formEntry.getId());
                        OrganismListAdapter spinnerArrayAdapter = new OrganismListAdapter(this, R.layout.entry_list_item, organismEntryList);
                        organismSpinner.setAdapter(spinnerArrayAdapter);
                        organismSpinner.setTag("ParentOrganismOption" + tagDelimiter + formEntry.getId());
                        organismSpinner.setId(getUniqueIdForParentEntryIDAndIndex(formEntry.getId(),getCurrentIndexForParent(formEntry.getId())));
                        organismSpinner.setOnItemSelectedListener(new OrganismChangeListener(formEntry.getId(),String.valueOf(getCurrentIndexForParent(formEntry.getId()))));
                        editLabel = new TextView(this);
                        editLabel.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                        editLabel.setText("");


                        cameraButton = new ImageView(this);
                        cameraButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        cameraButton.setImageResource(R.drawable.ic_photo_camera_grey600_36dp);
                        cameraButton.setOnClickListener(new CameraButtonListener(organismSpinner, String.valueOf(formEntry.getId()),String.valueOf(getCurrentIndexForParent(formEntry.getId()))));

                        imageListButton = new ImageView(this);
                        imageListButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        imageListButton.setImageResource(R.drawable.ic_perm_media_grey600_36dp);
                        imageListButton.setOnClickListener(new ImageListButtonListener());


                        entryRow.addView(organismSpinner);
                        entryRow.addView(cameraButton);
                        entryRow.addView(imageListButton);


                        //Add dividerfor organisms other than the first one
                        if(orgCount>1) {

                            TableRow dividerRow = new TableRow(this);
                            dividerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            divider = new ImageView(this);
                            TableRow.LayoutParams tableLayoutParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 5);
                            tableLayoutParam.setMargins(20, 15, 15, 10);
                            divider.setLayoutParams(tableLayoutParam);
                            divider.setBackgroundColor(getResources().getColor(R.color.divider_color));
                            dividerRow.addView(divider);
                            datasheetTableLayout.addView(dividerRow,currentEntryIndex);
                            currentEntryIndex++;
                        }

                        tempObservationImagesHandler.addFormAttribute(new TempObservationImage(formEntry.getId(),String.valueOf(getCurrentIndexForParent(formEntry.getId())),"","0","","","0"));

                        datasheetTableLayout.addView(entryRow,currentEntryIndex);
                        currentEntryIndex++;

                    } else if (organismType.equalsIgnoreCase("0")) {
                        //This is a single organism

                        //Log.d("DataSheetMainActivity","Parent Not Picklist");

                        editLabel = new TextView(this);
                        editLabel.setTypeface(null, Typeface.BOLD);
                        editLabel.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (int) (1 * scale + 0.5f)));
                        editLabel.setText(formEntry.getOrganismName());
                        //Log.d("DataSheetMainActivity","Org Name: "+ formEntry.getOrganismName());

                        cameraButton = new ImageView(this);
                        cameraButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        cameraButton.setImageResource(R.drawable.ic_photo_camera_grey600_36dp);
                        cameraButton.setOnClickListener(new CameraButtonListener(formEntry.getId(),String.valueOf(getCurrentIndexForParent(formEntry.getId())), formEntry.getOrganismInfoID()));

                        imageListButton = new ImageView(this);
                        imageListButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        imageListButton.setImageResource(R.drawable.ic_perm_media_grey600_36dp);
                        imageListButton.setOnClickListener(new ImageListButtonListener());


                        entryRow.addView(editLabel);
                        entryRow.addView(cameraButton);
                        entryRow.addView(imageListButton);

                        //Add for organisms other than the first one
                        if(orgCount>1) {
                            TableRow dividerRow = new TableRow(this);
                            dividerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            divider = new ImageView(this);
                            TableRow.LayoutParams tableLayoutParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 5);
                            tableLayoutParam.setMargins(20, 15, 15, 10);
                            divider.setLayoutParams(tableLayoutParam);
                            divider.setBackgroundColor(getResources().getColor(R.color.divider_color));

                            dividerRow.addView(divider);
                            datasheetTableLayout.addView(dividerRow, currentEntryIndex);
                            currentEntryIndex++;
                        }

                        tempObservationImagesHandler.addFormAttribute(new TempObservationImage(formEntry.getId(),String.valueOf(getCurrentIndexForParent(formEntry.getId())),String.valueOf(formEntry.getOrganismInfoID()),"0", "", "", "0"));

                        datasheetTableLayout.addView(entryRow, currentEntryIndex);
                        currentEntryIndex++;
                    }
                }
                else{
                    unsupportedParentFormEntries.add(formEntry.getId());
                }
            }

        }
        datasheetTableLayout.invalidate();
    }

    private int getUniqueIdForParentEntryIDAndIndex(String parentFormEntryID, int currentParentIndex) {

        //Set unique id for the entry (limitation is that we can have only 1000 of one form entry)
        return (Integer.parseInt(parentFormEntryID) * 1000) + currentParentIndex;
    }

    //Returns the index of the parent entry. Index starts with 0 and increments when user clicks report another.
    private int getCurrentIndexForParent(String parentFormEntryID) {

        if(!currentFormEntryIndexMap.containsKey(parentFormEntryID))
        {
            currentFormEntryIndexMap.put(parentFormEntryID, 0);
        }
        return currentFormEntryIndexMap.get(parentFormEntryID);
    }

    //Returns the index of the parent entry when user clicks report another.
    private void incrementCurrentIndexForParent(String parentFormEntryID) {

        if (!currentFormEntryIndexMap.containsKey(parentFormEntryID)) {
            currentFormEntryIndexMap.put(parentFormEntryID, 0);
        } else {
            currentFormEntryIndexMap.put(parentFormEntryID, currentFormEntryIndexMap.get(parentFormEntryID) + 1);
        }
    }

    private int getCurrentActualOrganismIndex(String organismID) {

        if(!currentFormEntryIndexMap.containsKey(organismID))
        {
            currentFormEntryIndexMap.put(organismID, 0);
        }
        int returnInt = currentFormEntryIndexMap.get(organismID);
        currentFormEntryIndexMap.put(organismID, returnInt + 1);
        return  returnInt;
    }

    private boolean validateValue(String valueType, String value) {

        //Log.d("DataSheetMainActivity","validateValue: Validating " + value + " for valuetype "+ valueType);
        if(valueType.equalsIgnoreCase("2")||valueType.equalsIgnoreCase("3")){
            try{
                Double d = Double.parseDouble(value);
            }
            catch (Exception e){
                Toast.makeText(this, value + " is not a valid number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(valueType.equalsIgnoreCase("4")){
            Toast.makeText(this, value + " is not a valid boolean. Enter true or false", Toast.LENGTH_SHORT).show();
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
                return true;
            }
        }
        else if(valueType.equalsIgnoreCase("6")){
            try {

                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm aaa");
                dateFormat.setLenient(true);
                dateFormat.parse(value);
            } catch (Exception e) {
                Toast.makeText(this, value + " is not a valid value. Please enter in format MM/DD/YYYY HH:MM AM/PM", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //Log.d("DataSheetMainActivity","Done validateValue: Valid");
        return true;
    }

    private View.OnClickListener saveObservationListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(validateFields()){
                tempObservationImagesHandler.getAllFinalTempObservationImages();
                dataSheetObservation.setOrganismObservations(organismObservations);
                XMLPreparer xmlWriter  = new XMLPreparer(dataSheetObservation, DatasheetMainActivity.this.getApplicationContext());
                try {
                    xmlWriter.createXML();
                    Toast.makeText(DatasheetMainActivity.this,"Observation Saved. Use ‘My Observations’ to upload your saved observation", Toast.LENGTH_SHORT).show();
                    loadMainMenu();
                } catch (SAXException e) {
                    e.printStackTrace();
                    Toast.makeText(DatasheetMainActivity.this,"E103: An error occurred while trying to create the upload file", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DatasheetMainActivity.this,"E104: An error occurred while trying to create the upload file", Toast.LENGTH_SHORT).show();
                }
            }

        }

    };

    //Traverses for non empty attrribute values in a view group recursively and adds to the observation list
    private EditText traverseEditTexts(ViewGroup v)
    {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++)
        {
            Object child = v.getChildAt(i);
            if (child instanceof EditText)
            {
                EditText e = (EditText)child;
                if(e.getText().length() == 0)
                {
                    if(Arrays.asList(requiredFieldNames).contains(e.getTag())) {
                        Toast.makeText(this, e.getTag()+ " cannot be empty.", Toast.LENGTH_SHORT).show();
                        this.dataSheetObservation.clearDatasheet();
                        return e;
                    }
                }
                else{
                    if(!addToObservation(e)){
                        this.dataSheetObservation.clearDatasheet();
                        return e;
                    }
                }
            }
            else if(child instanceof Spinner){
                Spinner spinner = (Spinner) child;
                String spinnerTag = (String) spinner.getTag();
                //If we find the location spinner create corresponding Location text and add to observation
                if(spinnerTag.equalsIgnoreCase("LocationSelect")){
                    EditText locationText = new EditText(this);
                    locationText.setTag("Location");
                    if(spinner.getSelectedItem() instanceof Location){
                            Location selectedLocation  = (Location) spinner.getSelectedItem();
                            if(TextUtils.isEmpty(selectedLocation.getAreadId())) {
                                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                                this.dataSheetObservation.clearDatasheet();
                                return  new EditText(this);
                            }
                            if(selectedLocation.getAreadId()!= null && !selectedLocation.getAreadId().equalsIgnoreCase("")){
                                EditText locationEdit = new EditText(this);
                                locationEdit.setTag("AreaID");
                                locationEdit.setText(selectedLocation.getAreadId());
                                addToObservation(locationEdit);


                                EditText observationName = new EditText(this);
                                observationName.setTag("LocationName");
                                observationName.setText(selectedLocation.getAreaName());
                                addToObservation(observationName);
                            }
                    }
                    else{
                        return locationText;
                    }
                }
                if(spinner.getSelectedItem() instanceof AttributeValuesPossible){
                    EditText e = new EditText(this);
                    e.setText(((AttributeValuesPossible) spinner.getSelectedItem()).getId());
                    e.setTag(((Spinner) child).getTag());
                    if(!addToObservation(e)){
                        return e;
                    }
                }
            }
            else if(child instanceof ViewGroup)
            {
                invalid = traverseEditTexts((ViewGroup)child);
                if(invalid != null)
                {
                    return invalid;
                }
            }
        }
        return invalid;
    }

    private int getInputType(String valueType){
        //Log.d("DataSheetMainActivity","Start: getInputType");
        int inputType = InputType.TYPE_CLASS_TEXT;

        try{
           int valueTypeInt = Integer.parseInt(valueType);
            switch (valueTypeInt){
                case 2:
                    inputType = InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                    break;
                case 3:
                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED;
                    break;
            }
        }
        catch (Exception e){

        }
        //Log.d("DataSheetMainActivity","Done: getInputType");
        return inputType;

    }

    private boolean addToObservation(EditText e) {
        SiteCharacteristicsHandler siteCharacteristicsHandler = new SiteCharacteristicsHandler(this);
        FormAttributeHandler formAttributeHandler = new FormAttributeHandler(this);
        String elementTag =(String) e.getTag();
        if(elementTag!=null) {
            String tagParts[] = elementTag.split(tagDelimiter);
            if (tagParts.length > 0) {
                //check if unit is there
                String unit = "";
                int parentIndex;
                //0Organism|1ParentFormEntryID|2AttributeTypeID|3AttributeName|4UnitID|5Id|6currentParentEntryIndex
                if (tagParts[0].equalsIgnoreCase("Organism")) {
                    if (tagParts.length > 4) {
                        unit= tagParts[4];
                    }
                    if(unit.equalsIgnoreCase("null")){
                        unit = "";
                    }

                    parentIndex = Integer.parseInt(tagParts[6]);

                    //HACK TO MAKE ORGANISMS WORK
                    String organismDetails[] = formAttributeHandler.getOrganismInfo(tagParts[1]);
                    String organismID=organismDetails[0];
                    if(organismID == null || organismID.trim().length()==0){ //this is a organism picklist
                        Spinner organismOptionSpinner = (Spinner) findViewById(getUniqueIdForParentEntryIDAndIndex(tagParts[1], parentIndex));
                        if(organismOptionSpinner.getSelectedItem() instanceof OrganismList){
                            OrganismList selectedOrganism = (OrganismList) organismOptionSpinner.getSelectedItem();
                            organismID = selectedOrganism.getId();
                        }
                    }


                    FormAttribute formAttribute = formAttributeHandler.getFormAttribute(Integer.parseInt(tagParts[5]));
                    if(formAttribute!=null && !validateValue(formAttribute.getValueType(),e.getText().toString())){
                        return false;
                    }
                    addOrganismAttribute(tagParts[1], tagParts[2], tagParts[3],unit, parentIndex,e.getText());
                } else if (tagParts[0].equalsIgnoreCase("SiteChar")) {
                    if (tagParts.length > 3) {
                        unit= tagParts[3];
                    }
                    if(unit.equalsIgnoreCase("null")){
                        unit = "";
                    }

                    SiteCharecteristics siteCharecteristics = siteCharacteristicsHandler.getSiteChar(Integer.parseInt(tagParts[4]));
                    if(siteCharecteristics!=null && !validateValue( siteCharecteristics.getValueType(),e.getText().toString())){
                        return false;
                    }
                    addSiteCharacteristics(tagParts[1], tagParts[2],unit, e.getText());
                } else {
                    addDataSheetAttribute(tagParts[0], e.getText());
                }
            }
        }
        return true;
    }

    private void addDataSheetAttribute(String identifier, Editable text) {
        //Log.d("DataSheetMainActivity","Start: addDataSheetAttribute");
        if(identifier.equalsIgnoreCase("DatasheetDate")){
            dataSheetObservation.setDate(text.toString());
        }
        else if(identifier.equalsIgnoreCase("LocationName")){
            dataSheetObservation.setObservationName(text.toString());
        }
        else if(identifier.equalsIgnoreCase("Latitude")){
            dataSheetObservation.setLatitude(text.toString());
        }
        else if(identifier.equalsIgnoreCase("Longitude")){
            dataSheetObservation.setLongitude(text.toString());
        }
        else if(identifier.equalsIgnoreCase("Accuracy")){
            dataSheetObservation.setAccuracy(text.toString());
        }
        else if(identifier.equalsIgnoreCase("SearchTime")){
            dataSheetObservation.setTime(text.toString());
        }
        else if(identifier.equalsIgnoreCase("Comments")){
            dataSheetObservation.setComments(text.toString());
        }
        else if(identifier.equalsIgnoreCase("AreaID")){
            dataSheetObservation.setAreaID(text.toString());
        }
        //Log.d("DataSheetMainActivity","Done: addDataSheetAttribute");
    }

    private void addSiteCharacteristics(String Id, String name,String unit, Editable text) {
        SiteCharecteristics siteChar = new SiteCharecteristics();
        siteChar.setId(Id);
        siteChar.setAttributeName(name);
        siteChar.setUnitID(unit);
        siteChar.setSelectedValue(text.toString());
        //Log.d("DataSheetMainActivity"," Adding SiteChar" + text + "for" + name);
        dataSheetObservation.addSiteCharecteristics(siteChar);
    }

    private void addOrganismAttribute( String parentFormEntryId, String Id, String name,String unit, int parentEntryIndex, Editable text) {

        String organismDetails[] = formAttributeHandler.getOrganismInfo(parentFormEntryId);
        String organismID=organismDetails[0];
        String organismName=organismDetails[1];
        if(organismID == null || organismID.trim().length()==0){ //this is a organism picklist
            Spinner organismOptionSpinner = (Spinner) findViewById(getUniqueIdForParentEntryIDAndIndex(parentFormEntryId, parentEntryIndex));
            if(organismOptionSpinner.getSelectedItem() instanceof OrganismList){
                OrganismList selectedOrganism = (OrganismList) organismOptionSpinner.getSelectedItem();
                organismID = selectedOrganism.getId();
                organismName = selectedOrganism.getName();
            }
        }
        if(organismID == null || organismID.trim().length()==0){
           //Log.d("DataSheetMainActivity","Failed retrieval");
            return;
        }
        OrganismObservation organismObservation = null;
        int organismIndex = -1;
        for(int i=0;i<organismObservations.size();i++){
            if(organismObservations.get(i).getId().equalsIgnoreCase(organismID) && organismObservations.get(i).getParentEntryIndex() == parentEntryIndex){
                organismIndex = i;
                break;
            }
        }
        if(organismIndex==-1) {
            organismObservation = new OrganismObservation();
            organismObservation.setId(organismID);
            organismObservation.setName(organismName);
            organismObservation.setParentEntryId(parentFormEntryId);
            organismObservation.setParentEntryIndex(parentEntryIndex);
            organismObservations.add(organismObservation);
            organismIndex=organismObservations.size()-1;
            tempObservationImagesHandler.updateEntryIndexForAttribuiteID(parentFormEntryId,parentEntryIndex,getCurrentActualOrganismIndex(organismID));
        }

        FormAttribute formAttribute = new FormAttribute();
        formAttribute.setId(Id);
        formAttribute.setAttributeName(name);
        formAttribute.setUnitID(unit);
        formAttribute.setSelectedValue(text.toString());

        if(!TextUtils.isEmpty(text.toString())) {
            tempObservationImagesHandler.updateDataEnteredForFormEntry(parentFormEntryId, String.valueOf(parentEntryIndex));
        }
        //Log.d("DataSheetMainActivity"," Adding OrgChar" + text + "for" + name + " to organism " + organismObservations.get(organismIndex).getName());
        organismObservations.get(organismIndex).addAttributes(formAttribute);
        //Log.d("DataSheetMainActivity","Size for the organism is now "+ organismObservations.get(organismIndex).getAttributes().size());

    }

    private boolean validateFields()
    {
        this.dataSheetObservation.clearDatasheet();
        EditText testText = traverseEditTexts(dataSheetMainLayout);
        if(testText != null)
        {
                testText.requestFocus();

        }
        return testText == null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datasheet_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View clickedEditText) {
        DialogFragment newFragment = new DatePickerFragment();
        currentDateText =(EditText) clickedEditText;
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public Dialog showDateTimePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.datetimepicker);
        //dialog.setTitle("Select a DateTime");
        dialog.show();
        return dialog;
    }

    private class DateTimePickClickListener implements View.OnClickListener,View.OnFocusChangeListener{
        DatePicker datePicker;
        TimePicker timepicker;
        Button okButton;
        Button cancelButton;


        public void onFocusChange(View view,boolean hasFocus) {
            if(hasFocus){
                onClick(view);
            }

        }

        public void onClick(View view) {
            final Dialog dialog = showDateTimePickerDialog();
            final TextView editTextView = (TextView) view;
            datePicker = (DatePicker) dialog.findViewById(R.id.datePickerDateTime);
            timepicker = (TimePicker) dialog.findViewById(R.id.timePickerDateTime);
            okButton = (Button) dialog.findViewById(R.id.dateTimePickerOK);
            cancelButton = (Button) dialog.findViewById(R.id.dateTimePickerCancel);
            cancelButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                   dialog.dismiss();
                }
            });
            okButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    Calendar cal = Calendar.getInstance();
                    cal.set( cal.YEAR, datePicker.getYear() );
                    cal.set( cal.MONTH, datePicker.getMonth());
                    cal.set( cal.DATE, datePicker.getDayOfMonth());

                    cal.set( cal.HOUR_OF_DAY, timepicker.getCurrentHour());
                    cal.set (cal.MINUTE,timepicker.getCurrentMinute());
                    cal.set (cal.SECOND,1);
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
                    editTextView.setText(format.format(cal.getTime()));
                    dialog.dismiss();
                }
            });
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            currentDateText.setText(String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? You will lose the data entered.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        //Clear temp images
                        clearTempImages(DatasheetMainActivity.this);
                        DatasheetMainActivity.this.finish();
                        loadMainMenu();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class OrganismChangeListener implements  Spinner.OnItemSelectedListener{

        String formEntryID;
        String entryIndex;

        public OrganismChangeListener(String formEntryID, String entryIndex) {
            this.formEntryID = formEntryID;
            this.entryIndex = entryIndex;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            OrganismList selectedOrganism = (OrganismList) adapterView.getItemAtPosition(i);
            tempObservationImagesHandler.updateOrganismForEormEntry(formEntryID, entryIndex, selectedOrganism.getId());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class ReportAnotherButtonListener implements Button.OnClickListener{

        private List<FormAttribute> formAttributes;
        private TableRow reportAnotherRow;
        private  String parentFormEntryID;
        public ReportAnotherButtonListener(String parentFormEntryID, TableRow reportAnotherRow) {
            formAttributes = formAttributeHandler.getFormEntriesForParent(parentFormEntryID);
            this.reportAnotherRow = reportAnotherRow;
            this.parentFormEntryID = parentFormEntryID;
        }

        public void onClick(View arg0) {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);

            incrementCurrentIndexForParent(this.parentFormEntryID);
            datasheetTableLayout = (TableLayout)findViewById(R.id.attibutesLayout);
            int indexOfRow = datasheetTableLayout.indexOfChild(this.reportAnotherRow);
            addFormEntriesToDatasheet(formAttributes, 1, indexOfRow + 1);
        }
    }

    private class ImageListButtonListener implements Button.OnClickListener{

        private ListView imageListView ;
        public ImagesListAdapter imagesListAdapter;

        public void onClick(View arg0) {

            AsyncTask imageLoader = new AsyncTask<Object, Object,  ArrayList<ImageFile>>()
            {
                private ProgressDialog pDialog = new ProgressDialog(DatasheetMainActivity.this);


                @Override
                protected void onPreExecute() {

                    super.onPreExecute();
                    pDialog = new ProgressDialog(DatasheetMainActivity.this);
                    pDialog.setMessage("Loading Images  ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                }

                @Override
                protected ArrayList<ImageFile> doInBackground(Object... params)
                {
                    String imageTempPath = DatasheetMainActivity.imageTempPath;
                    File f = new File(Environment.getExternalStorageDirectory() + imageTempPath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }

                    File file[] = f.listFiles();
                    ArrayList<ImageFile> listOfImages = new ArrayList<ImageFile>();


                    for (int i=0; i < file.length; i++)
                    {
                        listOfImages.add(new ImageFile(file[i]));
                    }
                    return listOfImages;
                }

                @Override
                protected void onPostExecute(ArrayList<ImageFile> listOfImages) {
                    if (listOfImages.size() > 0) {


                        final Dialog dialog = new Dialog(DatasheetMainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.image_list, null, false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setContentView(view);

                        imageListView = (ListView) view.findViewById(R.id.imageList);
                        imagesListAdapter = new ImagesListAdapter(DatasheetMainActivity.this, R.layout.image_list_item, listOfImages);
                        imageListView.setAdapter(imagesListAdapter);


                        Button btnOpenBrowser = (Button) dialog.findViewById(R.id.btn_delete_images);
                        btnOpenBrowser.setOnClickListener(new DeleteImageListener(listOfImages, dialog));

                        Button btnCancel = (Button) dialog.findViewById(R.id.btn_images_ok);
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Close the dialog
                                dialog.dismiss();
                            }
                        });
                        // Display the dialog
                        dialog.show();
                        pDialog.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No photos taken", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
            };
            imageLoader.execute();

        }
        private class DeleteImageListener implements Button.OnClickListener {

            ArrayList<ImageFile> listOfImages;
            private List<ImageFile> selectedFileList = new ArrayList<ImageFile>();
            private Dialog imageListDialog;
            public DeleteImageListener(ArrayList<ImageFile> listOfImages, Dialog imageListDialog){
                this.listOfImages = listOfImages;
                this.imageListDialog = imageListDialog;
            }


            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DatasheetMainActivity.this);
                builder.setMessage("Are you sure you want to delete?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getSelectedFiles();
                                File selectedFile;
                                for (int i = 0; i < selectedFileList.size(); i++) {
                                    selectedFile = selectedFileList.get(i).getFile();
                                    if (selectedFile.exists()) {
                                        selectedFile.delete();
                                        tempObservationImagesHandler.deleteImage(selectedFile.getAbsolutePath());
                                    }
                                }
                                imageListDialog.dismiss();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

            private void getSelectedFiles() {
                selectedFileList.clear();
                for (int i = 0; i < imagesListAdapter.getCount(); i++) {
                    ImageFile selectedFile = imagesListAdapter.getItem(i);
                    if (selectedFile.isChecked()) {
                        selectedFileList.add(selectedFile);
                    }
                }

            }
    }
    }

    private class CameraButtonListener implements Button.OnClickListener{

        private String fileName;
        private String imagePath = DatasheetMainActivity.imageTempPath;
        private File imageFile;
        private Boolean isForOrganismPickList=false;
        private Spinner organismSpinner = null;
        private String formEntryID = null;
        private String formEntryIndex = null;
        private String organismInfoID = null;

        CameraButtonListener(String fileName){
            this.fileName = fileName;
        }

        CameraButtonListener(String formEntryId, String formEntryIndex, String organismInfoID){
            this.formEntryID = formEntryId;
            this.formEntryIndex = formEntryIndex;
            this.organismInfoID = organismInfoID;
        }

        CameraButtonListener(Spinner pickList, String formEntryId, String formEntryIndex){
            this.isForOrganismPickList = true;
            this.organismSpinner = pickList;
            this.formEntryID = formEntryId;
            this.formEntryIndex = formEntryIndex;
        }

        private void nameFile(){
            File rootDir = new File(Environment.getExternalStorageDirectory() + imagePath);
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }
            if(TextUtils.isEmpty(fileName)){
                this.fileName = UUID.randomUUID().toString();
            }
            imageFile = new File(Environment.getExternalStorageDirectory() + imagePath + fileName + ".jpg");

        }

        public void onClick(View arg0) {
            //create parameters for Intent with filename
            if (this.isForOrganismPickList) {
                this.organismInfoID = ((OrganismList) organismSpinner.getSelectedItem()).getId();
                try {
                    Integer.parseInt(this.organismInfoID);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Please select an organism", Toast.LENGTH_SHORT).show();
                    return;
                }
                //this.fileName = "Datasheet_Organism_" + organismId + "_" + organismIndex;
            }
            nameFile();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION,"CitSci Image Capture");
            //create new Intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            activityData.put(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAIMAGEPROPERTY, imageFile.getAbsolutePath());
            activityData.put(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAATTRIBUTEID, this.formEntryID);
            activityData.put(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAATTRIBUTEINDEX, this.formEntryIndex);
            activityData.put(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAORGANISMINFOID, this.organismInfoID);
            startActivityForResult(intent, LAUNCH_CAMERA_REQ);
            this.fileName ="";

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        int maxSize = 1200;
        //Check that request code matches our camera launch request code
        if (requestCode == LAUNCH_CAMERA_REQ && resultCode == RESULT_OK)
        {

            String fileName = activityData.get(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAIMAGEPROPERTY);
            String formEntryID = activityData.get(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAATTRIBUTEID);
            String formEntryIndex = activityData.get(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAATTRIBUTEINDEX);
            String organismInfoID = activityData.get(LAUNCH_CAMERA_REQ + "." + CURRENTCAMERAORGANISMINFOID);

            Log.d("111", "new temp image for " + organismInfoID + " " +fileName);
            if(!TextUtils.isEmpty(formEntryID)) {
                TempObservationImage tempObservationImage = new TempObservationImage(formEntryID,formEntryIndex, organismInfoID, "1", fileName, "", "0");
                tempObservationImagesHandler.addFormAttribute(tempObservationImage);
                tempObservationImagesHandler.updateDataEnteredForFormEntry(formEntryID, formEntryIndex);
            }

            Bitmap capturedImage= BitmapFactory.decodeFile(fileName);
            boolean resizeRequired = false;

            int outWidth;
            int outHeight;
            int inWidth = capturedImage.getWidth();
            int inHeight = capturedImage.getHeight();
            if(inWidth > inHeight){
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
                resizeRequired= true;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
                resizeRequired = true;
            }
            if(resizeRequired) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(capturedImage, outWidth, outHeight, false);

                File file = new File(fileName);
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    capturedImage.recycle();
                    scaledBitmap.recycle();
                } catch (Exception e) {
                }
            }
        }
    }

    protected void loadMainMenu() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(mainIntent);
        this.finish();
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? You will lose the data entered.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        //Clear temp images
                        clearTempImages(DatasheetMainActivity.this);
                        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        edit.commit();
                        Intent mainIntent = new Intent(DatasheetMainActivity.this, MyActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DatasheetMainActivity.this.startActivity(mainIntent);
                        DatasheetMainActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void loadMainActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? You will lose the data entered.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        //Clear temp images
                        clearTempImages(DatasheetMainActivity.this);
                        Intent mainIntent = new Intent(DatasheetMainActivity.this, MyActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DatasheetMainActivity.this.startActivity(mainIntent);
                        DatasheetMainActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void loadAboutActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? You will lose the data entered.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent mainIntent = new Intent(DatasheetMainActivity.this, AboutActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DatasheetMainActivity.this.startActivity(mainIntent);
                        DatasheetMainActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
