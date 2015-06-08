
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Ball
{
	private double x, y, r;
	private double dx, dy;
	private Color ballColor;
	private double m;
	private static double e;
	private static double perlambatan;
		
	public Ball(double x, double y, double r,
				double dx, double dy, 
				Color ballColor, double m)
	{
		e = 0.8;
		perlambatan = 0.04;
		this.m = m;
		this.ballColor = ballColor;
		
		this.x = x;
		this.y = y;
		this.r = r;
		this.dx = dx;
		this.dy = dy;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getR()
	{
		return r;
	}
	
	public double getDx()
	{
		return dx;
	}
	
	public double getDy()
	{
		return dy;
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public void setDx(double dx)
	{
		this.dx = dx;
	}
	
	public void setDy(double dy)
	{
		this.dy = dy;
	}

	public void detectBall(ArrayList<Ball> balls)
	{
		for(int i=0; i<balls.size(); i++)
		{
			//check apakah bukan dirinya sendiri
			if(balls.get(i) != this)
			{
				//check apakah keduanya tubrukan
				if(distanceBall(balls.get(i)))
				{
					this.x -= dx;
					this.y += dy;
					
					double normalVectorX, normalVectorY;
					double tangentVectorX, tangentVectorY;					
										
					double projVectorN1, projVectorN1x, projVectorN1y;
					double projVectorN2, projVectorN2x, projVectorN2y;
					
					double projVectorT1x, projVectorT1y;
					double projVectorT2x, projVectorT2y;
					
					double newProjVectorN1, newProjVectorN2;
					double newProjVectorN1x, newProjVectorN1y;
					double newProjVectorN2x, newProjVectorN2y;
					
					//calculate the vector between the two colliding-ball's centers 
					//(the direction is from this ball to the other ball					
					normalVectorX = balls.get(i).x - this.x;
					normalVectorY = balls.get(i).y - this.y;
					//calculate the unit vector for this center vector
					normalVectorX = normalVectorX/calculateVectorLength(this.x, this.y, balls.get(i).x, balls.get(i).y);
					normalVectorY = normalVectorY/calculateVectorLength(this.x, this.y, balls.get(i).x, balls.get(i).y);
					normalVectorY = -normalVectorY;
										
					//calculate the projection of this ball's v to normal and tanget vector
					//(the dot between this ball's v and normal should be positive)
					projVectorN1 = dotProduct(this.dx, this.dy, normalVectorX, normalVectorY);					 
					//(the other ball should use the same normal and tangent vector as this ball) 
					projVectorN2 = dotProduct(balls.get(i).dx, balls.get(i).dy, normalVectorX, normalVectorY);
					
					newProjVectorN1 = bounceVelocity1(this.m, projVectorN1, balls.get(i).m, projVectorN2);
					newProjVectorN1x = newProjVectorN1 * normalVectorX;
					newProjVectorN1y = newProjVectorN1 * normalVectorY;
					newProjVectorN2 = bounceVelocity2(this.m, projVectorN1, balls.get(i).m, projVectorN2);
					newProjVectorN2x = newProjVectorN2 * normalVectorX;
					newProjVectorN2y = newProjVectorN2 * normalVectorY;
							
					//the y component (relative to normalVector) of this ball's velocity
					tangentVectorX = normalVectorY;
					tangentVectorY = -normalVectorX;
					
					projVectorT1x = (this.dx*tangentVectorX + this.dy*tangentVectorY) * tangentVectorX;
					projVectorT1y = (this.dx*tangentVectorX + this.dy*tangentVectorY) * tangentVectorY;
					projVectorT2x = (balls.get(i).dx*tangentVectorX + balls.get(i).dy*tangentVectorY) * tangentVectorX;
					projVectorT2y = (balls.get(i).dx*tangentVectorX + balls.get(i).dy*tangentVectorY) * tangentVectorY;
					
					this.dx = projVectorT1x + newProjVectorN1x;
					this.dy = projVectorT1y + newProjVectorN1y;
					balls.get(i).dx = projVectorT2x + newProjVectorN2x;
					balls.get(i).dy = projVectorT2y + newProjVectorN2y;
				}
			}
		}
	}
	
	private double dotProduct(double x1, double y1, double x2, double y2)
	{
		return (x1*x2) + (y1*y2);
	}
	private double calculateVectorLength(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((y2-y1)*(y2-y1) + (x2-x1)*(x2-x1));
	}
	private double bounceVelocity1(double m1, double v1, double m2, double v2)
	{
		return (m1*v1 + m2*v2 + m2*e*(v2-v1))/ (m1+m2);
	}
	private  double bounceVelocity2(double m1, double v1, double m2, double v2)
	{
		return (m1*v1 + m2*v2 - m1*e*(v2-v1))/(m1+m2);
	}
	private  boolean distanceBall(Ball anotherBall)
	{
		double distance = Math.sqrt(
			(this.x - anotherBall.x)*(this.x - anotherBall.x) +
			(this.y - anotherBall.y)*(this.y - anotherBall.y)
			);
		if(distance <= (this.r + anotherBall.r))
		{
			BallAnimation.sfx.get("hit").play();
			return true;
		}
		else
			return false;
	}
	
	public void speed()
	{
		x += dx;
		y -= dy;	
		
		double v = Math.sqrt(dx*dx + dy*dy);
		if (v < perlambatan)
		{
			dx = 0;
			dy = 0;
		}
		else
		{
			dx = (v - perlambatan)*dx/v ;
			dy = (v - perlambatan)*dy/v ;
		}
	
	}
	
	public double getV()
	{
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double bounceX(double wallNormalX, double wallNormalY)
	{
		return -2.0d*(dx * wallNormalX + dy * wallNormalY) * wallNormalX + dx;
	}
	public double bounceY(double wallNormalX, double wallNormalY)
	{
		return -2.0d*(dx * wallNormalX + dy * wallNormalY) * wallNormalY + dy;
	}
	
	public void WriteData(int w)
	{
		System.out.println("Direction wall "+ w +" : " + dx + "  " + dy);
		System.out.println("centre of the ball " + x +"  "+y);
		System.out.println();
	}
	
	public void detect(Wall wall1, Wall wall2,Wall wall3,Wall wall4)
	{
		double ndx , ndy;
		double wallNormalX, wallNormalY;
		if(wall1.distance(x, y) <= r)
		{
			ndx = bounceX(wall1.normalX(), wall1.normalY());
			ndy = bounceY(wall1.normalX(), wall1.normalY());
			x -= dx;
			y += dy;
			dx = ndx;
			dy = ndy;
			//WriteData(1); 
		}		
		if(wall2.distance(x, y) <= r)
		{
			ndx = bounceX(wall2.normalX(), wall2.normalY());
			ndy = bounceY(wall2.normalX(), wall2.normalY());
			x -= dx;
			y += dy;
			dx = ndx;
			dy = ndy;
			//WriteData(2); 
		}
		if(wall3.distance(x, y) <= r)
		{
			ndx = bounceX(wall3.normalX(), wall3.normalY());
			ndy = bounceY(wall3.normalX(), wall3.normalY());
			x -= dx;
			y += dy;
			dx = ndx;
			dy = ndy;
			//WriteData(3); 
		}
		if(wall4.distance(x, y) <= r)
		{
			ndx = bounceX(wall4.normalX(), wall4.normalY());
			ndy = bounceY(wall4.normalX(), wall4.normalY());
			x -= dx;
			y += dy;
			dx = ndx;
			dy = ndy;
			//WriteData(4); 
		}
	}
	
	public boolean isStop()
	{
		if(dx == 0 && dy == 0) {return true;}
		else {return false;}
	}
	
	public void draw(Graphics g)
	{
		g.setColor(ballColor);
		g.fillOval((int)(x-r),(int)(y-r),(int)(2*r), (int)(2*r));
		
		g.setColor(Color.BLACK);
		g.drawOval((int)(x-r),(int)(y-r),(int)(2*r), (int)(2*r));
	}
}
