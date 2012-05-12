package edu.idc.dtdc.algo;

import edu.idc.dtdc.data.Point;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public class MergeHull implements ConvexHull
{
    @Override
    public List<Point> calculate(Set<Point> points)
    {
        final List<Point> sortedListByX = X_Ordering.sortedCopy(points);

        //TODO: Implement
        return null;
    }

}
