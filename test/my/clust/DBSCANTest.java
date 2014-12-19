package my.clust;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import my.AttrProxMetric;
import my.Csv;
import my.Dataset;
import my.Item;
import my.ProximityMeasure;
import org.junit.Test;
import static org.junit.Assert.*;

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
    DBSCAN dbscan = new DBSCAN();
    
    static public List<Integer> uniq(int[] list) {
        int[] ordered = Arrays.copyOf(list, list.length);
        Arrays.sort(ordered);
        List<Integer> res = new ArrayList<Integer>();
        int next;
        res.add(next = ordered[0]);
        for(int i = 0; i < ordered.length; i++) {
            if (ordered[i] != next) {
                res.add(next = ordered[i]);
            }
        }
        return res;
    }
    @Test
    public void testEucl() {
        
        DBSCAN.DBSCANResult res = dbscan.dbscan(eds, 3, Math.sqrt(2.0), ProximityMeasure.Euclidean, null);
        List<Integer> clusterInds = uniq(res.clusterings);
        assertEquals(3, clusterInds.size());

        int target = DBSCAN.NOISE,
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
"z.zzzzzzzzzzzzzzz  zzzzzzzz.z.z "+
"z.............................z "+
"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz "
;
    Dataset lds;
    {
        StringBuilder intermed = new StringBuilder();
        int i = 0;
        for(String s : laby.split(" ")) {
            for(int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == 'z')
                    intermed.append(i+","+j+"\n");
            }
            i++;
        }
        lds = new Csv().from(new StringReader(intermed.toString()), ",");
    }
    
    @Test
    public void testManh() {
        DBSCAN.DBSCANResult res = dbscan.dbscan(lds, 2, 0.5, ProximityMeasure.Manhattan, null);
        Item[][] clust = res.getClusters();
        System.out.println(Arrays.toString(res.clusterings));
        assertEquals(3, clust.length);
        
    }

String words =
"MINE\nMIND\nMINED\nMINCED\n"+
"FAME\nNAME\nDAME\n"+
"OUTLIER"
;
    Dataset wds = new Csv().from(new StringReader(words), ",");
    List<AttrProxMetric> wapm = ProximityMeasure.metrics.forDs(wds);
    {
        wapm.set(0, AttrProxMetric.Levenshtein);
    }
    @Test
    public void testLevenshtein() {
        DBSCAN.DBSCANResult res = dbscan.dbscan(wds, 2, 1.0, ProximityMeasure.Manhattan, wapm);
        System.out.println(Arrays.toString(res.clusterings));
        assertEquals(3, res.getClusters().length);
    }
}
