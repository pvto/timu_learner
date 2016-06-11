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

    public Item subitem(int firstAttr, int lastAttrExclusive) {
        List<Attribute> subset = new ArrayList<>();
        for(int i = firstAttr; i < lastAttrExclusive; i++)
            subset.add(this.attributes.get(i));
        Item ret = new Item();
        ret.attributes = subset;
        return ret;
    }
    
    
    
    public static class Items {
        
        public static List<Attribute> pickOneAttribute(Item[] items, int indexOfAttribute) {
            List<Attribute> ret = new ArrayList<>(items.length);
            for(Item item : items) {
                ret.add(item.attributes.get(indexOfAttribute));
            }
            return ret;
        }
    }

}
