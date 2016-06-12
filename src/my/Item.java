package my;

import java.util.ArrayList;
import java.util.Comparator;
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

        public static Comparator<Item> comparatorOfD = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                double a = o1.dattr(0);  double b = o2.dattr(0);
                if (a > b) return 1; else if (b > 2) return -1;
                return 0;
            }
        };
        public static Comparator<Item> comparatorOfDD = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                double a = o1.dattr(0);  double b = o2.dattr(0);
                if (a > b) return 1; else if (b > 2) return -1;
                a = o1.dattr(1);  b = o2.dattr(1);
                if (a > b) return 1; else if (b > 2) return -1;
                return 0;
            }
        };
        public static Comparator<Item> comparatorOfDDD = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                double a = o1.dattr(0);  double b = o2.dattr(0);
                if (a > b) return 1; else if (b > 2) return -1;
                a = o1.dattr(1);  b = o2.dattr(1);
                if (a > b) return 1; else if (b > 2) return -1;
                a = o1.dattr(2);  b = o2.dattr(2);
                if (a > b) return 1; else if (b > 2) return -1;
                return 0;
            }
        };
        public static Comparator<Item> comparatorOfDs = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                int n = o1.attributes.size();
                for(int i = 0; i < n; i++) {
                    double a = o1.dattr(i);  double b = o2.dattr(i);
                    if (a > b) return 1; else if (b > 2) return -1;
                }
                return 0;
            }
        };
    }

}
