package idc.cgeom.ex3;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Ordering;
import delaunay_triangulation.Point_dt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public interface AdjacencyMatrix
{
    public static final Ordering<Guard> ByNumberOfDiamondsOrder = new Ordering<Guard>()
    {
        @Override
        public int compare(Guard left, Guard right)
        {
            return left.guardingDiamonds().size() - right.guardingDiamonds().size();
        }
    };

    public static final Ordering<Diamond> ByNumberOfGuardsOrder = new Ordering<Diamond>()
    {
        @Override
        public int compare(Diamond left, Diamond right)
        {
            return left.guardedByGuards().size() - right.guardedByGuards().size();
        }
    };

    public static final Function<PointWrapper, Point_dt> PointExtractor = new Function<PointWrapper, Point_dt>()
    {
        @Override
        public Point_dt apply(PointWrapper input)
        {
            return input.getPointDt();
        }
    };

    ImmutableCollection<Guard> guards();
    ImmutableCollection<Diamond> diamonds();

    AdjacencyMatrix reducedBy(Guard ... guards);
    AdjacencyMatrix reducedBy(Set<Guard> guards);

    void exportToCsv(PrintWriter writer) throws IOException;

    public interface PointWrapper
    {
        Point_dt getPointDt();
    }

    public interface Diamond extends PointWrapper
    {
        ImmutableCollection<Guard> guardedByGuards();
        boolean isGuardedBy(Guard guard);
    }

    public interface Guard extends PointWrapper
    {
        ImmutableCollection<Diamond> guardingDiamonds();
        boolean isGuarding(Diamond diamond);
    }
}
