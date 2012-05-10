package idc.cgeom.ex3;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public interface LineOfSightHelper
{
    boolean seenByEachOther(Point_dt p1, Point_dt p2);

    ImmutableCollection<Point_dt> elevate(Iterable<Point_dt> points);
}
