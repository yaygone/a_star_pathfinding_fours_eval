import java.io.*;
import java.util.*;

class AStar
{
	PriorityQueue<StateNode> frontier = new PriorityQueue<StateNode>(100, (StateNode s1, StateNode s2) -> (s1.fval() - s2.fval()));
	Coord goal;
	char[][] map;

	public static void main(String[] args)
	{ try { if (args.length == 1) new AStar().run(args[0]); } catch (Exception e) { e.printStackTrace(); } }

	public void run(String fileName) throws FileNotFoundException, IOException
	{
		// Process file into a 2-dimensional array. Goal recorded, and start point added as first frontier.
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> input = new ArrayList<String>();
		for (String s = reader.readLine(); s != null; s = reader.readLine()) input.add(s);
		reader.close();
		map = new char[input.size()][];
		for (int i = 0; i < map.length; i++)
		{
			map[i] = input.get(i).toCharArray();
			for (int j = 0; j < map[i].length; j++)
			{
				if (map[i][j] == 'G') goal = new Coord(j, i);
				if (map[i][j] == 'S') frontier.add(new StateNode(null, 0, new Coord(j, i)));
			}
		}

		// Find the shortest path to the goal. If the immediate top candidate in frontier does not reach the goal, 
		// then it adds subsequent states to the frontier to be processed.
		StateNode result;
		for (result = frontier.poll(); result != null; result = frontier.poll())
			if (result.reachedGoal()) break;
		
		// Draw the path on the map, and output the result.
		result.changeMap(true);
		for (char[] row : map)
			System.out.println(new String(row));
		System.out.println("Shortest path took " + result.cost + " steps");
	}

	/**
	 * Structure-wise, each node functions also functions as a node of a reverse-linked list.
	 */
	class StateNode
	{
		StateNode prev;
		Coord val;
		int cost;

		public StateNode(StateNode prev, int cost, Coord val)
		{
			this.prev = prev;
			this.cost = cost;
			this.val = val;
		}

		public boolean reachedGoal()
		{
			// Goal reached, return true
			if (val.dist(goal) == 0) return true;
			// Only add further states if this isn't looping. Otherwise, let it die
			if (!this.isLooping())
				for (Coord c : this.val.getAdjacent())
					// Allowable paths are only empty or the goal
					if (map[c.y][c.x] == ' ' || map[c.y][c.x] == 'G')
						frontier.add(new StateNode(this, this.cost + 1, c));
			return false;
		}

		public void changeMap(boolean isGoal)
		{
			map[val.y][val.x] = isGoal ? 'G' : '.';
			if (prev != null) prev.changeMap(false);
			else map[val.y][val.x] = 'S';
		}

		private boolean isLooping()
		{
			for (StateNode temp = prev; temp != null; temp = temp.prev)
				if (this.val.dist(temp.val) == 0) return true;
			return false;
		}

		private int fval()
		{ return cost + goal.dist(val); }
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
		{ return new Coord[] { new Coord(x - 1, y), new Coord(x, y - 1), new Coord(x + 1, y), new Coord(x, y + 1) }; }

		public int dist(Coord other)
		{ return Math.abs(this.x - other.x) + Math.abs(this.y - other.y); }
	}
}