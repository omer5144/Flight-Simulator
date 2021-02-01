package server_side;
import java.io.*;

public interface ClientHandler
{
	public void handleClient(InputStream in, OutputStream out);
}
