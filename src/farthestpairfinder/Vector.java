package farthestpairfinder;

public class Vector {
    
    double xComponent, yComponent;
    
    public Vector(double x, double y) {
        this.xComponent = x;
        this.yComponent = y;
    }
    
    //needed as part of the convex hull algorithm and for finding the farthest pair within the vertices of the convex hull
    public Vector subtract( Vector other ) {
        return new Vector( this.xComponent - other.xComponent, this.yComponent - other.yComponent);
    }
    
    //only used inside getAngle()
    public double dotProduct( Vector other ) {
        return this.xComponent*other.xComponent + this.yComponent*other.yComponent;
    }
    
    
    public double crossProduct ( Vector other ){
        return this.xComponent*other.yComponent - this.yComponent*other.xComponent;
    }
    
    //only used inside getAngle()
    public double magnitude() {
        return Math.sqrt( this.xComponent*this.xComponent + this.yComponent*this.yComponent);
    }
    
    public String toAngleString(){
        return ("" + (180/Math.PI)*Math.atan2(this.yComponent, this.xComponent));
    }
    
    public String toString(){
        return "(" +this.xComponent + "," + this.yComponent +  ")";
    }
}
