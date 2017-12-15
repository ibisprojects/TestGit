package com.nrel.citsci;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import AuthClasses.RefreshAccessToken;
import CitSciClasses.DataSheet;
import CitSciClasses.MinAppCheck;
import CitSciClasses.ObservationFile;
import CitSciClasses.ObservationsListAdapter;
import CitSciClasses.UploadAPIData;
import DB.DataSheetHandler;
import Upload.XMLPreparer;


public class ObservationsActivity extends Activity {

    private List<ObservationFile> observationFileList = new ArrayList<ObservationFile>();
    private List<ObservationFile> selectedFileList = new ArrayList<ObservationFile>();
    public ObservationsListAdapter observationsListAdapter;
    private ListView observationListView;
    private Button deleteButton;
    private Button uploadButton;
    public Button logoutButton;
    public Button homeButton;
    public Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observations_main);
        getFileList();

        observationListView = (ListView) findViewById(R.id.observationList);
        observationsListAdapter = new ObservationsListAdapter(this, R.layout.observation_list_item, observationFileList);
        observationListView.setAdapter(observationsListAdapter);
        observationListView.setOnItemClickListener(observationListSelectListener);

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new DeleteObservationListener());

        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new UploadObservationListener());

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



    }

    private void getFileList() {
        observationFileList.clear();
        String path = Environment.getExternalStorageDirectory().toString() + XMLPreparer.XML_ROOT_DIR;
        String fileName;
        String dataSheetID;
        String displayName;
        DataSheetHandler dataSheetHandler = new DataSheetHandler(this);
        DataSheet dataSheet;
        File obsDir = new File(path);
        if (!obsDir.exists()) {
            obsDir.mkdirs();
        }
        File file[] = obsDir.listFiles();

        Date lastModDate;
        if(file != null){
            for (int i = 0; i < file.length; i++) {
                fileName = file[i].getName();
                if (file[i].getPath().endsWith(".xml")) {
                    lastModDate = new Date(file[i].lastModified());
                    dataSheetID = file[i].getName().split("_")[1];
                    displayName = file[i].getName().split("_")[0];
                    try {
                        dataSheet = dataSheetHandler.getDatasheet(Integer.parseInt(dataSheetID));
                        displayName += "_" + dataSheet.getName();
                    } catch (Exception e) {
                        displayName = file[i].getName();
                    }
                    observationFileList.add(new ObservationFile(fileName, displayName, lastModDate.toString()));
                }

            }
        }
    }

    private void getSelectedFiles() {
        selectedFileList.clear();
        for (int i = 0; i < observationsListAdapter.getCount(); i++) {
            ObservationFile selectedFile = (ObservationFile) observationsListAdapter.getItem(i);
            if (selectedFile.isChecked()) {
                selectedFileList.add(selectedFile);
            }
        }

    }

    public void refreshFileList() {
        getFileList();
        ObservationsListAdapter observationsListAdapter = new ObservationsListAdapter(ObservationsActivity.this, R.layout.observation_list_item, observationFileList);
        observationListView.setAdapter(observationsListAdapter);
        observationListView.setOnItemClickListener(observationListSelectListener);
        observationsListAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener observationListSelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View item,
                                int position, long id) {
            ObservationFile clickedFile = (ObservationFile) observationsListAdapter.getItem(position);
            //Log.d("ObservationsActivity"," Clicked" + clickedFile.getDisplayName());
            clickedFile.toggleChecked();
            //Log.d("ObservationsActivity"," Clicked" + clickedFile.isChecked());
            clickedFile.getCheckBox().setChecked(clickedFile.isChecked());
            //Log.d("ObservationsActivity"," Checked? " + clickedFile.getCheckBox().isChecked());
            //Log.d("ObservationsActivity"," ID" + clickedFile.getCheckBox().getId());
        }
    };

    private class UploadObservationListener implements Button.OnClickListener {
        public void onClick(View arg0) {

            boolean appCheck = false;
            try {
                appCheck = new MinAppCheck().execute().get();
            } catch (Exception e) {
            }
            if(appCheck) {
                getSelectedFiles();
                SharedPreferences pref = ObservationsActivity.this.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                String token = pref.getString("AccessToken", "");
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
                    Toast.makeText(ObservationsActivity.this, MyActivity.TOKEN_FAILURE_TEXT, Toast.LENGTH_SHORT).show();
                    loadMainActivity();

                }
                UploadAPIData uploadAPIData =   new UploadAPIData(selectedFileList, token, ObservationsActivity.this);
                uploadAPIData.execute(ObservationsActivity.this);

            }
            else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ObservationsActivity.this);
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
    }



    private class DeleteObservationListener implements Button.OnClickListener {
        public void onClick(View arg0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ObservationsActivity.this);
            builder.setMessage("Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getSelectedFiles();
                            for (int i = 0; i < selectedFileList.size(); i++) {
                                deleteObservation(selectedFileList.get(i));
                            }
                            getFileList();
                            observationsListAdapter = new ObservationsListAdapter(ObservationsActivity.this, R.layout.observation_list_item, observationFileList);
                            observationListView.setAdapter(observationsListAdapter);
                            observationListView.setOnItemClickListener(observationListSelectListener);
                            observationsListAdapter.notifyDataSetChanged();
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

    public static void deleteObservation(ObservationFile selectedFile) {
        File xmlFile = new File(Environment.getExternalStorageDirectory() + XMLPreparer.XML_ROOT_DIR + selectedFile.getFileName());
        String imageFolder = FilenameUtils.removeExtension(xmlFile.getName());
        if (xmlFile.exists()) {
            xmlFile.delete();
        } else {
            Log.d("ObservationsActivity", "Already Deleted? " + selectedFile.getFileName());
        }
        clearImages(XMLPreparer.XML_ROOT_DIR+ imageFolder+ "/");

    }

    public static void clearImages(String imageTempPath){
        File dir = new File(Environment.getExternalStorageDirectory()+imageTempPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
            dir.delete();
        }
    }


    @Override
    public void onBackPressed() {
        loadMainActivity();
    }

    public void loadMainActivity() {
        Intent mainIntent = new Intent(ObservationsActivity.this, MyActivity.class);
        ObservationsActivity.this.startActivity(mainIntent);
        this.finish();
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        SharedPreferences pref = getSharedPreferences("AppPref", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        edit.commit();
                        Intent mainIntent = new Intent(ObservationsActivity.this, MyActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ObservationsActivity.this.startActivity(mainIntent);
                        ObservationsActivity.this.finish();
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
        Intent mainIntent = new Intent(ObservationsActivity.this, AboutActivity.class);
        ObservationsActivity.this.startActivity(mainIntent);
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_observations, menu);
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
}
