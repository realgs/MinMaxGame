using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Threading;
using System.Net.Sockets;

/*
	Windows C# client for playing a Kalaha game using the Kalaha game server.
	
	Author: Johan Hagelbäck (jhg@bth.se)
*/
namespace KalahaClient
{
    class Program
    {
        private StreamReader reader;
        private StreamWriter writer;
        private char[] split = { ';', ' ' };
        private int player = 0;

        static void Main(String[] args)
        {
            if (args.Length == 0) {
			    Program prg = new Program("localhost", 8888);
		    }
		    else if (args.Length == 2) {
			    try
                {
				    Program prg = new Program(args[0], System.Convert.ToInt32(args[1]));
			    }
			    catch
                {
				   Console.WriteLine("Usage: java Spectator [host] [port]");	
			    }
		    }
		    else
            {
                Console.WriteLine("Usage: java Spectator [host] [port]");
		    }
        }

        public Program(String host, int port)
        {
            try
            {
                //Init the sockets
                TcpClient socket = new TcpClient(host, port);
                NetworkStream mStream = socket.GetStream();
                reader = new StreamReader(mStream);
                writer = new StreamWriter(mStream);

                //Connect to a game
                writer.WriteLine("HELLO");
                writer.Flush();
                String response = reader.ReadLine();
                
                //Parse player number
                String[] tokens = response.Split(split);
                player = System.Convert.ToInt32(tokens[1]);
                Console.WriteLine("Connected to Kalaha server as player " + player);
                if (player != 1 && player != 2)
                {
                    Console.WriteLine("Invalid player number: " + player);
                    throw new Exception();
                }

                GameLoop();
            }
            catch
            {
                Console.WriteLine("Unable to connect to KalahaServer at " + host + ":" + port);
            }

            //Pause before exit
            Console.WriteLine("Press any key to exit client program.");
            Console.ReadKey();
        }

        private void GameLoop()
        {           
            bool gameRunning = true;

            while (gameRunning)
            {
                try
                {
                    //Check who's turn it is
                    writer.WriteLine("PLAYER");
                    writer.Flush();
                    String response = reader.ReadLine();
                    int cPlayer = System.Convert.ToInt32(response);

                    if (cPlayer == player)
                    {
                        //Your turn, make a move
                        MakeMove();
                    }
                }
                catch
                {
                    //Do nothing
                }

                Thread.Sleep(1000);
            }
        }

        private void MakeMove()
        {
            //Get current board
            writer.WriteLine("BOARD");
            writer.Flush();
            String response = reader.ReadLine();

            //Print the board
            Console.WriteLine(MakeBoardStr(response));

            try
            {
                //Ask the player for his move
                Console.WriteLine("\nYou are next! make a move.");
                Console.Write("[1,2,3,4,5,6] > ");
                int myMove = System.Convert.ToInt32(Console.ReadLine());

                //Send a move command to the Kalaha server
                SendMoveCmd(myMove);
            }
            catch
            {
                Console.WriteLine("Invalid move");
                MakeMove();
            }

        }

        private void SendMoveCmd(int myMove)
        {
            //Generate and send a move command
            String cmd = "MOVE " + myMove + " " + player;
            writer.WriteLine(cmd);
            writer.Flush();
            String response = reader.ReadLine();
        }

        private String MakeBoardStr(String response)
        {
            String[] tokens = response.Split(split);

            //Generate a nice output of the board.
	        String board = "\n[2]";

	        for(int i = 13; i > 7; i--)
            {
		        board += MakeSpaces(tokens[i]);
	        }

	        board += "\n" + MakeSpaces(tokens[0]) + "                  " + MakeSpaces(tokens[7]) + "\n" + "[1]";

	        for(int i = 1; i < 7; i++)
            {
		        board += MakeSpaces(tokens[i]);
	        }

	        return board;
        }

        private String MakeSpaces(String str)
        {
            String res = "";

            //Formats a number (0-99) to a nice string.
            if (str.Length == 2)
            {
                res = " " + str;
            }
            else if (str.Length == 1)
            {
                res = "  " + str;
            }
            else
            {
                res = str;
            }

            return res;
        }
    }
}
