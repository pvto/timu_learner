package my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import my.Attribute.DAttribute;

/** This provides access to means of sets of ds items and sets of
 * item attributes.
 *
 * @author Paavo Toivanen https://github.com/pvto
 */
public interface Means {


    Item mean(Item[] items);
    
    
    public static ItemMean append(Means base, AttrMean attrMean) {
        if (!(Means.class.isAssignableFrom(base.getClass())))
            throw new IllegalArgumentException("Expecting base class of type ItemMeans");
        return ItemMean.append((ItemMean)base, attrMean);
    }
    

    public class ItemMean implements Means{
        public AttrMean[] attrMeans;
        
        public ItemMean(AttrMean[] attrMeans) {
            this.attrMeans = attrMeans;
        }

        @Override
        public Item mean(Item[] items) {
            Item meanItem = new Item();
            meanItem.attributes = new ArrayList<>(items[0].attributes.size());
            for(int i = 0; i < items[0].attributes.size(); i++) {
                List<Attribute> tmp = Item.Items.pickOneAttribute(items, i);
                meanItem.attributes.add(attrMeans[i].mean(tmp));
            }
            return meanItem;
        }
        
        public static ItemMean append(ItemMean base, AttrMean attrMean) {
            AttrMean[] attrMeans = Arrays.copyOf(base.attrMeans, base.attrMeans.length + 1);
            attrMeans[attrMeans.length - 1] = attrMean;
            return new ItemMean(attrMeans);
        }
    }
    
    public interface AttrMean {
        Attribute mean (List<Attribute> attrs);
    }
    
    public class DoNotMeasureMean implements AttrMean {
        public Attribute mean (List<Attribute> attrs) {
            return Attribute.MISSING;
        }
    }
    static DoNotMeasureMean doNotMeasureMean = new DoNotMeasureMean();
    public static DoNotMeasureMean doNotMeasureMean() { return doNotMeasureMean; }
    
    public class DMean implements AttrMean {
        public Attribute mean (List<Attribute> attrs) {
            if (attrs.size() == 0)
                throw new IllegalArgumentException("can't compute mean of an empty set");
            double sum = 0.0d;
            for(Attribute a : attrs) {
                DAttribute da = (DAttribute)a;
                sum += da.value;
            }
            return new DAttribute(sum / attrs.size());
        }
    }
    static DMean dmean = new DMean();
    public static DMean dMean() { return dmean; }
}
