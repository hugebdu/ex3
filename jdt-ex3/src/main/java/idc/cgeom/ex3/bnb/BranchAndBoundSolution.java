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
import idc.cgeom.ex3.LineOfSightHelper;
import idc.cgeom.ex3.Solution;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

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
    
    private Tree<Node> head;
    private Tree<Node> current;

    @Override
    public Collection<Point_dt> solve(Delaunay_Triangulation triangulation, ImmutableCollection<Point_dt> guards, ImmutableCollection<Point_dt> diamonds)
    {
        this.head = createHead(triangulation, guards, diamonds);
        
        //TODO: Implement
        return null;
    }

    Tree<Node> createHead(Delaunay_Triangulation triangulation,
                                  ImmutableCollection<Point_dt> guards, 
                                  ImmutableCollection<Point_dt> diamonds)
    {
        AdjacencyMatrix matrix = new DefaultAdjacencyMatrix(guards, diamonds, LineOfSightHelper.on(triangulation));
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
        public final AdjacencyMatrix matrix;
        public final ImmutableSet<Guard> guards;
        public final Iterator<? extends Set<Guard>> guardsPickingIterator;

        Node(Set<Guard> guards, AdjacencyMatrix matrix, Iterator<? extends Set<Guard>> guardsPickingIterator)
        {
            this.guards = ImmutableSet.copyOf(guards);
            this.matrix = matrix;
            this.guardsPickingIterator = guardsPickingIterator;
        }
        
        @Override
        public String toString()
        {
            return "Node{" +
                    "matrix=" + matrix +
                    ", guards=" + guards +
                    '}';
        }
    }
}
