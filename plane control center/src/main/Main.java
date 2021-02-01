package main;

import java.util.Scanner;

import server_side.MyClientHandler;
import server_side.MySerialServer;

public class Main {

	public static void main(String[] args) {
		MySerialServer server = new MySerialServer();
		
		Scanner s = new Scanner(System.in);
		
		String answer;
		int port = 0;
		boolean beforeOpening = true;
		
		System.out.println("The Optional Commands:");
		System.out.println("1) open server [port] - open the server on port [port]");
		System.out.println("2) close server - close the server");
		System.out.println("3) ls - show optional commands");
		System.out.println("4) quiet - quiet the program");
		System.out.println();
		
		while(true)
		{
			System.out.print("/> enter command:   ");
			answer = s.nextLine();
			
			if(answer.startsWith("open server"))
			{
				String[] values = answer.split(" ");
				if(values.length!=3)
				{
					System.out.println("/> required to write the command on the folloing way: open server [port]");
					System.out.println();
				}
				else
				{
					try
					{
					port = Integer.parseInt(values[2]);}
					catch (NumberFormatException e)
					{
						System.out.println("/> required to write a number in the [port] field");
						System.out.println();
						continue;
					}
					if(port<0)
					{
						System.out.println("/> required a positive port number");
						System.out.println();
					}
					else
					{
						if(beforeOpening)
						{
							beforeOpening = false;
							server.open(port, new MyClientHandler());
							System.out.println("/> sever opened on port "+port);
							System.out.println();
						}
						else
						{
							System.out.println("/> you already opened the server");
							System.out.println();
						}
					}
				}
					
			}
			else if(answer.equals("close server"))
			{
				if(beforeOpening)
				{
					System.out.println("/> you havn't opened a server yet");
					System.out.println();
				}
				else
				{
					beforeOpening = true;
					server.close();
					System.out.println("/> server closed");
					System.out.println();
				}
			}
			else if(answer.equals("ls"))
			{
				System.out.println();
				System.out.println("The Optional Commands:");
				System.out.println("1) open server [port] - open the server on port [port]");
				System.out.println("2) close server - close the server");
				System.out.println("3) ls - show optional commands");
				System.out.println("4) quiet - quiet the program");
				System.out.println();
			}
			else if(answer.equals("quiet"))
			{
				if(!beforeOpening)
					server.close();
				s.close();
				return;
			}
			else
			{
				System.out.println("/> there is no such a command");
				System.out.println();
			}
			
		}
		
	}

}
