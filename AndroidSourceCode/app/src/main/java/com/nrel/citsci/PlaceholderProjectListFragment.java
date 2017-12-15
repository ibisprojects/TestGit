package com.nrel.citsci;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;


import AuthClasses.RefreshAccessToken;
import CitSciClasses.APIURL;
import CitSciClasses.AttributeValuesPossible;
import CitSciClasses.DataSheet;
import CitSciClasses.FormAttribute;
import CitSciClasses.GetCitSciAPIData;
import CitSciClasses.Location;
import CitSciClasses.MinAppCheck;
import CitSciClasses.OrganismList;
import CitSciClasses.Project;
import CitSciClasses.ProjectListAdapter;
import CitSciClasses.SiteCharecteristics;
import DB.AttributeValuesPossibleHandler;
import DB.DataSheetHandler;
import DB.FormAttributeHandler;
import DB.LocationHandler;
import DB.OrganismListHandler;
import DB.ProjectHandler;
import DB.SiteCharacteristicsHandler;

/**
 * Created by Manoj on 1/25/2015.
 */
public class PlaceholderProjectListFragment extends Fragment {


    private ListView projectListView;
    private boolean refreshProjects = true;
    private ImageButton reloadProjectsButton;

    public Button logoutButton;
    public Button homeButton;
    public Button aboutButton;


    public PlaceholderProjectListFragment() {
    }

    public void setRefreshProjects(boolean refreshProjects){
        this.refreshProjects = refreshProjects;
    }
    //@Override
    //public void onStart(){
    // /   super.onStart();
    //    // Apply any required UI change now that the Fragment is visible.
    //}

