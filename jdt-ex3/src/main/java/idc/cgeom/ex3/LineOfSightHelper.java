package idc.cgeom.ex3;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

import java.awt.geom.Line2D;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class LineOfSightHelper
{
    private final Delaunay_Triangulation triangulation;

    private LineOfSightHelper(Delaunay_Triangulation triangulation)
    {
        this.triangulation = triangulation;
    }

    public ImmutableList<Triangle_dt> getInBetweenRouteTriangles(Point_dt source, Point_dt target)
    {
        ImmutableList.Builder<Triangle_dt> builder = ImmutableList.builder();

        final Line2D path = make2DLine(source, target);

        final Triangle_dt targetTriangle = triangulation.find(target);
        Triangle_dt currentTriangle = triangulation.find(source);
        Triangle_dt previousTriangle = null;
        
        while (targetTriangle != currentTriangle)
        {
            builder.add(currentTriangle);
            
            for (OutlineHolder outline : outlinesFor(currentTriangle))
            {
                if (outline.line.intersectsLine(path) && outline.next() != previousTriangle)
                {
                    previousTriangle = currentTriangle;
                    currentTriangle = outline.next();
                    break;
                }
            }
        }

        builder.add(currentTriangle);
        
        return builder.build();
    }

    private Iterable<OutlineHolder> outlinesFor(final Triangle_dt triangle)
    {
        return new Iterable<OutlineHolder>()
        {
            @Override
            public Iterator<OutlineHolder> iterator()
            {
                return new OutlineHolderIterator(triangle);
            }
        };
    }

    private Line2D make2DLine(Point_dt source, Point_dt target)
    {
        return new Line2D.Double(source.x(), source.y(), target.x(), target.y());
    }

    public boolean seenByEachOther(Point_dt p1, Point_dt p2)
    {
        for (Triangle_dt triangle : getInBetweenRouteTriangles(p1, p2))
        {
            if (isBlockedBy(p1, p2, triangle))
                return false;
        }
        return true;
    }

    public boolean isBlockedBy(Point_dt p1, Point_dt p2, Triangle_dt triangle)
    {
        //TODO: implement
        throw new UnsupportedOperationException("implement");
    }

    public static LineOfSightHelper on(Delaunay_Triangulation triangulation)
    {
        return new LineOfSightHelper(triangulation);
    }
    
    class OutlineHolderIterator extends AbstractIterator<OutlineHolder>
    {
        private static final byte S_1_2 = 0;
        private static final byte S_2_3 = 1;
        private static final byte S_3_1 = 2;

        private byte side = S_1_2;

        private final Triangle_dt triangle;

        OutlineHolderIterator(Triangle_dt triangle)
        {
            this.triangle = triangle;
        }

        @Override
        protected OutlineHolder computeNext()
        {
            OutlineHolder holder;

            switch (side)
            {
                case S_1_2:
                    holder = new OutlineHolder(make2DLine(triangle.p1(), triangle.p2()), triangle, "1_2")
                    {
                        @Override
                        public Triangle_dt next()
                        {
                            return triangle.next_12();
                        }
                    };
                    break;
                case S_2_3:
                    holder = new OutlineHolder(make2DLine(triangle.p2(), triangle.p3()), triangle, "2_3")
                    {
                        @Override
                        public Triangle_dt next()
                        {
                            return triangle.next_23();
                        }
                    };
                    break;
                case S_3_1:
                    holder = new OutlineHolder(make2DLine(triangle.p3(), triangle.p1()), triangle, "3_1")
                    {
                        @Override
                        public Triangle_dt next()
                        {
                            return triangle.next_31();
                        }
                    };
                    break;
                default:
                    holder = endOfData();
            }

            side++;
            return holder;
        }
    }

    static abstract class OutlineHolder
    {
        final Line2D line;
        final Triangle_dt triangle;
        private final String label;

        protected OutlineHolder(Line2D line, Triangle_dt triangle, String label)
        {
            this.line = line;
            this.triangle = triangle;
            this.label = label;
        }

        public abstract Triangle_dt next();


        @Override
        public String toString()
        {
            return "OutlineHolder{" + label + '}';
        }
    }
}
