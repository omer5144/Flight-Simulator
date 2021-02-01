package server_side;

public interface Server
{
	public void RunServer(int port, ClientHandler c);
	
	public void open(int port, ClientHandler c);
	
	public void close();
}
