public class Kalaha
{
	private Node board[];

	public Kalaha()
	{
		createBoard();
	}

		//makes a move from specified ambo, as specified player. returns next player to make a move.
	public int sow(int ambo, int player)
	{
		int start = ambo;
		int n = 0;
		Node current;

		if(player == 2)
		{
			start +=7;
		}

		n = board[start].getPebbles();
		board[start].setPebbles(0);
		current = board[start];

		for(int i=n; i>0; i--)
		{
			current = current.getNext();
			if((current.getPlayer() != player) && (current.getType().equals("Kalah") ) )
			{
				current = current.getNext();
			}

			current.incPebbles();
		}

		if( (current.getPlayer() == player) && (current.getType().equals("Ambo") ) && (current.getPebbles() == 1) && (current.getOpposite().getPebbles() > 0) )
		{
			int total = current.getPebbles() + current.getOpposite().getPebbles();
			current.setPebbles(0);
			current.getOpposite().setPebbles(0);
			if(player == 1)
			{
				board[7].setPebbles( board[7].getPebbles() + total);
			}
			if(player == 2)
			{
				board[0].setPebbles( board[0].getPebbles() + total);
			}
			return next(player);
		}

		if( (current.getPlayer() == player) && (current.getType().equals("Kalah") ) )
		{
			return player;
		}
		return next(player);
	}

		//returns true if someone has won, false if not.
	public boolean isWon()
	{
		int sum = 0;

		for(int i=1; i<7; i++)
		{
			sum += board[i].getPebbles();
		}

		if(sum == 0)
		{
			for(int i=8; i<14; i++)
			{
				sum += board[i].getPebbles();
				board[i].setPebbles(0);
			}
			board[0].setPebbles( board[0].getPebbles() + sum);
			return true;
		}

		sum = 0;

		for(int i=8; i<14; i++)
		{
			sum += board[i].getPebbles();
		}

		if(sum == 0)
		{
			for(int i=1; i<7; i++)
			{
				sum += board[i].getPebbles();
				board[i].setPebbles(0);
			}
			board[7].setPebbles( board[7].getPebbles() + sum);
			return true;
		}
		return false;
	}

		//returns what player has the most pebbles, 0 if its a tie, -1 if something fucked up.
	public int getWinner()
	{
		if(board[0].getPebbles() < board[7].getPebbles() )
		{
			return 1;
		}
		else if(board[0].getPebbles() > board[7].getPebbles() )
		{
			return 2;
		}
		else if(board[0].getPebbles() == board[7].getPebbles() )
		{
			return 0;
		}
		return -1;
	}

		//tooggles between 1 and 2.
	public int next(int prev)
	{
		if(prev == 1)
			return 2;
		else
			return 1;
	}

		//returns how many pebbles that are in one ambo
	public int getAmboSize(int _player, int _ambo)
	{
		int ret = _ambo;
		if(_player == 2)
		{
			ret += 7;
		}

		return (board[ret].getPebbles() );
	}

		//creates a board, fills it with pebbles and connects the links.
	public void createBoard()
	{
		board = new Node[14];

		board[0] = new Node("Kalah", 2, 0);
		board[7] = new Node("Kalah", 1, 0);

		for(int i=1; i<7; i++)
		{
			board[i] = new Node("Ambo", 1, 6);
		}

		for(int i=8; i<14; i++)
		{
			board[i] = new Node("Ambo", 2, 6);
		}

		for(int i=0; i<13; i++)
		{
			board[i].setNext(board[i+1]);
		}
		board[13].setNext(board[0]);



		board[1].setOpposite(board[13]);
		board[13].setOpposite(board[1]);

		board[2].setOpposite(board[12]);
		board[12].setOpposite(board[2]);

		board[3].setOpposite(board[11]);
		board[11].setOpposite(board[3]);

		board[4].setOpposite(board[10]);
		board[10].setOpposite(board[4]);

		board[5].setOpposite(board[9]);
		board[9].setOpposite(board[5]);

		board[6].setOpposite(board[8]);
		board[8].setOpposite(board[6]);
	}

		//returns the string representation of the class
	public String toString()
	{
		String ret = new String();

		for(int i=0; i<14; i++)
		{
			ret = ""+ ret + board[i]+ ";";
		}

		return ret;
	}

		//prints what the board looks like now.
	public void printBoard()
	{
		print("   ");

		for(int i=13; i>7; i--)
		{
			printNode(board[i]);
		}

		print("\n");
		printNode(board[0]);
		print("                      ");
		printNode(board[7]);
		print("\n   ");

		for(int i=1; i<7; i++)
		{
			printNode(board[i]);
		}

		println("\n\n ===========================\n");
	}

		//prints the amount of pebbles in a node, with a fixed width of 3.
	public void printNode(Node node)
	{
		String out = new String(""+node.getPebbles() );
		int size = out.length();

		if(size == 3)
		{
			print(""+node.getPebbles()+" ");
		}
		if( size == 2)
		{
			print(" "+node.getPebbles()+" ");
		}
		if( size == 1)
		{
			print("  "+node.getPebbles()+" ");
		}
	}

		//prints stuff
	public void println(Object o)
	{
		System.out.println(""+o);
	}

		//prints stuff
	public void print(Object o)
	{
		System.out.print(""+o);
	}
}