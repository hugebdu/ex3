package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;
import gui.Visibility;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class LineOfSightHelperTest
{
    private static final String TEST_DATA_TSIN = "/test_data.tsin";
    private static final String GUARDS_DATA_TSIN = "/G1.tsin";
    private static final String DIAMONDS_DATA_TSIN = "/C1.tsin";

    @Test
    public void testGetInBetweenRouteTriangles_SourceAndTargetInTheSameTriangle() throws Exception
    {
        List<Triangle_dt> result = makeHelper(
                new Point_dt(0, 0, 0),
                new Point_dt(0, 1, 1),
                new Point_dt(1, 0, 2)).getInBetweenRouteTriangles(new Point_dt(0.9d, 0.01d), new Point_dt(0.01d, 0.9d));
        
        assertThat(result.size(), is(1));
    }

    @Test
    public void testGetInBetweenRouteTriangles_TwoTriangles() throws Exception
    {
        List<Triangle_dt> result = makeHelper(
                new Point_dt(0, 0),
                new Point_dt(1, 1),
                new Point_dt(1, -1),
                new Point_dt(2, 0)
        ).getInBetweenRouteTriangles(new Point_dt(0.001d, 0), new Point_dt(1.999d, 0));

        assertThat(result.size(), is(2));
    }

    @Test @Ignore
    public void testGetInBetweenRouteTriangles_FromFile() throws Exception
    {
        Delaunay_Triangulation triangulation = loadTriangulation(TEST_DATA_TSIN);

        Point_dt source = new Point_dt(137, 437);
        Point_dt target = new Point_dt(508, 310);

        List<Triangle_dt> result = DefaultLineOfSightHelper.on(triangulation)
                .getInBetweenRouteTriangles(source, target);

        Visibility visibility = new Visibility(triangulation);
        visibility.los(source, target);
        
        assertThat(result.size(), is(visibility._tr.size()));
        assertThat(result, hasItems(visibility._tr.toArray(new Triangle_dt[visibility._tr.size()])));
    }

    // WTF: Handle halfplane triangles
    @Test
    public void testIsBlockedBy_FromFile() throws Exception
    {
        Delaunay_Triangulation triangulation = loadTriangulation(TEST_DATA_TSIN);
        Visibility visibility = new Visibility(triangulation);
        DefaultLineOfSightHelper helper = DefaultLineOfSightHelper.on(triangulation);

        List<Point_dt> guards = loadPoints(GUARDS_DATA_TSIN);
        List<Point_dt> diamonds = loadPoints(DIAMONDS_DATA_TSIN);

        for (Point_dt guard : guards)
            for (Point_dt diamond : diamonds)
                assertThat(format("Failure on points {%s, %s}", guard, diamond), helper.seenByEachOther(diamond, guard), is(visibility.los(diamond, guard)));
    }

    @Test
    public void testIsBlockedBy_Blocked() throws Exception
    {
        Point_dt p1 = new Point_dt(1,1,1);
        Point_dt p2 = new Point_dt(10,1,5);
        Point_dt tp1 = new Point_dt(5, 10, 0);
        Point_dt tp2 = new Point_dt(5, -10, 0);
        Point_dt tp3 = new Point_dt(5, 0, 10);
        Triangle_dt t = new Triangle_dt(tp1, tp2, tp3);

        boolean blocked = makeHelper(
                new Point_dt(0, 0),
                new Point_dt(1, 1),
                new Point_dt(1, -1),
                new Point_dt(2, 0)
        ).isBlockedBy(p1,p2,t);

        assertTrue(blocked);
    }

    @Test
    public void testIsBlockedBy_NotBlocked1() throws Exception
    {
        Point_dt p1 = new Point_dt(1,1,31);
        Point_dt p2 = new Point_dt(10,1,31);
        Point_dt tp1 = new Point_dt(5, 10, 0);
        Point_dt tp2 = new Point_dt(5, -10, 0);
        Point_dt tp3 = new Point_dt(5, 0, 10);
        Triangle_dt t = new Triangle_dt(tp1, tp2, tp3);

        boolean blocked = makeHelper(
                new Point_dt(0, 0),
                new Point_dt(1, 1),
                new Point_dt(1, -1),
                new Point_dt(2, 0)
        ).isBlockedBy(p1,p2,t);

        assertFalse(blocked);
    }

    @Test
    public void testIsBlockedBy_NotBlocked2() throws Exception
    {
        Point_dt p1 = new Point_dt(1,1,1);
        Point_dt p2 = new Point_dt(2,1,1);
        Point_dt tp1 = new Point_dt(5, 10, 0);
        Point_dt tp2 = new Point_dt(5, -10, 0);
        Point_dt tp3 = new Point_dt(5, 0, 10);
        Triangle_dt t = new Triangle_dt(tp1, tp2, tp3);

        boolean blocked = makeHelper(
                new Point_dt(0, 0),
                new Point_dt(1, 1),
                new Point_dt(1, -1),
                new Point_dt(2, 0)
        ).isBlockedBy(p1,p2,t);

        assertFalse(blocked);
    }
    
    private List<Point_dt> loadPoints(String fileName) throws Exception
    {
        String file = getClass().getResource(fileName).getFile();
        return newArrayList(Delaunay_Triangulation.read_file(file));
    }

    private Delaunay_Triangulation loadTriangulation(String fileName) throws Exception
    {
        String file = getClass().getResource(fileName).getFile();
        return new Delaunay_Triangulation(file);        
    }

    private DefaultLineOfSightHelper makeHelper(Point_dt ... points)
    {
        return DefaultLineOfSightHelper.on(new Delaunay_Triangulation(points));
    }
}
