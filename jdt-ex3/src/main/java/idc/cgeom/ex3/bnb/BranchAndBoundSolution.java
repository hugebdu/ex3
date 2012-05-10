package idc.cgeom.ex3.bnb;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.AdjacencyMatrix;
import idc.cgeom.ex3.DefaultAdjacencyMatrix;
import idc.cgeom.ex3.DefaultLineOfSightHelper;
import idc.cgeom.ex3.Solution;

import java.util.*;

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;
import static idc.cgeom.ex3.AdjacencyMatrix.Diamond;
import static idc.cgeom.ex3.AdjacencyMatrix.Guard;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */

public class BranchAndBoundSolution implements Solution
{
    private static final Function<Node, Double> energyFunction = new GuardsToDiamondsCoefficientEnergyFunction();

    private static final Ordering<Guard> orderingByNumOfGuardedDiamonds = new Ordering<Guard>()
    {
        @Override
        public int compare(Guard left, Guard right)
        {
            return left.guardingDiamonds().size() - right.guardingDiamonds().size();
        }
    };

    private static final Function<Guard, Set<Guard>> toSingletonSets = new Function<Guard, Set<Guard>>()
    {
        @Override
        public Set<Guard> apply(Guard input)
        {
            return Collections.singleton(input);
        }
    };

    private static final Function<Diamond, Guard> takeSingleGuard = new Function<Diamond, Guard>()
    {
        @Override
        public Guard apply(Diamond diamond)
        {
            return diamond.guardedByGuards().iterator().next();
        }
    };
    
    Tree<Node> head;
    Tree<Node> current;

    PriorityQueue<Node> queue;

    @Override
    public Collection<Point_dt> solve(Delaunay_Triangulation triangulation, ImmutableCollection<Point_dt> guards, ImmutableCollection<Point_dt> diamonds)
    {
        this.head = createHead(triangulation, guards, diamonds);
//        this.queue = new PriorityQueue<Node>(100, )
        
        //TODO: Implement
        return null;
    }

    Tree<Node> createHead(Delaunay_Triangulation triangulation,
                                  ImmutableCollection<Point_dt> guards, 
                                  ImmutableCollection<Point_dt> diamonds)
    {
        AdjacencyMatrix matrix = new DefaultAdjacencyMatrix(guards, diamonds, DefaultLineOfSightHelper.on(triangulation));
        Iterator<? extends Set<Guard>> guardsPickingIterator = makeGuardPickingIterator(matrix);
        Node node = new Node(ImmutableSet.<Guard>of(), matrix, guardsPickingIterator);
        return new Tree<Node>(node);
    }

    Iterator<? extends Set<Guard>> makeGuardPickingIterator(AdjacencyMatrix matrix)
    {
        Iterable<Diamond> guardedByASingleGuard = Iterables.filter(matrix.diamonds(), withGuardsOfSize(1));
        
        if (!isEmpty(guardedByASingleGuard))
            return transform(guardedByASingleGuard, compose(toSingletonSets, takeSingleGuard)).iterator();
        
        return transform(orderingByNumOfGuardedDiamonds.immutableSortedCopy(matrix.guards()), toSingletonSets).iterator();
    }

    private Predicate<? super Diamond> withGuardsOfSize(final int size)
    {
        return new Predicate<Diamond>()
        {
            @Override
            public boolean apply(Diamond input)
            {
                return input.guardedByGuards().size() == size;
            }
        };
    }

    class Node
    {
        private final AdjacencyMatrix matrix;
        private final ImmutableSet<Guard> guards;
        private final Iterator<? extends Set<Guard>> guardsPickingIterator;
        
        private Integer numOfGuardsUsedSoFar;

        Node(Set<Guard> guards, AdjacencyMatrix matrix, Iterator<? extends Set<Guard>> guardsPickingIterator)
        {
            this.guards = ImmutableSet.copyOf(guards);
            this.matrix = matrix;
            this.guardsPickingIterator = guardsPickingIterator;
        }

        public int getNumOfGuardsUsedSoFar()
        {
            if (numOfGuardsUsedSoFar == null)
                numOfGuardsUsedSoFar = calculateNumOfGuardsUsedSoFar();
            return numOfGuardsUsedSoFar;
        }

        public AdjacencyMatrix getMatrix()
        {
            return matrix;
        }

        public ImmutableSet<Guard> getGuards()
        {
            return guards;
        }

        public Iterator<? extends Set<Guard>> getGuardsPickingIterator()
        {
            return guardsPickingIterator;
        }

        private int calculateNumOfGuardsUsedSoFar()
        {
            Tree<Node> parent = head.getTree(this).getParent();
            return guards.size() + (parent != null ? parent.getHead().getNumOfGuardsUsedSoFar() : 0);
        }

        @Override
        public String toString()
        {
            return "Node{" +
                    "matrix=" + matrix +
                    ", guards=" + guards +
                    ", numOfGuardsUsedSoFar=" + getNumOfGuardsUsedSoFar() +
                    '}';
        }
    }

    static final class GuardsToDiamondsCoefficientEnergyFunction implements Function<Node, Double>
    {
        @Override
        public Double apply(Node node)
        {
            if (node.getNumOfGuardsUsedSoFar() == 0)
                return Double.MAX_VALUE;

            return ((double) node.getMatrix().diamonds().size()) / node.getNumOfGuardsUsedSoFar();
        }
    }
}
