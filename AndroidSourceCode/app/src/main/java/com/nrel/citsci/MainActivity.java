package com.nrel.citsci;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends Activity {




    SharedPreferences pref;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            pref = getSharedPreferences("AppPref", MODE_PRIVATE);
            String ProjectID = pref.getString("SelectedProject", "0");
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            if (Integer.parseInt(ProjectID) > 0) {
                Fragment mainFragment= new PlaceholderMainFragment();
                fragmentTransaction.add(R.id.container, mainFragment,"current").commit();
            } else {
                Fragment projectListFragment = new PlaceholderProjectListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("refreshprojects", "yes");
                projectListFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.container,projectListFragment,"current").commit();
            }

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
