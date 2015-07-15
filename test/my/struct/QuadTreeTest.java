package my.struct;

import my.struct.QuadTree.CoordHolder;
import my.struct.QuadTree.Quad;
import org.junit.Test;
import static org.junit.Assert.*;

public class QuadTreeTest {
    
    public QuadTreeTest() {
    }

    @Test
    public void testQuadTree()
    {
        QuadTree<Integer> q = new QuadTree<>();
        double[] coords = new double[]
        {
            1.0, 1.0,
            2.0, 2.0,
            0.0, 0.0,
            3.0, 3.0,
            0.5, 0.5
        };
        for(int i = 0; i < coords.length;)
        {
            q.place(coords[i++], coords[i++], i / 2);
        }
        
        assertEquals(coords.length / 2, q.size());
        assertNotNull(q.root.LL);
        assertEquals(2, q.root.UL.UL.items.size());
        assertEquals(2, q.root.UL.LR.items.size());
        
        Quad quad = q.root.UL.UL;
        CoordHolder ch = q.root.UL.UL.items.get(0);
        ch.x -= 0.5;
        ch.replace();
        assertEquals(1, quad.items.size());
        assertNotNull(quad.parent.parent.parent);

        //q.print(System.out);
    }
    
    @Test public void testBig10_0() { testBig(10, 0); }
    @Test public void testBig10_1() { testBig(10, 0); }
    @Test public void testBig10_2() { testBig(10, 0); }
    @Test public void testBig10D_0() { testBig(10, 1); }
    @Test public void testBig10D_1() { testBig(10, 1); }
    @Test public void testBig10D_2() { testBig(10, 1); }
    @Test public void testBig100_0() { testBig(100, 0); }
    @Test public void testBig100_1() { testBig(100, 0); }
    @Test public void testBig100_2() { testBig(100, 0); }
    @Test public void testBig1000_0() { testBig(1000, 0); }
    @Test public void testBig1000_1() { testBig(1000, 0); }
    @Test public void testBig1000_2() { testBig(1000, 0); }
    
    public void testBig(int MAX, int DYNAMIC)
    {
        long time = System.currentTimeMillis();
        QuadTree<Integer> q = new QuadTree<>();
        q.LEAF_MAX_OBJECTS = MAX;
        if (DYNAMIC > 0)
            q.DYNAMIC_MAX_OBJECTS = true;
        for(int i = 0; i < 1000000; i++)
        {
            q.place(Math.round(Math.random()*1000), Math.round(Math.random()*1000), i);
        }
        long passed = System.currentTimeMillis() - time;
        System.out.println(passed + " ms, MAX="+MAX+",DYN="+DYNAMIC);
        if (q.size() < 20)
            q.print(System.out);
    }
}
