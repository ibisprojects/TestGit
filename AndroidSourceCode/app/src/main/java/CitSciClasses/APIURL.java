package CitSciClasses;

/**
 * Created by manoj on 10/28/2015.
 */
public class APIURL {
    //App Verions check API
    public static String MIN_APP_REVISION_URL ="https://ibis-apis.nrel.colostate.edu/app.php/API/GetMinAppRevision";

    //OAuth APIs
    public static String REFRESH_URL = "https://ibis-apis.nrel.colostate.edu/app.php/oAuth/refreshAccessToken";
    public static String REDIRECT_URI = "https://ibis-apis.nrel.colostate.edu/app.php/oAuth/testRedirectURL";
    public static String TOKEN_URL = "https://ibis-apis.nrel.colostate.edu/app.php/oAuth/getAccessToken";
    public static String OAUTH_URL = "https://ibis-apis.nrel.colostate.edu/app.php/oAuth/Auth";

    //CitSci APIs
    public static String UPLOAD_URL = "https://ibis-apis.nrel.colostate.edu/app.php/API/UploadData";
    public static String PROJECT_URL = "https://ibis-apis.nrel-test.colostate.edu/app.php/API/GetProjectList";
    public static String PROJECTSANDDATASHEETS_URL = "https://ibis-apis.nrel.colostate.edu/app.php/API/GetProjectsAndDatasheets";
    public static String DATASHEET_URL = "https://ibis-apis.nrel.colostate.edu/app.php/API/GetDatasheets";
    public static String USER_URL = "https://ibis-apis.nrel.colostate.edu/app.php/API/GetProfile";
}
