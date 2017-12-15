package Upload;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Manoj on 3/23/2015.
 */
public class AttributeValue implements Serializable
{
    String mName;
    String mType;
    String mId;
    String mSelectedValue = "";
    ArrayList<String> mOptions = new ArrayList<String>();


    public String getmSelectedValue() {
        return mSelectedValue;
    }
    public void setmSelectedValue(String mSelectedValue) {
        this.mSelectedValue = mSelectedValue;
    }
    public String getmId() {
        return mId;
    }
    public void setmId(String mId) {
        this.mId = mId;
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public String getmType() {
        return mType;
    }
    public void setmType(String mType) {
        this.mType = mType;
    }
    public ArrayList<String> getmOptions() {
        return mOptions;
    }
    public void setmOptions(String mOptions) {
        this.mOptions.add(mOptions);
    }



}

