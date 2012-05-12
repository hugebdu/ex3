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

    @Test
    public void testCalcTime() throws Exception
    {
        int NUMBUR_OF_TESTS = 5;
        String file = getClass().getResource("terra_13000.tsin").getFile();
        long[] times = new long[NUMBUR_OF_TESTS];
        long totalTime = 0;

        for (int i = 0;i<NUMBUR_OF_TESTS;i++)
        {
            System.out.println(String.format("Execution #%s", i));
            long startTimeMs = System.currentTimeMillis();
            Delaunay_Triangulation triangulation = new Delaunay_Triangulation(file);
            times[i]=System.currentTimeMillis() - startTimeMs;
            totalTime+=times[i];
        }

        System.out.println(String.format("Average processing time of %s iterations is %s ms", NUMBUR_OF_TESTS, totalTime / NUMBUR_OF_TESTS));
        for (int i = 0;i<NUMBUR_OF_TESTS;i++)
        {
            System.out.println(String.format("   - iteration #%s time is %s ms", i+1, times[i]));
        }

    }
}
