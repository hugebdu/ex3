package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.greedy.InversedGreedySolution;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static idc.cgeom.ex3.IOHelper.*;
import static java.lang.String.format;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/20/12
 */
public class Main
{
    private static final String TEST_DATA_TSIN = "test_data.tsin";
    private static final String GUARDS_TSIN = "G1.tsin";
    private static final String DIAMONDS_TSIN = "C1.tsin";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) throws IOException
    {
        trace("Loading terrain from '%s'", TEST_DATA_TSIN);
        Point_dt[] terrain = readPointsToArray(resourceToURL("/" + TEST_DATA_TSIN));
        
        trace("Loading guards points set from '%s'", GUARDS_TSIN);
        List<Point_dt> guards = readPoints(resourceToURL("/" + GUARDS_TSIN));

        trace("Loading diamonds points set from '%s'", DIAMONDS_TSIN);
        List<Point_dt> diamonds = readPoints(resourceToURL("/" + DIAMONDS_TSIN));
        
        trace("Running Delaunay Triangulation");
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(terrain);

        LineOfSightHelper helper = DefaultLineOfSightHelper.on(triangulation);

        trace("Running inversed greedy solution");
        Collection<Point_dt> solution = new InversedGreedySolution().solve(triangulation,
                helper.elevate(guards), helper.elevate(diamonds));
        
        File resultsFile = resultsFile();
        trace("Writing results to '%s'", resultsFile.getAbsolutePath());
        writePoints(solution, resultsFile);
    }

    private static File resultsFile()
    {
        String fileName = "results_" + DATE_FORMAT.format(new Date()) + ".tsin";
        return new File(System.getProperty("user.dir"), fileName);
    }

    private static void trace(String message, Object ... args)
    {
        System.out.println(format(message, args));
    }
}
