package idc.cgeom.ex3;

import com.google.common.collect.ImmutableCollection;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

import java.io.FileWriter;
import java.io.PrintWriter;

import static idc.cgeom.ex3.IOHelper.*;
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
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(resourceToAbsoluteFilePath("/test_data.tsin"));
        LineOfSightHelper helper = DefaultLineOfSightHelper.on(triangulation);
        //helper = new VisibilityLineOfSightHelper(triangulation);
        Point_dt[] guards = readPointsToArray(resourceToURL("/G1.tsin"));
        Point_dt[] diamonds = readPointsToArray(resourceToURL("/C1.tsin"));

        ImmutableCollection<Point_dt> elevatedGuards = helper.elevate(asList(guards));
        ImmutableCollection<Point_dt> elevatedDiamonds = helper.elevate(asList(diamonds));
        AdjacencyMatrix matrix = new DefaultAdjacencyMatrix(elevatedGuards, elevatedDiamonds, helper);
        PrintWriter writer = new PrintWriter(new FileWriter("matrix.csv"));
        matrix.exportToCsv(writer);
        writer.close();
    }
}
