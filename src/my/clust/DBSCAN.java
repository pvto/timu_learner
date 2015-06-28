package my.clust;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import my.AttrProxMetric;
import my.Dataset;
import my.ProximityMeasure;
import static my.clust.Clustering.NOISE;
import static my.clust.Clustering.UNCLUSTERED;
import static my.clust.Clustering.checkClassAttribute;
import my.f.Dist;
import my.f.Doub;
import my.f.Int;

/**
 * @author pvto https://github.com/pvto
 */
public class DBSCAN {
    

    
    public Clustering dbscan(Dataset ds, int minPts, double Eps, ProximityMeasure m, List<AttrProxMetric> metrics) {
        
        checkClassAttribute(ds);
        
        Clustering res = new Clustering(ds);
        // get distance matrix
        double[][] dist = res.dist = Dist.distances(ds, m, metrics);
        // preprocess neighborhood sizes
        int[] nbsize = res.nbsize;
        for(int i = 0; i < nbsize.length; i++) {
            double[] row = dist[i];
            for(int j = 0; j < row.length; j++) { 
                if (row[j] <= Eps) { nbsize[i]++; }
            }
        }
        // prepare random pickup order 
        List<Integer> rndord = Int.range(0, ds.size());
        Collections.shuffle(rndord);
        // start the main algorithm
        int currCluster = 0;
        int[] clusterings = res.clusterings;
        int[] modes = res.itemModes;
        for(Integer off : rndord) {
            if (clusterings[off] != UNCLUSTERED) { }
            else if (nbsize[off] == 1) {
                // classify a NOISE point
                clusterings[off] = NOISE;
            }
            else if (nbsize[off] >= minPts) {
                scanCluster(off, currCluster, dist, minPts, Eps, clusterings, nbsize, modes);
                currCluster++;
            }
        }
        // mark the rest of the noise points
        for(int i = 0; i < clusterings.length; i++) {
            if (clusterings[i] == UNCLUSTERED) {
                clusterings[i] = NOISE;
            }
        }
        return res;
    }
    
    

    private void scanCluster(int item, int currCluster, double[][] dist, 
                            int minPts, double Eps, int[] clusterings, int[] nbsize, int[] modes) {
        
        clusterings[item] = currCluster;
        modes[item] = Clustering.MODE_CORE;
        
        double[] di = dist[item];
        for(int i = 0; i < di.length; i++) {
            if (clusterings[i] != UNCLUSTERED) { continue; }
            else if (di[i] <= Eps) {
                if (nbsize[i] >= minPts) {
                    scanCluster(i, currCluster, dist, minPts, Eps, clusterings, nbsize, modes);
                } else {
                    clusterings[i] = currCluster;
                }
            }
        }
    }

    
    
    
    
    /** Bonus: finding an Eps, based on inter-item k-distances over a dataset. 
     *  This is experimental.
     * 
     *  k is taken as minPts - 1
     * 
     *  The idea is to find an Eps that will leave noise points outside clusters.
     * 
     *  Returns one of computed k-distances. A k-distance means the
     *  distance of a dataset item to its kth nearest neighbor.
     * 
     *  The k-distances are sorted to get a monotonously increasing series of values.
     * 
     *  Specifically, a k-distance at an index, on which an approximated 2nd derivative 
     *  changes most rapidly, is returned.
     * 
     *  The 2nd derivative is noise-softened.
     * 
     *  A sanity check forces that at least 1/4 items have a smaller k-distance
     *  than that returned.
     * 
     *  Note: if the dataset is very small, this resorts to returning
     *  an average over the measured k-distances.
     */
    public double suggestEps(int minPts, Dataset ds, ProximityMeasure m, List<AttrProxMetric> metrics) {

        checkClassAttribute(ds);
        
        int k = minPts - 1;
        
        double[] y1 = Dist.kdistance(1, ds, m, metrics);
        double[] y = Dist.kdistance(k, ds, m, metrics);
        double clumping = Doub.avg(Doub.div(y1, y));
        if (ds.size() < 20) {
            return Doub.avg(y);
        }
        Arrays.sort(y);
        double[] dy = Doub.ndy(y),
                ddy = Doub.ndy(dy);
        int     off = ddy.length / 4,
                imax = off;
        double  max = 0.0,
                NOISE = 0.1;
        for(; off < ddy.length; off++) {
            double third = (off == ddy.length - 1 ? ddy[off] : ddy[off + 1]);
            double delta = ((ddy[off - 1] + ddy[off] + third) / 3.0 + NOISE)
                    /
                    ((ddy[off - 4] + ddy[off - 3] + ddy[off - 2]) / 3.0 + NOISE);
            if (delta > max) {
                max = delta;
                imax = off;
            }
        }
        return y[imax + 2];
        
    }
}
