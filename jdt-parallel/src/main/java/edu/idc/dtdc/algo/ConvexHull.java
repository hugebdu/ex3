package edu.idc.dtdc.algo;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import edu.idc.dtdc.data.Point;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */

public interface ConvexHull
{
    public static final DiscreteDomain<Long> PositiveLongs = new DiscreteDomain<Long>()
    {
        @Override
        public Long next(Long value)
        {
            if (value == Long.MAX_VALUE)
                return null;
            return value + 1;
        }

        @Override
        public Long previous(Long value)
        {
            if (value == 0)
                return null;
            return value - 1;
        }

        @Override
        public long distance(Long start, Long end)
        {
            return end - start;
        }

        @Override
        public Long minValue()
        {
            return 0l;
        }

        @Override
        public Long maxValue()
        {
            return Long.MAX_VALUE;
        }
    };

    public static final Ordering<Point> X_Ordering = new Ordering<Point>()
    {
        @Override
        public int compare(Point left, Point right)
        {
            return Doubles.compare(left.x, right.x);
        }
    };
    
    public static final Ordering<Point> Y_Ordering = new Ordering<Point>()
    {
        @Override
        public int compare(Point left, Point right)
        {
            return Doubles.compare(left.y, right.y);
        }
    };
    
    List<Point> calculate(Set<Point> points);
}
