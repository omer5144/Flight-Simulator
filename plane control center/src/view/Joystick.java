package view;

import java.util.ArrayList;
import java.util.List;


import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Joystick extends Canvas{
	
	public class Point
	{
		private double a;
		private double b;
		
		public Point() {
			this.a=0;
			this.b=0;
		}
		
		public Point(double a, double b) {
			this.a=a;
			this.b=b;
		}
		
		public double getA() {
			return a;
		}
		public void setA(double a) {
			this.a = a;
		}
		public double getB() {
			return b;
		}
		public void setB(double b) {
			this.b = b;
		}
		
	}
	
	//data_members:
	
	//values
	private DoubleProperty _x;
	private DoubleProperty _y;

	//look
	private double W;
	private double H;
	
	private StringProperty rwPercent;
	private StringProperty rhPercent;

	private double rw;
	private double rh;
	
	private String plate;
	
	//event
	private EventHandler<MouseEvent> whenChange;
	
	//Constructor:
	public Joystick() {
		
		_x = new SimpleDoubleProperty(0);
		_y = new SimpleDoubleProperty(0);
		
		Platform.runLater(()->{
	
			W = getWidth();
			H = getHeight();
			
			rw=W*Double.parseDouble(rwPercent.get())/100;
			rh=H*Double.parseDouble(rhPercent.get())/100;
			
			plate = "ellipse";
			whenChange = null;
			
			draw(convertValueToLocation(0, W, rw), convertValueToLocation(0, H, rh));
			
			
			setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
						if(inPlate(event.getX(), event.getY(), plate))
						{
							draw(event.getX(), event.getY());
							
							_x.set(convertLocationToValue(event.getX(), W, rw));
							_y.set(-1*convertLocationToValue(event.getY(), H, rh));
						}
						else
						{
							Point point = extractWithPlate(event.getX(), event.getY(), plate);
							draw(point.getA(), point.getB());
							
							_x.set(convertLocationToValue(point.getA(), W, rw));
							_y.set(-1*convertLocationToValue( point.getB(), H, rh));
						}
						
						if(whenChange!=null)
							whenChange.handle(event);
						see();
				}

			});
			
			setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					
					List<Point> list;
					if(inPlate(event.getX(), event.getY(), plate))
					{
						list = pointsOnLineToCenter(event.getX(), event.getY(), (int)distanceFromCenter(event.getX(), event.getY()));
					}
					else
					{
						Point point = extractWithPlate(event.getX(), event.getY(), plate);
						list = pointsOnLineToCenter(point.getA(), point.getB(), (int)distanceFromCenter(point.getA(), point.getB()));
					}
					new Thread(()->
					{
						for(Point p:list)
						{
							draw(p.getA(), p.getB());
							_x.set(convertLocationToValue(p.getA(), W, rw));
							_y.set(-1*convertLocationToValue(p.getB(), H, rh));
							
							see();
							
							try {	Thread.sleep(1);} catch (InterruptedException e) {}
						}
					}).start();
					
					_x.set(0);
					_y.set(0);
						
					if(whenChange!=null)
						whenChange.handle(event);
				}
			});
		});
	}
	
	//look:
	private List<Point> pointsOnLineToCenter(double x, double y, int n)
	{
		x-=W/2;
		y-=H/2;
		
		n-=1;
		
		List<Point> list = new ArrayList<Point>();
		
		for(int i=0;i<n;i++)
		{
			
			//l : i
			//k : n-i
			list.add(0, (new Point((x*i)/n +W/2,(y*i)/n +H/2)));
		}
		
		
		return list;
	}
	
	private void draw(double x, double y)
	{
		GraphicsContext gc = getGraphicsContext2D();
	
		
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, W, H);
		
		gc.setFill(Color.LIGHTGRAY);
		gc.fillOval(rw, rh, W-2*rw, H-2*rh);
		gc.setFill(Color.WHITE);
		gc.strokeOval(rw, rh, W-2*rw, H-2*rh);
		
		gc.setFill(Color.GRAY);
		gc.fillOval(x-rw, y-rh, 2*rw, 2*rh);
		gc.setFill(Color.WHITE);
		gc.strokeOval(x-rw, y-rh, 2*rw, 2*rh);

	}
	
	private boolean inPlate(double x, double y, String s)
	{
		if(s.equals("circle"))
		{
			return inBigCircle(x, y);
		}
		else if(s.equals("ellipse"))
		{
			return inBigEllipse(x, y);
		}
		else
			return false;
	}
	
	private boolean inBigCircle(double x, double y)
	{
		
		return (x-W/2)*(x-W/2) + (y-H/2)*(y-H/2)<= ((W-2*rw)*(W-2*rw))/4;
	}
	
	private boolean inBigEllipse(double x, double y)
	{
		
		return ((x-W/2)*(x-W/2))/(((W-2*rw)*(W-2*rw))/4) + ((y-H/2)*(y-H/2))/(((H-2*rh)*(H-2*rh))/4)<= 1;
	}

	private Point extractWithPlate(double x, double y, String s)
	{
		if(s.equals("circle"))
		{
			return extractWithCircle(x, y);
		}
		else if(s.equals("ellipse"))
		{
			return extractWithEllipse(x, y);
		}
		else
			return new Point();
	}
	
	private Point extractWithCircle(double x, double y)
	{
		x-=W/2;
		y-=H/2;
		
		double newx = (((W-2*rw)/2)*x)/Math.sqrt(x*x+y*y);
		double newy = (((H-2*rh)/2)*y)/Math.sqrt(x*x+y*y);
		
		return new Point(newx+W/2, newy+H/2);
	}
	
	private Point extractWithEllipse(double x, double y)
	{
		x-=W/2;
		y-=H/2;
		
		double a = (W-2*rw)/2;
		double b = (H-2*rh)/2;
		
		double newx = 1/(Math.sqrt((1/(a*a))+((y*y)/(x*x*b*b))));
		double newy = b*Math.sqrt(1-(newx*newx)/(a*a));
		
		if(x<0)
			newx*=-1;
		if(y<0)
			newy*=-1;
		
		return new Point(newx+W/2, newy+H/2);
	}
	
	private double convertValueToLocation(double x, double WH, double rwh)
	{
		return x*(WH/2-rwh)+WH/2;
	}
	
	private double convertLocationToValue(double x, double WH, double rwh)
	{
		return (x-(WH/2))/(WH/2-rwh);
	}

	private double distanceFromCenter(double x, double y)
	{
		x-=W/2;
		y-=H/2;

		return Math.sqrt(x*x+y*y);
	}
	
	//getters and setters:
	
	public void setWhenChange(EventHandler<MouseEvent> wc)
	{
		whenChange = wc;
	}
	
	public String getRwPercent() {
		return rwPercent.get();
	}
	public void setRwPercent(String s) {
		if(this.rwPercent==null)
			rwPercent=new SimpleStringProperty();
		this.rwPercent.set(s);
	}
	public String getRhPercent() {
		return rhPercent.get();
	}
	public void setRhPercent(String s) {
		if(this.rhPercent==null)
			rhPercent=new SimpleStringProperty();
		this.rhPercent.set(s);	
	}
	
	public DoubleProperty getXProperty()
	{
		return _x;
	}
	public DoubleProperty getYProperty()
	{
		return _y;
	}
	
	private void see()
	{
		//System.out.println(_x.get()+","+_y.get());
	}
	
	
}
