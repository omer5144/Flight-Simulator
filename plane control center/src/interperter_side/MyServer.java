package interperter_side;

import java.io.*;
import java.net.*;
import java.util.*;

public class MyServer implements Server {

	private volatile boolean stop;
	private Thread t;
	private Handler h;
	private Thread run_server;
	
	public MyServer() {
		stop = true;
		h = new Handler("./resources/generic_small.xml");
	}
	
	@Override
	public void start(int port, int frequency) {
		stop = false;
		t = Thread.currentThread();
		
		run_server = (new Thread(()->runServer(port, frequency)));
		run_server.start();
		
		synchronized (t)
		{
			try {t.wait();} catch (InterruptedException e) {}
		}
	}
	
	private void runServer(int port, int frequency){
		
		try {
			ServerSocket server=new ServerSocket(port);
			try{
				
				server.setSoTimeout(100);
				Socket client =null;
				while(!stop && client==null)
				{
					try {
						client=server.accept();
					}catch(SocketTimeoutException e){}
				}
				
				if(client == null)
					return;
				
				synchronized (t)
				{
					t.notify();
				}
				
				BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
				String line=null;
				while(!stop)
				{
					line = in.readLine();
					if(line!=null)
					{
						String[] values = line.split(",");
						
						h.update(values);
						
						
						Thread.sleep(1000/frequency);
					}
				}
				in.close();
				client.close();
			}catch (InterruptedException e) {}
			server.close();
		} catch (IOException e)   {} 
	}
	

	@Override
	public void stop() {
		stop = true;

	}

	
}
