import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Triangle_dt;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class LoadFileTest
{
    @Test
    public void testLoadFile() throws Exception
    {
        String file = getClass().getResource("terra_13000.tsin").getFile();
        Delaunay_Triangulation triangulation = new Delaunay_Triangulation(file);


        for (Triangle_dt triangle_dt : triangulation.triangles())
        {
            System.out.println(triangle_dt);
        }
    }
}
