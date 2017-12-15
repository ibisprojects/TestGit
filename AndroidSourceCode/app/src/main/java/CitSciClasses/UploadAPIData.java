package CitSciClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Upload.XMLPreparer;
import com.nrel.citsci.ObservationsActivity;

/**
 * Created by manojsre on 8/19/2014.
 */
public class UploadAPIData extends AsyncTask<ObservationsActivity, String, ObservationsActivity> {
    private String token;
    private String apiURL = APIURL.UPLOAD_URL;
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private List<ObservationFile> selectedFileList;
    private ProgressDialog pDialog;
    private Activity activity;
    private String uploadMessage = "";
    private int uploadCounter =1;
    public UploadAPIData(List<ObservationFile> selectedFileList, String token,Activity activity) {
        this.selectedFileList = selectedFileList;
        this.token = token;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Loading Images  ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public ObservationsActivity doInBackground(ObservationsActivity... input) {

        for (int i = 0; i < selectedFileList.size(); i++) {
            publishProgress("Uploading " + (i + 1) + " of " + selectedFileList.size() + " observations");
            ObservationFile selectedFile = selectedFileList.get(i);
            File xmlFile = new File(Environment.getExternalStorageDirectory() + XMLPreparer.XML_ROOT_DIR + selectedFile.getFileName());
            File[] imageFiles = getImagesForObservation(getFileNameWithoutExtension(xmlFile));
            if (xmlFile.exists()) {
                // Making HTTP request
                try {
                    // DefaultHttpClient
                    int TIMEOUT_MILLISEC = 600000;
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpClient client = new DefaultHttpClient(httpParams);
                    HttpPost request = new HttpPost(this.apiURL);
                    MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();

                    ConnManagerParams.setMaxTotalConnections(httpParams, 5);
                    ConnManagerParams.setTimeout(httpParams, TIMEOUT_MILLISEC);


                    Log.v("AccessToken",token);
                    if (xmlFile != null) {
                        StringBody tokenBody = new StringBody(token, ContentType.TEXT_PLAIN);
                        //Log.v("UploadAPIData: Token: ", token);
                        //Log.v("UploadAPIData:NumFile: ", String.valueOf(imageFiles.length));
                        //Log.v("UploadAPIData:XMLFile: ", xmlFile.getAbsolutePath());
                        StringBody numFilesBody = new StringBody("" + imageFiles.length, ContentType.TEXT_PLAIN);
                        mpEntity.addPart("Token", tokenBody);
                        mpEntity.addPart("NumFiles", numFilesBody);
                        mpEntity.addPart("XMLData ", new ByteArrayBody(FileUtils.readFileToByteArray(xmlFile), ContentType.TEXT_XML, xmlFile.getName()));

                        mpEntity.addPart("XMLData", new FileBody(xmlFile));
                        for (int j = 0; j < imageFiles.length; j++) {
                            mpEntity.addPart("userfile[" + j + "]", new FileBody(imageFiles[j]));
                        }
                        request.setEntity(mpEntity.build());

                        HttpResponse response = client.execute(request);

                        HttpEntity httpEntity = response.getEntity();
                        is = httpEntity.getContent();

                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();

                    //Log.v("UploadAPIData:HTMLRe: ", json);

                    jObj = new JSONObject(json);
                    if (jObj.getString("status").equalsIgnoreCase("success")){
                        uploadMessage += uploadCounter + ") Uploaded " + selectedFile.displayName + ". \n";
                        ObservationsActivity.deleteObservation(selectedFileList.get(i));
                    }
                    else{
                        throw new Exception(" Got failed status from Upload API");
                    }
                } catch (Exception e) {
                    //Log.e("JSON Parser", "Error parsing data " + e.toString());
                    e.printStackTrace();
                    uploadMessage += uploadCounter + ") Upload failed for " + selectedFile.displayName + ". \n";
                }
                uploadCounter++;
            }
        }
        return input[0];
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(null);
        if (values != null && values.length > 0) {
            pDialog.setMessage(values[0]);
        }
    }

    private File[] getImagesForObservation(String dataSheetName){
        List<File> imageList = new ArrayList<File>();
        File dir = new File(Environment.getExternalStorageDirectory()+ XMLPreparer.XML_ROOT_DIR+dataSheetName);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                imageList.add(new File(dir, children[i]));
            }
        }
        return  imageList.toArray(new File[imageList.size()]);
    }

    private String getFileNameWithoutExtension(File inputFile){
        int pos = inputFile.getName().lastIndexOf(".");
        String name = pos > 0 ? inputFile.getName().substring(0, pos) : inputFile.getName();
        return  name;
    }

    @Override
    protected void onPostExecute(ObservationsActivity obj) {

        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        new AlertDialog.Builder(activity)
                .setTitle("Upload Status")
                .setMessage(uploadMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();

        obj.refreshFileList();
    }
}
