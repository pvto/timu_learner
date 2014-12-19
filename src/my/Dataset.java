package my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import my.Attribute.BIAttribute;
import my.Attribute.DAttribute;

public class Dataset {
    public List<Item> items = new ArrayList<>();
    public int classAttribute = SUPERVISED;
    
    
    static public final int SUPERVISED = -1, UNSUPERVISED = -2;

    static public class UnsupervisedDataset extends Dataset {
        {classAttribute = UNSUPERVISED;}
    }
    static public int classAttribute(Dataset ds) {
        if (ds == null) {
            return UNSUPERVISED;
        }
        if (ds.classAttribute == SUPERVISED) {
            ds.classAttribute = ds.items.get(0).attributes.size() - 1;
        }
        return ds.classAttribute;
    }

    
    
    
    static public int attributeCount(Dataset ds) {
        return ds.items.get(0).attributes.size();
    }
    static public int nonClassAttributeCount(Dataset ds) {
        int n = attributeCount(ds);
        if (classAttribute(ds) != UNSUPERVISED) {
            n -= 1;
        }
        return n;        
    }
    
    public int size() { return items.size(); }
    public Item item(int row) { return items.get(row); }
    public Attribute min(int column) { return getCache().min(column); }
    public Attribute max(int column) { return getCache().max(column); }
    public double[] range(int column) { return getCache().range(column); }
    public double var(int column) { return getCache().var(column); }
    public double std(int column) { return Math.sqrt( getCache().var(column) ); }
    
    private Cache cache;
    public Cache getCache() { return cache != null ? cache : new Cache(this); }
    static public class Cache {
        Dataset ds;
        public Cache(Dataset das) { 
            this.ds = das;
            dirty = fill(new int[Dataset.attributeCount(ds)], MIN|MAX|MEAN|VAR);
        }
        
        static final private int MIN=1, MAX=2, MEAN=4, VAR=8;
        public int[] dirty;
        private Attribute[] min;
        private Attribute[] max;
        private double[] mean;
        private double[] var;

        private int[] fill(int[] arr, int val) {
            Arrays.fill(arr, val);
            return arr;
        }
        
        public Attribute min(int column) {
            if ((dirty[column] & MIN) != 0 || min == null) {
                Attribute x = null;
                for(Item i : ds.items) {
                    Attribute cand = i.attributes.get(column);
                    if (x == null || cand.compareTo(x) < 0) {
                        x = cand;
                    }
                }
                if (min == null) {
                    min = new Attribute[ds.items.get(0).attributes.size()];
                }
                min[column] = x;
                dirty[column] &= ~MIN;
            }
            return min[column];
        }

        public Attribute max(int column) {
            if ((dirty[column] & MAX) != 0 || max == null) {
                Attribute x = null;
                for(Item i : ds.items) {
                    Attribute cand = i.attributes.get(column);
                    if (x == null || cand.compareTo(x) > 0) {
                        x = cand;
                    }
                }
                if (max == null) {
                    max = new Attribute[ds.items.get(0).attributes.size()];
                }
                max[column] = x;
                dirty[column] &= ~MAX;
            }
            return max[column];
        }

        public double[] range(int column) {
            Attribute min_ = min(column),
                    max_ = max(column)
                    ;
            if (min_ instanceof Attribute.DAttribute) {
                return new double[] {((DAttribute)min_).value, ((DAttribute)max_).value};
            } else if (BIAttribute.class.isAssignableFrom(min_.getClass())) {
                return new double[] {((BIAttribute)min_).value, ((BIAttribute)max_).value};
            }
            throw new UnsupportedOperationException("Range is not meaningful for an attribute of " + min_.getClass());
        }

        public double mean(int column) {
            if (mean == null) {
                mean = new double[attributeCount(ds)];
            }
            if ((dirty[column] & MEAN) != 0) {
                double sum = 0.0;
                for(Item it : ds.items) {
                    Attribute a = it.attributes.get(column);
                    sum += Attribute.as.doublee(a);
                }
                int n = ds.items.size();
                if (n == 0) { n = 1; }
                mean[column] = sum / n;
                dirty[column] &= ~MEAN;
            }
            return mean[column];
        }
        
        public double var(int column) {
            if (var == null) {
                var = new double[attributeCount(ds)];
            }
            if ((dirty[column] & VAR) != 0) {
                double mean = mean(column);
                double sum = 0.0;
                for(Item it : ds.items) {
                    Attribute a = it.attributes.get(column);
                    sum += Math.pow(Attribute.as.doublee(a) - mean, 2);
                }
                var[column] = sum / (ds.items.size() - 1);
                dirty[column] &= ~VAR;
            }
            return var[column];
        }
    }

    
    
    static public interface RowSelection {
        boolean doInclude(Item item, Dataset ds, int rowIndex);
    }
    
    
    static public Dataset subset(Dataset ds, RowSelection sel) {
        Dataset res = new Dataset();
        res.classAttribute = Dataset.classAttribute(ds);
        int i = 0;
        for(Item item : ds.items) {
            if (sel.doInclude(item, ds, i)) {
                res.items.add(new Item(item));
            }
            i++;
        }
        return res;
    }
    
    static public double conditionalProb(Dataset ds, int column, Attribute ival, int classColumn, Attribute classVal) {
        int count = 0, acount = 0;
        for(Item it : ds.items) {
            
            if (Item.a(it, column).equals(ival)) {
                acount++;
                if (Item.a(it, classColumn).equals(classVal)) {
                    count++;
                }
            }
        }
        return count / (double)acount;
    }
    
    static public Set<Attribute> distinctAttributeSet(Dataset ds, int column) {
        Set<Attribute> s = new HashSet<Attribute>();
        for(Item it : ds.items) {
            s.add(Item.a(it, column));
        }
        return s;
    }
    
    
    static public double[][] distances(Dataset ds, ProximityMeasure m, List<AttrProxMetric> metrics) {
        if (metrics == null) {
            metrics = ProximityMeasure.metrics.forDs(ds);
        }
        double[][] dist = new double[ds.size()][ds.size()];
        for(int i = 0; i < ds.size(); i++) {
            for(int j = i + 1; j < ds.size(); j++) {
                dist[j][i] = dist[i][j] = m.distance(metrics, ds.item(i), ds.item(j), ds);
            }
            dist[i][i] = 0.0;
        }
        return dist;
    }

    static public class ItemDistComparator implements Comparator<Item> {
        private final Item base;
        private final ProximityMeasure dm;
        private final List<AttrProxMetric> metrics;
        private final Dataset ds;
        
        public ItemDistComparator(Item base, ProximityMeasure dm, List<AttrProxMetric> metrics, Dataset ds) {
            this.base = base;
            this.dm = dm;
            this.metrics = metrics;
            this.ds = ds;
        }
        
        @Override
        public int compare(Item o1, Item o2) {
            double d1 = dm.distance(metrics, base, o1, ds);
            double d2 = dm.distance(metrics, base, o2, ds);
            if (d1 > d2) {
                return 1;
            }
            if (d2 > d1) {
                return -1;
            }
            return 0;
        }
        
    }

}
