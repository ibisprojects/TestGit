package DB;

/**
 * Created by Manoj on 1/18/2015.
 */
public class CommonFunctions {

    public static Integer IntegerSmartParse(String in) {
        try {
            return new Integer(in);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
