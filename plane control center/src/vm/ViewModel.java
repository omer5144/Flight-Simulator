package vm;

import java.io.*;
import java.util.*;

import javax.swing.text.StyledEditorKit.BoldAction;

import model.Model;
import model.MyInterpreter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ViewModel extends Observable implements Observer {
	
	//data_members:
	
	private Model m;
	
	//map look
	private Integer[][] matrix;
	private List<String> points;
	
	//x place
	private IntegerProperty iX;
	private IntegerProperty jX;
	
	//plane place
	private IntegerProperty iPlane;
	private IntegerProperty jPlane;
	DoubleProperty heading;
	
	//plain controls
	private DoubleProperty aileron;
	private DoubleProperty rudder;
	private DoubleProperty throttle;
	private DoubleProperty elevator;
	
	//path server data
	private StringProperty ipPath;
	private StringProperty portPath;
	
	//simulator server data
	private StringProperty ipSimulator;
	private StringProperty portSimulator;
	private BooleanProperty isConnectedToSimulator;

	//code
	private StringProperty autoPilotCode;
	
	//error
	private StringProperty error;
	
	//constructor, update:
	
	public ViewModel(Model m) {
		this.m = m;
		
		iPlane = new SimpleIntegerProperty();
		jPlane = new SimpleIntegerProperty();
		iX = new SimpleIntegerProperty();
		jX = new SimpleIntegerProperty();
		ipPath = new SimpleStringProperty();
		portPath = new SimpleStringProperty();
		ipSimulator = new SimpleStringProperty();
		portSimulator = new SimpleStringProperty();
		autoPilotCode = new SimpleStringProperty();
		elevator = new SimpleDoubleProperty();
		rudder = new SimpleDoubleProperty();
		throttle = new SimpleDoubleProperty();
		aileron = new SimpleDoubleProperty();
		isConnectedToSimulator = new SimpleBooleanProperty(false);
		heading = new SimpleDoubleProperty();
		
		matrix = null;
		points = null;
		
		error = new SimpleStringProperty();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o==m)
		{
			if(arg.equals("matrix"))
			{
				matrix = m.getMatrix();
				setChanged();
				notifyObservers("matrix");
			}
			else if(arg.equals("path"))
			{

				points = m.getPoints();
				setChanged();
				notifyObservers("path");
			}
			else if(arg.equals("path server opened"))
			{
				ipPath.set(m.getIpPath());
				portPath.set(m.getPortPath().toString());
			}
			else if(arg.equals("error"))
			{
				error.set(m.getError());
				setChanged();
				notifyObservers("error");
			}
			else if(arg.equals("is Connected To Simulator"))
			{
				isConnectedToSimulator.set(m.getIsConnectedToSumulator());
			}
			else if(arg.equals("plain location"))
			{
				jPlane.set(m.getXLocation());
				iPlane.set(m.getYLocation());
				heading.set(m.getHeading());
				setChanged();
				notifyObservers("plain location");
			}
			
		}
		
	}
	
	//do-methodes:
	
	public void calculateMatrix(File f)
	{
		m.calculateCSV(f);
	}
	
	public void calculatePath()
	{
		m.CalculatePath(iPlane.get(), iX.get(), jPlane.get(), jX.get(), ipPath.get(), portPath.get());
	}
	
	public boolean ConnectToSimulator()
	{
		return m.setSimulatorDetails(ipSimulator.get(), portSimulator.get());
	}
	
	public boolean connectToSimulator()
	{
		return m.connectToSimulator();
	}
	
	public void disConnectFromSimulator()
	{
		m.disConnectFromSimulator();
	}
	
	public void joystickMoved()
	{
		m.setAileron(aileron.get());
		m.setElevator(elevator.get());
	}
	
	public void scrollBarRudderMoved()
	{
		m.setRudder(rudder.get());
	}
	
	public void scrollBarThrottleMoved()
	{
		m.setThrottle(throttle.get());
	}
	
	public void openDataServer() {
		m.openDataServer();
	}
	
	 public void interpret()
	 {
		 String str = autoPilotCode.get();
		 String[] lines;
		 int length = 0;
		 
		 Scanner s = new Scanner(str);

		 while(s.hasNextLine())
		 {
			 length++;
			 s.nextLine();
			 
		 }
		 
		 lines = new String[length];
		 
		 s.close();
		 
		 Scanner s1= new Scanner(str);

		 int i=0;
		 while(s1.hasNextLine())
		 {
			 lines[i] = s1.nextLine();
			 i++;
		 }
		 
		 s1.close();

		 m.interpret(lines);
	 }
	 
	 public void closeInterperter()
	 {
		 m.closeInterperter();
	 }
	
	public void end()
	{
		m.end();
	}
	
	//get-methodes:
	
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
	public StringProperty getIpSimulator() {
		return ipSimulator;
	}
	public StringProperty getPortSimulator() {
		return portSimulator;
	}
	public StringProperty getIpPath() {
		return ipPath;
	}
	public StringProperty getPortPath() {
		return portPath;
	}
	public StringProperty getAutoPilotCode() {
		return autoPilotCode;
	}
	public DoubleProperty getAileron() {
		return aileron;
	}
	public DoubleProperty getRudder() {
		return rudder;
	}
	public DoubleProperty getThrottle() {
		return throttle;
	}
	public DoubleProperty getElevator() {
		return elevator;
	}
	public StringProperty getError() {
		return error;
	}
	public Integer[][] getMatrix()
	{
		return matrix;
	}
	public List<String> getPoints()
	{
		return points;
	}
	public BooleanProperty getIsConnectedToSimulator()
	{
		return isConnectedToSimulator;
	}

	public boolean ligellIp(String ip)
	{
		return m.ligellIp(ip);
	}
	
	public boolean ligellPort(String port)
	{
		return m.ligellPort(port);
	}
	public DoubleProperty getHeading()
	{
		return this.heading;
	}
	//error
	
	public void fileNotFound() {
		m.fileNotFound();
	}
}
