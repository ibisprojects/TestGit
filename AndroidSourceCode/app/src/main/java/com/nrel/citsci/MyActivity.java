package com.nrel.citsci;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import AuthClasses.GetAccessToken;
import CitSciClasses.APIURL;
import CitSciClasses.MinAppCheck;

public class MyActivity extends Activity {

    public static String CLIENT_ID = "SE8vUpYjPZ0TFi4q7aeP";
    public static String CLIENT_SECRET = "d0HaKsG7ubvtce0Z4jhQ";
    public static String REDIRECT_URI = APIURL.REDIRECT_URI;
    public static String GRANT_TYPE = "authorization_code";
    public static String GRANT_REFRESH_TYPE = "refresh_token";
    public static String TOKEN_URL = APIURL.TOKEN_URL;
    public static String OAUTH_URL = APIURL.OAUTH_URL;
    public static String OAUTH_SCOPE = "user";


    public static String TOKEN_FAILURE_TEXT = "Login credentials could not be validated. Please login again.";

    WebView web;
    Button auth;
    SharedPreferences pref;

    private View.OnClickListener signInListener = new View.OnClickListener() {
        Dialog auth_dialog;

        @Override
        public void onClick(View arg0) {

            if (isNetworkAvailable()) {
                boolean appCheck = false;
                try {
                    appCheck = new MinAppCheck().execute().get();
                } catch (Exception e) {
                }
                if(appCheck) {
                    auth_dialog = new Dialog(MyActivity.this);
                    auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    auth_dialog.setContentView(R.layout.authorization);
                    web = (WebView) auth_dialog.findViewById(R.id.authwebv);
                    web.getSettings().setLoadWithOverviewMode(true);
                    web.getSettings().setUseWideViewPort(true);
                    web.getSettings().setJavaScriptEnabled(true);
                    web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                    web.setScrollbarFadingEnabled(false);
                    web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);
                    //Log.d("URL",OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);
                    web.setWebViewClient(new WebViewClient() {
                        boolean authComplete = false;
                        Intent resultIntent = new Intent();

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                        }

                        String authCode;

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            if (url.contains("?code=") && authComplete != true) {
                                Uri uri = Uri.parse(url);
                                authCode = uri.getQueryParameter("code");
                                Log.i("", "CODE : " + authCode);
                                authComplete = true;
                                resultIntent.putExtra("code", authCode);
                                MyActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("Code", authCode);
                                edit.commit();
                                auth_dialog.dismiss();
                                new TokenGet().execute();
                                //Toast.makeText(getApplicationContext(), "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                            } else if (url.contains("error=access_denied")) {
                                Log.i("", "ACCESS_DENIED_HERE");
                                resultIntent.putExtra("code", authCode);
                                authComplete = true;
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                                auth_dialog.dismiss();
                            }
                        }
                    });
                    auth_dialog.show();
                    auth_dialog.setTitle("Authorize CitSci Mobile");
                    auth_dialog.setCancelable(true);
                }
                else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyActivity.this);
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
            else{
                showSettingsAlert();
            }
        }
    };

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager  = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Network Settings");

        // Setting Dialog Message
        alertDialog.setMessage("Internet is not available. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String Code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyActivity.this);
            pDialog.setMessage("Contacting CitSci ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            Code = pref.getString("Code", "");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            GetAccessToken jParser = new GetAccessToken();
            JSONObject json = jParser.gettoken(TOKEN_URL, Code, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null) {
                try {
                    String accessTok = json.getString("access_token");
                    String expiresIn = json.getString("expires_in");
                    String expires = json.getString("expires");
                    String refreshTok = json.getString("refresh_token");
                    //Log.d("MyActivity: Token Access", accessTok);
                   // Log.d("MyActivity: Expire", expiresIn);
                    //Log.d("MyActivity: Refresh", refreshTok);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.remove("Code");
                    edit.putString("AccessToken", accessTok);
                    edit.putString("RefreshToken", refreshTok);
                    edit.commit();
                    loadMainMenu();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }

    protected void loadMainMenu() {
        Intent mainIntent = new Intent(MyActivity.this, MainActivity.class);
        //mainIntent.putExtra("key", value); //Optional parameters
        MyActivity.this.startActivity(mainIntent);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        //SharedPreferences.Editor edit = pref.edit();
        //edit.clear();
        //edit.commit();

        String RefreshToken = pref.getString("RefreshToken", "");
        //Log.d("MyActivity: RefreshToken",RefreshToken);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home);
        auth = (Button) findViewById(R.id.signin);
        auth.setOnClickListener(signInListener);
        if (RefreshToken.length() > 0) {
            loadMainMenu();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }
}
