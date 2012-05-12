package edu.idc.dtdc.data;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public class Point
{
    public final Double x;
    public final Double y;
    public final Double z;

    public Point(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
        this.z = null;
    }

    public Double getX()
    {
        return x;
    }

    public Double getY()
    {
        return y;
    }

    public Double getZ()
    {
        return z;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != null ? !x.equals(point.x) : point.x != null) return false;
        if (y != null ? !y.equals(point.y) : point.y != null) return false;
        if (z != null ? !z.equals(point.z) : point.z != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
