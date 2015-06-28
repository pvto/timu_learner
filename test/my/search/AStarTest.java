package my.search;

import java.util.ArrayList;
import java.util.List;
import my.DirectedGraph;
import my.DirectedGraph.Edge;
import my.DirectedGraph.Vertex;
import my.f.Fn.F1D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AStarTest {

        String map =
"#########\n"+
"#.....#.#\n"+
"#.....#.#\n"+
"#.....#.#\n"+
"#.....#.#\n"+
"#.......#\n"+
"#########";

    @Test
    public void testConstructGraph()
    {
        List<Vertex> list = DirectedGraph.constructGraph(map.split("\r?\n"));
        assertEquals(7 * 9, list.size());
        assertEquals(list.get(11), list.get(10).edges.get(0).target);
        assertEquals(list.get(19), list.get(10).edges.get(1).target);
    }
    
    @Test
    public void testGetConnectingEdges()
    {
        List<Vertex> list = DirectedGraph.constructGraph(map.split("\r?\n"));
        List<Vertex> path = new ArrayList<>();
        for(Integer i : new int[]{10,11,12,21,30})
            path.add(list.get(i));
        List<Edge> edges = DirectedGraph.getConnectingEdges(path);
        assertEquals(4, edges.size());
        assertEquals(path.get(3), edges.get(2).target);
    }
    
    @Test
    public void testAstar()
    {
        List<Vertex> list = DirectedGraph.constructGraph(map.split("\r?\n"));
        Vertex start = list.get(10);
        final Vertex end = list.get(16);
        assertEquals(1, end.edges.size());
        List<Edge> path = new AStar().astar(start, end, new Eucl(end));
        assertTrue(20 > path.size());
        assertTrue(path.size() > 0);
        System.out.print(path.size() + " -> ");
        path = DirectedGraph.shorten(start, path);
        System.out.println(path.size());
    }
    
    public static class Eucl implements F1D<Vertex> {
        public Vertex end;
        public Eucl(Vertex end) { this.end = end; }
        DirectedGraph.NodeDist dist = new DirectedGraph.EuclideanNodeDist();
        @Override
        public double eval(Vertex a)
        {
            return dist.dist(a.node, end.node);
        }
    }
    
String map2 =
"###################################\n"+
"#.#...#...###..#...#....#.......#.#\n"+
"#...#.#.#......#......#.....##..#.#\n"+
"#####.#.######.##########..##...#.#\n"+
"#..........#......##......##..#.#.#\n"+
"#.########.#.#.##..########..####.#\n"+
"#.#.#....#...#.......#............#\n"+
"#.....#..#####.......########.#####\n"+
"#######......#..........#.........#\n"+
"#.....##.##.#####.###############.#\n"+
"#.................................#\n"+
"###################################";

    @Test
    public void testLaby()
    {
        List<Vertex> list = DirectedGraph.constructGraph(map2.split("\r?\n"));
        Vertex start = list.get(36);
        final Vertex end = list.get(68);
        assertEquals(1, end.edges.size());
        List<Edge> path = new AStar().astar(start, end, new Eucl(end));
        char[] map3 = map2.toCharArray();
        for(Edge e : path)
            map3[((int)e.target.node.dimensionPos(1))*36 + ((int)e.target.node.dimensionPos(0))] = '+';
        System.out.println(new String(map3));

        System.out.print(path.size() + " -> ");
        path = DirectedGraph.shorten(start, path);
        System.out.println(path.size());
    }
}
