package idc.cgeom.ex3;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import delaunay_triangulation.Point_dt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static java.util.Collections.singleton;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/6/12
 */
public class DefaultAdjacencyMatrix implements AdjacencyMatrix
{
    private static final Joiner CSV_JOINER = Joiner.on(',');

    private final boolean[][] matrix;
    private final ImmutableBiMap<Guard, Integer> guards;
    private final ImmutableBiMap<Diamond, Integer> diamonds;

    private final Map<PointWrapper, ImmutableCollection> cache = newHashMap();

    public DefaultAdjacencyMatrix(Iterable<Point_dt> guards, Iterable<Point_dt> diamonds, LineOfSightHelper helper)
    {
        this.guards = wrapGuards(guards);
        this.diamonds = wrapDiamonds(diamonds);
        this.matrix = initMatrix(helper);
    }
    
    private DefaultAdjacencyMatrix(boolean[][] matrix, Map<Guard, Integer> guards, Map<Diamond, Integer> diamonds)
    {
        this.matrix = matrix;
        this.guards = rewrapKeys(guards, Guard.class);
        this.diamonds = rewrapKeys(diamonds, Diamond.class);
    }

    @Override
    public void exportToCsv(PrintWriter writer) throws IOException
    {
        // write header
        writer.println(CSV_JOINER.join(concat(singleton(""), transform(guards(), toGuardNames()))));
        
        int diamondIndex = 1;
        
        for (Diamond diamond : diamonds())
            writer.println(CSV_JOINER.join(concat(singleton("D" + (diamondIndex++)), transform(guards(), toGuardingFlag(diamond)))));
    }

    @Override
    public ImmutableCollection<Guard> guards()
    {
        return guards.keySet();
    }

    @Override
    public ImmutableCollection<Diamond> diamonds()
    {
        return diamonds.keySet();
    }

    @Override
    public AdjacencyMatrix reducedBy(Set<Guard> reducedGuards)
    {
        return new DefaultAdjacencyMatrix(matrix,
                filterKeys(guards, not(in(reducedGuards))),
                filterKeys(diamonds, not(guardedByAny(reducedGuards))));
    }

    @Override
    public AdjacencyMatrix reducedBy(Guard ... guards)
    {
        return reducedBy(ImmutableSet.copyOf(guards));
    }

    @Override
    public String toString()
    {
        return "DefaultAdjacencyMatrix{diamondsCount=" + diamonds.size() + ", guardsCount=" + guards.size() + '}';
    }

    private Function<? super Guard, ? extends String> toGuardingFlag(final Diamond diamond)
    {
        return new Function<Guard, String>()
        {
            @Override
            public String apply(Guard guard)
            {
                return guard.isGuarding(diamond) ? "1" : "0";
            }
        };
    }

    private Function<? super Guard, ? extends String> toGuardNames()
    {
        return new Function<Guard, String>()
        {
            private int index = 1;

            @Override
            public String apply(Guard input)
            {
                return "G" + (index++);
            }
        };
    }

