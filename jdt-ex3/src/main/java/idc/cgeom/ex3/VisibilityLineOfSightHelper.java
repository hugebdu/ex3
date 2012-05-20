package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import gui.Visibility;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class VisibilityLineOfSightHelper implements LineOfSightHelper
{
    private final Visibility visibility;

    public VisibilityLineOfSightHelper(Delaunay_Triangulation triangulation)
    {
        visibility = new Visibility(triangulation);
    }

    @Override
    public boolean seenByEachOther(Point_dt p1, Point_dt p2)
    {
        return visibility.los(p1, p2);
    }

    @Override
    public ImmutableCollection<Point_dt> elevate(Iterable<Point_dt> points)
    {
        return ImmutableList.copyOf(points);
    }
}
