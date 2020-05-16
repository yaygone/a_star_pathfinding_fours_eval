import java.util.*;

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

		public void run()
		{
			// System.out.println(expression);
			if (this.matchesTarget())
			{
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
			if (expression.charAt(expression.length() - 1) != ')')
			{
				queue.add(new StateNode(expression + "4", addDecimal, depth));
				queue.add(new StateNode("("+ expression + ")", false, depth));
			}
		}

		public boolean matchesTarget()
		{
			// TODO Implement parser
			return parseExpression(expression) == target;
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

		private double parseExpression(String expr)
		{
			int i = 0;
			for (; i < expr.length(); i++)
				if (expr.charAt(i) == '+' || expr.charAt(i) == '-')
					break;
			return (i == expr.length()) ? parseTerm(expr)
				                        : (expr.charAt(i) == '+') ? parseTerm(expr.substring(0, i)) + parseTerm(expr.substring(i + 1, expr.length()))
										                          : parseTerm(expr.substring(0, i)) + parseTerm(expr.substring(i + 1, expr.length()));
		}

		private double parseTerm(String term)
		{
			int i = 0;
			for (; i < term.length(); i++)
				if (term.charAt(i) == '*' || term.charAt(i) == '/')
					break;
			return (i == term.length()) ? parseFactor(term)
				                        : (term.charAt(i) == '*') ? parseFactor(term.substring(0, i)) * parseTerm(term.substring(i + 1, term.length()))
										                          : parseFactor(term.substring(0, i)) / parseTerm(term.substring(i + 1, term.length()));
		}

		private double parseFactor(String factor)
		{
			if (factor.charAt(0) == '(')
				return (factor.charAt(factor.length() - 1) == ')') ? parseExpression(factor.substring(1, factor.length())) : Double.NaN;

			int i = 0;
			for (; i < factor.length(); i++)
				if (factor.charAt(i) == '^')
					break;
			return (i == factor.length()) ? Double.parseDouble(factor)
			                              : Math.pow(Double.parseDouble(factor.substring(0, i)), parseExpression(factor.substring(i + 1, factor.length())));

		}
	}
}