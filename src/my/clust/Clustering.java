
package my.clust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import my.AttrProxMetric;
import my.Dataset;
import my.Item;
import my.Ples.Tuple;
import my.ProximityMeasure;
import my.f.Int;

/**
 * @author pvto https://github.com/pvto
 */
public class Clustering {

    static public int 
            NOISE = -1,
            UNCLUSTERED = -2
            ;
    static public int
            MODE_DEFAULT = 0,
            MODE_CORE = 1
            ;
        
    

    public static void checkClassAttribute(Dataset ds) {
        if (ds.classAttribute != Dataset.UNSUPERVISED)
            throw new IllegalArgumentException("Clustering requires an unsupervised dataset; please set dataset.classAttribute = Dataset.UNSUPERVISED");
    }
    
    
    
    
    public Clustering(Dataset ds) { 
        this.ds = ds;
        nbsize = new int[ds.size()];
        Arrays.fill(nbsize, 0);
        clusterings = new int[ds.size()];
        itemModes = new int[ds.size()];
        Arrays.fill(clusterings, UNCLUSTERED);
        Arrays.fill(itemModes, MODE_DEFAULT);
    }
    public Dataset ds;
    public double[][] dist;
    public int[] nbsize;
    public int[] clusterings;
    public int[] itemModes;
    /** The proximity measure used in obtaining this clustering */
    public ProximityMeasure proximityMeasure;
    /** The attribute proximity metrics used in obtaining this clustering */
    public List<AttrProxMetric> attrProxMetrics;

    public Item[][] getClusters() { return getClusters(true); }
    public Item[][] getClustersWithoutNoisePoints() { return getClusters(false); }
    public Item[][] getClusters(boolean INCLUDE_NOISE_POINT_CLUSTER) {

        int cmax = Int.max(clusterings),
            cmin = Int.min(clusterings);
        if (!INCLUDE_NOISE_POINT_CLUSTER && cmin == NOISE) {
            cmin++;
        }
        int clusters = cmax - cmin + 1;
        Item[][] res = new Item[clusters][];

        int off = 0;
        for(int clust = cmin; clust <= cmax; clust++) {
            int count = Int.count(clusterings, clust);
            res[off] = new Item[count];
            int j = 0;
            for(int i = 0; i < clusterings.length; i++) {

                if(clusterings[i] != clust) { continue; }
                res[off][j++] = ds.item(i);

            }
            off++;
        }
        return res;
    }
    
    public String toString() {
        Item[][] cl = getClusters(true);
        StringBuilder s = new StringBuilder();
        s.append("clusters: ").append(cl.length).append("\n");
        for(Item[] clust : cl)
            s.append("  ").append(Arrays.toString(clust)).append("\n");
        return s.toString();
    }
    
    
    
    
    
    
    
    public static class Splitter {
        
        public static Clustering split(Clustering clustering, int maxCorePoints, int minCorePoints, int minPoints) {
            Clustering ret = new Clustering(clustering.ds);
//            Item[][] clusters = clustering.getClusters();
//            boolean firstNoiseCluster = Int.min(clustering.clusterings) == NOISE;
//            int start = firstNoiseCluster ? 1 : 0;
//            for (int i = start; i < clusters.length; i++) {
//                Item[] cluster = clusters[i];
//                
//                
//            }
            int cmax = Int.max(clustering.clusterings),
                cmin = Int.min(clustering.clusterings),
                clusters = cmax - cmin + 1;
            Item[][] res = new Item[clusters][];
            List<List<Tuple<Item,Integer>>> re = new ArrayList<>();

            for(int clust = cmin; 
                    clust <= cmax; clust++) {
                int count = Int.count(clustering.clusterings, clust);
                List<Tuple<Item,Integer>> items = new ArrayList<>();
                int 
                        j = 0,
                        nCore = 0
                        ;
                for(int i = 0; i < clustering.clusterings.length; i++) {

                    int CLU = clustering.clusterings[i];
                    if (CLU != clust) { continue; }
                    if (CLU == NOISE && minPoints > 1) { continue; }
                    items.add(new Tuple(clustering.ds.item(i), i));
                    if (clustering.itemModes[i] == MODE_CORE)
                        nCore++;
                }
                if (nCore < minCorePoints)
                    continue;
                if (nCore > maxCorePoints) {
                    
                }
            }
            return ret;
        }
    }

}
