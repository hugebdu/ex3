package idc.cgeom.ex3;

import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class LineOfSightHelper
{
    private final Delaunay_Triangulation triangulation;

    private LineOfSightHelper(Delaunay_Triangulation triangulation)
    {
        this.triangulation = triangulation;
    }

    public ImmutableList<Triangle_dt> getInBetweenRouteTriangles(Point_dt source, Point_dt target)
    {
        //TODO: implement
        throw new UnsupportedOperationException("implement");
    }

    public boolean seenByEachOther(Point_dt p1, Point_dt p2)
    {
        //TODO: implement
        throw new UnsupportedOperationException("implement");
    }

    public boolean isBlockedBy(Point_dt p1, Point_dt p2, Triangle_dt triangle)
    {
        //TODO: implement
        throw new UnsupportedOperationException("implement");
    }

    public static LineOfSightHelper on(Delaunay_Triangulation triangulation)
    {
        return new LineOfSightHelper(triangulation);
    }
}
