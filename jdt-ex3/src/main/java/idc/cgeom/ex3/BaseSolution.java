package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public abstract class BaseSolution implements Solution
{
    protected AdjacencyMatrix createInitialAdjacencyMatrix(Delaunay_Triangulation triangulation, ImmutableCollection<Point_dt> guards, ImmutableCollection<Point_dt> diamonds)
    {
        return new DefaultAdjacencyMatrix(guards, diamonds,
                createLineOfSightHelper(triangulation));
    }

    protected LineOfSightHelper createLineOfSightHelper(Delaunay_Triangulation triangulation)
    {
        return DefaultLineOfSightHelper.on(triangulation);
    }
}
