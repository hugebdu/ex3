package idc.cgeom.ex3;

import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.greedy.GreedySolution;
import idc.cgeom.ex3.greedy.InversedGreedySolution;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class SolutionsBadCaseTest extends BaseTest
{
    final InversedGreedySolution inversedGreedySolution = new InversedGreedySolution()
    {
        @Override
        protected LineOfSightHelper createLineOfSightHelper(Delaunay_Triangulation triangulation)
        {
            return helperMock;
        }
    };

    final GreedySolution greedySolution = new GreedySolution()
    {
        @Override
        protected LineOfSightHelper createLineOfSightHelper(Delaunay_Triangulation triangulation)
        {
            return helperMock;
        }
    };

    @Before
    public void resetMocks()
    {
        reset(helperMock);
    }

    @Test
    public void testGreedy() throws Exception
    {
        ImmutableList<Point_dt> guards = guards();
        ImmutableList<Point_dt> diamonds = diamonds();

        buildGraph();

        Collection<Point_dt> result = greedySolution.solve(null, guards, diamonds);

        assertThat(result.size(), is(3));
    }

    @Test
    public void testInversedGreedy() throws Exception
    {
        ImmutableList<Point_dt> guards = guards();
        ImmutableList<Point_dt> diamonds = diamonds();

        buildGraph();

        Collection<Point_dt> result = inversedGreedySolution.solve(null, guards, diamonds);

        assertThat(result.size(), is(2));
    }

    private ImmutableList<Point_dt> diamonds()
    {
        return ImmutableList.of(DIAMOND1, DIAMOND2, DIAMOND3, DIAMOND4, DIAMOND5, DIAMOND6);
    }

    private ImmutableList<Point_dt> guards()
    {
        return ImmutableList.of(GUARD1, GUARD2, GUARD3);
    }

    private void buildGraph()
    {
        wireSeenPair(GUARD1, DIAMOND1);
        wireSeenPair(GUARD1, DIAMOND2);
        wireSeenPair(GUARD1, DIAMOND3);

        wireSeenPair(GUARD3, DIAMOND4);
        wireSeenPair(GUARD3, DIAMOND5);
        wireSeenPair(GUARD3, DIAMOND6);

        wireSeenPair(GUARD2, DIAMOND2);
        wireSeenPair(GUARD2, DIAMOND3);
        wireSeenPair(GUARD2, DIAMOND4);
        wireSeenPair(GUARD2, DIAMOND5);
    }
}
