import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.DecimalFormat;

public class Wall
{
	public double x1, x2, y1, y2;
	
	public Wall(double x1, double y1, double x2, double y2)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public double distance(double xc, double yc)
	{
		double a = (-y1) - (-y2);
		double b = x2 - x1;
		double c = ( (-y2) - (-y1)) * x1 -
					(x2-x1)*(-y1);
		return Math.abs((a*xc)+(b*(-yc)) + c)/Math.sqrt(a*a + b*b);
	}
	
	public double normalX()
	{
		return wallDirecty() ;
	}
	
	public double normalY()
	{
		return -wallDirectx();
	}
	
	public double getX1()              
	{
		return x1;
	}
	public double getY1()
	{
		return y1;
	}
	public double getX2()
	{
		return x2;
	}
	public double getY2()
	{
		return y2;
	}
	
	public double wallDirectx()              // ambil posisi x1 auto normal
	{
		return (x2-x1)/Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
	}
	
	public double wallDirecty()              // ambil posisi y1 auto normal
	{
		return ((-y2)-(-y1))/Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
	}
	public void draw(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.drawLine((int)x1,(int)y1, (int)x2,(int)y2);
	}
}
