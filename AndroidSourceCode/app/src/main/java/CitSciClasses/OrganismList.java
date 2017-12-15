package CitSciClasses;

/**
 * Created by Manoj on 11/11/2014.
 */
public class OrganismList {

    private String attributeId;
    private String id;
    private String name;

    public OrganismList(String attributeId, String id, String name) {
        this.attributeId = attributeId;
        this.id = id;
        this.name = name;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
