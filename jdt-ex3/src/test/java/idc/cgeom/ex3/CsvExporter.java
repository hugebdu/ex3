package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

import java.io.FileWriter;
import java.io.PrintWriter;

import static java.util.Arrays.asList;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class CsvExporter
{
    public static void main(String[] args) throws Exception
    {
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(resourceToFile("/test_data.tsin"));
        LineOfSightHelper helper = new VisibilityLineOfSightHelper(triangulation);
        Point_dt[] guards = Delaunay_Triangulation.read_file(resourceToFile("/G1.tsin"));
        Point_dt[] diamonds = Delaunay_Triangulation.read_file(resourceToFile("/C1.tsin"));

        AdjacencyMatrix matrix = new DefaultAdjacencyMatrix(asList(guards), asList(diamonds), helper);
        PrintWriter writer = new PrintWriter(new FileWriter("matrix.csv"));
        matrix.exportToCsv(writer);
        writer.close();
    }

    private static String resourceToFile(String resource)
    {
        return CsvExporter.class.getResource(resource).getFile();
    }
}
