package farthestpairfinder;

import javax.swing.JFrame;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;

public class FarthestPairFinder extends JFrame {

    int pointSize = 10;                                                         //changable: size of point
    int numPoints = 50;                                                         //changable: number of points
    Point2D first;                                                              //defined as the point with the lowest y value
    double xMin, xMax, yMin, yMax, xInt, yInt;                                  //help determine the window region
    int xSize = 800;                                                            //these determine the size of the screen
    int ySize = 800;
    ArrayList<int[]> possDist = new ArrayList();                                //possible distances
    
    Point2D[] S = new Point2D[numPoints];
    Point2D[] farthestPair = new Point2D[2];                                    //the two points of the farthest pair
    Point2D[] ordered;                                                          //all the points in order of slope

    ArrayList<Point2D> convexHull = new ArrayList();                            //the vertices of the convex hull of S

    Color convexHullColour = Color.white;                                       //color of the line that connects the hull
    Color pointColour = Color.yellow;                                           //point color

    public void makeRandomPoints() {                                            //chooses random values between 0 and 500 for x and y values of points
        Random rand = new Random();
        for (int i = 0; i < numPoints; i++) {
            double x = rand.nextDouble() * 5;
            double y = rand.nextDouble() * 500;
            S[i] = new Point2D(x, y);
        }
    }

