package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.greedy.GreedySolution;

import java.util.Collection;

import static com.google.common.collect.ImmutableList.copyOf;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class SolutionsDriver
{
    public static void main(String[] args) throws Throwable
    {
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(resourceToFile("/test_data.tsin"));
        Point_dt[] guards = Delaunay_Triangulation.read_file(resourceToFile("/G1.tsin"));
        Point_dt[] diamonds = Delaunay_Triangulation.read_file(resourceToFile("/C1.tsin"));

        Collection<Point_dt> solution = new GreedySolution().solve(triangulation, copyOf(guards), copyOf(diamonds));

        System.out.println(solution.size());
    }

    private static String resourceToFile(String resource)
    {
        return CsvExporter.class.getResource(resource).getFile();
    }
}
