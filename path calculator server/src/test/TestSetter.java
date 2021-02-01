package test;

import server_side.*;

public class TestSetter {
	

	static Server s; 
	
	public static void runServer(int port) {
		// put the code here that runs your server
		s=new MySerialServer(); // initialize
		s.open(port,new MyClientHandler());
	}

	public static void stopServer() {
		s.close();
	}
	

}
