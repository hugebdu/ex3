package idc.cgeom.ex3;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import delaunay_triangulation.Point_dt;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.toArray;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public abstract class IOHelper
{
    private static final Splitter SPLITTER = Splitter.on(' ');

    public static String resourceToAbsoluteFilePath(String resource)
    {
        return IOHelper.class.getResource(resource).getFile();
    }

    public static URL resourceToURL(String resource)
    {
        return IOHelper.class.getResource(resource);
    }

    public static List<Point_dt> readPoints(InputStream stream) throws IOException
    {
        return processLines(CharStreams.readLines(supplier(stream)));
    }

    public static List<Point_dt> readPoints(URL file) throws IOException
    {
        return readPoints(file.openStream());
    }

    public static Point_dt[] readPointsToArray(URL file) throws IOException
    {
        return toArray(readPoints(file), Point_dt.class);
    }

    public static List<Point_dt> readPoints(File file) throws IOException
    {
        return processLines(Files.readLines(file, Charsets.ISO_8859_1));
    }

    private static InputSupplier<InputStreamReader> supplier(final InputStream stream)
    {
        return new InputSupplier<InputStreamReader>()
        {
            @Override
            public InputStreamReader getInput() throws IOException
            {
                return new InputStreamReader(stream, Charsets.ISO_8859_1);
            }
        }; 
    }

    private static List<Point_dt> processLines(List<String> lines)
    {
        List<Point_dt> result = null;

        for (String line : lines)
        {
            if (result == null)
                result = new ArrayList<Point_dt>(parseInt(line));
            else
                result.add(parsePoint(line));
        }
        return result;
    }

    private static Point_dt parsePoint(String line)
    {
        Iterable<String> split = SPLITTER.split(line);
        return new Point_dt(
                parseDouble(get(split, 0)),
                parseDouble(get(split, 1)),
                parseDouble(get(split, 2))
        );
    }

    public static void writePoints(Collection<Point_dt> points, File file) throws IOException
    {
        PrintWriter writer = new PrintWriter(file);
        writer.println(points.size());

        for (Point_dt point : points)
            writer.println(format("%s %s %s", point.x(), point.y(), point.z()));

        writer.close();
    }
}
