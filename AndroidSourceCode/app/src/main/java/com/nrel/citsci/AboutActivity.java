package com.nrel.citsci;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends Activity {

    public Button logoutButton;
    public Button homeButton;
    public Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_main);
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


    @Override
    public void onBackPressed() {
        loadMainActivity();
    }

    public void loadMainActivity() {
        Intent mainIntent = new Intent(AboutActivity.this, MyActivity.class);
        AboutActivity.this.startActivity(mainIntent);
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
                        Intent mainIntent = new Intent(AboutActivity.this, MyActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AboutActivity.this.startActivity(mainIntent);
                        AboutActivity.this.finish();
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
