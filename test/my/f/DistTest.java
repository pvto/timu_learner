
package my.f;

import static my.f.Dist.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DistTest {

    @Test
    public void testStringOneDistance() {
        assertEquals(0.0, stringOnedistance("", ""), 1e-6);
        assertEquals(1.0, stringOnedistance("", "a"), 1e-6);
        assertEquals(1.0, stringOnedistance("a", ""), 1e-6);
        assertEquals(0.0, stringOnedistance("a", "a"), 1e-6);
        assertEquals(1.0, stringOnedistance("ab", "a"), 1e-6);
        assertEquals(1.0, stringOnedistance("ba", "a"), 1e-6);
        assertEquals(1.0, stringOnedistance("a", "ab"), 1e-6);
        assertEquals(1.0, stringOnedistance("a", "ba"), 1e-6);
        assertEquals(1.0, stringOnedistance("ba", "ca"), 1e-6);
        assertEquals(1.0, stringOnedistance("abcd", "aabcd"), 1e-6);
        assertEquals(2.0, stringOnedistance("cba", "abc"), 1e-6);
        assertEquals(4.0, stringOnedistance("dcba", "abcd"), 1e-6);
        
    }

}