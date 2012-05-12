package edu.idc.dtdc.data;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public class Triangle
{
    public final Point a;
    public final Point b;
    public final Point c;

    public Triangle(Point a, Point b, Point c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Point getA()
    {
        return a;
    }

    public Point getB()
    {
        return b;
    }

    public Point getC()
    {
        return c;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triangle triangle = (Triangle) o;

        if (a != null ? !a.equals(triangle.a) : triangle.a != null) return false;
        if (b != null ? !b.equals(triangle.b) : triangle.b != null) return false;
        if (c != null ? !c.equals(triangle.c) : triangle.c != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Triangle{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
