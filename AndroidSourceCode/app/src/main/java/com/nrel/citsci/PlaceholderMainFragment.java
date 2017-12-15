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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import CitSciClasses.DataSheet;
import CitSciClasses.Project;
import DB.DataSheetHandler;
import DB.ProjectHandler;

/**
 * Created by Manoj on 1/25/2015.
 */
public class PlaceholderMainFragment extends Fragment {



    private View setProjectView;
    private View setDatasheetView;
    private View viewObservationsView;
    private TextView homeTitleTextView;
    private TextView selectedProjectNameTextView;
    private TextView selectedDataSheetNameTextView;
    private Button addObservationsButton;

    public Button logoutButton;
    public Button aboutButton;


    private SharedPreferences pref;
    public PlaceholderMainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        pref =  getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);

        setProjectView = (View) rootView.findViewById(R.id.setProjectView);
        setProjectView.setOnClickListener(new SetProjectClickListener());

        setDatasheetView = (View) rootView.findViewById(R.id.setDatasheetView);
        setDatasheetView.setOnClickListener(new SetDatashseetClickListener());

        homeTitleTextView = (TextView) rootView.findViewById(R.id.homeTitle);
        homeTitleTextView.setText("Welcome " + pref.getString("UserName", ""));

        viewObservationsView = (View) rootView.findViewById(R.id.viewObservationsView);
        viewObservationsView.setOnClickListener(new ViewObservationsClickListener());


        logoutButton = (Button) rootView.findViewById(R.id.logout);
        aboutButton = (Button) rootView.findViewById(R.id.about);


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

        String selectedDatasheet =pref.getString("SelectedDatasheet", "");
        String selectedProject =pref.getString("SelectedProject", "");

        getActivity().setTitle("Home");

        try{
            ProjectHandler projectHandler = new ProjectHandler(getActivity());
            Project project = projectHandler.getProject(Integer.parseInt(selectedProject));
            selectedProject = project.getProjectName();

        }catch (Exception e){
            selectedProject ="";
        }

        try{

            DataSheetHandler dataSheetHandler = new DataSheetHandler(getActivity());
            DataSheet dataSheet = dataSheetHandler.getDatasheet(Integer.parseInt(selectedDatasheet));
            selectedDatasheet= dataSheet.getName();

        }catch (Exception e){
            selectedDatasheet="" ;
        }


        selectedProjectNameTextView= (TextView) rootView.findViewById(R.id.selectedProjectName);
        selectedProjectNameTextView.setText(selectedProject);

        selectedDataSheetNameTextView= (TextView) rootView.findViewById(R.id.selectedDataSheetName);
        selectedDataSheetNameTextView.setText(selectedDatasheet);

        addObservationsButton = (Button) rootView.findViewById(R.id.addObservationButton);
        addObservationsButton.setOnClickListener(new AddObservationClickListener());

        return rootView;
    }

    private class AddObservationClickListener implements Button.OnClickListener{

        public void onClick(View arg0) {
            pref =  getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            String selectedDatasheet = pref.getString("SelectedDatasheet","0");
            //Load the datasheet page here
            try{
                if(Integer.parseInt(selectedDatasheet)>0) {
                    Intent datasheetIntent = new Intent(getActivity(), DatasheetMainActivity.class);
                    datasheetIntent.putExtra("SelectedDatasheetID", selectedDatasheet);
                    Log.d("Selected DS1", selectedDatasheet);
                    DatasheetMainActivity.clearTempImages(getActivity());
                    getActivity().startActivity(datasheetIntent);
                    getActivity().finish();
                }
                else{
                    Toast.makeText(getActivity(), "Please select a datasheet before adding an observations", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e) {
                Toast.makeText(getActivity(), "Please select a datasheet before adding an observations" , Toast.LENGTH_SHORT).show();
            }
        }

    };

    private class SetDatashseetClickListener implements View.OnClickListener {
        public void onClick(View arg0) {
            String selectedProject = pref.getString("SelectedProject", "");
            try {
                if (Integer.parseInt(selectedProject) > 0) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    PlaceholderDatasheetFragment datasheetListListFragment = new PlaceholderDatasheetFragment();
                    Fragment currentFrag = getFragmentManager().findFragmentByTag("current");
                    if (currentFrag != null) {
                        //fragmentTransaction.remove(currentFrag);
                    }
                    fragmentTransaction.replace( R.id.container,datasheetListListFragment ).addToBackStack("current").commit();
                    //fragmentTransaction.add(R.id.container, datasheetListListFragment, "current").commit();
                } else {
                    Toast.makeText(getActivity(), "Please select a project before setting a datasheet", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Please select a project before setting a datasheet", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class SetProjectClickListener implements View.OnClickListener{
        public void onClick(View arg0) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            PlaceholderProjectListFragment projectListFragment = new PlaceholderProjectListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("refreshprojects", "no");
            projectListFragment.setArguments(bundle);

            Fragment currentFrag =  getFragmentManager().findFragmentByTag("current");
            if (currentFrag != null) {
                //fragmentTransaction.remove(currentFrag);
            }
            fragmentTransaction.replace( R.id.container,projectListFragment ).addToBackStack( "current" ).commit();
            //fragmentTransaction.add(R.id.container, projectListFragment, "current").commit();
        }
    }

    private class ViewObservationsClickListener implements Button.OnClickListener{
        public void onClick(View arg0) {
            /*
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment currentFrag =  getFragmentManager().findFragmentByTag("current");
            if (currentFrag != null) {
                fragmentTransaction.remove(currentFrag);
            }
            fragmentTransaction.commit();*/

            Intent observationsIntent = new Intent(getActivity(), ObservationsActivity.class);
            getActivity().startActivity(observationsIntent);
            getActivity().finish();
        }
    }

    public void loadMainActivity() {
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {                        //do finish
                        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
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
                        //do nothing
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