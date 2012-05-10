package idc.cgeom.ex3;

import com.google.common.base.Predicate;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import org.junit.Before;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/7/12
 */
public abstract class BaseTest
{
    protected static final Point_dt GUARD1 = new Point_dt(0, 1);
    protected static final Point_dt GUARD2 = new Point_dt(0, 2);
    protected static final Point_dt GUARD3 = new Point_dt(0, 3);

    protected static final Point_dt DIAMOND1 = new Point_dt(1, 0);
    protected static final Point_dt DIAMOND2 = new Point_dt(2, 0);
    protected static final Point_dt DIAMOND3 = new Point_dt(3, 0);
    protected static final Point_dt DIAMOND4 = new Point_dt(4, 0);
    protected static final Point_dt DIAMOND5 = new Point_dt(5, 0);
    protected static final Point_dt DIAMOND6 = new Point_dt(6, 0);

    protected final DefaultLineOfSightHelper helperMock = mock(DefaultLineOfSightHelper.class);

    @Before
    public void setUp() throws Exception
    {
        reset(helperMock);

        wireSeenPair(GUARD1, DIAMOND1);
        wireSeenPair(GUARD1, DIAMOND2);
        wireSeenPair(GUARD2, DIAMOND3);
        wireSeenPair(GUARD3, DIAMOND2);
        wireSeenPair(GUARD3, DIAMOND3);
    }

    protected void wireSeenPair(Point_dt p1, Point_dt p2)
    {
        when(helperMock.seenByEachOther(p1, p2)).thenReturn(true);
        when(helperMock.seenByEachOther(p2, p1)).thenReturn(true);
    }

    protected DefaultAdjacencyMatrix makeMatrix()
    {
        return new DefaultAdjacencyMatrix(
                asList(GUARD1, GUARD2, GUARD3),
                asList(DIAMOND1, DIAMOND2, DIAMOND3),
                helperMock);
    }

    protected Predicate<AdjacencyMatrix.PointWrapper> thatWraps(final Point_dt point_dt)
    {
        return new Predicate<AdjacencyMatrix.PointWrapper>()
        {
            @Override
            public boolean apply(AdjacencyMatrix.PointWrapper input)
            {
                return input.getPointDt() == point_dt;
            }
        };
    }

    protected List<Point_dt> loadPoints(String fileName) throws Exception
    {
        return IOHelper.readPoints(getClass().getResourceAsStream(fileName));
    }

    protected Delaunay_Triangulation loadTriangulation(String fileName) throws Exception
    {
        String file = getClass().getResource(fileName).getFile();
        return new Delaunay_Triangulation(file);
    }
}
