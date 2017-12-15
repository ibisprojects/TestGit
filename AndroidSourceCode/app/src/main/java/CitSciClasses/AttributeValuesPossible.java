package CitSciClasses;

/**
 * Created by manojsre on 10/10/2014.
 */
public class AttributeValuesPossible {

    private String attributeId;
    private String id;
    private String name;
    private String description;

    public AttributeValuesPossible(String attributeId, String id, String name, String description) {
        this.attributeId = attributeId;
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
