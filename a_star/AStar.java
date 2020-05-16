import java.io.*;
import java.util.*;

class AStar
{
	PriorityQueue<StateNode> frontier = new PriorityQueue<StateNode>(100, (StateNode s1, StateNode s2) -> (s1.fval() - s2.fval()));
	StateNode goal;
	char[][] map;
	StateNode[][] bestPath;
	boolean goDiag;

	public static void main(String[] args)
	{
		try 
			{ if (args.length == 1 || args.length == 2) new AStar().run(args); }
		catch (Exception e)
		{
			System.err.println("Usage: java AStar <map file name> <optional: diagonal flag>");
			e.printStackTrace();
		}
	}

	public void run(String[] args) throws FileNotFoundException, IOException
	{
		String fileName;
		goDiag = (args.length == 2);
		if (args.length == 1 || args[1].equals("-d")) fileName = args[0];
		else if (args[0].equals("-d")) fileName = args[1];
		else throw new IllegalArgumentException();

		final long startTime = System.currentTimeMillis();

		// Process file into a 2-dimensional array. Goal recorded, and start point added as first frontier.
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> input = new ArrayList<String>();
		for (String s = reader.readLine(); s != null; s = reader.readLine()) input.add(s);
		reader.close();
		map = new char[input.size()][];
		bestPath = new StateNode[input.size()][];
		for (int i = 0; i < map.length; i++)
		{
			map[i] = input.get(i).toCharArray();
			bestPath[i] = new StateNode[input.get(i).length()];
			for (int j = 0; j < map[i].length; j++)
			{
				if (map[i][j] == 'G') goal = new StateNode(null, 0, j, i);
				if (map[i][j] == 'S') frontier.add(new StateNode(null, 0, j, i));
			}
		}

		// Find the shortest path to the goal. If the immediate top candidate in frontier does not reach the goal, 
		// then it adds subsequent states to the frontier to be processed.
		StateNode result;
		for (result = frontier.poll(); result != null; result = frontier.poll())
			if (result.reachedGoal()) break;
		
		// Draw the path on the map, and output the result.
		result.prev.showOnMap();
		for (char[] row : map)
			System.out.println(new String(row));
		System.out.println("Shortest path took " + result.cost + " steps");

		final long endTime = System.currentTimeMillis();
		System.out.println("Program took " + (endTime - startTime) + " milliseconds to run.");
	}

	/**
	 * Structure-wise, each node functions also functions as a node of a reverse-linked list.
	 */
	class StateNode
	{
		StateNode prev;
		double cost;
		int x, y;

		public StateNode(StateNode prev, double cost, int x, int y)
		{
			this.prev = prev;
			this.cost = cost;
			this.x = x;
			this.y = y;
		}

		public boolean reachedGoal()
		{
			// Goal reached, return true
			if (distanceTo(goal) == 0) return true;
			// Only add further states if this isn't looping. Otherwise, let it die
			if (!this.isLooping())
				for (StateNode state : getAdjacent())
					// Allowable paths are only empty or the goal. Only enqueue if the most efficient way to that point.
					if ((map[state.y][state.x] == ' ' || map[state.y][state.x] == 'G') && (bestPath[state.y][state.x] == null || bestPath[state.y][state.x].cost > state.cost))
					{
						frontier.add(state);
						bestPath[state.y][state.x] = state;
					}
			return false;
		}

		public void showOnMap()
		{
			map[y][x] = (prev == null) ? 'S' : '.';
			if (prev != null) prev.showOnMap();
		}

		public List<StateNode> getAdjacent()
		{
			List<StateNode> returnList = new ArrayList<StateNode>();
			returnList.add(new StateNode(this, this.cost + 1, x - 1, y));
			returnList.add(new StateNode(this, this.cost + 1, x, y - 1));
			returnList.add(new StateNode(this, this.cost + 1, x + 1, y));
			returnList.add(new StateNode(this, this.cost + 1, x, y + 1));
			if (goDiag)
			{
				returnList.add(new StateNode(this, this.cost + Math.sqrt(2), x + 1, y + 1));
				returnList.add(new StateNode(this, this.cost + Math.sqrt(2), x + 1, y - 1));
				returnList.add(new StateNode(this, this.cost + Math.sqrt(2), x - 1, y + 1));
				returnList.add(new StateNode(this, this.cost + Math.sqrt(2), x - 1, y - 1));
			}
			return returnList;
		}

		private boolean isLooping()
		{
			for (StateNode temp = prev; temp != null; temp = temp.prev)
				if (this.distanceTo(temp) == 0) return true;
			return false;
		}

		private int fval()
		{ return (int)((cost + distanceTo(goal)) * 100); }

		public double distanceTo(StateNode other)
		{ return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)); }
	}
}