package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public interface Solution
{
    Collection<Point_dt> solve(Delaunay_Triangulation triangulation,
                               ImmutableCollection<Point_dt> guards,
                               ImmutableCollection<Point_dt> diamonds);
}
