package farthestpairfinder;

import java.awt.*;
import java.util.Arrays;

public class Point2D {
    
    double x, y, Angle;
    boolean visited; //might need this in the convex hull finding algorithm
    Color color;
    
    public Point2D(int x, int y) {
        this.x = (double)x;
        this.y = (double)y;
        this.visited = false;
        this.color = Color.yellow;
    }
    
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.color = Color.yellow;
    }
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    //Returns the vector that stretches between this and other.
    public Vector subtract( Point2D other ) {
        return new Vector( this.x - other.x, this.y - other.y);
    }
}

