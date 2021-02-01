package model;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

public class MyModel extends Observable implements Model {
	
	//data_members:
	
	//plain data
	private Double xStart;
	private Double yStart;
	private Double area;
	private Integer[][] matrix;
	private Integer xLocation;
	private Integer yLocation;
	private Double heading;

	//path data
	private List<String> points;
	
	//path server data
	private Integer portPath;
	private String ipPath;
	 
	//simulator server data
	private Integer portSimulator;
	private String ipSimulator;
	private Socket socketSet;
	private PrintWriter outSet;
	private BufferedReader inSet;
	
	private Socket socketGet;
	private PrintWriter outGet;
	private BufferedReader inGet;
	
	private boolean isConnectedToSimulator;
	private volatile boolean continueGetVars;
	
	//error
	String error;

	
	/*private Socket simulator;
	private PrintWriter out;*/ //option1 to connect
	 
	//constructors:
	
	public MyModel()
	{
		xStart = null;
		yStart = null;
		area = null;
		matrix = null;
		xLocation = null;
		yLocation = null;
		
		points = null;
		
		portPath = null;
		ipPath = null;
		
		portSimulator = null;
		ipSimulator=null;
		socketGet = null;
		outGet = null;
		inGet = null;
		socketSet = null;
		outSet = null;
		inSet = null;
		isConnectedToSimulator = false;
		continueGetVars = false;
	}
	
	
	//do-methodes:

	@Override
	public void calculateCSV(File f)
	{
		xStart = null;
		yStart = null;
		area = null;
				
		try {
			Scanner sLines = new Scanner(f);

			if(sLines.hasNextLine())
			{
				Scanner sValues = new Scanner(sLines.nextLine());
				sValues.useDelimiter(",");
				
				if(sValues.hasNext())
					xStart = sValues.nextDouble();
				
				if(sValues.hasNext())
					yStart = sValues.nextDouble();
				else
				{
					csvTemplateError();
					sValues.close();
					return;
				}
				

				sValues.close();
			}
			
			
			if(sLines.hasNextLine())
			{
				Scanner sValues = new Scanner(sLines.nextLine());
				sValues.useDelimiter(",");
				
				if(sValues.hasNext())
					area = sValues.nextDouble();
				else
				{
					csvTemplateError();
					sValues.close();
					return;
				}
				sValues.close();
			}
			else
			{
				csvTemplateError();
				return;
			}
			
			List<List<Integer>> mat = new ArrayList<List<Integer>>();
			
			if(!sLines.hasNext())
			{
				csvTemplateError();
				return;
			}
			
			int temp = 0;
			boolean flag = true;
			
			while(sLines.hasNextLine())
			{
				List<Integer> row = new ArrayList<Integer>();
				
				String line = sLines.nextLine();

				Scanner sValues = new Scanner(line);
				sValues.useDelimiter(",");
				
				if(!sValues.hasNext())
				{
					csvTemplateError();
					sValues.close();
					return;
				}
				
				while(sValues.hasNext())
				{
					row.add(sValues.nextInt());
				}
				
				if(flag)
				{
					temp = row.size();
					flag = false;
				}
				else
				{
					if(row.size()!=temp)
					{
						csvTemplateError();
						sValues.close();
						return;
					}
				}

				
				mat.add(row);
				
				sValues.close();
			}
			
			sLines.close();
			
			matrix = new Integer[mat.size()][mat.get(0).size()];
			
			int min = mat.get(0).get(0);
			
			for(List<Integer> row : mat)
			{
				for(Integer value:row)
				{
					if(value<min)
					{
						min = value;
					}
				}
			}
			
			if(min>0)
			{
				for(int i=0;i<matrix.length;i++)
				{
					for(int j =0;j<matrix[0].length;j++)
					{
						matrix[i][j] = mat.get(i).get(j);
					}
				}
			}
			else
			{
				for(int i=0;i<matrix.length;i++)
				{
					for(int j =0;j<matrix[0].length;j++)
					{
						matrix[i][j] = mat.get(i).get(j)+(-1)*min+1;
					}
				}
			}
			
			setChanged();
			notifyObservers("matrix");

		} catch (FileNotFoundException e) {fileNotFound();}
	}
	
