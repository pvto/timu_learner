
package my.clust;

import java.util.Arrays;
import my.Dataset;
import my.Item;
import static my.clust.DBSCAN.UNCLUSTERED;
import my.f.Int;

/**
 * @author pvto https://github.com/pvto
 */
public class Clustering {

        
    public Clustering(Dataset ds) { 
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

}
