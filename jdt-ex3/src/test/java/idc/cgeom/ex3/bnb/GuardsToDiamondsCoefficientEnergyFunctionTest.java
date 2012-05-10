package idc.cgeom.ex3.bnb;

import com.google.common.collect.ImmutableList;
import idc.cgeom.ex3.AdjacencyMatrix;
import idc.cgeom.ex3.bnb.BranchAndBoundSolution.Node;
import org.junit.Test;

import java.util.Collections;

import static idc.cgeom.ex3.AdjacencyMatrix.Diamond;
import static idc.cgeom.ex3.bnb.BranchAndBoundSolution.GuardsToDiamondsCoefficientEnergyFunction;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/9/12
 */
public class GuardsToDiamondsCoefficientEnergyFunctionTest
{
    final GuardsToDiamondsCoefficientEnergyFunction function = new GuardsToDiamondsCoefficientEnergyFunction();
    
    @Test
    public void testNoMoreDiamonds() throws Exception
    {
        assertThat(function.apply(node(0, 20)), is(0d));
    }

    @Test
    public void testNoGuardsAtRoot() throws Exception
    {
        assertThat(function.apply(node(20, 0)), is(Double.MAX_VALUE));
    }

    @Test
    public void testOrder() throws Exception
    {
        assertTrue(function.apply(node(10, 3)) < function.apply(node(10, 5)));

        assertTrue(function.apply(node(2, 5)) < function.apply(node(10, 5)));
    }

    private Node node(int numOfDiamonds, int numOfGuards)
    {
        Node node = mock(Node.class);
        when(node.getNumOfGuardsUsedSoFar()).thenReturn(numOfGuards);
        
        Diamond diamond = mock(Diamond.class);

        AdjacencyMatrix matrix = mock(AdjacencyMatrix.class);
        
        when(matrix.diamonds()).thenReturn(ImmutableList.copyOf(Collections.nCopies(numOfDiamonds, diamond)));
        when(node.getMatrix()).thenReturn(matrix);

        return node;
    }
}
