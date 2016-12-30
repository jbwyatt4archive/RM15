/*
 * Copyright (c) 2010, ReportMill Software. All rights reserved.
 */
package com.reportmill.graphics;
import snap.gfx.Point;
import snap.gfx.Rect;

/**
 * This class models a simple line, providing methods for extracting points, distance calculation, bisection,
 * hit detection and such.
 */
public class RMLine implements Cloneable {
    
    // Line start point
    public double _spx, _spy;
    
    // Line end point
    public double _epx, _epy;

/**
 * Creates a new line.
 */
public RMLine()  { }

/**
 * Creates a new line for the given x & y start/end points.
 */
public RMLine(double x1, double y1, double x2, double y2)  { _spx = x1; _spy = y1; _epx = x2; _epy = y2; }

/**
 * Creates a new line for the given start point and end point.
 */
public RMLine(Point sp, Point ep)  { _spx = sp.getX(); _spy = sp.getY(); _epx = ep.getX(); _epy = ep.getY(); }

/**
 * Returns the start point x.
 */
public double getSPx()  { return _spx; }

/**
 * Returns the start point y.
 */
public double getSPy()  { return _spy; }

/**
 * Returns the start point.
 */
public Point getSP()  { return Point.get(_spx,_spy); }

/**
 * Returns the end point x.
 */
public double getEPx()  { return _epx; }

/**
 * Returns the end point y.
 */
public double getEPy()  { return _epy; }

/**
 * Returns the end point.
 */
public Point getEP()  { return Point.get(_epx,_epy); }

/**
 * Returns the point on this line at the parametric location t (defined from 0-1).
 */
public final Point getPoint(double t)  { return getPoint(t, new Point()); }

/**
 * Returns the point on this line at the parametric location t (defined from 0-1).
 */
public Point getPoint(double t, Point aPoint)
{
    double x = _spx + t*(_epx - _spx);
    double y = _spy + t*(_epy - _spy);
    if(aPoint==null) return new Point(x, y);
    aPoint.setXY(x, y); return aPoint;
}

/**
 * Returns the point count of segment.
 */
public int getPointCount()  { return 2; }

/**
 * Returns the x of point at given index.
 */
public double getPointX(int anIndex)  { return anIndex==0? _spx : _epx; }

/**
 * Returns the y of point at given index.
 */
public double getPointY(int anIndex)  { return anIndex==0? _spy : _epy; }

/**
 * Returns the last x.
 */
public double getLastX()  { return getPointX(getPointCount()-1); }

/**
 * Returns the last y.
 */
public double getLastY()  { return getPointY(getPointCount()-1); }

/**
 * Returns the minimum distance from the given point to this segment.
 */
public double getDistance(double aX, double aY)  { return getDistanceLine(aX, aY); }

/**
 * Returns the minimum distance from the given point to this line.
 */
public double getDistanceLine(double aX, double aY)  { return Math.sqrt(getDistanceLineSquared(aX, aY)); }

/**
 * Returns the minimum distance from the given point to this line, squared.
 */
public double getDistanceLineSquared(double anX, double aY)
{
    return getDistanceLineSquared(anX, aY, _spx, _spy, _epx, _epy);
}

/**
 * Returns the distance from the given point components (p0) to the given line components (p1->p2).
 */
public static double getDistanceLineSquared(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y)
{
    // Declare vars for closest point
    double x = p1x;
    double y = p1y;

    // If line end points differ, find closest point
    if(p1x!=p2x || p1y!=p2y) {
        
        // Get parametric location of closest point
        double width = p2x - p1x;
        double height = p2y - p1y;
        double lengthSquared = width*width + height*height;
        double r = ((p0x - p1x)*width + (p0y - p1y)*height)/lengthSquared;
        
        // Clamp r to line bounds (0-1) and get actual location of closest point from parametric location
        r = r>=1? 1 : r<0? 0 : r;
        x = p1x + r*(p2x - p1x);
        y = p1y + r*(p2y - p1y);
    }

    // Return distance squared to point
    double dx = p0x - x;
    double dy = p0y - y;
    return dx*dx + dy*dy;
}

/**
 * Returns the min x point of this line.
 */
public double getMinX()  { return Math.min(_spx, _epx); }

/**
 * Returns the min y point of this line.
 */
public double getMinY()  { return Math.min(_spy, _epy); }

/**
 * Returns the max x point of this line.
 */
public double getMaxX()  { return Math.max(_spx, _epx); }

/**
 * Returns the max y point of this line.
 */
public double getMaxY()  { return Math.max(_spy, _epy); }

/**
 * Returns the bounds of the line.
 */
public final RMRect getBounds()  { RMRect rect = new RMRect(); getBounds(rect); return rect; }

/**
 * Get bounds of line in given rect.
 */
public void getBounds(Rect aRect)
{
    double x = getMinX(), y = getMinY();
    aRect.setRect(x, y, getMaxX()-x, getMaxY()-y);
}

/**
 * Returns a new line from this line's start point to given parametric location t (defined from 0-1) on this line.
 */
public RMLine getHead(double t)  { Point p = getPoint(t); return new RMLine(_spx, _spy, p.getX(), p.getY()); }

/**
 * Returns a new line from given parametric location t (defined from 0-1) on this line to this line's end point.
 */
public RMLine getTail(double t)  { Point p = getPoint(t); return new RMLine(p.getX(), p.getY(), _epx, _epy); }

/** 
 * Reset this curve's end point to the given parametric location (0-1).
 */
public void setEnd(double t)  { Point p = getPoint(t); _epx = p.getX(); _epy = p.getY(); }

/**
 * Reset this curve's start point to the given parametric location (0-1).
 */
public void setStart(double t)  { Point p = getPoint(t); _spx = p.getX(); _spy = p.getY(); }

/**
 * Returns a hit info object for this line and the given line.
 */
public RMHitInfo getHitInfo(RMLine aLine)
{
    // If this line is completely above/below/right/left of given line, return false 
    if(getMinX()>aLine.getMaxX() || getMaxX()<aLine.getMinX() ||
        getMinY()>aLine.getMaxY() || getMaxY()<aLine.getMinY())
        return null;

    // Get points for each line
    double p1x = _spx, p1y = _spy, p2x = _epx, p2y = _epy;
    double p3x = aLine._spx, p3y = aLine._spy, p4x = aLine._epx, p4y = aLine._epy;
    
    // Probably some line slope stuff, I can't really remember
    double numerator1 = (p1y-p3y)*(p4x-p3x) - (p1x-p3x)*(p4y-p3y);
    double numerator2 = (p1y-p3y)*(p2x-p1x) - (p1x-p3x)*(p2y-p1y);
    double denominator = (p2x-p1x)*(p4y-p3y) - (p2y-p1y)*(p4x-p3x);
    
    // Calculate parametric locations of intersection (line1:r, line2:2)
    double r = numerator1/denominator;
    double s = numerator2/denominator;
    
    // If parametric locations outside 0-1 range, then return null because lines don't really intersect
    if(r<0 || r>1 || s<0 || s>1)
        return null;
    
    // Return new hit info (last parameter is intersections - two if intersection was at vertex of second line)
    return new RMHitInfo(s<1? 1 : 0, r, s, 0);
}

/**
 * Returns a hit info object for this line and the given bezier curve.
 * HitInfo._index is overloaded to contain number of hits.
 */
public RMHitInfo getHitInfo(RMQuadratic aCurve)
{
    // If line is completely above/below/right/left of bezier, return false 
    if(getMinX()>aCurve.getMaxX() || getMaxX()<aCurve.getMinX() ||
        getMinY()>aCurve.getMaxY() || getMaxY()<aCurve.getMinY())
        return null;

    // If control point almost on end ponts line, return hit info for line
    double dist = aCurve.getDistanceLine(aCurve.getCP1x(), aCurve.getCP1y());
    if(dist<.25)
        return getHitInfo((RMLine)aCurve);
    
    // Get bezier and hit info for head and tail of aBezier
    RMQuadratic c1 = new RMQuadratic();
    RMQuadratic c2 = new RMQuadratic();
    aCurve.subdivide(c1, c2);
    RMHitInfo c1HitInfo = getHitInfo(c1);
    RMHitInfo c2HitInfo = getHitInfo(c2);
    
    // Get the hit info that hit closest to the base of this line
    RMHitInfo hitInfo = c1HitInfo;
    if(hitInfo==null || (c2HitInfo!=null && c2HitInfo._r<hitInfo._r))
        hitInfo = c2HitInfo;
    
    // If neither hit info hit line, return null
    if(hitInfo==null)
        return null;

    // Adjust parametric location of intersection for bezier
    hitInfo._s = hitInfo._s*.5f + (hitInfo==c1HitInfo? 0 : .5f);

    // Adjust intersections count
    hitInfo._hitCount = (c1HitInfo==null? 0 : c1HitInfo.getHitCount()) + (c2HitInfo==null? 0 : c2HitInfo.getHitCount());
    
    // Return hit info
    return hitInfo;
}

/**
 * Returns a hit info object for this line and the given bezier.
 */
public RMHitInfo getHitInfo(RMBezier aBezier)  { return RMBezierLineHit.getHitInfo(aBezier, this, 1); }

/**
 * Returns a string representation of this line.
 */
public String toString()  { return "Line: [" + _spx + " " + _spy + "], [" + _epx + " " + _epy + "]"; }

/**
 * Returns the angle at given parametric point (in degrees).
 */
public double getAngle(double t)  { return getTangent(t, null); }

/**
 * Return the tangent at given point.
 */
public double getTangent(double t, RMSize tan)
{
    // Declare variables for degree and tan width/height
    int degree = getPointCount() - 1;
    double tanW = 0;
    double tanH = 0;
    
    // Handle start point.  If one (or more) of the control points is the same as an endpoint, the tangent
    // calculation in the de Casteljau algorithm will return a point instead of the real tangent.
    if(t==0) { double point0X = getPointX(0), point0Y = getPointY(0);
        for(int i=1; i<=degree; ++i) { double pointX = getPointX(i), pointY = getPointY(i);
            if(pointX!=point0X || pointY!=point0Y) {
                tanW = pointX - point0X;
                tanH = pointY - point0Y;
                break;
            }
        }
    }
    
    // Handle end point (same as above)
    else if(t==1) { double pointNX = getPointX(degree), pointNY = getPointY(degree);
        for(int i=degree-1; i>=0; --i) { double pointX = getPointX(i), pointY = getPointY(i);
            if(pointX!=pointNX || pointY!=pointNY) {
                tanW = pointNX - pointX;
                tanH = pointNY - pointY;
                break;
            }
        }
    }
    
    // Handle intermediate points
    else {
        
        // Get float array of points
        double points[] = new double[2*(degree+1)];
        for(int i=0; i<=degree; i++) { points[i*2] = getPointX(i); points[i*2+1] = getPointY(i); }
        
        // Triangle computation
        for(int i=1; i<degree; i++)
            for(int j=0; j<=2*(degree-i)+1; j++)
                points[j] = (1 - t)*points[j] + t*points[j+2];
        
        // Get tangent
        tanW = points[2] - points[0];
        tanH = points[3] - points[1];
    }
    
    // If tan is present, set and normalize
    if(tan!=null) {
        tan.width = (float)tanW; tan.height = (float)tanH; tan.normalize(); }
    
    // Return atan converted to degrees
    return Math.atan2(tanH, tanW)*180/Math.PI;
}

/**
 * Standard clone implementation.
 */
public RMLine clone()
{
    try { return (RMLine)super.clone(); }
    catch(CloneNotSupportedException e) { System.err.println(e); return null; }
}

}