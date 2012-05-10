package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.greedy.GreedySolution;
import idc.cgeom.ex3.greedy.InversedGreedySolution;

import java.util.Collection;

import static com.google.common.collect.ImmutableList.copyOf;
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
        Point_dt[] guards = readPoints(resourceToURL("/G1.tsin")).toArray(new Point_dt[0]);
        Point_dt[] diamonds = readPoints(resourceToURL("/C1.tsin")).toArray(new Point_dt[0]);

        Collection<Point_dt> s1 = new InversedGreedySolution().solve(triangulation, copyOf(guards), copyOf(diamonds));
        Collection<Point_dt> s2 = new GreedySolution().solve(triangulation, copyOf(guards), copyOf(diamonds));

        System.out.println("Inversed: " + s1.size() + ", greedy: " + s2.size());
    }
}
