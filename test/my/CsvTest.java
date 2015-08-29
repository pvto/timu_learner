
package my;

import java.io.StringReader;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CsvTest {

    public CsvTest() {
    }

    @Test
    public void testConv() {
        Csv csv = new Csv();
        Dataset ds = csv.from(new StringReader("-15\n-100\n-aurinko"), ",");
        assertEquals(3, ds.size());
        assertEquals(1, ds.item(0).attributes.size());
        assertEquals(Attribute.SAttribute.class, ds.item(0).attributes.get(0).getClass());
        assertEquals(Attribute.SAttribute.class, ds.item(2).attributes.get(0).getClass());
        
    }

}