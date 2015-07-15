package my.clust;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import my.AttrProxMetric;
import my.Attribute;
import my.Attribute.DAttribute;
import my.Csv;
import my.Dataset;
import my.Item;
import my.ProximityMeasure;
import my.f.Attr;
import my.f.Int;
import my.plot.Cout;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class DBSCANTest {
    
String euclData =
//noise points
"10.0,10.0\n"+
"-10,10\n"+
"10,-10\n"+
//cluster 1
"0,0\n"+
"0,1\n"+
"1,0\n"+
//cluster 2
"5,0\n5,1\n"+
"6,0\n6,1\n"+
"7,0\n7,1\n"
;
    Dataset eds = new Csv().from(new StringReader(euclData), ",");
    { eds.classAttribute = Dataset.UNSUPERVISED; }
    DBSCAN dbscan = new DBSCAN();
    

    @Test
    public void testEucl() {
        
        Clustering res = dbscan.dbscan(eds, 3, Math.sqrt(2.0), ProximityMeasure.Euclidean, null);
        List<Integer> clusterInds = Int.uniq(res.clusterings);
        assertEquals(3, clusterInds.size());

        int target = Clustering.NOISE,
                count = 0;
        for(int i = 0; i < res.clusterings.length; i++) {
            if (res.clusterings[i] == target) { count++; }
        }
        assertEquals(3, count);
        
        int 
                t2 = 0, count2 = 0,
                t3 = 1, count3 = 0;
        for(int i = 0; i < res.clusterings.length; i++) {
            if (res.clusterings[i] == t2) { count2++; }
            if (res.clusterings[i] == t3) { count3++; }
        }
        assertEquals(3, Math.min(count2, count3));
        assertEquals(6, Math.max(count2, count3));
        
    }
    
String laby =
"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz "+
"..............................z "+
"zzzzzzzzzzzzzzzzzzzzzzzzzzzzz.z "+
"z...........................z.z "+
"z.zzz....zzzz...zzzz..zzzzz.z.z "+
"z.z.zzzzzz..zzz.z..z..zzz.z.z.z "+
"z.z.............z.........z.z.z "+
"z.zzzzzzzzzzzzzzz..zzzzzzzz.z.z "+
"z.............................z "+
"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
;
    Dataset lds;
    {
        StringBuilder intermed = new StringBuilder();
        int i = 0;
        for(String s : laby.split(" ")) {
            for(int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == 'z') {
                    double  x = i + Math.random() * 0.1,
                            y = j + Math.random() * 0.1;
                    intermed.append(String.format(Locale.ENGLISH, "%.2f,%.2f\n", x, y));
                }
            }
            i++;
        }
        lds = new Csv().from(new StringReader(intermed.toString()), ",");
        lds.classAttribute = Dataset.UNSUPERVISED;
    }
    
    @Test
    public void testManh() {
        Clustering res = dbscan.dbscan(lds, 2, 0.6, ProximityMeasure.Manhattan, null);
        Item[][] clust = res.getClusters();
        Cout.plot(System.out, lds.column(1), lds.column(0), Attr.icol(res.clusterings));
        assertEquals(3, clust.length);
    }

    
    
String words =
"MINI\nMINE\nMIND\nMINED\nMINCED\n"+
"FAME\nNAME\nSAME\n"+
"OUTLIER"
;
    Dataset wds = new Csv().from(new StringReader(words), ",");
    { wds.classAttribute = Dataset.UNSUPERVISED; }
    List<AttrProxMetric> wapm = AttrProxMetric.metrics.forDs(wds);
    {
        wapm.set(0, AttrProxMetric.Levenshtein);
    }
    @Test
    public void testLevenshtein() {
        Clustering res = dbscan.dbscan(wds, 2, 1.0, ProximityMeasure.Manhattan, wapm);
        System.out.println(Arrays.toString(res.clusterings));
        assertEquals(3, res.getClusters().length);
    }
    
    
    
    Dataset pds = new Dataset();
    {
        for(int i = 0; i < 10000; i++) {
            Item it = new Item();
            it.attributes = new ArrayList<Attribute>();
            for(Double d : new double[]{ Math.random(), Math.random() })
                it.attributes.add(new DAttribute(d));
            pds.items.add(it);
        }
    }
    
    @Ignore
    @Test
    public void testBig() {
        Clustering res = dbscan.dbscan(pds, 10, 0.01, ProximityMeasure.Euclidean, null);
        Item[][] clu = res.getClusters();
        assertTrue(clu.length > 1);
    }
    
    
    
    @Test
    public void testSuggestEps() {
        
        double sugg = dbscan.suggestEps(2, lds, ProximityMeasure.Manhattan, null);
        System.out.println("suggested Eps: " + sugg);
        assertTrue(sugg > 0.5);
        
    }
}