    public void paint(Graphics g) {
        findWindow(S);                                                          //gets the window size
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, xSize, ySize);                                         //background: black
        //draws the points in S in yellow
        g.setColor(pointColour);
        for (int i = 0; i < S.length; i++) {
            g.fillOval((int) findP(S[i]).x - pointSize / 2, (int) findP(S[i]).y - pointSize / 2, pointSize, pointSize);
        }
        //draws the lines on the convex hull in white
        if (S.length > 4) {
            g.setColor(convexHullColour);
            g.drawLine((int) findP(convexHull.get(0)).x, (int) findP(convexHull.get(0)).y, (int) findP(convexHull.get(convexHull.size() - 1)).x, (int) findP(convexHull.get(convexHull.size() - 1)).y);
            for (int i = 1; i < convexHull.size(); i++) {
                g.drawLine((int) findP(convexHull.get(i - 1)).x, (int) findP(convexHull.get(i - 1)).y, (int) findP(convexHull.get(i)).x, (int) findP(convexHull.get(i)).y);
            }
        }
        //draws a red line connecting the farthest pair
        g.setColor(Color.RED);
        g.drawLine((int) findP(farthestPair[0]).x, (int) findP(farthestPair[0]).y, (int) findP(farthestPair[1]).x, (int) findP(farthestPair[1]).y);
    }

    public void findWindow(Point2D[] points) {                                  //finds window size given the points; adjusts window size accordingly
        xMin = points[0].x;
        xMax = points[0].x;
        yMax = points[0].y;
        yMin = points[0].y;
        for (int i = 1; i < points.length; i++) {
            double testx = points[i].x;
            double testy = points[i].y;
            if (testx > xMax) {
                xMax = testx;
            } else if (testx < xMin) {
                xMin = testx;
            }
            if (testy > yMax) {
                yMax = testy;
            } else if (testy < yMin) {
                yMin = testy;
            }
        }
        xInt = 0.1 * (xMax - xMin);                                             //x and y int are boundaries so the points aren't too far off the edge
        yInt = 0.1 * (yMax - yMin);
        xMin -= xInt;
        xMax += xInt;
        yMin -= yInt;
        yMax += yInt;
    }

    public Point2D findP(Point2D point) {                                       //for all point coordinates, find where it will be plotted
        double x = point.x;
        double y = point.y;
        x = x - xMin;
        x = x / (12 * xInt);
        x = x * xSize;
        y = y - yMin;
        y = y / (12 * yInt);
        y = y * ySize;
        Point2D ans = new Point2D(x, y);
        return ans;
    }

    public void grahamScan(Point2D[] points) {                                  //the Graham Scan way of finding convex hull
        int bottom = findBottom(points);                                        //first finds the point that is the bottom most point
        first = points[bottom];                                                 //sets the first point "p0"
        Point2D[] rest = findRest(bottom, points);                              //gives the rest of the points without first point
        ordered = getOrdered(first, rest);                                      //gives rest of points in order from angle of original
        convexHull.add(first);                                                  //adds first point to the convex hull
        convexHull.add(ordered[0]);                                             //adds smallest angle point into the convex hull
        seeIfOn(0, 1);                                                          //checks if next angle point is on the convex hull
    }

    public int findBottom(Point2D[] points) {
        int ans = 0;
        double bottom = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i].y > bottom) {
                ans = i;
                bottom = points[i].y;
            }
        }
        return ans;
    }

    public int findBottom(ArrayList<Point2D> points) {
        int ans = 0;
        double bottom = points.get(0).y;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).y < bottom) {
                ans = i;
                bottom = points.get(i).y;
            }
        }
        return ans;
    }

    public int findTop(ArrayList<Point2D> points) {
        int ans = 0;
        double top = 0;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).y > top) {
                ans = i;
                top = points.get(i).y;
            }
        }
        return ans;
    }

    public Point2D[] findRest(int p0, Point2D[] points) {
        Point2D[] ans = new Point2D[points.length - 1];
        for (int i = 0; i < p0; i++) {
            ans[i] = points[i];
        }
        for (int i = p0 + 1; i < points.length; i++) {
            ans[i - 1] = points[i];
        }
        return ans;
    }

    public Point2D[] getOrdered(Point2D p0, Point2D[] points) {
        double p0x = p0.x;
        double p0y = -p0.y;
        for (int i = 0; i < points.length; i++) {
            double px = points[i].x;
            double py = -points[i].y;
            points[i].Angle = Math.atan2(py - p0y, px - p0x);
        }
        return mergeSort(points);                                               //orders the points by slope using merge sort
    }

    public static Point2D[] mergeSort(Point2D[] a) {
        int n = a.length;
        if (n == 1) {
            Point2D[] temp = {a[0]};
            return temp;
        } else {
            int indexOfMidpoint = n / 2;
            Point2D[] leftHalf = new Point2D[indexOfMidpoint];
            Point2D[] rightHalf = new Point2D[n - indexOfMidpoint];
            for (int i = 0; i < indexOfMidpoint; i++) {
                leftHalf[i] = a[i];
            }
            for (int i = 0; i < n - indexOfMidpoint; i++) {
                rightHalf[i] = a[indexOfMidpoint + i];
            }
            Point2D[] sortedLeftHalf = mergeSort(leftHalf);
            Point2D[] sortedRightHalf = mergeSort(rightHalf);
            return merge(sortedLeftHalf, sortedRightHalf);
        }
    }

    public static Point2D[] merge(Point2D[] a, Point2D[] b) {
        Point2D[] c = new Point2D[a.length + b.length];
        int i = 0; //i is the current index of a
        int j = 0; //j is the current index of b 
        int k = 0; //k is the current index of c
        while (i < a.length && j < b.length) { //as long as both arrays still contain unused numbers
            if (a[i].Angle >= b[j].Angle) {  // if the first item in a is smaller than the first item in b...
                c[k] = a[i]; // put the current a-value into the current c-value 
                i++;
            } else {
                c[k] = b[j];
                j++;
            }
            k++;
        }
        if (i == a.length) { //if a is out of numbers, just fill c with the remainder of b  
            for (int m = j; m < b.length; m++) {
                c[k] = b[m];
                k++;
            }
        } else if (j == b.length) { //if b is out of numbers, just fill c with the remainder of a
            for (int m = i; m < a.length; m++) {
                c[k] = a[m];
                k++;
            }
        }
        return c;
    }

    public void seeIfOn(int p0, int p2) {                                       //O(n)
        if (ordered.length > p2) {
            Vector first = convexHull.get(p0 + 1).subtract(convexHull.get(p0));
            Vector second = ordered[p2].subtract(convexHull.get(p0 + 1));

            if (first.crossProduct(second) >= 0) {                              //if cross product is positive, ie turns right all the time, adds the point to the convex hull list
                convexHull.add(ordered[p2]);
                seeIfOn(p0 + 1, p2 + 1);
            } else {                                                            //else removes the previous point on the convex hull
                convexHull.remove(convexHull.size() - 1);
                seeIfOn(p0 - 1, p2);
            }
        }
    }

    public double getDistSquared(Point2D p1, Point2D p2) {                      //finds the distance squared between two points
        return Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2);
    }

    public void rotatingCalipers(){                                             //rotating calipers method
        
        double maxDist = 0;
        int minIndex = findMaxandMin(convexHull)[0];
        int maxIndex = findMaxandMin(convexHull)[1];
        
        Vector start1 = new Vector(1, 0);
        Vector start2 = new Vector(-1, 0);
        possDist.add(new int[]{maxIndex, minIndex});
        seeSmallerAngle(minIndex, maxIndex, start1, start2);
        for (int i = 0; i < possDist.size(); i++) {
            Point2D point1 = convexHull.get(possDist.get(i)[0]);
            Point2D point2 = convexHull.get(possDist.get(i)[1]);
            double test = getDistSquared(point1, point2);
            if (test > maxDist) {
                maxDist = test;
                farthestPair[0] = point1;
                farthestPair[1] = point2;
            }
        }
    }

    public int[] findMaxandMin(ArrayList<Point2D> convex) {                     //finds max and min of set of points
        int maxAns = findTop(convex);
        int minAns = findBottom(convex);
        int[] ans = new int[]{minAns, maxAns};
        return ans;
    }

    public void seeSmallerAngle(int index1, int index2, Vector parallel1, Vector parallel2) { //looks like it runs in O(n^2), but due to how convex hulls work, only runs in O(n)
        if (index1 != 1) {                                                      // only runs when it hasn't hit end
            int index1Next;
            if (index1 == convexHull.size() - 1) {
                index1Next = 0;
            }
            else{
                index1Next = index1 + 1;
            }
            Vector v1 = convexHull.get(index1Next).subtract(convexHull.get(index1));
            Vector v2 = convexHull.get(index2 + 1).subtract(convexHull.get(index2));
            double angle1 = Math.acos((v1.dotProduct(parallel1)) / (v1.magnitude() * parallel1.magnitude()));
            double angle2 = Math.acos((v2.dotProduct(parallel2)) / (v2.magnitude() * parallel2.magnitude()));
            if (angle1 < angle2) {
                possDist.add(new int[]{index1Next, index2});
                seeSmallerAngle(index1Next, index2, v1, opposite(v1));
            } else {
                possDist.add(new int[]{index1, index2 + 1});
                seeSmallerAngle(index1, index2 + 1, opposite(v2), v2);          //recursion
            }
        }
    }

    public static Vector opposite(Vector v) {
        double x = -v.xComponent;
        double y = -v.yComponent;
        Vector ans = new Vector(x, y);
        return ans;
    }                       //vector in opposite direction

    public void findFarthestPair_EfficientWay(Point2D[] points) {               //finds the longest distance efficiently
        if (points.length < 2) {                                                //if less than 2 points, no longest distance
            System.out.println("no solution");
        } else if (points.length < 4) {                                         //if points are less than 4, there isn't a convex hull and n is small is O(n^2), so just use brute force
            findFarthestPair_BruteForceWay(points);
        } else {                                                                //else use Graham scan on all the points
            grahamScan(points);
            rotatingCalipers();                                                 //uses rotating calipers method
        }
    }

    public void findFarthestPair_BruteForceWay(Point2D[] points) {              //tests all points with another to see if they are the longest distance from one another
        double longest = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double test = getDistSquared(points[i], points[j]);
                if (test > longest) {
                    farthestPair[0] = points[i];
                    farthestPair[1] = points[j];
                    longest = test;
                }
            }
        }
    }

    public void findFarthestPair_BruteForceWay(ArrayList<Point2D> points) {     //same as previous, but is integrated for arraylists
        double longest = 0;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                double test = getDistSquared(points.get(i), points.get(j));
                if (test > longest) {
                    farthestPair[0] = points.get(i);
                    farthestPair[1] = points.get(j);
                    longest = test;
                }
            }
        }
    }

    public static void main(String[] args) {
        //no changes are needed in main(). Just code the blank methods above.
        FarthestPairFinder fpf = new FarthestPairFinder();

        fpf.setSize(fpf.xSize, fpf.ySize);
        fpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fpf.makeRandomPoints();
        fpf.findFarthestPair_EfficientWay(fpf.S);

        fpf.setVisible(true);
    }
}