package idc.cgeom.ex3.bnb;

import com.google.common.collect.ImmutableList;
import idc.cgeom.ex3.AdjacencyMatrix;
import idc.cgeom.ex3.BaseAdjacencyTest;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;
import static idc.cgeom.ex3.AdjacencyMatrix.Guard;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/7/12
 */
public class BranchAndBoundSolutionTest extends BaseAdjacencyTest
{
    private final BranchAndBoundSolution solution = new BranchAndBoundSolution();

    @Test
    public void testMakeGuardPickingIterator_HasDiamondsGuardedByOneGuard() throws Exception
    {
        AdjacencyMatrix matrix = makeMatrix();

        List<? extends Set<Guard>> iteratorAsList = ImmutableList.copyOf(solution.makeGuardPickingIterator(matrix));

        assertThat(iteratorAsList.size(), is(1));
        
        Set<Guard> set = iteratorAsList.get(0);
        assertThat(set.size(), is(1));
        assertThat(set, hasItem(find(matrix.guards(), thatWraps(GUARD1))));
    }

    @Test
    public void testMakeGuardPickingIterator_NoDiamondsGuardedByOneGuard() throws Exception
    {
        wireSeenPair(GUARD3, DIAMOND1);
        AdjacencyMatrix matrix = makeMatrix();

        List<? extends Set<Guard>> iteratorAsList = ImmutableList.copyOf(solution.makeGuardPickingIterator(matrix));

        assertThat(iteratorAsList.size(), is(3));
        
        assertThat(iteratorAsList.get(0), is((Set<Guard>)newHashSet(find(matrix.guards(), thatWraps(GUARD2)))));
        assertThat(iteratorAsList.get(1), is((Set<Guard>)newHashSet(find(matrix.guards(), thatWraps(GUARD1)))));
        assertThat(iteratorAsList.get(2), is((Set<Guard>)newHashSet(find(matrix.guards(), thatWraps(GUARD3)))));

    }
}
