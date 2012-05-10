package idc.cgeom.ex3.greedy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import idc.cgeom.ex3.*;

import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static idc.cgeom.ex3.AdjacencyMatrix.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/10/12
 */
public class InversedGreedySolution extends BaseSolution
{
    private static final Predicate<Diamond> GuardedBySingleGuard = new Predicate<Diamond>()
    {
        @Override
        public boolean apply(Diamond input)
        {
            return input.guardedByGuards().size() == 1;
        }
    };

    @Override
    public Collection<Point_dt> solve(Delaunay_Triangulation triangulation, 
                                      ImmutableCollection<Point_dt> guards, 
                                      ImmutableCollection<Point_dt> diamonds)
    {
        AdjacencyMatrix adjacencyMatrix = createInitialAdjacencyMatrix(triangulation, guards, diamonds);

        ImmutableList.Builder<Guard> builder = ImmutableList.builder();
        
        while (hasUnguardedDiamonds(adjacencyMatrix))
        {
            Iterable<Diamond> diamondsWithOneGuard = filter(adjacencyMatrix.diamonds(), GuardedBySingleGuard);
            Set<Guard> diamondsGuards = collectGuards(diamondsWithOneGuard);

            if (!diamondsGuards.isEmpty())
            {
                builder.addAll(diamondsGuards);
                adjacencyMatrix = adjacencyMatrix.reducedBy(diamondsGuards);
            }
            else
            {
                Diamond diamondWithMinNumOfGuards = ByNumberOfGuardsOrder.min(adjacencyMatrix.diamonds());
                Guard preferredGuard = ByNumberOfDiamondsOrder.max(diamondWithMinNumOfGuards.guardedByGuards());
                builder.add(preferredGuard);
                adjacencyMatrix = adjacencyMatrix.reducedBy(preferredGuard);
            }
        }

        return Lists.transform(builder.build(), PointExtractor);
    }

    private Set<Guard> collectGuards(Iterable<Diamond> diamonds)
    {
        Set<Guard> result = newHashSet();
        for (Diamond diamond : diamonds)
            result.addAll(diamond.guardedByGuards());
        return result;
    }

    private boolean hasUnguardedDiamonds(AdjacencyMatrix adjacencyMatrix)
    {
        return !adjacencyMatrix.diamonds().isEmpty();
    }
}
