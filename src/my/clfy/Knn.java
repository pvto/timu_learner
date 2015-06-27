package my.clfy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import my.Attribute;
import my.AttrProxMetric;
import my.Dataset;
import my.ProximityMeasure;
import my.Item;
import my.Voting;
import my.f.Int;

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
        List<AttrProxMetric> metrics = AttrProxMetric.metrics.forDs(data);
        Collections.sort(copy, new Dataset.ItemDistComparator(sample, m, metrics, data));
        copy = copy.subList(0, n);
        KnnResult res = voteKnnResult(copy, sample, data, m, metrics, v);
        return res;
    }

    private KnnResult voteKnnResult(List<Item> copy, Item sample, Dataset data, ProximityMeasure m, List<AttrProxMetric> metrics, Voting v) {
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
        List<Entry<Attribute, Double>> winners = new ArrayList<>();
        for (Entry<Attribute, Double> e : counts.entrySet()) {
            if (winners.isEmpty() || 
                    (smallerBetter ? winners.get(0).getValue() < e.getValue() 
                    : winners.get(0).getValue() > e.getValue())) {
                winners.clear();
                winners.add(e);
            }
            else if (!winners.isEmpty() 
                    && winners.get(0).getValue() == e.getValue()) {
                winners.add(e);
            }
        }
        if (winners.size() > 0) {
            int offset = Int.irand(winners.size());
            winners.set(0, winners.get(offset));
        }
        KnnResult res = new KnnResult();
        res.predictedClass = winners.get(0).getKey();
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
