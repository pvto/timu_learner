
package my;

import java.util.ArrayList;
import java.util.List;

/**
 * A directed graph containing nodes that could be
 * mapped over continuous n-dim spaces.
 * 
 * @author pvto https://github.com/pvto
 */
public class DirectedGraph {

/*           .-->[Edge]-->[Vertex]--> ...
            /             
    [Vertex]-->[Edge]-->[Vertex]--> ...
     |                   |
     |                   |
   [Node]               [Node]
    (x,y,)               (x,y,)
*/
    
    public interface Node<T> {
        
        double dimensionPos(int dimension);
        int dimensions();
        T getContent();
    }
    
    public interface NodeDist {
        
        double dist(Node a, Node b);
    }    

    public static class Vertex {

        public Node node;
        public List<Edge> edges = new ArrayList<>();
        public int dirty = 0;
    }

    public static class Edge {

        public double length = 1.0;
        public Vertex target;
        public Edge(Vertex targ) { this.target = targ; }
    }

    
    
    public static Node NODE0 = new NodeDN(0);
    
    public static class NodeD2<T> implements Node {
        
        public final double[] dims = {0,0};
        
        @Override 
        public double dimensionPos(int dimension) {
            return dimension < 2 ? dims[dimension] : 0.0;
        }
        
        @Override
        public int dimensions() { return 2; }
        
        public NodeD2(double x, double y) {
            dims[0] = x; dims[1] = y;
        }
        
        @Override
        public String toString() {
            return "(" + dims[0] + "," + dims[1] + ")";
        }

        @Override
        public T getContent() {
            throw new UnsupportedOperationException("Implement me.");
        }
        
    }

    
    public static class NodeDN<T> implements Node {
        
        public final double[] dims;
        
        @Override
        public double dimensionPos(int dimension) {
            return dimension < dims.length ? dims[dimension] : 0.0;
        }
        
        @Override
        public int dimensions() {
            return dims.length;
        }

        public NodeDN(int n) {
            dims = new double[n];
        }
        
        @Override
        public T getContent() {
            throw new UnsupportedOperationException("Implement me.");
        }
    }

    
    
    

    
    
    public static class EuclideanNodeDist implements NodeDist {
        
        @Override
        public double dist(Node a, Node b) {
            double sum = 0.0;
            for(int i = 0; i < Math.max(a.dimensions(), b.dimensions()); i++) {
                double d = a.dimensionPos(i) - b.dimensionPos(i);
                sum += d * d;
            }
            return Math.sqrt(sum);
        }
    }
    
    
    
    public static class ManhattanNodeDist implements NodeDist {
        
        @Override
        public double dist(Node a, Node b) {
            double sum = 0.0;
            for(int i = 0; i < Math.max(a.dimensions(), b.dimensions()); i++) {
                double d = a.dimensionPos(i) - b.dimensionPos(i);
                sum += Math.abs(d);
            }
            return sum;
        }
    }  
    
    

    
    public static List<Vertex> constructGraph(String[] map2d) {
        
        String[] rows = map2d;
        List<Vertex> ret = new ArrayList<>();
        for(int j = 0; j < rows.length; j++) {
            
            String row = rows[j];
            for (int i = 0; i < row.length(); i++) {
                
                Vertex v = new Vertex();
                ret.add(v);
                if (row.charAt(i) == '#') {
                    v.node = NODE0;
                    continue;
                }
                v.node = new NodeD2( i,j );
                if (i > 0) {
                    
                    Vertex prev = ret.get(ret.size() - 2);
                    if (prev.node.dimensions() > 0 && v.node.dimensions() > 0) {
                        
                        prev.edges.add(new Edge(v));
                        v.edges.add(new Edge(prev));
                    }
                }
                if (j > 0) {
                    
                    Vertex prev = ret.get(ret.size() - 1 - row.length());
                    if (prev.node.dimensions() > 0 && v.node.dimensions() > 0) {
                        
                        prev.edges.add(new Edge(v));
                        v.edges.add(new Edge(prev));
                    }
                }
            }
        }
        return ret;
    }
    
    
    
    
    public static List<Edge> getConnectingEdges(List<Vertex> stack) {
        List<Edge> edges = new ArrayList<>(stack.size() - 1);
        for(int i = 0; i < stack.size() - 1; i++) {
            Vertex v = stack.get(i);
            Vertex next = stack.get(i+1);
            for (Edge e : v.edges) {
                if (next == e.target) {
                    edges.add(e);
                    break;
                }
            }
        }
        return edges;
    }

    public static List<Edge> shorten(Vertex start, List<Edge> edges) {
        Vertex place = start;
        int last = edges.size() - 1;
        for(int first = 0;  
                first < last - 1;  
                place = edges.get(first++).target, 
                    last = edges.size() - 1) {
            OHMY: while(last > first + 1) {
                for(Edge e : place.edges)
                    if (e.target == edges.get(last).target) {
                        List<Edge> tmp = (first <= 0 ? new ArrayList<Edge>() : edges.subList(0, first));
                        tmp.addAll(edges.subList(last, edges.size()));
                        edges = tmp;
                        break OHMY;
                    }
                last--;
            }
        }
        return edges;
    }
}