	@Override
	public void CalculatePath(int istart, int iend, int jstart, int jend, String ip, String port)
	{
		ipPath = ip;
		portPath =Integer.parseInt(port);
		
		points = new ArrayList<String>();
		
		Socket s=null;
		PrintWriter out=null;
		BufferedReader in=null;
		
		boolean errorFlag = false;
		try{
			s=new Socket(ipPath,portPath);
			s.setSoTimeout(3000);
			out=new PrintWriter(s.getOutputStream());
			in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			int j;
			for(int i=0;i<matrix.length;i++){
				for(j=0;j<matrix[i].length-1;j++){
					out.print(matrix[i][j]+",");
				}
				out.println(matrix[i][j]);
			}
			out.println("end");

			out.println(istart+","+jstart);
			out.println(iend+","+jend);
			out.flush();
			String usol=in.readLine();
			
			int i1 = istart;
			int j1 = jstart;
			
			StringBuilder answer = new StringBuilder("");
			
			String[] arrows = usol.split(",");
			int index = 0;
			
			while(!(i1==iend && j1==jend) && index<arrows.length)
			{

					answer.append(i1+","+j1+"#");
					if(arrows[index].equals("Up"))
						i1--;
					else if(arrows[index].equals("Down"))
						i1++;
					else if(arrows[index].equals("Right"))
						j1++;
					else if(arrows[index].equals("Left"))
						j1--;
					index++;

			}
			answer.append(i1+","+j1);
			usol = answer.toString();
			String[] path;
			
			if(usol.contains("#"))
				path = usol.split("#");
			else
			{
				path = new String[1];
				path[0] = usol;
			}
			
			for(String point : path)
			{
				points.add(point);
			}
			
			setChanged();
			notifyObservers("path");
		}
		catch(SocketTimeoutException e){timeExeption(); }
		catch(IOException e){ioExeption(); errorFlag = true; }
		finally{
			try {
				in.close();
				out.close();
				s.close();
			} catch (Exception e) {if(!errorFlag) ioExeption();}
		}
		
		
		
	}
	
	@Override
	public boolean setSimulatorDetails(String ip, String port) {
	
		if(ip==null || ip=="")
		{
			invalidIp();
			return false;
		}
		
		if(port==null || port=="")
		{
			invalidPort();
			return false;
		}
		
		String[] strs = ip.split("\\.");

		if(strs.length!=4)
		{
			invalidIp();
			return false;
		}
		
		for(String s:strs)
		{
			int number = 0;
			try
			{
				number = Integer.parseInt(s);
			}
			catch(NumberFormatException e)
			{
				invalidIp();
				return false;
			}
			
			if(number<0 || number>255)
			{
				invalidIp();
				return false;
			}
		}
		
		int p;
		
		try
		{
			p = Integer.parseInt(port);
		}
		catch(NumberFormatException e)
		{
			invalidPort();
			return false;
		}
		
		if(p<0)
		{
			invalidPort();
			return false;
		}
		
		ipSimulator = ip;
		portSimulator = p;
	
		String[] lines = {"connect "+ipSimulator+" "+portSimulator};
		
		MyInterpreter.interpret(lines);
		
		isConnectedToSimulator = true;

		try {
			socketSet = new Socket(ipSimulator,portSimulator);
			outSet = new PrintWriter(socketSet.getOutputStream());	
			inSet = new BufferedReader(new InputStreamReader(socketSet.getInputStream()));
			
			socketGet = new Socket(ipSimulator,portSimulator);
			outGet = new PrintWriter(socketGet.getOutputStream());	
			inGet = new BufferedReader(new InputStreamReader(socketGet.getInputStream()));
			
		} catch (IOException e) {cantConnect();isConnectedToSimulator=false; return false;}
		
		continueGetVars = true;
		new Thread(()->{
			try
			{
				while(socketGet.isConnected())
				{
					try
					{
						if(matrix!=null && outGet!=null && inGet!=null)
						{
							Double lon, lat;
							String read;
							String[] reads;
							double steps =Math.sqrt(area);
							
							read = sendAndRead(inGet, outGet, "get position/latitude-deg");
							if(read==null)
								break;
							if(read.startsWith("/> "));
							read = read.substring(3);
							reads = read.split(" ");
							lat = new Double(reads[2].substring(1, reads[2].length()-1));
		
							
							read = sendAndRead(inGet, outGet, "get position/longitude-deg");
							if(read==null)
								break;
							if(read.startsWith("/> "));
								read = read.substring(3);
							reads = read.split(" ");
							lon = new Double(reads[2].substring(1, reads[2].length()-1));
							
							read = sendAndRead(inGet, outGet, "get instrumentation/heading-indicator/indicated-heading-deg");
							if(read==null)
								break;
							if(read.startsWith("/> "));
								read = read.substring(3);
							reads = read.split(" ");
		
							heading = new Double(reads[2].substring(1, reads[2].length()-1));
							
							Double[] location = convertToIndexes(xStart, lon, yStart, lat);
							
							xLocation = (int)(location[0].doubleValue() / steps);
			                yLocation = (int)((-1 * location[1].doubleValue()) / steps);
							
							setChanged();
							notifyObservers("plain location");	
						}
						else  if(!continueGetVars)
						{
							break;
						}
					}
					catch(Exception e)
					{
						break;
					}
					try {Thread.sleep(250);} catch (InterruptedException e) {}
				}
			}
			catch(Exception e)
			{
				return;
			}
			
			
		}).start();
		
		setChanged();
		notifyObservers("is Connected To Simulator");
		return true;
		
	}
	
