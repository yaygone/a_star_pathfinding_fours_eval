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
				for (StateNode node : results) System.out.println(node.expression + " = " + node.parseExpression(node.expression));
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
			if (this.matchesTarget())
			{
				System.out.println("matched target found");
				results.add(this);
				// If this is a solution, the while loop above should only look for 
				// other solutions of equal length as this, and not any longer solutions.
				maxDepth = depth;
			}
				
			//next set of states added to queue with incremented depth
			queue.add(new StateNode(expression + "+4", true, ++depth));
			queue.add(new StateNode(expression + "-4", true, ++depth));
			queue.add(new StateNode(expression + "*4", true, ++depth));
			queue.add(new StateNode(expression + "/4", true, ++depth));
			queue.add(new StateNode(expression + "^4", true, ++depth));

			// avoid expressions such as "4.44.4", "(4)4" and "((4))"
			if (addDecimal) queue.add(new StateNode(expression + ".4", false, depth));
			if(expression.charAt(expression.length() - 1) != ')')
			{
				queue.add(new StateNode(expression + "4", addDecimal, depth));
				queue.add(new StateNode("("+ expression + ")", false, depth));
			} 
		}

		public boolean matchesTarget() throws ScriptException
		{
			Double evaluatedValue = parseExpression(expression);
			//System.out.println("Expression found " + evaluatedValue);
			//System.out.println(evaluatedValue == target);
			return evaluatedValue == target;
			
		}

		/**
		 * 	E -> T
			  -> T+E
			  -> T-E
			T -> F
			  -> F*T
			  -> F/T
			W -> numVal
			  -> numVal^E
			  -> (E)
		 * @param expression
		 * @return
		 */

		private double parseExpression(String expr) throws ScriptException
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			System.out.println(engine.eval(expr));
			String something = engine.eval(expr).toString();
			double expressionValue = Double.parseDouble(something);
			return expressionValue;
		} 
		
	}
	//###################END of INNER CLASS 
}//################END of FOURS CLASS