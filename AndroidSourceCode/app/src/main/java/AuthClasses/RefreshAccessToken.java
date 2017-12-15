package AuthClasses;

/**
 * Created by manojsre on 8/8/2014.
 */

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import CitSciClasses.APIURL;
import com.nrel.citsci.MyActivity;

public class RefreshAccessToken extends AsyncTask<String, String, Boolean> {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private SharedPreferences pref;
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    DefaultHttpClient httpClient;
    HttpPost httpPost;
    private static String refreshURL = APIURL.REFRESH_URL;

    public RefreshAccessToken(SharedPreferences pref) {
        this.pref = pref;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        boolean retValue = refreshToken(this.pref);
        return retValue;
    }

    public boolean refreshToken(SharedPreferences pref) {
        // Making HTTP request
        try {
            boolean isRefreshRequired = true;
            if (isRefreshRequired) {
                // DefaultHttpClient
                httpClient = new DefaultHttpClient();
                httpPost = new HttpPost(refreshURL);
                params.add(new BasicNameValuePair("client_id", MyActivity.CLIENT_ID));
                params.add(new BasicNameValuePair("client_secret", MyActivity.CLIENT_SECRET));
                params.add(new BasicNameValuePair("redirect_uri", MyActivity.REDIRECT_URI));
                params.add(new BasicNameValuePair("grant_type", MyActivity.GRANT_REFRESH_TYPE));
                params.add(new BasicNameValuePair("refresh_token", pref.getString("RefreshToken","")));
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            e.getMessage();
            Log.e("GetAccessToken:", "Error converting result " + e.toString());
            return false;
        }
        // Parse the String to a JSON Object
        try {
            jObj = new JSONObject(json);
            String accessTok = jObj.getString("access_token");
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("AccessToken", accessTok);
            edit.commit();
        } catch (Exception e) {
            Log.e("RefreshAccessToken", json);
            Log.e("RefreshAccessToken", "Error parsing data " + e.toString());
            return false;
        }
        // Return JSON String
        Log.v("GetCitSciApiData", "Refreshing End");
        return true;
    }
}