    private boolean[][] initMatrix(LineOfSightHelper helper)
    {
        boolean[][] result = new boolean[diamonds.size()][guards().size()];

        for (Map.Entry<Diamond, Integer> diamondEntry : diamonds.entrySet())
        {
            for (Map.Entry<Guard, Integer> guardEntry : guards.entrySet())
                result[diamondEntry.getValue()][guardEntry.getValue()] =
                        helper.seenByEachOther(diamondEntry.getKey().getPointDt(), guardEntry.getKey().getPointDt());
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends PointWrapper> ImmutableBiMap<T, Integer> rewrapKeys(Map<T, Integer> wrappers, Class<T> wrapperClass)
    {
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (Map.Entry<T, Integer> entry : wrappers.entrySet())
            builder.put((T) (wrapperClass.equals(Diamond.class) ?
                    new DiamondImpl(entry.getKey().getPointDt()) :
                    new GuardImpl(entry.getKey().getPointDt())),
                    entry.getValue());

        return builder.build();
    }

    private ImmutableBiMap<Diamond, Integer> wrapDiamonds(Iterable<Point_dt> diamonds)
    {
        ImmutableBiMap.Builder<Diamond, Integer> builder = ImmutableBiMap.builder();
        int index = 0;

        for (Point_dt point : diamonds)
            builder.put(new DiamondImpl(point), index++);

        return builder.build();
    }

    private ImmutableBiMap<Guard, Integer> wrapGuards(Iterable<Point_dt> guards)
    {
        ImmutableBiMap.Builder<Guard, Integer> builder = ImmutableBiMap.builder();
        int index = 0;

        for (Point_dt point : guards)
            builder.put(new GuardImpl(point), index++);

        return builder.build();
    }

    abstract class PointWrapperImpl<T extends PointWrapper> implements PointWrapper
    {
        protected final Point_dt point_dt;

        protected PointWrapperImpl(Point_dt point_dt)
        {
            this.point_dt = point_dt;
        }
        @Override
        public Point_dt getPointDt()
        {
            return point_dt;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;

            if (obj == this)
                return true;

            if (getClass() != obj.getClass())
                return false;

            PointWrapperImpl other = (PointWrapperImpl) obj;

            return other.point_dt.x() == this.point_dt.x() &&
                    other.point_dt.y() == this.point_dt.y() &&
                    other.point_dt.z() == this.point_dt.z();
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(point_dt.x(), point_dt.y(), point_dt.z());
        }

        @SuppressWarnings("unchecked")
        protected ImmutableCollection<T> related()
        {
            ImmutableCollection<T> cached = cache.get(this);

            if (cached == null)
            {
                cached = calculateRelated();
                cache.put(this, cached);
            }

            return cached;
        }

        protected abstract ImmutableCollection<T> calculateRelated();

    }

    class GuardImpl extends PointWrapperImpl<Diamond> implements Guard
    {
        public GuardImpl(Point_dt point_dt)
        {
            super(point_dt);
        }

        @Override
        public boolean isGuarding(Diamond diamond)
        {
            return matrix[diamonds.get(diamond)][guards.get(this)];
        }

        @Override
        protected ImmutableCollection<Diamond> calculateRelated()
        {
            return ImmutableList.copyOf(filter(diamonds.keySet(), guardedBy(this)));
        }
        @Override
        public ImmutableCollection<Diamond> guardingDiamonds()
        {
            return related();
        }

        @Override
        public String toString()
        {
            return format("Guard{x=%s, y=%s, z=%s, numOfDiamonds=%d}", point_dt.x(), point_dt.y(), point_dt.z(), guardingDiamonds().size());
        }
    }

    class DiamondImpl extends PointWrapperImpl<Guard> implements Diamond
    {
        public DiamondImpl(Point_dt point_dt)
        {
            super(point_dt);
        }

        @Override
        public boolean isGuardedBy(Guard guard)
        {
            return matrix[diamonds.get(this)][guards.get(guard)];
        }

        @Override
        public ImmutableCollection<Guard> guardedByGuards()
        {
            return related();
        }
        @Override
        public String toString()
        {
            return format("Diamond{x=%s, y=%s, z=%s, numOfGuards=%d}", point_dt.x(), point_dt.y(), point_dt.z(), guardedByGuards().size());
        }

        @Override
        protected ImmutableCollection<Guard> calculateRelated()
        {
            return ImmutableList.copyOf(filter(guards.keySet(), isGuarding(this)));
        }

    }

    private Predicate<Guard> isGuarding(final Diamond diamond)
    {
        return new Predicate<Guard>()
        {
            @Override
            public boolean apply(Guard guard)
            {
                return matrix[diamonds.get(diamond)][guards.get(guard)];
            }
        };
    }

    private Predicate<Diamond> guardedBy(final Guard guard)
    {
        return new Predicate<Diamond>()
        {
            @Override
            public boolean apply(Diamond diamond)
            {
                return matrix[diamonds.get(diamond)][guards.get(guard)];
            }
        };
    }

    private Predicate<Diamond> guardedByAny(Set<Guard> reducedGuards)
    {
        return or(transform(reducedGuards, new Function<Guard, Predicate<Diamond>>()
        {
            @Override
            public Predicate<Diamond> apply(Guard input)
            {
                return guardedBy(input);
            }
        }));
    }
}
