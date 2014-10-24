package my;

import java.util.ArrayList;
import java.util.List;

public class Item {

    public List<Attribute> attributes;

    static public Attribute a(Item i, int attribute) {
        return i.attributes.get(attribute);
    }

    public Item() {}
    public Item(Item src) {
        this.attributes = new ArrayList<Attribute>(src.attributes.size());
        for(Attribute a : src.attributes) {
            this.attributes.add(Attribute.clone.a(a));
        }
    }
    public String toString() { return attributes.toString(); }
}
