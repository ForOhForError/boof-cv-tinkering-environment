import georegression.struct.point.Point2D_I32;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

class ContourBoundingBox
{
    static Point2D_I32[] farpoints;
    static final double ACCEPT_SLOPE_DELTA = 1;

    static
    {
        farpoints = new Point2D_I32[4];
        farpoints[0] = new Point2D_I32(-9999,-9999);
        farpoints[1] = new Point2D_I32(9999,-9999);
        farpoints[2] = new Point2D_I32(9999,9999);
        farpoints[3] = new Point2D_I32(-9999,9999);
    }

    Point2D_I32[] corners;
    Point2D_I32[] midpoints;
    double[] slopes;

    public ContourBoundingBox(List<Point2D_I32> contour)
    {
        corners = new Point2D_I32[4];
        midpoints = new Point2D_I32[4];
        slopes = new double[4];

        for(Point2D_I32 p : contour)
        {
            for(int i=0; i<4; i++)
            {
                if(corners[i]==null)
                {
                    corners[i] = p.copy();
                }
                else
                {
                    double d1 = corners[i].distance(farpoints[i]);
                    double d2 = p.distance(farpoints[i]);
                    if(d2<d1)
                    {
                        corners[i] = p.copy();
                    }
                }
            }
        }

        for(int i=0; i<4; i++)
        {
            int j = (i+1)%4;
            midpoints[i] = new Point2D_I32(
                (corners[i].x + corners[j].x)/2, 
                (corners[i].y + corners[j].y)/2
            );
            slopes[i] = slope(corners[i],corners[j]);
        }
    }

    public double area()
    {
        return midpoints[0].distance(midpoints[2]) * 
            midpoints[1].distance(midpoints[3]);
    }

    public boolean isRoughlyRecttangular()
    {
        double d1 = slopes[0]/slopes[2];
        double d2 = slopes[1]/slopes[3];
        double low = 0;
        double high = 3;
        return d1 > low && d1 < high && d2 > low && d2 < high;
    }

    private static double slope(Point2D_I32 p1, Point2D_I32 p2)
    {
        if(p1.x == p2.x)
        {
            return 9999999;
        }
        else
        {
            return (p1.y-p2.y)/(double)(p1.x-p2.x);
        }
    }

    public void draw(Graphics g)
    {
        if(isRoughlyRecttangular())
        {
            g.setColor(Color.GREEN);
        }
        else
        {
            g.setColor(Color.ORANGE);
        }
        for(int i=0; i<4; i++)
        {
            int j = (i+1)%4;
            g.drawLine(
                corners[i].x,
                corners[i].y,
                corners[j].x,
                corners[j].y
            );
        }
    }
}