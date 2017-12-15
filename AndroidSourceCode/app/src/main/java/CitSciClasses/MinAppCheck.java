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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manojsre on 8/19/2014.
 */
public class MinAppCheck extends AsyncTask<Void,Void,Boolean>{

    private Map<String, String> attributes;

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static DefaultHttpClient httpClient;
    static HttpPost httpPost;

    private static final double appVersion = 2.1;


    public Boolean doInBackground(Void... input) {
        // Making HTTP request
        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(APIURL.MIN_APP_REVISION_URL);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
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
            double apiVersion = Double.parseDouble(jObj.getString("message"));
            if(apiVersion == appVersion){
                return true;
            }
        } catch (JSONException e) {
            return true;
        }
        catch (Exception e) {
            return true;
        }
        // Return JSON String
        return false;
    }

}
