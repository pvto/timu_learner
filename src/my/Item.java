package my;

import java.util.ArrayList;
import java.util.List;
import my.Attribute.BIAttribute;
import my.Attribute.BSAttribute;
import my.Attribute.DAttribute;

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

    
    public int iattr(int i) {       return ((BIAttribute) attributes.get(i)).value; }
    public double dattr(int i) {    return ((DAttribute) attributes.get(i)).value; }
    public String sattr(int i) {    return ((BSAttribute) attributes.get(i)).value; }

}
