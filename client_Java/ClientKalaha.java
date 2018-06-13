import java.io.*;
import java.net.*;

public class ClientKalaha
{
	private int player;
	private String host;
	private int port;
	private boolean ai;

		//sorts out commandline arguments and starts the application
	public static void main(String args[]) throws Exception
	{
		boolean aiFlag = false;
		if(args.length == 3)
		{
			if(args[2].toUpperCase().equals("AI") )
				aiFlag = true;
		}
		else if(args.length != 2)
		{
			System.out.println("Syntax: ClientKalaha [host] [port] [ai]");
			System.exit(0);
		}

		ClientKalaha app = new ClientKalaha(args[0], Integer.parseInt(args[1]), aiFlag );

	}
		//handles all the input/output.
	public ClientKalaha(String _host, int _port, boolean _ai) throws Exception
	{
		int next_player = 0;

		port = _port;
		host = _host;
		ai = _ai;

		String input;
		String reply;
		String output;
		BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in) );

		Socket socket = new Socket(host, port);
		PrintWriter outData = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader inData = new BufferedReader(new InputStreamReader(socket.getInputStream() ) );

		outData.println("HELLO");
		reply = inData.readLine();

		String parse[] = reply.split(" ");
		player = Integer.parseInt(parse[1]);

		print("You are player "+player+".");

		while(true)
		{
			outData.println("WINNER");
			reply = inData.readLine();

			if(reply.equals("1") || reply.equals("2") )
			{
				System.out.println("\n\nPlayer "+reply+" won!\n Press Enter to play again.");
				terminalInput.readLine();
				outData.println("NEW");
				reply = inData.readLine();
			}

			outData.println("PLAYER");
			reply = inData.readLine();
			try
			{
				next_player = Integer.parseInt(reply);
			}
			catch(NumberFormatException e)
			{
				next_player = 1;
			}

			if(next_player == player)
			{
				outData.println("BOARD");
				reply = inData.readLine();
				System.out.println(parseBoardToPrint(reply) + "\n");
				if(ai == false)
				{
					System.out.print("You are next! make a move.\n[1,2,3,4,5,6,N]  > ");
					input = terminalInput.readLine();
				}
				else
				{
					input = new String(""+getRandom() );
				}

				if(input.toUpperCase().startsWith("N") )
				{
					output = new String("NEW");
				}
				else if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5") || input.equals("6") )
				{
					output = new String("MOVE "+input+" "+player);
				}
				else
				{
					output = new String("BOARD");
				}

				outData.println(output);
				reply = inData.readLine();

				if(reply.startsWith("ERROR") )
				{
					System.out.println(reply);
				}
				else
				{
					System.out.println(parseBoardToPrint(reply) );
				}
			}
			else
			{
				outData.println("BOARD");
				reply = inData.readLine();
				System.out.println(parseBoardToPrint(reply) + "\n");
				if(player == 1)
				{
					System.out.println("\nWaiting for player 2, press enter to update.");
				}
				else
				{
					System.out.println("\nWaiting for player 1, press enter to update.");
				}
				terminalInput.readLine();
			}
		}
	}

		//returns a ranom number from 1 to 6.
	public int getRandom()
	{
		return 1+ (int) (Math.random()*6);
	}

		//returns the string with a fixed length of 3 chars.
	public String makeSpaces(String _in)
	{
		String _out = new String();

		if(_in.length() == 2)
		{
			_out = " "+_in;
		}
		else if(_in.length() == 1)
		{
			_out = "  "+_in;
		}
		else
		{
			_out = _in;
		}

		return _out;
	}

		//returns a formatted string ready to print out, directly from a string returned by the server.
	public String parseBoardToPrint(String _parse)
	{
		String board[] = _parse.split(";");
		String out = new String("\n[2]");

		for(int i=13; i>7; i--)
		{
			out += makeSpaces(board[i]);
		}

		out += "\n" + makeSpaces(board[0]) + "                  " + makeSpaces(board[7]) + "\n" + "[1]";

		for(int i=1; i<7; i++)
		{
			out += makeSpaces(board[i]);
		}
		return out;
	}
		//prints stuff.
	public void print(Object o)
	{
		System.out.println(""+o);
	}
}