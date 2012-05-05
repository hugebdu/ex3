package idc.cgeom.ex3;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;
import gui.Visibility;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void testGetInBetweenRouteTriangles_FromFile() throws Exception
    {
        Delaunay_Triangulation triangulation = load(TEST_DATA_TSIN);

        Point_dt source = new Point_dt(137, 437);
        Point_dt target = new Point_dt(508, 310);

        List<Triangle_dt> result = LineOfSightHelper.on(triangulation)
                .getInBetweenRouteTriangles(source, target);

        Visibility visibility = new Visibility(triangulation);
        visibility.los(source, target);
        
        assertThat(result.size(), is(visibility._tr.size()));
        assertThat(result, hasItems(visibility._tr.toArray(new Triangle_dt[visibility._tr.size()])));
    }
    
    private Delaunay_Triangulation load(String fileName) throws Exception
    {
        String file = getClass().getResource(fileName).getFile();
        return new Delaunay_Triangulation(file);        
    }

    private LineOfSightHelper makeHelper(Point_dt ... points)
    {
        return LineOfSightHelper.on(new Delaunay_Triangulation(points));
    }
}
