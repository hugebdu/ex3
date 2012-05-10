package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.greedy.GreedySolution;
import idc.cgeom.ex3.greedy.InversedGreedySolution;

import java.util.Collection;

import static idc.cgeom.ex3.IOHelper.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class SolutionsDriver
{
    public static void main(String[] args) throws Throwable
    {
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(resourceToAbsoluteFilePath("/test_data.tsin"));

        LineOfSightHelper helper = DefaultLineOfSightHelper.on(triangulation);

        ImmutableCollection<Point_dt> guards = helper.elevate(readPoints(resourceToURL("/G1.tsin")));
        ImmutableCollection<Point_dt> diamonds = helper.elevate(readPoints(resourceToURL("/C1.tsin")));


        Collection<Point_dt> s1 = new InversedGreedySolution().solve(triangulation, guards, diamonds);
        Collection<Point_dt> s2 = new GreedySolution().solve(triangulation, guards, diamonds);

        System.out.println("Inversed: " + s1.size() + ", greedy: " + s2.size());
    }
}
