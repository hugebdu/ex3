package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import delaunay_triangulation.Point_dt;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public interface AdjacencyMatrix
{
    ImmutableCollection<Guard> guards();
    ImmutableCollection<Diamond> diamonds();

    AdjacencyMatrix reducedBy(Guard ... guards);
    AdjacencyMatrix reducedBy(ImmutableSet<Guard> guards);

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