    @Override
    public void onStart() {

        super.onStart();
        boolean appCheck = false;
        try {
            appCheck = new MinAppCheck().execute().get();
        } catch (Exception e) {
        }
        if(appCheck) {
            DisplayProgress.showSimpleProgressDialog(getActivity(), "Loading", "Fetching Projects", false);
            //Bundle bundle =  this.getArguments();
            final Handler handler = new Handler();

            new Thread() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity().getWindow() != null)
                                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            getActivity().setTitle("Select A Project");
                        }
                    });
                    SharedPreferences pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);


                    try {


                        Map<String, String> attributes = new HashMap<String, String>();
                        attributes.put("ProjectID", null);

                        ProjectHandler projectHandler = new ProjectHandler(getActivity());
                        List projectList;
                        if (refreshProjects) {

                            boolean tokenRefreshed = false;
                            int iCount = 0;
                            while (iCount < 3) {
                                RefreshAccessToken refreshAccessToken = new RefreshAccessToken(pref);
                                try {
                                    if (refreshAccessToken.execute().get()) {
                                        tokenRefreshed = true;
                                        iCount = 100;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                iCount++;
                            }

                            if (!tokenRefreshed) {
                                Log.v("GetCitSciApiData", "Token was not refreshed");
                                SharedPreferences.Editor edit = pref.edit();
                                edit.clear();
                                edit.commit();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DisplayProgress.removeSimpleProgressDialog();
                                        if (getActivity().getWindow() != null)
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    }
                                });
                                loadMainActivity();

                            }

                            GetCitSciAPIData getProjectList = new GetCitSciAPIData(pref);
                            getProjectList.setApiURL(APIURL.PROJECTSANDDATASHEETS_URL);

                            //Get Profile Details
                            GetCitSciAPIData getUserProfile = new GetCitSciAPIData(pref);
                            getUserProfile.setApiURL(APIURL.USER_URL);

                            JSONObject profileJSON = getUserProfile.execute().get();
                            JSONObject profileData = profileJSON.getJSONObject("data");
                            String userLogin = profileData.getString("Login");
                            String userName = profileData.getString("FirstName");
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("UserName", userName);
                            edit.commit();

                            JSONObject projectJSON = getProjectList.execute().get();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DisplayProgress.changeMessage("Cleaning up");
                                }
                            });

                            projectHandler.cleanUpDatabase();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DisplayProgress.changeMessage("Saving your datasheets");
                                }
                            });
                            projectList = new ArrayList();
                            //Log.d("MainActivity:Obtained JSON string",String.valueOf(projectJSON));
                            JSONArray jsonData = projectJSON.getJSONArray("data");
                            //Log.d("MainActivity",String.valueOf(jsonData.length()));
                            Project project;

                            DataSheet dataSheet;
                            DataSheetHandler dataSheetHandler = new DataSheetHandler(getActivity());
                            FormAttributeHandler formAttributeHandler = new FormAttributeHandler(getActivity());
                            SiteCharacteristicsHandler siteCharacteristicsHandler = new SiteCharacteristicsHandler(getActivity());
                            LocationHandler locationHandler = new LocationHandler(getActivity());
                            AttributeValuesPossibleHandler attributeValuesPossibleHandler = new AttributeValuesPossibleHandler(getActivity());
                            OrganismListHandler organismListHandler = new OrganismListHandler(getActivity());
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject projectDetail = jsonData.getJSONObject(i);
                                project = new Project(projectDetail.getString("ProjectID"), projectDetail.getString("ProjectName"), projectDetail.getString("Status"), projectDetail.getString("Description"), projectDetail.getString("PinLatitude"), projectDetail.getString("PinLongitude"), userLogin);
                                projectList.add(project);
                                projectHandler.smartAdd(project);
                                // Get all datasheets
                                JSONArray datasheetJsonData = projectDetail.getJSONArray("Datasheets");
                                for (int j = 0; j < datasheetJsonData.length(); j++) {
                                    JSONObject dataSheetDetail = datasheetJsonData.getJSONObject(j);
                                    // Create the data sheet object
                                    dataSheet = new DataSheet(projectDetail.getString("ProjectID"), dataSheetDetail.getString("DataSheetID"), dataSheetDetail.getString("Name"), dataSheetDetail.getString("AreaSubTypeID"), dataSheetDetail.getString("Predefined"));
                                    // Check if data sheet has predefined locations
                                    if (dataSheetDetail.getString("Predefined").equalsIgnoreCase("1")) {
                                        JSONArray locations = dataSheetDetail.getJSONArray("locations");
                                        for (int l = 0; l < locations.length(); l++) {
                                            // Create location object
                                            JSONObject location = locations.getJSONObject(l);
                                            Location locationDB = new Location(dataSheetDetail.getString("DataSheetID"), location.getString("AreaID"), location.getString("AreaName"));
                                            //Log.d("MainActivity","Stored location "+ location.getString("AreaName")+" for datasheet "+dataSheetDetail.getString("DataSheetID"));
                                            // Store the location
                                            locationHandler.smartAdd(locationDB);
                                        }
                                    }
                                    dataSheetHandler.smartAdd(dataSheet);

                                    JSONArray attributeArray = dataSheetDetail.getJSONArray("OrgAttributes");
                                    // Find all organism attributes in the datasheet.
                                    for (int k = 0; k < attributeArray.length(); k++) {
                                        String organismName = "";
                                        String unitName = "";
                                        String unitAbbreviation = "";
                                        // Get a attribute
                                        JSONObject attribute = attributeArray.getJSONObject(k);
                                        // Check if organism detail is specified
                                        // Log.d("MainActivity:ProcessingAttribute",attribute.getString("ID"));
                                        if (attribute.getString("OrganismInfoID") != null && !attribute.getString("OrganismInfoID").equalsIgnoreCase("null") && attribute.getString("OrganismInfoID").length() > 0) {
                                            //Log.d("HERE",attribute.getString("OrganismInfoID"));
                                            JSONObject organismDetail = attribute.getJSONObject("OrganismDetails");
                                            organismName = organismDetail.getString("Name");
                                        }
                                        // Check if unit detail is specified
                                        if (attribute.getString("UnitID") != null && attribute.getString("UnitID") != "null") {
                                            JSONObject unitDetail = attribute.getJSONObject("UnitDetails");
                                            unitName = unitDetail.getString("Name");
                                            unitAbbreviation = unitDetail.getString("Abbreviation");
                                        }
                                        // Check if picklist
                                        if (attribute.getString("Picklist").equalsIgnoreCase("1")) {
                                            JSONArray organismListSet = attribute.getJSONArray("OrganismList");
                                            JSONObject organismDetail;
                                            OrganismList organismList;
                                            //Log.d("PlaceholderProjectListFragment","For attribute ID " + attribute.getString("ID"));
                                            for (int o = 0; o < organismListSet.length(); o++) {
                                                organismDetail = organismListSet.getJSONObject(o);
                                                //Log.d("PlaceholderProjectListFragment","Added for "+ attribute.getString("ID") +" Organism "+  organismDetail.getString("ID") + " with name " + organismDetail.getString("Name"));
                                                organismList = new OrganismList(attribute.getString("ID"), organismDetail.getString("ID"), organismDetail.getString("Name"));
                                                organismListHandler.smartAdd(organismList);
                                            }
                                        }
                                        //Check if attribute is picklist
                                        if (attribute.getString("ValueType").equalsIgnoreCase("1")) {
                                            JSONArray attributeValuesPossibleSet = attribute.getJSONArray("AttributeValuesPossible");
                                            JSONObject attributeValueDetail;
                                            AttributeValuesPossible attributeValuesPossible;
                                            //Log.d("MainActivity","Attr"+attribute.getString("ID")+" is picklist");
                                            for (int a = 0; a < attributeValuesPossibleSet.length(); a++) {
                                                attributeValueDetail = attributeValuesPossibleSet.getJSONObject(a);
                                                attributeValuesPossible = new AttributeValuesPossible(attribute.getString("ID"), attributeValueDetail.getString("ID"), attributeValueDetail.getString("Name"), attributeValueDetail.getString("Description"));
                                                //Log.d("MainActivity","Smart adding "+ attributeValueDetail.getString("ID")+attributeValueDetail.getString("Name")+ " for " + attribute.getString("ID"));
                                                attributeValuesPossibleHandler.smartAdd(attributeValuesPossible);
                                            }
                                        }
                                        // Create the attribute object
                                        FormAttribute formAttribute = new FormAttribute(dataSheetDetail.getString("DataSheetID"), attribute.getString("ID"), attribute.getString("Picklist"), attribute.getString("ParentFormEntryID"), attribute.getString("SubplotTypeID"), attribute.getString("OrderNumber"), attribute.getString("MinimumValue"), attribute.getString("MaximumValue"), attribute.getString("Description"), attribute.getString("Name"), attribute.getString("AttributeTypeID"), attribute.getString("AttributeValueID"), attribute.getString("OrganismInfoID"), organismName, attribute.getString("HowSpecified"), attribute.getString("UnitID"), unitName, unitAbbreviation, attribute.getString("ValueType"), attribute.getString("OrganismType"));
                                        // Store the attribute
                                        formAttributeHandler.smartAdd(formAttribute);
                                    }

                                    JSONArray siteCharArray = dataSheetDetail.getJSONArray("SiteChar");
                                    // Find all site characteristics in the datasheet.
                                    for (int k = 0; k < siteCharArray.length(); k++) {
                                        String organismName = "";
                                        String unitName = "";
                                        String unitAbbreviation = "";
                                        // Get a site char
                                        JSONObject attribute = siteCharArray.getJSONObject(k);
                                        // Check if organism detail is specified
                                        //Log.d("MainActivity:ProcessingAttribute",attribute.getString("ID"));

                                        // Check if unit detail is specified
                                        if (attribute.getString("UnitID") != null && attribute.getString("UnitID") != "null") {
                                            JSONObject unitDetail = attribute.getJSONObject("UnitDetails");
                                            unitName = unitDetail.getString("Name");
                                            unitAbbreviation = unitDetail.getString("Abbreviation");
                                        }

                                        //Check if site characteristic is picklist
                                        if (attribute.getString("ValueType") != null && attribute.getString("ValueType").equalsIgnoreCase("1")) {
                                            //Log.d("MainActivity",attribute.getString("ID") + " is a list item");
                                            JSONArray attributeValuesPossibleSet = attribute.getJSONArray("AttributeValuesPossible");
                                            JSONObject attributeValueDetail;
                                            AttributeValuesPossible attributeValuesPossible;
                                            for (int a = 0; a < attributeValuesPossibleSet.length(); a++) {
                                                attributeValueDetail = attributeValuesPossibleSet.getJSONObject(a);
                                                attributeValuesPossible = new AttributeValuesPossible(attribute.getString("ID"), attributeValueDetail.getString("ID"), attributeValueDetail.getString("Name"), attributeValueDetail.getString("Description"));
                                                //Log.d("MainActivity",attribute.getString("ID") + " adding "+ attributeValueDetail.getString("Name"));
                                                attributeValuesPossibleHandler.smartAdd(attributeValuesPossible);
                                            }
                                        }
                                        // Create the attribute object
                                        SiteCharecteristics siteCharecteristic = new SiteCharecteristics(dataSheetDetail.getString("DataSheetID"), attribute.getString("ID"), attribute.getString("Picklist"), attribute.getString("ParentFormEntryID"), attribute.getString("SubplotTypeID"), attribute.getString("OrderNumber"), attribute.getString("MinimumValue"), attribute.getString("MaximumValue"), attribute.getString("Description"), attribute.getString("Name"), attribute.getString("AttributeTypeID"), attribute.getString("AttributeValueID"), attribute.getString("OrganismInfoID"), organismName, attribute.getString("HowSpecified"), attribute.getString("UnitID"), unitName, unitAbbreviation, attribute.getString("ValueType"));
                                        // Store the attribute
                                        siteCharacteristicsHandler.smartAdd(siteCharecteristic);


                                    }
                                }

                            }

                        } else {
                            projectList = projectHandler.getAllProjects();
                        }
                        projectListView = (ListView) getView().findViewById(R.id.projectList);
                        final ProjectListAdapter pAdapter = new ProjectListAdapter(getActivity(), R.layout.project_list_item, projectList);
                        projectListView.setOnItemClickListener(projectListSelectListener);
                        //Run on UI thread
                        projectListView.post(new Runnable() {
                            public void run() {
                                pAdapter.notifyDataSetChanged();
                                projectListView.setAdapter(pAdapter);
                                pAdapter.notifyDataSetChanged();
                                projectListView.invalidateViews();
                            }
                        });

                        logoutButton = (Button) getView().findViewById(R.id.logout);
                        homeButton = (Button) getView().findViewById(R.id.home);
                        aboutButton = (Button) getView().findViewById(R.id.about);

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


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DisplayProgress.removeSimpleProgressDialog();
                            if (getActivity().getWindow() != null)
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                    });


                }

            }.start();

        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Update Required");
            alertDialog.setMessage("A new version of the app is available. Please update the app from the playstore.");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String refreshProjects = getArguments().getString("refreshprojects","yes");

        View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
        reloadProjectsButton = (ImageButton) rootView.findViewById(R.id.reloadProjects);
        reloadProjectsButton.setOnClickListener(new ReloadProjectClickListener());

        if(refreshProjects.equalsIgnoreCase("yes")){
            setRefreshProjects(true);
        }
        else{
            setRefreshProjects(false);
        }
        return rootView;
    }

    private class ReloadProjectClickListener implements Button.OnClickListener{
        public void onClick(View arg0) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            PlaceholderProjectListFragment projectListFragment = new PlaceholderProjectListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("refreshprojects", "yes");
            projectListFragment.setArguments(bundle);
            Fragment currentFrag =  getFragmentManager().findFragmentByTag("current");
            if (currentFrag != null) {
                fragmentTransaction.remove(currentFrag);
            }
            fragmentTransaction.add(R.id.container,projectListFragment,"current").commit();
        }
    }
    private AdapterView.OnItemClickListener projectListSelectListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Project project = (Project) parent.getItemAtPosition(position);
            //Toast.makeText(getActivity(), "You selected : " + project.getProjectName(), Toast.LENGTH_SHORT).show();
            SharedPreferences pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("SelectedProject", project.getProjectID());
            edit.putString("SelectedDatasheet", "");
            edit.commit();
            loadMainMenu();
        }



    };

    public void loadMainMenu() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(mainIntent);
    }

    public void loadMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MyActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Toast.makeText(getActivity(), MyActivity.TOKEN_FAILURE_TEXT, Toast.LENGTH_SHORT).show();
        getActivity().startActivity(mainIntent);
        getActivity().finish();
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        SharedPreferences pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        edit.commit();
                        Intent mainIntent = new Intent(getActivity(), MyActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(mainIntent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void loadAboutActivity() {
        Intent mainIntent = new Intent(getActivity(), AboutActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(mainIntent);
        getActivity().finish();
    }

}
