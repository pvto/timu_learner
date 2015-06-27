package my.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import my.DirectedGraph;
import my.DirectedGraph.Edge;
import my.DirectedGraph.Vertex;
import static my.DirectedGraph.getConnectingEdges;
import my.f.Fn.F1D;

/**
 * @author pvto https://github.com/pvto
 */
public class AStar {

    
    
    public List<Edge> astar(Vertex start, Vertex end, F1D<Vertex> f)
    {
        List<Vertex> stack = new ArrayList<>();
        Vertex cur = start;
        for(;;)
        {
            cur.dirty++;
            if (cur == end)
            {
                break;
            }
            double best = Double.MAX_VALUE;
            int bestInd = -1;
            
            for(int i = 0; i < cur.edges.size(); i++)
            {
                Vertex may = cur.edges.get(i).target;
                if (may.dirty > 0)
                    continue;
                double fit = f.eval(may);
                if (fit < best)
                {
                    best = fit;
                    bestInd = i;
                }
            }
            if (bestInd == -1)
            {
                if (stack.isEmpty())
                    return Collections.EMPTY_LIST;
                best = Double.MAX_VALUE;
                for(int i = 0; i < stack.size(); i++)
                {
                    Vertex stc = stack.get(i);
                    if (f.eval(stc) < best)
                        bestInd = i;
                }
                cur = stack.remove(bestInd);
            }
            else 
            {
                stack.add(cur);
                cur = cur.edges.get(bestInd).target;
            }
        }
        List<Edge> edges = getConnectingEdges(stack);
        return edges;
    }
    

}
