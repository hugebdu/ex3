package idc.cgeom.ex3;

import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class HeightCalculationTest
{
    @Test
    public void testName() throws Exception
    {
        Triangle_dt triangle = new Triangle_dt(
                point(0, 0, 0),
                point(1, 3, 2),
                point(3, 1, 2));

        Point_dt z = triangle.z(point(1, 1));
        System.out.println(z);
    }

    private Point_dt point(double x, double y)
    {
        return new Point_dt(x, y);
    }

    private Point_dt point(double x, double y, double z)
    {
        return new Point_dt(x, y, z);
    }
}
