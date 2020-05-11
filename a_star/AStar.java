import java.io.*;
import java.util.*;

class AStar
{
	PriorityQueue<StateNode> frontier = new PriorityQueue<StateNode>(100, (StateNode s1, StateNode s2) -> (s1.fval() - s2.fval()));
	Coord goal;
	char[][] map;

	public static void main(String[] args)
	{ try { new AStar().run("map1.txt"); } catch (Exception e) { System.err.println("Exception: " + e.getMessage()); } }

	public void run(String fileName) throws FileNotFoundException, IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> input = new ArrayList<String>();
		for (String s = reader.readLine(); s != null; s = reader.readLine())
			input.add(s);
		reader.close();

		map = new char[input.size()][];
		for (int i = 0; i < map.length; i++)
		{
			map[i] = input.get(i).toCharArray();
			for (int j = 0; j < map[i].length; j++)
			{
				System.out.println("Passing through " + j + " " + i);
				if (map[i][j] == 'G') goal = new Coord(j, i);
				if (map[i][j] == 'S') frontier.add(new StateNode(null, new Coord(j, i)));
			}
		}

		StateNode result;
		for (result = frontier.poll(); result != null; result = frontier.poll())
			if (result.reachedGoal()) break;
		
		result.changeMap();
		for (char[] row : map)
		{
			System.out.print("\n");
			for (char c : row)
				System.out.print(c);
		}
	}

	/**
	 * Structure-wise, each node functions also functions as a node of a reverse-linked list.
	 */
	class StateNode
	{
		StateNode prev;
		Coord val;

		public StateNode(StateNode prev, Coord val)
		{
			this.prev = prev;
			this.val = val;
		}

		public boolean reachedGoal()
		{
			if (prev != null) System.out.println("Searching through chain (" + val.x + "," + val.y + ") " + prev.output());
			if (val.dist(goal) == 0) return true;
			if (!this.isLooping() && map[val.y][val.x] != ' ') for (Coord c : this.val.getAdjacent()) frontier.add(new StateNode(this, c));
			return false;
		}

		private String output()
		{
			String outputString = "(" + val.x + "," + val.y + ") ";
			if (prev != null) outputString += prev.output();
			return outputString;
		}

		public void changeMap()
		{
			map[val.y][val.x] = '.';
			if (prev != null) prev.changeMap();
		}

		private boolean isLooping()
		{
			if (prev == null) return false;
			for (StateNode temp = prev; temp != null; temp = prev.prev)
				if (this.val.dist(temp.val) == 0) return true;
			return prev.isLooping();
		}

		private int fval()
		{ return cval() + goal.dist(val); }

		private int cval()
		{ return prev == null ? 0 : 1 + prev.cval(); }
	}

	class Coord
	{
		int x, y;

		public Coord(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public Coord[] getAdjacent()
		{ return new Coord[] {new Coord(x - 1, y), new Coord(x, y - 1), new Coord(x + 1, y), new Coord(x, y + 1)}; }

		public int dist(Coord other)
		{ return Math.abs(this.x - other.x) + Math.abs(this.y - other.y); }
	}
}