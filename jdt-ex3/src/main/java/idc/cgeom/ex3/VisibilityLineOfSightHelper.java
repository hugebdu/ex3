package idc.cgeom.ex3;

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
}
