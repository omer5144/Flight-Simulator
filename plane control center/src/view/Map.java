package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Map extends Canvas{
	//data_members:
	
	//map look
	private Integer[][] map;	
	private WritableImage img;
	private List<String> points;
	private StringProperty imgPlane;
	private StringProperty imgX;
	
	//plane place
	private IntegerProperty iPlane;
	private IntegerProperty jPlane;
	private DoubleProperty heading;
	
	//x place
	private IntegerProperty iX;
	private IntegerProperty jX;
	
	//size
	private double W;
	private double H;
	
	//events
	private EventHandler<MouseEvent> whenPressed = null;
	
	//constructor:
	public Map() {
		Platform.runLater(()->{
			map = null;
			points = null;
			img = null;
			
			iPlane = new SimpleIntegerProperty(-1);
			jPlane = new SimpleIntegerProperty(-1);
			heading=new SimpleDoubleProperty(0);
			
			iX = new SimpleIntegerProperty(-1);
			jX = new SimpleIntegerProperty(-1);
			
			W = getWidth();
			H = getHeight();

			
			imgPlane = new SimpleStringProperty();
			try {
				Image img = new Image(new FileInputStream("./resources/mapNotExist.jpeg"));
				GraphicsContext gc = getGraphicsContext2D();
				gc.drawImage(img, 0, 0, W, H);
			} catch (FileNotFoundException e) {e.printStackTrace();}
			
			/*imgPlane = new SimpleStringProperty();
			imgPlane.set("./resources/plane.png");*/
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
						if(map!=null)
						{
							double wLength = W/map[0].length;
							double hLength = H/map.length;
							
							iX.set((int)(event.getY()/hLength));
							jX.set((int)(event.getX()/wLength));
							
							if(whenPressed != null)
								whenPressed.handle(event);
							
							draw();
						}
				}

			});
		});
	}

	//looks:
	public synchronized void draw()
	{
			GraphicsContext gc = getGraphicsContext2D();
			gc.setTextBaseline(VPos.TOP);
			
			double wLength = W/map[0].length;
			double hLength = H/map.length;
	
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, W, H);
			
			
			gc.drawImage(img, 0, 0, W, H);
			
			if(points!=null && points.size()>1 && isPlaneInMap() && isXInMap())
			{
				String[] start = points.get(0).split(",");
				String[] end = new String[2];
				
				for(int i=1;i<points.size();i++)
				{
					end = points.get(i).split(",");
					gc.setStroke(Color.WHITE);
					gc.strokeLine(Integer.parseInt(start[1])*wLength+wLength/2, Integer.parseInt(start[0])*hLength+hLength/2, Integer.parseInt(end[1])*wLength+wLength/2, Integer.parseInt(end[0])*hLength+hLength/2);
		
					start = end;
				}
			}
			else
			{
				points=null;
			}
			
			
			if(isPlaneInMap())
			{
				double head=heading.get();
				head=head%360;
				if(head<0)
					head+=360;
				
				if(head>22.5 && head<=67.5)
					imgPlane.setValue("./resources/plane45.png");
				else if(head>67.5 && head<=112.5)
					imgPlane.setValue("./resources/plane90.png");
				else if(head>112.5 && head<=157.5)
					imgPlane.setValue("./resources/plane135.png");
				else if(head>157.5 && head<=202.5)
					imgPlane.setValue("./resources/plane180.png");
				else if(head>202.5 && head<=247.5)
					imgPlane.setValue("./resources/plane225.png");
				else if(head>247.5 && head<=292.5)
					imgPlane.setValue("./resources/plane270.png");
				else if(head>292.5 && head<=337.5)
					imgPlane.setValue("./resources/plane315.png");
				else if(head>337.5 || head<=22.5)
					imgPlane.setValue("./resources/plane0.png");
				try {
					Image img = new Image(new FileInputStream(imgPlane.get()));
					gc.drawImage(img, jPlane.get()*wLength+wLength/2-5, iPlane.get()*hLength+hLength/2-5, 10, 10);
				} catch (FileNotFoundException e) {e.printStackTrace();}
				
			}
			if(isXInMap())
			{
				try {
					Image img = new Image(new FileInputStream(imgX.get()));
					gc.drawImage(img, jX.get()*wLength+wLength/2-4, iX.get()*hLength+hLength/2-4, 8, 8);
				} catch (FileNotFoundException e) {e.printStackTrace();}
				
			}
	}
	
	public void drawText(String text, double leftX, double topY, double w, double h, GraphicsContext gc)
	{
		double num = text.length()*6.46875;
		
		if(h>10)
		{
			if(num>w)
				gc.fillText(text, leftX+1, topY-4+(h-10)/2, w-2);
			else
			{
				gc.fillText(text, leftX+(w-num)/2, topY-4+(h-10)/2);
			}
		}
		else
		{
			if(num>w)
				gc.fillText(text, leftX+1, topY-4, w-2);
			else
			{
				gc.fillText(text, leftX+(w-num)/2, topY-4);
			}
		}
	
	}
	
	public Color numberToColor(int min, int max, int value)
	{
		double mn = 0;
		double mx = max-min;
		double v = value-min;
		
		int g =0, r =0;
		if(v/mx < 0.5)
		{
			r=255;
			g=(int)((v/(0.5*mx))*255);
			

		}
		else
		{
			g=255;
			r=(int)(255-255*((v-((mn+mx)/2))/(0.5*(mx-mn))));
			

		}
		
		return Color.rgb(r, g, 0, 1);
	}

	public List<Integer> minmax(Integer[][] map)
	{
		int min = map[0][0];
		int max = map[0][0];
		
		for(int i = 0; i<map.length;i++)
		{
			for(int j=0;j<map[0].length;j++)
			{
				if(min>map[i][j])
				{
					min = map[i][j];
				}
				if(max<map[i][j])
				{
					max = map[i][j];
				}
			}
		}
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(min);
		list.add(max);
		
		return list;
	}
	
	//getters, setters:
	
	public void setWhenPressed(EventHandler<MouseEvent> wp)
	{
		whenPressed = wp;
	}
	
	public void setPoints(List<String> list)
	{
		this.points = list;
		
		if(map!=null)
			draw();
	}
	
	public Integer[][] getMap() {
		return map;
	}
	public void setMap(Integer[][] map)
	{
		this.map = map;
		
		
		List<Integer> l = minmax(map);
		img = new WritableImage(map[0].length,map.length);
		PixelWriter pw = img.getPixelWriter();
		
		for(int i =0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				pw.setColor(j, i, numberToColor(l.get(0), l.get(1), map[i][j]));
			}
		}
		
		draw();
	}
	
	public String getImgX() {
		return imgX.get();
	}
	public void setImgX(String imgX) {
		if(this.imgX==null)
			this.imgX = new SimpleStringProperty();
		this.imgX.set(imgX);
	}
	
	public IntegerProperty getiPlane() {
		return iPlane;
	}
	public IntegerProperty getjPlane() {
		return jPlane;
	}
	public IntegerProperty getiX() {
		return iX;
	}
	public IntegerProperty getjX() {
		return jX;
	}
	
	public DoubleProperty getHeading()
	{
		return this.heading;
	}
	public Boolean isPlaneInMap()
	{
		return iPlane.get()>=0 && jPlane.get()>=0 && iPlane.get()<=map.length && jPlane.get()<=map[0].length;
	}
	public Boolean isXInMap()
	{
		return iX.get()>=0 && jX.get()>=0 && iX.get()<=map.length && jX.get()<=map[0].length;
	}
}
