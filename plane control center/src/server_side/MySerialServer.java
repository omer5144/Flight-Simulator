package server_side;
import java.net.*;
import java.io.*;

public class MySerialServer implements Server
{
	private boolean stop;
	
	public MySerialServer()
	{
		stop = false;
	}
	
	public void RunServer(int port, ClientHandler c)
	{
		try
		{
			ServerSocket server = new ServerSocket(port);
			server.setSoTimeout(1000);
			while(!stop)
			{
				try
				{
					Socket aClient=server.accept();
					try
					{
						c.handleClient(aClient.getInputStream(),aClient.getOutputStream());			
						aClient.close();
					}
					catch (SocketTimeoutException e) {}
				}
				catch (SocketTimeoutException e) {}
			}
			server.close();
		}
		catch(IOException e) {}
	}
	
	public void open(int port, ClientHandler c)
	{
		stop = false;
		new Thread(()->RunServer(port, c)).start();
	}
	
	public void close()
	{
		stop=true;
	}
}