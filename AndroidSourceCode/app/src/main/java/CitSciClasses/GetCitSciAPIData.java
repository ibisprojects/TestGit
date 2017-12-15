package CitSciClasses;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import AuthClasses.RefreshAccessToken;

/**
 * Created by manojsre on 8/19/2014.
 */
public class GetCitSciAPIData extends AsyncTask<Void, Void, JSONObject> {
    private SharedPreferences pref;
    private String apiURL;
    private Map<String, String> attributes;

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    Map<String, String> mapn;
    DefaultHttpClient httpClient;
    HttpPost httpPost;


    public GetCitSciAPIData(SharedPreferences pref) {
        this.pref = pref;
        this.attributes = new HashMap<String, String>();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public JSONObject doInBackground(Void... input) {
        // Making HTTP request
        try {
            // DefaultHttpClient

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(this.apiURL);

            params.clear();
            params.add(new BasicNameValuePair("Token", this.pref.getString("AccessToken", "")));

            //Log.d("GetCitSCiAPIData: Access token is", this.pref.getString("AccessToken", ""));
            //Log.d("GetCitSCiAPIData: Params are ", params.toString());
            for (Map.Entry<String, String> param : attributes.entrySet()) {
                params.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
            }

            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            //Log.d("HTML Response",json.toString());
        } catch (Exception e) {
            e.getMessage();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // Parse the String to a JSON Object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return JSON String
        return jObj;
    }

    protected void onPostExecute(JSONObject json) {

    }
}
