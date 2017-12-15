package com.nrel.citsci;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import CitSciClasses.DataSheet;
import CitSciClasses.DatasheetListAdapter;
import CitSciClasses.Project;
import DB.DataSheetHandler;
import DB.ProjectHandler;

/**
 * Created by Manoj on 1/25/2015.
 */
public class PlaceholderDatasheetFragment extends Fragment {

    private ListView datasheetListView ;
    public DatasheetListAdapter datasheetListAdapter;

    public Button logoutButton;
    public Button homeButton;
    public Button aboutButton;

    public PlaceholderDatasheetFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_datasheet_list, container, false);
        SharedPreferences pref =  getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String selectedProject = pref.getString("SelectedProject","");
        ProjectHandler projectHandler = new ProjectHandler(getActivity());
        Project project = projectHandler.getProject(Integer.parseInt(selectedProject));
        DataSheetHandler dataSheetHandler = new DataSheetHandler(getActivity());
        List dataSheetList;
        dataSheetList = dataSheetHandler.getDataSheetList(Integer.parseInt(selectedProject));
        datasheetListView = ( ListView ) rootView.findViewById(R.id.datasheetList);
        datasheetListAdapter =  new DatasheetListAdapter(getActivity(), R.layout.datasheet_list_item, dataSheetList );
        datasheetListView.setAdapter(datasheetListAdapter);
        datasheetListView.setOnItemClickListener(datasheetListSelectListener);

        logoutButton = (Button) rootView.findViewById(R.id.logout);
        homeButton = (Button)rootView.findViewById(R.id.home);
        aboutButton = (Button) rootView.findViewById(R.id.about);

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

        return rootView;
    }

    private AdapterView.OnItemClickListener datasheetListSelectListener =new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            DataSheet selectedDatasheet = ( DataSheet) datasheetListAdapter.getItem(position);

            SharedPreferences pref = getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("SelectedDatasheet", selectedDatasheet.getDatasheetID());
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
        getActivity().startActivity(mainIntent);
        getActivity().finish();
    }


}