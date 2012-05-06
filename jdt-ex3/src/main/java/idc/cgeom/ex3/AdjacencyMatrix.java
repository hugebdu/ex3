package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Point_dt;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public interface AdjacencyMatrix
{
    ImmutableCollection<Guard> guards();
    ImmutableCollection<Diamond> diamonds();

    AdjacencyMatrix reducedBy(Guard guard);

    public interface PointWrapper
    {
        Point_dt getPointDt();
    }

    public interface Diamond extends PointWrapper
    {
        ImmutableCollection<Guard> guardedByGuards();
    }

    public interface Guard extends PointWrapper
    {
        ImmutableCollection<Diamond> guardingDiamonds();
    }
}
