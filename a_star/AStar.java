import java.io.*;
import java.util.*;

class AStar
{
	PriorityQueue<StateNode> frontier = new PriorityQueue<StateNode>(
		100, (StateNode s1, StateNode s2) -> (s1.fval() - s2.fval())
	);
	Coord goal;
	char[][] map;

	public static void main(String[] args)
	{ try { new AStar().run(args[0]); } catch (Exception e) { System.err.println("Exception: " + e.getMessage()); } }

	public void run(String fileName) throws FileNotFoundException, IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> input = new ArrayList<String>();
		for (String s = reader.readLine(); s != null; s = reader.readLine())
			input.add(s);
		reader.close();
		map = new char[input.size()][];
		int xrange = input.get(0).length() - 1;
		for (int i = 0; i < map.length; i++)
			map[i] = Arrays.copyOfRange(input.get(i).toCharArray(), 1, xrange);
	}

	class StateNode
	{
		StateNode next;
		int cost;
		Coord val;

		public int fval()
		{ return cval() + hval(); }

		public int cval()
		{ return next == null ? cost : cost + next.cval(); }

		public int hval()
		{ return goal.dist(val); }
	}

	class Coord
	{
		int x, y;

		public Coord(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int dist(Coord other)
		{ return Math.abs(this.x - other.x) + Math.abs(this.y - other.y); }
	}
}