package idc.cgeom.ex3.bnb;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.AdjacencyMatrix;
import idc.cgeom.ex3.BaseTest;
import idc.cgeom.ex3.bnb.BranchAndBoundSolution.Node;
import org.junit.Before;
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
public class BranchAndBoundSolutionTest extends BaseTest
{
    private final BranchAndBoundSolution solution = new BranchAndBoundSolution();

    @Before
    public void setUpSolution() throws Exception
    {
        solution.head = null;
        solution.current = null;
    }

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

    @Test
    public void testNode_GetNumOfGuardsUsedSoFar() throws Exception
    {
        Tree<Node> subTree3 = new Tree<Node>(nodeWithNumOfGuards(3));
        Tree<Node> subTree2 = subTree3.setAsParent(nodeWithNumOfGuards(2));
        solution.head = subTree2.setAsParent(nodeWithNumOfGuards(1));
        
        assertThat(subTree3.getHead().getNumOfGuardsUsedSoFar(), is(6));
        assertThat(subTree2.getHead().getNumOfGuardsUsedSoFar(), is(3));
    }

    private Node nodeWithNumOfGuards(int guardsCount)
    {
        ImmutableSet.Builder<Guard> builder = ImmutableSet.builder();

        for (int i = 0; i < guardsCount; i++)
            builder.add(new GuardStub());

        return solution.new Node(builder.build(), null, null);
    }

    static class GuardStub implements Guard
    {
        @Override
        public ImmutableCollection<AdjacencyMatrix.Diamond> guardingDiamonds()
        {
            return ImmutableList.of();
        }

        @Override
        public boolean isGuarding(AdjacencyMatrix.Diamond diamond)
        {
            return false;
        }

        @Override
        public Point_dt getPointDt()
        {
            return null;
        }
    }
}
