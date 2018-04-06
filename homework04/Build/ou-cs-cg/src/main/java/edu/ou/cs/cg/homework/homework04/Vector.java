package main.java.edu.ou.cs.cg.homework.homework04;

public final class Vector
{

    public double x;
    public double y;

    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public Vector multiply(double scalar)
    {
        return new Vector(x * scalar, y * scalar);
    }
}