import java.io.*;
import java.net.*;

public class ServerKalaha
{
	private Kalaha game;

	private int port;

	private int players;
	private int playerTurn;

		//handles commandline arguments and starts the application
	public static void main(String[] args) throws Exception
	{
		if(args.length != 1)
		{
			System.out.println("Syntax: ServerKalaha [port]");
			System.exit(0);
		}
		ServerKalaha app = new ServerKalaha(Integer.parseInt(args[0]) );
	}

		//initializes the server and handles incoming connections
	public ServerKalaha(int _port) throws Exception
	{
		port = _port;
		players = 0;
		playerTurn = 1;

		game = new Kalaha();

		ServerSocket serverSocket = null;

		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch(Exception IO)
		{
			System.out.println("Could not listen on port "+port+".");
		}

		while(true)
		{
			new ServerThread(serverSocket.accept() ).start();
		}
	}

	private class ServerThread extends Thread
	{
		private Socket socket;

		public ServerThread(Socket _socket)
		{
			this.socket = _socket;
		}

			//parses the input and returns what the board looks like after the move, or returns an error.
		public String move(String _in)
		{
			String cmd[] = _in.split(" ");
			int _move;
			int _player;

			if(cmd.length != 3)
			{
				return "ERROR ARGLENGTH_NOT_VALID";
			}

			try
			{
				_move = Integer.parseInt(cmd[1]);
				_player = Integer.parseInt(cmd[2]);
			}
			catch(NumberFormatException exc_move)
			{
				return "ERROR ARGTYPE_NOT_VALID";
			}

			if(_player != playerTurn)
			{
				return "ERROR PLAYER_OUT_OF_TURN";
			}

			if(game.getAmboSize(_player, _move) == 0)
			{
				return "ERROR AMBO_EMPTY";
			}

			playerTurn = game.sow(_move, _player);

			if(game.isWon() )
			{
				return ""+ game + (0 - game.getWinner() );
			}


			return ""+ game + playerTurn;
		}

			//handles the input/output to and from the server.
		public void run()
		{
			try
			{
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream() ) );

				String input, output;

				while ((input = in.readLine() ) != null)
				{
					if(input.equals("HELLO") )
					{
						if(players >= 2)
						{
							output = new String("ERROR GAME_FULL");
						}
						else
						{
							players++;
							output = new String("HELLO "+ players);
						}
					}
					else if(input.equals("BOARD") )
					{
						output = ""+ game + playerTurn;
						game.printBoard();
					}
					else if(input.startsWith("MOVE") )
					{
						if(players != 2)
						{
							output = new String("ERROR GAME_NOT_FULL");
						}
						else
						{
							output = move(input);
							game.printBoard();
						}
						game.printBoard();
					}
					else if(input.equals("PLAYER") )
					{
						if(players != 2)
						{
							output = new String("ERROR GAME_NOT_FULL");
						}
						else
						{
							output = ""+ playerTurn;
						}
					}
					else if(input.equals("NEW") )
					{
						if(players != 2)
						{
							output = new String("ERROR GAME_NOT_FULL");
						}
						else
						{
							game = new Kalaha();
							playerTurn = 1;
							output = ""+ game + playerTurn;
						}
					}
					else if(input.equals("WINNER") )
					{
						if(game.isWon() )
						{
							output = new String(""+game.getWinner() );
						}
						else
						{
							output = new String("-1");
						}
					}
					else
					{
						output = new String("ERROR CMD_NOT_FOUND");
					}

					out.println(output);
				}
				out.close();
				in.close();
				socket.close();
			}
			catch(Exception IO)
			{
				System.out.println("Someone Disconnected.");
			}
		}
	}

		//prints stuff
	public void print(Object o)
	{
		System.out.println(""+o);
	}
}