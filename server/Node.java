public class Node
{
	private String type;
	private int player;
	private int pebbles;
	private Node opposite;
	private Node next;

	public Node(String _type, int _player, int _pebbles)
	{
		type = new String(_type);
		player = _player;
		pebbles = _pebbles;
	}

	public String toString()
	{
		return ""+pebbles;
	}

	public void setNext(Node _next)
	{
		next = _next;
	}

	public Node getNext()
	{
		return next;
	}

	public void setPlayer(int _player)
	{
		player = _player;
	}

	public int getPlayer()
	{
		return player;
	}

	public void setOpposite(Node _opposite)
	{
		opposite = _opposite;
	}

	public Node getOpposite()
	{
		return opposite;
	}

	public void setType(String _type)
	{
		type = new String(_type);
	}

	public String getType()
	{
		return type;
	}

	public void incPebbles()
	{
		pebbles++;
	}

	public void setPebbles(int n)
	{
		pebbles = n;
	}

	public int getPebbles()
	{
		return pebbles;
	}

}