import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.awt.geom.AffineTransform;

public class BallAnimation extends JFrame implements Runnable
{
	private Thread animator;
	private Graphics g;
	private BufferedImage dbImage, table, cue;
	
	private Pockets pocket1, pocket2, pocket3, pocket4, pocket5, pocket6;
	private Wall wall1, wall2, wall3, wall4;
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	private int jumlahBola = 16;
	
	private double tableWidth = 800;
	private double tableHeight = tableWidth/2;
	private double ballSize = tableHeight/15;
	
	private double[] posX = new double[16];
	private double[] posY = new double[16];
	
	private int score;
	private int mainBallIndex;
	private double v;
	private double incV;
	private double angle;
	private int mousePressedX;
	private int mousePressedY;
	private Line2D lineDirection;
	private boolean isPressed;
	private boolean powFull;

	public static HashMap<String, SoundEffect> sfx;
	
	AffineTransform backup;
	AffineTransform trans;
	
	public BallAnimation()
	{
		//configuring the main frame
		//setExtendedState(MAXIMIZED_BOTH);         // full screen
		setSize(1000,800);
		setResizable(false);
		setVisible(true);                         // can be seen
		setDefaultCloseOperation(EXIT_ON_CLOSE);  // end by exit
	  
		//image where everything is drawn
		dbImage = (BufferedImage) createImage(getWidth(), getHeight());
		g = dbImage.getGraphics();
						
		//creating balls on the canvas with random position and color
		generateTable();
		
		//creating a line which will be used to indicate the direction
		lineDirection = new Line2D.Double(balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY(),
									balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY());

		 try {                
          table = ImageIO.read(new File("table.PNG"));
		  cue	= ImageIO.read(new File("cue.png"));
		} catch (IOException ex) {
            // handle exception...
		}
		
		//adding listener to listen and react when the mouse is moved (adjusting lineDirection position)		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub				
				lineDirection.setLine(lineDirection.getX1(), lineDirection.getY1(), arg0.getX(), arg0.getY());
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sfx = new HashMap<String, SoundEffect>();
		sfx.put("hit", new SoundEffect("hit.wav"));
		sfx.put("pocket", new SoundEffect("pocket.wav"));
		sfx.put("cue", new SoundEffect("cue.wav"));
		
		//initializing the velocity (and its increment) used when the user click the mouse 
		v = 0;
		incV = 0.2;
		isPressed = false;
		addMouseListener(new MouseListener() {			
			@Override			
			public void mouseReleased(MouseEvent arg0) {
				if(!isMoving())
				{
					sfx.get("cue").play();
					
					// TODO Auto-generated method stub
					isPressed = false;
					
					//calculate new vx and vy for the main ball (indicated by mainBallIndex)
					int centerX, centerY;
					double centerV;
					
					//calculate the vector between the main ball and mouse pointer
					centerX = mousePressedX - (int)balls.get(mainBallIndex).getX();
					centerY = mousePressedY - (int)balls.get(mainBallIndex).getY();
					centerV = Math.sqrt(centerX*centerX + centerY*centerY);
					
					//calculate the velocity of the main ball based on how long the user pressed the mouse
					double vx, vy;
					vx = v/centerV*centerX;
					vy = v/centerV*centerY;
					balls.get(mainBallIndex).setDx(-vx);
					balls.get(mainBallIndex).setDy(vy);
					
					//reset the v for the next input
					v = 0;
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mousePressedX = arg0.getX();
				mousePressedY = arg0.getY();				
				isPressed = true;				
				lineDirection.setLine(lineDirection.getX1(), lineDirection.getY1(), arg0.getX(), arg0.getY());
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});	

		animator = new Thread(this);
		animator.start();
	}
	
	public void run() {
		while(true)
		{
			update();
			render();
			paintScreen();	
			try
			{
				animator.sleep(10);
			}
			catch(Exception ex)
			{
			}
		}
	}
	
	public void update()
	{		
		for(int i=0; i<balls.size(); i++)
		{
			balls.get(i).speed();
			if(balls.get(i).getX() >= pocket1.getX() - pocket1.getR() && balls.get(i).getX() <= pocket1.getX() + pocket1.getR()
				&& balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR() && balls.get(i).getY() >= pocket1.getY() - pocket1.getR())
			{
				detectpockets(i);
			}
			else if(balls.get(i).getX() <= wall1.getX1() + 2*balls.get(i).getR() && balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR()
				&& balls.get(i).getY() >= pocket2.getY() - pocket2.getR() )
			{
				detectpockets(i);
			}
			else if(balls.get(i).getX() >= wall1.getX2() - 2*balls.get(i).getR() && balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR()
					&& balls.get(i).getY() >= pocket3.getY() - pocket3.getR() )
			{
				detectpockets(i);
			}
			else if(balls.get(i).getX() <= wall4.getX1() + 2*balls.get(i).getR() && balls.get(i).getY() >= wall4.getY1() - 2*balls.get(i).getR()
					&& balls.get(i).getY() <= pocket4.getY() + pocket4.getR() )
			{
				detectpockets(i);
			}
			else if(balls.get(i).getX() >= pocket5.getX() - pocket5.getR() && balls.get(i).getX() <= pocket5.getX() + pocket5.getR() 
					&& balls.get(i).getY() >= wall3.getY1() - 2*balls.get(i).getR() && balls.get(i).getY() >= pocket5.getY() - pocket5.getR() )
			{
				detectpockets(i);
			}
			else if(balls.get(i).getX() >= wall2.getX1() - 2*balls.get(i).getR() && balls.get(i).getY() >= wall3.getY1() - 2*balls.get(i).getR()
					&& balls.get(i).getY() >= pocket6.getY() - pocket6.getR())
			{
				detectpockets(i);
			}
			else balls.get(i).detect(wall1, wall2, wall3, wall4);
			balls.get(i).detectBall(balls);
		}
		lineDirection.setLine(balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY(), lineDirection.getX2(), lineDirection.getY2());
		
		double deltaX = lineDirection.getX2() - lineDirection.getX1();
		double deltaY = lineDirection.getY2() - lineDirection.getY1();
		angle = Math.atan2(deltaY,deltaX) * 180 / Math.PI;
		
		if(isPressed)
		{
			if(v <= 30)	
				v += incV;
		}
	}
	public void render()
	{
		wall1.draw(g);
		wall2.draw(g);
		wall3.draw(g);
		wall4.draw(g);

		g.setColor(Color.WHITE);
		g.fillRect(0,0, getWidth(), getHeight());
	
		g.setColor(new Color(100,100,255));
		g.fillRect((int)wall1.x1, (int)wall1.y1, (int)tableWidth, (int)tableHeight);
		
		g.drawImage(table, (int)wall1.x1-86, (int)wall1.y1-76, null);
		
		/*if(lineDirection != null)
		{
			g.setColor(Color.RED);
			g.drawLine((int)lineDirection.getX1(), 
					(int)lineDirection.getY1(), 
					(int)lineDirection.getX2(), 
					(int)lineDirection.getY2());	
		}*/
		
		for(int i=0; i<jumlahBola; i++)
		{
			balls.get(i).draw(g);
		}
		
		pocket1.draw(g);
		pocket2.draw(g);
		pocket3.draw(g);
		pocket4.draw(g);
		pocket5.draw(g);
		pocket6.draw(g);
		
		if(!isMoving()) drawStick((Graphics2D)g);
		
		g.setColor(Color.RED);
		g.fillRect(20, (int)wall3.getY2()-(int)((v/30)*400), 20,  (int)((v/30)*400));
	}
	
	
	public void paintScreen()
	{
		Graphics frameGraphics = getGraphics();
		frameGraphics.drawImage(dbImage, 0, 0, null);
	}
	
	public void generateTable()
	{
		Random randomObj = new Random();
		
		double x3, y3 , x4, y4;
		double sizewidth = tableHeight;
		wall1 = new Wall(100, 100, 100+tableWidth, 100);
		x4 = wall1.getX1() + sizewidth * wall1.normalX();
		y4 = wall1.getY1() - sizewidth * wall1.normalY();
	
		x3 = wall1.getX2() + sizewidth * wall1.normalX();
		y3 = wall1.getY2() - sizewidth * wall1.normalY();
		
		wall2 = new Wall(wall1.getX2(), wall1.getY2(), x3, y3);
		wall3 = new Wall(x3, y3, x4, y4);
		wall4 = new Wall(x4,y4,wall1.getX1(), wall1.getY1());	
		
		pocket1 = new Pockets(wall1.x1+tableWidth/2,wall1.y1-20,			(ballSize/2)*1.5);
		pocket2 = new Pockets(wall1.x1,			 	wall1.y1,				(ballSize/2)*1.5);
		pocket3 = new Pockets(wall1.x1+tableWidth,	wall1.y1,				(ballSize/2)*1.5);
		pocket4 = new Pockets(wall1.x1,			 	wall1.y1+tableHeight,	(ballSize/2)*1.5);
		pocket5 = new Pockets(wall1.x1+tableWidth/2,wall1.y1+tableHeight+20,(ballSize/2)*1.5);
		pocket6 = new Pockets(wall1.x1+tableWidth,	wall1.y1+tableHeight,	(ballSize/2)*1.5);
		
		//cue ball
		posX[0] = wall1.x1+(tableWidth/4); posY[0] = wall1.y1+(tableHeight/2); 
		balls.add(new Ball(posX[0] , posY[0], ballSize/2, 0, 0, 
					Color.WHITE, 
					10));
		
		posX[1] = wall1.x1+(3*tableWidth/4); posY[1] = wall1.y1+(tableHeight/2); 
		
		posX[2] = posX[1]+ballSize-2; posY[2] = posY[1]+ballSize/2+0.5;
		posX[3] = posX[1]+ballSize-2; posY[3] = posY[1]-ballSize/2-0.5;
		
		posX[4] = posX[2]+ballSize; posY[4] = posY[2]+ballSize/2+0.5;
		posX[5] = posX[2]+ballSize; posY[5] = posY[2]-ballSize/2-0.5;
		posX[6] = posX[3]+ballSize; posY[6] = posY[3]-ballSize/2-0.5;
		
		posX[ 7] = posX[4]+ballSize; posY[ 7] = posY[4]+ballSize/2+0.5;
		posX[ 8] = posX[4]+ballSize; posY[ 8] = posY[4]-ballSize/2-0.5;
		posX[ 9] = posX[5]+ballSize; posY[ 9] = posY[5]-ballSize/2-0.5;
		posX[10] = posX[6]+ballSize; posY[10] = posY[6]-ballSize/2-0.5;
		
		posX[11] = posX[ 7]+ballSize; posY[11] = posY[ 7]+ballSize/2+0.5;
		posX[12] = posX[ 7]+ballSize; posY[12] = posY[ 7]-ballSize/2-0.5;
		posX[13] = posX[ 8]+ballSize; posY[13] = posY[ 8]-ballSize/2-0.5;
		posX[14] = posX[ 9]+ballSize; posY[14] = posY[ 9]-ballSize/2-0.5;
		posX[15] = posX[10]+ballSize; posY[15] = posY[10]-ballSize/2-0.5;
		
		
		for(int i=1; i<jumlahBola;i++)
		{	
			if(i == 5)
			{
				balls.add(new Ball(posX[i] , posY[i], 
						ballSize/2, 
						0, 0, 
						(Color.BLACK), 
						10));
			}
			else
			{
				balls.add(new Ball(posX[i] , posY[i], ballSize/2, 0, 0, 
						new Color(randomObj.nextInt(256),randomObj.nextInt(256),randomObj.nextInt(256)), 
						10));
			}
		}
	}
	
	public void detectpockets(int i)
	{
		if(balls.get(i).getV() < 9)
		{
			if(i == 0)
			{
				sfx.get("pocket").play();
				balls.get(i).setX(posX[0]);
				balls.get(i).setY(posY[0]);
				balls.get(i).setDx(0);
				balls.get(i).setDy(0);
			}
			else
			{
				sfx.get("pocket").play();
				score++;
				balls.get(i).setX(100+score*ballSize*2);
				balls.get(i).setY(700);
				balls.get(i).setDx(0);
				balls.get(i).setDy(0);
			}
		}
		
		if(balls.get(i).getY() <= wall1.getY1()) {
			if(pocket1.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket1.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() <= pocket2.getX()  && balls.get(i).getY() <= pocket2.getY() ) {
			if(pocket2.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket2.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() >= pocket3.getX()  && balls.get(i).getY() <= pocket3.getY()) {
			if(pocket3.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket3.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() <= pocket4.getX()  && balls.get(i).getY() >= pocket4.getY() ) {
			if(pocket4.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket4.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getY() >= wall3.getY1()) {
			if(pocket5.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket5.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() >= pocket6.getX()  && balls.get(i).getY() >= pocket6.getY() ) {
			if(pocket6.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= pocket6.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
	}
	
	public void drawStick(Graphics2D g)
	{
		backup = g.getTransform();
		trans = new AffineTransform();
		trans.rotate( Math.toRadians(angle), (int)lineDirection.getX1(),(int)lineDirection.getY1());

		g.transform( trans );
		g.drawImage(cue, (int)(lineDirection.getX1() + ballSize + ((v/30)*400)), (int)(lineDirection.getY1() - 15), 500, 30, null);
		g.setTransform( backup );
	}
	
	public boolean isMoving()
	{
		for(int i=0; i<jumlahBola; i++)
		{
			if(!balls.get(i).isStop()) return true;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		BallAnimation animasi = new BallAnimation();
	}
	
}
