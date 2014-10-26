package my.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import my.Attribute;
import my.Attribute.Metric;
import my.Dataset;
import my.ProximityMeasure;
import my.Item;
import my.Voting;

public class Knn {

    static public class KnnResult {
        public Attribute predictedClass;
        public List<Item> nearestNeighbours;
        public String toString() {
            return "Predicted class: " + predictedClass + "\n"
                    + "Nearest neighbours: " + nearestNeighbours;
        }
    }

    public KnnResult knn(Item sample, Dataset data, int n, ProximityMeasure m, Voting v) {
        List<Item> copy = new ArrayList<>(data.items);
        List<Metric> metrics = ProximityMeasure.metrics.forDs(data);
        Collections.sort(copy, new Dataset.ItemDistComparator(sample, m, metrics, data));
        copy = copy.subList(0, n);
        KnnResult res = voteKnnResult(copy, sample, data, m, metrics, v);
        return res;
    }

    private KnnResult voteKnnResult(List<Item> copy, Item sample, Dataset data, ProximityMeasure m, List<Metric> metrics, Voting v) {
        CountingSet<Attribute> counts = new CountingSet<>();
        boolean smallerBetter = true;
        switch(v) {
            case EqualWeights:
                for(Item i : copy) {
                    counts.increment(i.attributes.get(Dataset.classAttribute(data)), 1.0);
                }
                break;
            case InverseDistanceWeighted:
                for(Item i : copy) {
                    double dist = m.distance(metrics, sample, i, data);
                    if (dist == 0) {
                        dist = 1e6; // fast hack for division by zero
                    } else {
                        dist = 1.0 / (dist*dist);
                    }
                    counts.increment(i.attributes.get(Dataset.classAttribute(data)), 
                            dist);
                }
                break;
            case OneMinusDistanceWeighted:
                smallerBetter = false;
                for(Item i : copy) {
                    double dist = 1.0 - m.distance(metrics, sample, i, data);
                    counts.increment(i.attributes.get(Dataset.classAttribute(data)), dist);
                }
                break;
            default:
                throw new UnsupportedOperationException("Voting " + v + " not supported by algorithm");
        }
        Entry<Attribute, Double> winner = null;
        for (Entry<Attribute, Double> e : counts.entrySet()) {
            if (winner == null || 
                    (smallerBetter ? winner.getValue() < e.getValue() 
                    : winner.getValue() > e.getValue())) {
                winner = e;
                // this deterministically chooses first equal-item in case of conflict, but
                // in an ideal implementation, one should be picked evenly from the distribution
                // ...would require an intermediate winner-list and a dice...
            }
        }
        KnnResult res = new KnnResult();
        res.predictedClass = winner.getKey();
        res.nearestNeighbours = copy;
        return res;
    }

    
    static private class CountingSet<K> extends HashMap<K, Double> {

        public Double increment(K key, double amount) {
            Double d = getCount(key);
            put(key, (d = d + amount));
            return d;
        }
        
        public double getCount(K key) {
            Double d = get(key);
            if (d == null) {
                return 0.0;
            }
            return d;
        }
    }
    

    
}
