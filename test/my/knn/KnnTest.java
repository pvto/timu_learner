package my.knn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import my.Attribute.DAttribute;
import my.Csv;
import my.Dataset;
import my.ProximityMeasure;
import my.Item;
import my.Voting;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnnTest {
    
    public KnnTest() { }

String data = 
"1,A,X\n"+
"1,B,Z\n"+
"4,A,Y\n"+
"4,B,Y\n"+
"9,C,Z\n"
;
    Dataset ds = new Csv().from(new StringReader(data), ",\\s*");
    int CLASS = Dataset.classAttribute(ds);
    Item test = ds.items.get(3);
    Knn knn = new Knn();

    @Test
    public void testKnn1() {
        Knn.KnnResult res = knn.knn(test, ds, 1, ProximityMeasure.HEOM, Voting.EqualWeights);
        assertEquals(1, res.nearestNeighbours.size());
        assertEquals(Item.a(test, CLASS), res.predictedClass);
    }

    @Test
    public void testKnn2() {
        Knn.KnnResult res = knn.knn(test, ds, 2, ProximityMeasure.HEOM, Voting.InverseDistanceWeighted);
        assertEquals(2, res.nearestNeighbours.size());
        assertEquals(ds.items.get(1), res.nearestNeighbours.get(1));
        assertEquals(Item.a(test, CLASS), res.predictedClass);
    }

    @Test
    public void testKnn3() {
        Knn.KnnResult res = knn.knn(test, ds, 3, ProximityMeasure.HEOM, Voting.EqualWeights);
        assertEquals(3, res.nearestNeighbours.size());
        assertEquals(ds.items.get(1), res.nearestNeighbours.get(1));
        assertEquals(ds.items.get(2), res.nearestNeighbours.get(2));
        assertEquals(Item.a(test, CLASS), res.predictedClass);
    }
    
String manhattanData =
"1,0,0,A\n"+
"1,0,1,A\n"+
"1,1,1,B\n"+
"0,0,2,A\n"+
"1,0,4,B\n"
;
    Dataset mds = new Csv().from(new StringReader(manhattanData), ",");
    int MCL = Dataset.classAttribute(mds);
    Item mtest = mds.items.get(2);
    
    @Test
    public void testManhattanKnn4() {
        Knn.KnnResult res = knn.knn(mtest, mds, 4, ProximityMeasure.Manhattan, Voting.EqualWeights);
        assertEquals(4, res.nearestNeighbours.size());
        assertEquals(mds.items.get(1), res.nearestNeighbours.get(1));
        assertEquals(mds.items.get(0), res.nearestNeighbours.get(2));
        assertEquals(mds.items.get(3), res.nearestNeighbours.get(3));
        assertEquals("A", res.predictedClass.toString());
    }
    
    @Test
    public void irisTest() throws FileNotFoundException {
        Dataset iris = new Csv().from(new FileInputStream("data/iris.data"), Charset.forName("UTF-8"), ",");
        int ICL = Dataset.classAttribute(iris);
        Item itest = new Item(iris.items.get(0));
        DAttribute da = (DAttribute)itest.attributes.get(2);
        da.value = 1.45;
        Knn.KnnResult res = knn.knn(itest, iris, 4, ProximityMeasure.Euclidean, Voting.InverseDistanceWeighted);
        assertEquals(4, res.nearestNeighbours.size());
        assertEquals(iris.items.get(0), res.nearestNeighbours.get(0));
        assertEquals("Iris-setosa", res.predictedClass.toString());
        
        // cross-validate with k 1-item test sets
        int correctlyClassified = 0,
                erroneouslyClassified = 0
                ;
        for(int i = 0; i < iris.items.size(); i++) {
            final int i_ = i;
            Dataset.RowSelection sel = new Dataset.RowSelection() {
                @Override
                public boolean doInclude(Item item, Dataset ds, int rowIndex) {
                    return rowIndex != i_;
                }
            };
            Dataset subset = Dataset.subset(iris, sel);
            assertTrue(subset.items.size() == iris.items.size() - 1);
            Collections.shuffle(subset.items);
            Item excluded = iris.items.get(i);
            Knn.KnnResult r = knn.knn(excluded, subset, 3, ProximityMeasure.Euclidean, Voting.InverseDistanceWeighted);
            
            if (r.predictedClass.equals(Item.a(excluded, ICL))) {
                correctlyClassified++;
            } else {
                erroneouslyClassified++;
            }
        }
        System.out.println("knn:iris - cross-validation with k 1-item test sets;\n correctly classified = " + correctlyClassified
                + "\n erroneously classified = " + erroneouslyClassified);
        assertTrue(((double)correctlyClassified)/(correctlyClassified+erroneouslyClassified) > 0.95);
        
    }
}
