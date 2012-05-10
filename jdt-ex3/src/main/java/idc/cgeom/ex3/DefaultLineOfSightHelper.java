package idc.cgeom.ex3;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.geom.Line2D;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class DefaultLineOfSightHelper implements LineOfSightHelper
{
    private final Delaunay_Triangulation triangulation;

    private DefaultLineOfSightHelper(Delaunay_Triangulation triangulation)
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

    @Override
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
        double A = triangle.p1().y() * (triangle.p2().z() - triangle.p3().z()) + triangle.p2().y() * (triangle.p3().z() - triangle.p1().z()) + triangle.p3().y() * (triangle.p1().z() - triangle.p2().z());
        double B = triangle.p1().z() * (triangle.p2().x() - triangle.p3().x()) + triangle.p2().z() * (triangle.p3().x() - triangle.p1().x()) + triangle.p3().z() * (triangle.p1().x() - triangle.p2().x());
        double C = triangle.p1().x() * (triangle.p2().y() - triangle.p3().y()) + triangle.p2().x() * (triangle.p3().y() - triangle.p1().y()) + triangle.p3().x() * (triangle.p1().y() - triangle.p2().y());
        double D = -1 * (triangle.p1().x() * (triangle.p2().y() * triangle.p3().z() - triangle.p3().y() * triangle.p2().z()) + triangle.p2().x() * (triangle.p3().y() * triangle.p1().z() - triangle.p1().y() * triangle.p3().z()) + triangle.p3().x() * (triangle.p1().y() * triangle.p2().z() - triangle.p2().y() * triangle.p1().z()));

        double p1_dist  = (A * p1.x() + B * p1.y() + C * p1.z() + D) / Math.sqrt(Math.pow(A,2) + Math.pow(B,2) + Math.pow(C,2));
        double p2_dist  = (A * p2.x() + B * p2.y() + C * p2.z() + D) / Math.sqrt(Math.pow(A,2) + Math.pow(B,2) + Math.pow(C,2));

        if(p1_dist * p2_dist > 0) // points are on the same side of the plane
            return false;

        double t = -1 * (D + A*p1.x() + B * p1.y() + C * p1.z()) / (A * (p2.x() - p1.x()) + B *(p2.y() - p1.y())+ C *(p2.z() - p1.z()));

        Point_dt intersection_p = new Point_dt(
                p1.x() + (p2.x() - p1.x()) * t,
                p1.y() + (p2.y() - p1.y()) * t,
                p1.z() + (p2.z() - p1.z()) * t);

        Point3d p = new Point3d(intersection_p.x(),intersection_p.y(),intersection_p.z());
        Point3d a = new Point3d(triangle.p1().x(),triangle.p1().y(),triangle.p1().z());
        Point3d b = new Point3d(triangle.p2().x(),triangle.p2().y(),triangle.p2().z());
        Point3d c = new Point3d(triangle.p3().x(),triangle.p3().y(),triangle.p3().z());

        return pointInTriangle(p,a,b,c);
    }

    private boolean sameSide(Point3d p1 ,Point3d p2, Point3d a,Point3d b)
    {
        Vector3d vectorBA = new Vector3d(b.x - a.x,b.y - a.y,b.z - a.z);
        Vector3d vectorP1A = new Vector3d(p1.x - a.x,p1.y - a.y,p1.z - a.z);
        Vector3d vectorP2A = new Vector3d(p2.x - a.x,p2.y - a.y,p2.z - a.z);

        Vector3d cp1 = new Vector3d();
        cp1.cross(vectorBA,vectorP1A);

        Vector3d cp2 = new Vector3d();
        cp2.cross(vectorBA, vectorP2A);

        return cp1.dot(cp2) >= 0;
    }

    private boolean pointInTriangle(Point3d p,Point3d  a,Point3d b,Point3d c)
    {
        return sameSide(p, a, b, c)
                && sameSide(p, b, a, c)
                && sameSide(p, c, a, b);
    }

    public static DefaultLineOfSightHelper on(Delaunay_Triangulation triangulation)
    {
        return new DefaultLineOfSightHelper(triangulation);
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
