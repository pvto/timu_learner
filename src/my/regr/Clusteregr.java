
package my.regr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import my.Attribute.IAttribute;
import my.Dataset;
import my.Item;
import my.Means;
import my.clust.Clustering;

/** A regressor that uses a clustered input dataset as a base for forming
 *  a regression model.
 * 
 *  In the learning phase, cluster mean values are computed
 *  and ordered.
 * 
 *  Regression is then simply finding the two smallest-distance cluster 
 *  mean values and taking linear distance from new item x to a vertex
 *  drawn between them.
 *  
 *  This approach to regression has the benefit of being resilient to update,
 *  since adding new data to the model requires only a partial re-evaluation
 *  of clusters.
 * 
 *  Dynamic adjustments to a regressor might include:
 *  - Adding a new item to dataset: find nearest cluster, add item and recompute cluster mean
 *  - Redistribute data from clusters by combining them and 
 *    then splitting result into three new clusters; recompute cluster means and add those to model
 * 
 * @author pvto https://github.com/pvto
 */
public class Clusteregr {
    
    public Clustering clustering;
    public Dataset dataset;
    public Means itemMean;
    public List<Item> clusterMeanValues;
    public int[] clusterMeanValuesOriginalIndices;
    
    /**
     * 
     * @param ds dataset to regress
     * @param clustering a clustering of the same dataset (formed externally with kmeans, for instance)
     * @param itemMean a function to compute cluster mean values with
     * @param ordering for sorting the cluster mean values into a sequence;
     *   please note that learnRegression() appends cluster index as an IAttribute to a mean-value item.
     *   This might have an unwanted effect in the sorting phase, if supplied Comparator function 
     *   takes that last IAttribute into account.
     */
    public void learnRegression(Dataset ds, Clustering clustering, Means itemMean, Comparator<Item> ordering) {
        this.dataset = ds;
        this.clustering = clustering;
        this.itemMean = Means.append(itemMean, Means.doNotMeasureMean);
        Item[][] clusters = clustering.getClustersWithoutNoisePoints();
        List<Item> clusterMeanValues = new ArrayList<>(clusters.length);
        for(int i = 0; i < clusters.length; i++) {
            Item[] cluster = clusters[i];
            Item clusterMean = itemMean.mean(cluster);
            clusterMean.attributes.add(new IAttribute(i));
            clusterMeanValues.add(clusterMean);
        }
        Collections.sort(clusterMeanValues, ordering);
        this.clusterMeanValues = clusterMeanValues;
    }
    
    
    
    public Item regress(Item newItem) {
        // 1) find closest item in the set of cluster mean values
        int closestIndex = 0;
        double closestDistance = Double.MAX_VALUE;
        for(int i = 0; i < clusterMeanValues.size(); i++) {
            double dist = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, clusterMeanValues.get(i), dataset);
            if (dist < closestDistance) {
                closestDistance = dist;
                closestIndex = i;
            }
        }
        // 2) find the next-to-closest item, on either side to the closest in the ordering
        Item nextClosest = null;
        if (closestIndex > 0) {
            nextClosest = clusterMeanValues.get(closestIndex - 1);
        }
        if (closestIndex < clusterMeanValues.size() - 1) {
            Item tmp = clusterMeanValues.get(closestIndex + 1);
            if (nextClosest == null) {
                nextClosest = tmp;
            } else {
                double distA = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, nextClosest, dataset);
                double distB = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, tmp, dataset);
                if (distB < distA) {
                    nextClosest = tmp;
                }
            }
        }
        // 3) compute and return regression
        Item low = clusterMeanValues.get(closestIndex);
        if (nextClosest == null) {
            return low;
        } else {
            // Return a linear projection of newItem on a line that passes through closest and nextClosest.
            // This is accomplished here by a subdividing approximation that runs between the two points.
            Item high = nextClosest;
            Item mid = itemMean.mean(new Item[]{low, high});
            double distLow = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, low, dataset);
            double distHigh = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, high, dataset);
            for(int i = 0; i < 4; i++) {
                double distMid = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, mid, dataset);
                if (distLow < distMid) {
                    high = mid;
                    distHigh = distMid;
                }
                else {
                    Item postMid = itemMean.mean(new Item[]{mid, high});
                    double distPostMid = clustering.proximityMeasure.distance(clustering.attrProxMetrics, newItem, postMid, dataset);
                    if (distLow < distPostMid) {
                        high = mid;
                        distHigh = distMid;
                    } else {
                        low = mid;
                        distLow = distMid;
                    }
                }
                mid = itemMean.mean(new Item[]{low, high});
            }
        }
        return low;
    }
}
