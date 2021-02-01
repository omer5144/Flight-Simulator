package model;

import java.io.*;
import java.util.List;

public interface Model {
	
	//do
	public void calculateCSV(File f);
	public void CalculatePath(int istart, int iend, int jstart, int jend, String ip, String port);
	public boolean setSimulatorDetails(String ip, String port);
	public boolean connectToSimulator();
	public void openDataServer();
	public void interpret(String[] strs);
	public void disConnectFromSimulator();
	public void closeInterperter();
	public void end();
	public void setElevator(Double value);
	public void setThrottle(Double value);
	public void setAileron(Double value);
	public void setRudder(Double value);
	//get
	public Integer[][] getMatrix();
	public int getXLocation();
	public int getYLocation();
	public List<String> getPoints();
	public String getIpPath();
	public Integer getPortPath();
	public String getError();
	public boolean getIsConnectedToSumulator();
	public Double getHeading();
	//check
	public boolean ligellIp(String ip);
	public boolean ligellPort(String port);
	
	//error
	public void fileNotFound();
}
