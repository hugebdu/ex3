package idc.cgeom.ex3.greedy;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.AdjacencyMatrix;
import idc.cgeom.ex3.AdjacencyMatrix.Guard;
import idc.cgeom.ex3.BaseSolution;

import java.util.Collection;

import static com.google.common.collect.Lists.transform;
import static idc.cgeom.ex3.AdjacencyMatrix.ByNumberOfDiamondsOrder;
import static idc.cgeom.ex3.AdjacencyMatrix.PointExtractor;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class GreedySolution extends BaseSolution
{
    @Override
    public Collection<Point_dt> solve(Delaunay_Triangulation triangulation, ImmutableCollection<Point_dt> guards, ImmutableCollection<Point_dt> diamonds)
    {
        AdjacencyMatrix adjacencyMatrix = createInitialAdjacencyMatrix(triangulation, guards, diamonds);

        ImmutableList.Builder<Guard> builder = ImmutableList.builder();

        while (!adjacencyMatrix.diamonds().isEmpty())
        {
            Guard guard = ByNumberOfDiamondsOrder.max(adjacencyMatrix.guards());
            builder.add(guard);
            adjacencyMatrix = adjacencyMatrix.reducedBy(guard);
        }

        return transform(builder.build(), PointExtractor);
    }
}
