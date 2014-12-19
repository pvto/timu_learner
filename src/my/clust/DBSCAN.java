package my.clust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import my.AttrProxMetric;
import my.Dataset;
import my.Item;
import my.ProximityMeasure;
import my.f.Int;

/**
 * @author pvto https://github.com/pvto
 */
public class DBSCAN {
    
    static public int 
            NOISE = -1,
            UNCLUSTERED = -2
            ;

    public static class DBSCANResult {
        
        public DBSCANResult(Dataset ds) { 
            this.ds = ds;
            nbsize = new int[ds.size()];
            Arrays.fill(nbsize, 0);
            clusterings = new int[ds.size()];
            Arrays.fill(clusterings, UNCLUSTERED);
        }
        public Dataset ds;
        public double[][] dist;
        public int[] nbsize;
        public int[] clusterings;
        
        public Item[][] getClusters() {
            
            int clusters = Int.max(clusterings) - Int.min(clusterings) + 1;
            Item[][] res = new Item[clusters][];
            
            for(int clust = NOISE; clust < clusters - 1; clust++) {
                int count = Int.count(clusterings, clust);
                res[clust + 1] = new Item[count];
                int j = 0;
                for(int i = 0; i < clusterings.length; i++) {

                    if(clusterings[i] != clust) { continue; }
                    res[clust + 1][j++] = ds.item(i);

                }
            }
            return res;
        }
    }
    
    public DBSCANResult dbscan(Dataset ds, int minPts, double Eps, ProximityMeasure m, List<AttrProxMetric> metrics) {
        
        
        DBSCANResult res = new DBSCANResult(ds);
        // get distance matrix
        double[][] dist = res.dist = Dataset.distances(ds, m, metrics);
        // preprocess neighborhood sizes
        int[] nbsize = res.nbsize;
        for(int i = 0; i < nbsize.length; i++) {
            double[] row = dist[i];
            for(int j = 0; j < row.length; j++) { 
                if (row[j] <= Eps) { nbsize[i]++; }
            }
        }
        // prepare random pickup order 
        List<Integer> rndord = range(0, ds.size());
        Collections.shuffle(rndord);
        // do the main algorithm
        int currCluster = 0;
        int[] clusterings = res.clusterings;
        for(Integer off : rndord) {
            if (clusterings[off] != UNCLUSTERED) {
                continue;
            }
            else if (nbsize[off] == 1) {
                // classify a NOISE point
                clusterings[off] = NOISE;
            }
            else if (nbsize[off] >= minPts) {
                scanCluster(off, currCluster, dist, minPts, Eps, clusterings, nbsize);
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

    private void scanCluster(int item, int currCluster, double[][] dist, int minPts, double Eps, int[] clusterings, int[] nbsize) {
        clusterings[item] = currCluster;
        double[] di = dist[item];
        for(int i = 0; i < di.length; i++) {
            if (clusterings[i] != UNCLUSTERED) { continue; }
            else if (di[i] <= Eps) {
                if (nbsize[i] >= minPts) {
                    scanCluster(i, currCluster, dist, minPts, Eps, clusterings, nbsize);
                } else {
                    clusterings[i] = currCluster;
                }
            }
        }
    }

    static public List<Integer> range(int start, int end) {
        List<Integer> res = new ArrayList<>(end - start);
        for(int i = start; i < end; i++) { res.add(i); }
        return res;
    }
}
