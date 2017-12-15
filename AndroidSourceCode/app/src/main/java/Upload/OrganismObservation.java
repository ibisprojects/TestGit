package Upload;

import java.io.Serializable;
import java.util.ArrayList;

import CitSciClasses.FormAttribute;

/**
 * Created by Manoj on 3/23/2015.
 */
public class OrganismObservation implements Serializable {

    String Id="";
    String name="";
    String comment="";
    String imageFilename="";
    int parentEntryIndex;

    String parentEntryId;
    byte[] icon;
    byte[] image;
    ArrayList<FormAttribute> attributes = new ArrayList<FormAttribute>();

    public int getParentEntryIndex() {
        return parentEntryIndex;
    }
    public void setParentEntryIndex(int parentEntryIndex) {
        this.parentEntryIndex = parentEntryIndex;
    }

    public String getParentEntryId() {
        return parentEntryId;
    }

    public void setParentEntryId(String parentEntryId) {
        this.parentEntryId = parentEntryId;
    }

    public String getId() {
        return Id;
    }
    public void setId(String mId) {
        this.Id = mId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name =name;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String mComment) {
        this.comment = comment;
    }
    public ArrayList<FormAttribute> getAttributes() {
        return attributes;
    }
    public void addAttributes(FormAttribute attribute) {
        this.attributes.add(attribute);
    }
    public byte[] getIcon() {
        return icon;
    }
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getImageFilename() {
        return imageFilename;
    }
    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
}
