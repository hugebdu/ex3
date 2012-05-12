package edu.idc.dtdc.algo;

import edu.idc.dtdc.data.Point;
import org.junit.Test;

import static edu.idc.dtdc.algo.Algo.LineOrientation.*;
import static edu.idc.dtdc.algo.Algo.lineToPointRelation2D;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public class AlgoTest
{
    @Test
    public void testLineToPointRelation2D() throws Exception
    {
        final Point lineSource = new Point(0, 0);
        final Point lineTarget = new Point(2, 2);

        assertThat(lineToPointRelation2D(lineSource, lineTarget, new Point(1, 1)), is(OnLine));
        assertThat(lineToPointRelation2D(lineSource, lineTarget, new Point(1, 2)), is(Above));
        assertThat(lineToPointRelation2D(lineSource, lineTarget, new Point(1, 0)), is(Bellow));
    }
}
