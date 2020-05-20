import java.util.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

class Fours
{

	static List<StateNode> results = new ArrayList<StateNode>();
	static double target;
	static int maxDepth;
	Queue<StateNode> queue = new LinkedList<StateNode>();

	public static void main(String[] args)
	{ new Fours().run(args); }
	
	public void run(String[] args)
	{
		try
		{
			target = Double.parseDouble(args[0]);
			maxDepth = Integer.parseInt(args[1]);
			queue.add(new StateNode("4", true, 0));
			for (StateNode currNode = queue.remove(); currNode != null; currNode = queue.remove())
			{
				if (currNode.depth > maxDepth) break;
				currNode.run();
			}
			
			if (results.isEmpty()) System.out.println("No solution found, try increasing depth.");
			else 
			{
				System.out.println("Shortest solutions:");
				for (StateNode node : results) System.out.println(node.expression + " = " + node.parseExpression());
			}
		}
		catch (Exception e) { System.err.println("Usage: java Fours <target value> <expression depth>"); e.printStackTrace(); }
	}
	//#################################################################################################################//
	//Inner class starts here
	class StateNode
	{
		String expression;
		boolean addDecimal;
		int depth;

		public StateNode(String expr, boolean addDec, int currDepth)
		{
			expression = expr;
			addDecimal = addDec;
			depth = currDepth;
		}

		public void run() throws ScriptException
		{
			System.out.println("currently tested expression " + expression);
			if (this.parseExpression() == target)
			{
				System.out.println("matched target found");
				results.add(this);
				// If this is a solution, the while loop above should only look for 
				// other solutions of equal length as this, and not any longer solutions.
				maxDepth = depth;
			}
			
			//next set of states added to queue with incremented depth
			queue.add(new StateNode(expression + "+4", true, ++depth));
			queue.add(new StateNode(expression + "-4", true, depth));
			queue.add(new StateNode(expression + "*4", true, depth));
			queue.add(new StateNode(expression + "/4", true, depth));
			queue.add(new StateNode(expression + "^4", true, depth));

			// avoid expressions such as "4.44.4", "(4)4" and "((4))"
			if (addDecimal) queue.add(new StateNode(expression + ".4", false, depth));
			if(expression.charAt(expression.length() - 1) != ')')
			{
				queue.add(new StateNode(expression + "4", addDecimal, depth));
				queue.add(new StateNode("("+ expression + ")", false, depth));
			} 
		}

		public double parseExpression() throws ScriptException
		{
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			String output = engine.eval(expression).toString();
			System.out.println(output);
			return Double.parseDouble(output);
			
		}
	}
	//###################END of INNER CLASS 
}//################END of FOURS CLASS