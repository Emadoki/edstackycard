package com.emadoki.edstackycard;

public class Vector2D
{
    public float x;
    public float y;

    public Vector2D()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2D set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D add(float value)
    {
        return add(value, value);
    }

    public Vector2D add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D subtract(float value)
    {
        return subtract(value, value);
    }

    public Vector2D subtract(float x, float y)
    {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2D multiply(float value)
    {
        return multiply(value, value);
    }

    public Vector2D multiply(float x, float y)
    {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2D normalised()
    {
        double f = 1f / length();
        this.x *= f;
        this.y *= f;
        return this;
    }

    public float length()
    {
        return (float) Math.sqrt((x * x) + (y * y));
    }
}
