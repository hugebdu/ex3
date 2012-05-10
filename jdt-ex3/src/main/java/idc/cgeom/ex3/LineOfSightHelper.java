package idc.cgeom.ex3;

import delaunay_triangulation.Point_dt;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public interface LineOfSightHelper
{
    boolean seenByEachOther(Point_dt p1, Point_dt p2);
}