	@Override
	public boolean connectToSimulator() {
		if(ipSimulator==null||portSimulator==null)
		{
			mustConnect();
			return false;
		}

		return true;
		
	}
	
	//dont forget
	@Override
	public void openDataServer() {
		String[] lines = {"print \"plane control center opened\"","openDataServer 5400 10"};
		
		MyInterpreter.interpret(lines);
	}

	//dont forget
	@Override
	public void interpret(String[] strs) {
		MyInterpreter.interpret(strs);
		
	}
	

	@Override
	public void closeInterperter() {
		MyInterpreter.close();
	}


	@Override
	public void end() {
		

		if(isConnectedToSimulator)
		{
			String[] lines = {"disconnect","print \"plane control center closed\"","return 0"};
			
			MyInterpreter.interpret(lines);
		}
		else
		{
			String[] lines = {"print \"plane control center closed\"","return 0"};
			
			MyInterpreter.interpret(lines);
		}	
		matrix = null;
		continueGetVars=false;
	}
	
	@Override
	public void disConnectFromSimulator() {	
		continueGetVars = false;
		
		try {
			if(outGet!=null)
			{
				outGet.close();
				outGet = null;
			}
			
			if(inGet!=null)
			{
				inGet.close();
				inGet=null;
			}
			
			if(socketGet!=null)
			{
				socketGet.close();
				socketGet=null;
			}
			
			if(outSet!=null)
			{
				outSet.close();
				outSet=null;
			}
			
			if(inSet!=null)
			{
				inSet.close();
				inSet=null;
			}
			
			if(socketSet!=null)
			{
				socketSet.close();
				socketSet=null;
			}
		} catch (IOException e) {e.printStackTrace();	}
		
		
		
	}
	
	@Override
	public void setElevator(Double value) {
		//send(out, "set controls/flight/elevator "+value);
		sendAndRead(inSet, outSet, "set controls/flight/elevator "+value);
	}
	@Override
	public void setAileron(Double value) {
		//send(out, "set controls/flight/aileron "+value);
		sendAndRead(inSet, outSet, "set controls/flight/aileron "+value);
	}
	@Override
	public void setRudder(Double value) {
		//send(out, "set controls/flight/rudder "+value);
		sendAndRead(inSet, outSet, "set controls/flight/rudder "+value);
	}
	@Override
	public void setThrottle(Double value) {
		//send(out, "set /controls/engines/current-engine/throttle "+value);
		sendAndRead(inSet, outSet, "set /controls/engines/current-engine/throttle "+value);
	}
	
	//get-methodes:
	
	@Override
	public Integer[][] getMatrix()
	{
		return matrix;
	}
	
	@Override
	public List<String> getPoints()
	{
		return points;
	}
	
	@Override
	public String getIpPath()
	{
		return ipPath;
	}
	
