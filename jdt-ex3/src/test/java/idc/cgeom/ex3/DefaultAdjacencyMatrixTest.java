package idc.cgeom.ex3;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import delaunay_triangulation.Point_dt;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static idc.cgeom.ex3.AdjacencyMatrix.*;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public class DefaultAdjacencyMatrixTest extends BaseAdjacencyTest
{
    private static final Function<PointWrapper, Point_dt> unwrap = new Function<PointWrapper, Point_dt>()
    {
        @Override
        public Point_dt apply(PointWrapper input)
        {
            return input.getPointDt();
        }
    };

    private static final Function<PointWrapper, Point_dt> extractPointDt = new Function<PointWrapper, Point_dt>()
    {
        @Override
        public Point_dt apply(PointWrapper input)
        {
            return input.getPointDt();
        }
    };

    @Test
    public void testGuards() throws Exception
    {
        DefaultAdjacencyMatrix matrix = makeMatrix();

        ImmutableCollection<Guard> guards = matrix.guards();

        assertThat(guards.size(), is(3));
        assertThat(transform(guards, unwrap), hasItems(GUARD1, GUARD2, GUARD3));

        assertDiamondsFor(guards, GUARD1, DIAMOND1, DIAMOND2);
        assertDiamondsFor(guards, GUARD2, DIAMOND3);
        assertDiamondsFor(guards, GUARD3, DIAMOND2, DIAMOND3);
    }

    @Test
    public void testDiamonds() throws Exception
    {
        DefaultAdjacencyMatrix matrix = makeMatrix();
        Collection<Diamond> diamonds = matrix.diamonds();
        
        assertThat(diamonds.size(), is(3));
        assertThat(transform(diamonds, unwrap), hasItems(DIAMOND1, DIAMOND2, DIAMOND3));
        
        assertGuardsFor(diamonds, DIAMOND1, GUARD1);
        assertGuardsFor(diamonds, DIAMOND2, GUARD1, GUARD3);
        assertGuardsFor(diamonds, DIAMOND3, GUARD2, GUARD3);
    }

    @Test
    public void testReducedBy() throws Exception
    {
        AdjacencyMatrix matrix = makeMatrix();
        
        Guard wrappedGuard1 = find(matrix.guards(), thatWraps(GUARD1));
        Guard wrappedGuard2 = find(matrix.guards(), thatWraps(GUARD2));
        
        AdjacencyMatrix reduced = matrix.reducedBy(wrappedGuard1);

        Collection<Guard> guards = reduced.guards();
        Collection<Diamond> diamonds = reduced.diamonds();

        assertThat(guards.size(), is(2));
        assertThat(transform(guards, unwrap), hasItems(GUARD2, GUARD3));

        assertThat(diamonds.size(), is(1));
        assertThat(transform(diamonds, unwrap), hasItems(DIAMOND3));

        assertGuardsFor(diamonds, DIAMOND3, GUARD2, GUARD3);

        assertDiamondsFor(guards, GUARD2, DIAMOND3);
        assertDiamondsFor(guards, GUARD3, DIAMOND3);
        
        reduced = reduced.reducedBy(wrappedGuard2);
        
        guards = reduced.guards();
        diamonds = reduced.diamonds();
        
        assertThat(guards.size(), is(1));
        assertThat(diamonds.size(), is(0));

        assertThat(Iterables.getLast(guards).guardingDiamonds().size(), is(0));
    }

    @Test
    public void testReducedByMultiple() throws Exception
    {
        AdjacencyMatrix matrix = makeMatrix();

        Guard wrappedGuard1 = find(matrix.guards(), thatWraps(GUARD1));
        Guard wrappedGuard3 = find(matrix.guards(), thatWraps(GUARD3));

        AdjacencyMatrix reduced = matrix.reducedBy(wrappedGuard1, wrappedGuard3);

        Collection<Guard> guards = reduced.guards();
        Collection<Diamond> diamonds = reduced.diamonds();

        assertThat(guards.size(), is(1));
        assertThat(transform(guards, unwrap), hasItems(GUARD2));

        assertThat(diamonds.size(), is(0));
        
        assertThat(Iterables.getLast(guards).guardingDiamonds().size(), is(0));
    }

    private void assertGuardsFor(Collection<Diamond> diamonds, Point_dt diamond, Point_dt ... guards)
    {
        Map<Point_dt, Diamond> asMap = uniqueIndex(diamonds, extractPointDt);
        Collection<Guard> guardedByGuards = asMap.get(diamond).guardedByGuards();
        
        assertThat(guardedByGuards.size(), is(guards.length));
        assertThat(transform(guardedByGuards, unwrap), hasItems(guards));
    }

    private void assertDiamondsFor(Collection<Guard> guards, Point_dt guard, Point_dt ... diamonds)
    {
        Map<Point_dt, Guard> asMap = uniqueIndex(guards, extractPointDt);

        Collection<Diamond> guardedDiamonds = asMap.get(guard).guardingDiamonds();
        assertThat(guardedDiamonds.size(), is(diamonds.length));
        assertThat(transform(guardedDiamonds, unwrap), hasItems(diamonds));
    }
}
