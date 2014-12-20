package my.f;

import java.util.Arrays;
import java.util.List;
import my.AttrProxMetric;
import my.Dataset;
import my.ProximityMeasure;

/**
 * @author pvto https://github.com/pvto
 */
public class Dist {

    static public int levenshteinDistance(String s, String t) {

        int     m = s.length(),
                n = t.length()
            ;
        int[][] d = new int[m + 1][n + 1];
        for(int[] d0 : d)
            Arrays.fill(d0, 0);

        for (int i = 0; i <= m; i++) {
            d[i][0] = i;
        }

        for(int j = 0; j <= n; j++) {
            d[0][j] = j;
        }

        for(int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (s.charAt(i-1) == t.charAt(j-1)) {
                  d[i][j] = d[i-1][j-1];       // no operation required
                } else {
                    d[i][j] = Int.min
                    (
                        d[i-1][j] + 1,  // a deletion
                        d[i][j-1] + 1,  // an insertion
                        d[i-1][j-1] + 1 // a substitution
                    );
                }
            }
        }

        return d[m][n];

    }

    
    static public double[] kdistance(int k, Dataset ds, ProximityMeasure m, List<AttrProxMetric> am) {
        
        if (k < 1 || k >= ds.size() - 1)
            throw new IllegalArgumentException("k-distance is not defined for neighbor " + k + "(only for 1 <= k <= |set| - 1)");
        
        double[][] dist = distances(ds, m, am);
        double[] res = new double[dist.length];
        for (int i = 0; i < res.length; i++) {
            double[] ss = Arrays.copyOf(dist[i], dist[i].length);
            Arrays.sort(ss);
            res[i] = ss[k];
        }
        return res;
    }
    

    static public double[][] distances(Dataset ds, ProximityMeasure m, List<AttrProxMetric> metrics) {

        if (metrics == null) {
            metrics = AttrProxMetric.metrics.forDs(ds);
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

}