	@Override
	public Integer getPortPath()
	{
		return portPath;
	}

	@Override
	public String getError()
	{
		return error;
	}
	
	@Override
	public boolean getIsConnectedToSumulator() {
		return isConnectedToSimulator;
	}
	
	public Double getHeading()
	{
		return this.heading;
	}
	
	//errors
	public void csvTemplateError()
	{
		error = "The file is not according to the template";
		setChanged();
		notifyObservers("error");
	}
	
	public void fileNotFound()
	{
		error = "file not found";
		setChanged();
		notifyObservers("error");
	}
	
	public void ioExeption()
	{
		error = "path calculator server ran into some IO exception";
		setChanged();
		notifyObservers("error");
	}
	
	public void timeExeption()
	{
		error = "path calculator server takes over 3 seconds to answer";
		setChanged();
		notifyObservers("error");
	}

	public void invalidIp()
	{
		error = "invalid ip";
		setChanged();
		notifyObservers("error");
	}
	
	public void invalidPort()
	{
		error = "invalid port";
		setChanged();
		notifyObservers("error");
	}
	
	public void mustConnect()
	{
		error = "you must connect first";
		setChanged();
		notifyObservers("error");
	}
	
	public void cantConnect()
	{
		error = "can't connect to simulator";
		setChanged();
		notifyObservers("error");
	}
	
	//help

	private String sendAndRead(BufferedReader in, PrintWriter out, String s)
	{
			//System.out.println("sending: "+s);
			if(!socketGet.isConnected())
				return null;
			out.println(s);
			out.flush();
			try {
				String str = in.readLine();
				return str;
			} catch (IOException e) {return null;}
			catch (Exception e) {return null;}
	}
	
	@Override
	public int getXLocation() {
		return xLocation;
	}


	@Override
	public int getYLocation() {
		return yLocation;
	}
	
	private Double[] convertToIndexes(Double lonS, Double lonE, Double latS, Double latE) {
	    Double distance;
	    Double xy[] = new Double[2];
	    if (lonS == lonE && latS == latE) {
	      distance = new Double(0);
	    }
	    else {
	      distance = Math.sin(Math.toRadians(latS)) * Math.sin(Math.toRadians(latE)) + Math.cos(Math.toRadians(latS)) * Math.cos(Math.toRadians(latE)) * Math.cos(Math.toRadians(lonS - lonE));
	      distance = Math.acos(distance);
	      distance = Math.toDegrees(distance);
	      distance = distance * 111.18957696;
	    } 
	    Double deltaLong = Math.toRadians(lonE) - Math.toRadians(lonS);
	    Double degree = Math.toDegrees(Math.atan2(Math.sin(deltaLong) * Math.cos(Math.toRadians(latE)), Math.cos(Math.toRadians(latS)) * Math.sin(Math.toRadians(latE)) - Math.sin(Math.toRadians(latS)) * Math.cos(Math.toRadians(latE)) * Math.cos(deltaLong)));
	    Double bearing = (degree + 360) % 360;
	    xy[0] = distance * Math.sin(Math.toRadians(bearing));
	    xy[1] = distance * Math.cos(Math.toRadians(bearing));
	    return xy;
	  }


	@Override
	public boolean ligellIp(String ip) {
		if(ip==null || ip=="")
		{
			invalidIp();
			return false;
		}
		
		String[] strs = ip.split("\\.");

		if(strs.length!=4)
		{
			invalidIp();
			return false;
		}
		
		for(String s:strs)
		{
			int number = 0;
			try
			{
				number = Integer.parseInt(s);
			}
			catch(NumberFormatException e)
			{
				invalidIp();
				return false;
			}
			
			if(number<0 || number>255)
			{
				invalidIp();
				return false;
			}
		}
		
		return true;
		
		
	}


	@Override
	public boolean ligellPort(String port) {
		if(port==null || port=="")
		{
			invalidPort();
			return false;
		}
		
		int p;
		
		try
		{
			p = Integer.parseInt(port);
		}
		catch(NumberFormatException e)
		{
			invalidPort();
			return false;
		}
		
		if(p<0)
		{
			invalidPort();
			return false;
		}
		
		return true;
	}









}


