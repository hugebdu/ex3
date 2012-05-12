package edu.idc.dtdc.algo;

import edu.idc.dtdc.data.Point;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public abstract class Algo
{
    public enum LineOrientation { Above, Bellow, OnLine }

    public static LineOrientation lineToPointRelation2D(Point lineSource, Point lineTarget, Point testPoint)
    {
        checkNotNull(lineSource);
        checkNotNull(lineTarget);
        checkNotNull(testPoint);

        double det =
                lineSource.x * lineTarget.y +
                lineSource.y * testPoint.x +
                lineTarget.x * testPoint.y -
                lineTarget.y * testPoint.x -
                lineSource.y * lineTarget.x -
                lineSource.x * testPoint.y;

        return det == 0 ? LineOrientation.OnLine :
                det > 0 ? LineOrientation.Above : LineOrientation.Bellow;
    }
}
