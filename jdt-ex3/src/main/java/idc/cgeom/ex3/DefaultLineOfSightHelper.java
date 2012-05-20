package idc.cgeom.ex3;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.sun.j3d.utils.behaviors.picking.Intersect;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

import javax.media.j3d.PickSegment;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.geom.Line2D;
import java.util.Iterator;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/5/12
 */
public class DefaultLineOfSightHelper implements LineOfSightHelper
{
    private final Delaunay_Triangulation triangulation;

    private final Function<Point_dt, Point_dt> elevation = new Function<Point_dt, Point_dt>()
    {
        @Override
        public Point_dt apply(Point_dt input)
        {
            double z = triangulation.z(input.x(), input.y());
            return new Point_dt(
                    input.x(),
                    input.y(),
                    input.z() + z);
        }
    };

    private DefaultLineOfSightHelper(Delaunay_Triangulation triangulation)
    {
        this.triangulation = triangulation;
    }

    @Override
    public ImmutableCollection<Point_dt> elevate(Iterable<Point_dt> points)
    {
        return copyOf(transform(points, elevation));
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
            if (isBlockedBy2(p1, p2, triangle))
                return false;

        }
        return true;
    }

    public boolean isBlockedBy2(Point_dt p1, Point_dt p2, Triangle_dt triangle)
    {
        Intersect inter = new Intersect();
        PickSegment pick = new PickSegment(new Point3d(p1.x(), p1.y(), p1.z()), new Point3d(p2.x(), p2.y(), p2.z()));

        Point3d[] trianglePoints = new Point3d[3];
        trianglePoints[0] = new Point3d(triangle.p1().x(), triangle.p1().y(), triangle.p1().z());
        trianglePoints[1] = new Point3d(triangle.p2().x(), triangle.p2().y(), triangle.p2().z());
        trianglePoints[2] = new Point3d(triangle.p3().x(), triangle.p3().y(), triangle.p3().z());

        double[] toReturn = new double[3];
        boolean segmentAndTriangle = inter.segmentAndTriangle(pick, trianglePoints, 0, toReturn);

        return segmentAndTriangle;
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

        double t = 1 * (D + A*p1.x() + B * p1.y() + C * p1.z()) / (A * (p1.x() - p2.x()) + B *(p1.y() - p2.y())+ C *(p1.z() - p2.z()));

        Point_dt intersection_p = new Point_dt(
                p1.x() + (p2.x() - p1.x()) * t,
                p1.y() + (p2.y() - p1.y()) * t,
                p1.z() + (p2.z() - p1.z()) * t);


        Point3d p = new Point3d(intersection_p.x(),intersection_p.y(),intersection_p.z());
        Point3d a = new Point3d(triangle.p1().x(),triangle.p1().y(),triangle.p1().z());
        Point3d b = new Point3d(triangle.p2().x(),triangle.p2().y(),triangle.p2().z());
        Point3d c = new Point3d(triangle.p3().x(),triangle.p3().y(),triangle.p3().z());

        Point3d pp1 = new Point3d(p1.x(),p1.y(),p1.z());
        Point3d pp2 = new Point3d(p2.x(),p2.y(),p2.z());

        //Point3d intersection_p1 = getIntersectionPoint(pp1,pp2,a,b,c);

        return pointInTriangle3(p, a, b, c);
        //return pointInTriangleB(p,a,b,c);
        //return pointInTriangle(p,a,b,c);
    }

    private Point3d getIntersectionPoint(Point3d p1 ,Point3d p2, Point3d a,Point3d b,Point3d c)
    {
        Vector3d vectorBA = new Vector3d(b.x - a.x,b.y - a.y,b.z - a.z);
        Vector3d vectorCA = new Vector3d(c.x - a.x,c.y - a.y,c.z - a.z);

        Vector3d N = new Vector3d();
        N.cross(vectorBA,vectorCA);
        N.normalize();

        Vector3d vectorAX = new Vector3d(a.x - p1.x,a.y - p1.y,a.z - p1.z);

        double d = N.dot(vectorAX);

        Vector3d vectorYX = new Vector3d(p2.x - p1.x,p2.y - p1.y,p2.z - p1.z);
        double e = N.dot(vectorYX);


        if( e!=0 )
        {
            vectorYX.scale(d/e);
            p1.add(vectorYX);
            return p1;
            //O = X + W * d/e;          // одна точка
        }
        else if( d==0)
        {
            p1.add(vectorYX);
            return p1;
            //O =X + W * (anything)     // прямая принадлежит плоскости
        }
        else
            return null;                // прямая параллельна плоскости
    }

    //from http://dxdy.ru/topic52519.html
    private boolean pointInTriangle3(Point3d p , Point3d a,Point3d b,Point3d c)
    {
        Vector3d vectorAB = new Vector3d(b.x - a.x,b.y - a.y,b.z - a.z); //v0
        Vector3d vectorAC = new Vector3d(c.x - a.x,c.y - a.y,c.z - a.z); //v1
        Vector3d vectorPA = new Vector3d(a.x - p.x,a.y - p.y,a.z - p.z); //v2
        Vector3d vectorPB = new Vector3d(b.x - p.x,b.y - p.y,b.z - p.z); //v2
        Vector3d vectorPC = new Vector3d(c.x - p.x,c.y - p.y,c.z - p.z); //v2

        Vector3d PAxPB = new Vector3d();
        PAxPB.cross(vectorPA,vectorPB);

        Vector3d PBxPC = new Vector3d();
        PBxPC.cross(vectorPB,vectorPC);

        Vector3d PCxPA = new Vector3d();
        PCxPA.cross(vectorPC,vectorPA);

        Vector3d ABxAC = new Vector3d();
        ABxAC.cross(vectorAB,vectorAC);

        return PAxPB.length() + PBxPC.length() +  PCxPA.length() <= ABxAC.length();
    }
    // From http://www.blackpawn.com/texts/pointinpoly/default.html
    private boolean pointInTriangleB(Point3d p , Point3d a,Point3d b,Point3d c)
    {
        // Compute vectors
//        v0 = C - A
//        v1 = B - A
//        v2 = P - A
        Vector3d vectorBA = new Vector3d(b.x - a.x,b.y - a.y,b.z - a.z); //v0
        Vector3d vectorCA = new Vector3d(c.x - a.x,c.y - a.y,c.z - a.z); //v1
        Vector3d vectorPA = new Vector3d(p.x - a.x,p.y - a.y,p.z - a.z); //v2

        // Compute dot products
        double dot00 = vectorBA.dot(vectorBA);
        double dot01 = vectorBA.dot(vectorCA);
        double dot02 = vectorBA.dot(vectorPA);
        double dot11 = vectorCA.dot(vectorCA);
        double dot12 = vectorCA.dot(vectorPA);

        // Compute barycentric coordinates
        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u >= 0) && (v >= 0) && (u + v < 1);
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
