
package my.clust;

import java.util.Arrays;
import my.Dataset;
import my.Item;
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
        Arrays.fill(clusterings, MODE_DEFAULT);
    }
    public Dataset ds;
    public double[][] dist;
    public int[] nbsize;
    public int[] clusterings;
    public int[] itemModes;

    public Item[][] getClusters() {

        int cmax = Int.max(clusterings),
            cmin = Int.min(clusterings),
            clusters = cmax - cmin + 1;
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
        Item[][] cl = getClusters();
        StringBuilder s = new StringBuilder();
        s.append("clusters: ").append(cl.length).append("\n");
        for(Item[] clust : cl)
            s.append("  ").append(Arrays.toString(clust)).append("\n");
        return s.toString();
    }
    
    
    
    
    
    
    
    public static class Splitter {
        
        public static Clustering split(Clustering clustering, int maxCorePoints, int minCorePoints) {
            Clustering ret = new Clustering(clustering.ds);
            //TODO
            return ret;
        }
    }

}
