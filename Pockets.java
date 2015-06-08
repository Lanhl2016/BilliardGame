import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Pockets
{
	private double x, y, r;
	private int score;
	
	public Pockets(double x, double y, double r)
	{
		this.x = x;
		this.y = y;
		this.r = r;
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
	
	public double distance(double xc, double yc)
	{
		double a = x - xc;
		double b = y - yc;

		return Math.sqrt(a*a + b*b);
	}
	
	public void detect(int i, Wall wall1, Wall wall2, Wall wall3, Wall wall4, ArrayList<Ball> balls, double[] posX, double[] posY)
	{
		if(i == 0)
		{
			balls.get(i).setX(posX[0]);
			balls.get(i).setY(posY[0]);
			balls.get(i).setDx(0);
			balls.get(i).setDy(0);
		}
		else
		{
			score++;
			balls.get(i).setX(100+score*r*2);
			balls.get(i).setY(700);
			balls.get(i).setDx(0);
			balls.get(i).setDy(0);
		}
		
		if(balls.get(i).getY() <= wall1.getY1()) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2 >= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
		else if(balls.get(i).getX() <= getX()  && balls.get(i).getY() <= getY() ) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2 >= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
		else if(balls.get(i).getX() >= getX()  && balls.get(i).getY() <= getY() ) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2 >= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
		else if(balls.get(i).getY() >= wall3.getY1()) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2 >= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
		else if(balls.get(i).getX() >= getX()  && balls.get(i).getY() >= getY() ) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2>= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
		else if(balls.get(i).getX() <= getX()  && balls.get(i).getY() >= getY() ) 
		{
			if(distance(balls.get(i).getX(), balls.get(i).getY()) + balls.get(i).getR() + 2>= getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillOval((int)(x-r),(int)(y-r),(int)(2*r), (int)(2*r));
	}
}
