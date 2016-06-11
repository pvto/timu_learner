
package my.regr;

import java.io.StringReader;
import java.util.List;
import my.Csv;
import my.Dataset;
import my.Item;
import my.Means;
import my.Means.AttrMean;
import my.ProximityMeasure;
import my.clust.Clustering;
import my.clust.DBSCAN;
import my.f.Int;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClusteregrTest {

    
String euclData =
//noise points
"10.0,10.0\n"+
"-10,10\n"+
"10,-10\n"+
//cluster 1
"0,0\n"+
"0,1\n"+
"1,1\n"+
//cluster 2
"5,0\n5,1\n"+
"6,0\n6,1\n"+
"7,0\n7,1\n"+
//cluster "in middle"
"3,2\n3,2.5\n3.5,2"
;
    Dataset eds = new Csv().from(new StringReader(euclData), ",");
    { eds.classAttribute = Dataset.UNSUPERVISED; }
    DBSCAN dbscan = new DBSCAN();
    

    @Test
    public void testRegressorBasic() {
        
        Clustering clustering = dbscan.dbscan(eds, 3, Math.sqrt(2.0), ProximityMeasure.Euclidean, null);
        List<Integer> clusterInds = Int.uniq(clustering.clusterings);
        assertEquals(4, clusterInds.size()); // just check that we have 1+3 clusters as expected from data

        Clusteregr regr = new Clusteregr();
        Means itemMean = new Means.ItemMean(new AttrMean[]{Means.dmean, Means.dmean});

        regr.learnRegression(eds, clustering, itemMean, 
                (Item o1, Item o2) -> { 
                    double a = o1.dattr(0);  double b = o2.dattr(0);
                    if (a > b) return 1; else if (b > 2) return -1;
                    a = o1.dattr(1);  b = o2.dattr(1);
                    if (a > b) return 1; else if (b > 2) return -1;
                    return 0;
                }
        );
        System.out.println(regr.clusterMeanValues);
        Item cNoise = regr.regress(eds.item(0));
        System.out.println(cNoise);
        assertTrue(cNoise.dattr(0) < eds.item(0).dattr(0)); // projection of noise point is towards some cluster center on x axis
        assertTrue(cNoise.dattr(1) < eds.item(0).dattr(1)); // ... y axis
        Item c00 = regr.regress(eds.item(4));
        System.out.println(c00);
        assertEquals((0+0+1)/3.0, c00.dattr(0), 1e-6); // should match to cluster center mean on x axis (since this cluster is on "edge")
        assertEquals((0+1+1)/3.0, c00.dattr(1), 1e-6); // ... y axis
        Item c12 = regr.regress(eds.item(12));
        System.out.println(c12);
        // c12 approaches to (3,2) by being somewhere between cluster 1 center (0.333,0.666) and cluster "in middle" center (3.167,2.167)
        assertTrue(
                clustering.proximityMeasure.distance(clustering.attrProxMetrics, c12.subitem(0,2), eds.item(12), eds)
                <
                clustering.proximityMeasure.distance(clustering.attrProxMetrics, regr.clusterMeanValues.get(2).subitem(0,2), eds.item(12), eds)
        );
        
    }
    @Test
    public void testRegression() {
    }

}