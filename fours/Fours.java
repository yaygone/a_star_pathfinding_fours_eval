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
			maxDepth = (args.length == 2) ? Integer.parseInt(args[1]) : Integer.MAX_VALUE;
			if (args.length > 2) throw new IllegalArgumentException();

			System.out.println("\nLooking for expressions matching " + args[0]);
			queue.add(new StateNode("4", true, 0));
			for (StateNode currNode = queue.remove(); currNode != null; currNode = queue.remove())
			{
				if (currNode.depth > maxDepth) break;
				currNode.run();
			}
			
			if (results.isEmpty()) System.out.println("No solution found, try increasing depth limit.");
			else 
			{
				System.out.println("Shortest solutions:");
				for (StateNode node : results) System.out.println(node.expression + " = " + args[0]);
			}
		}
		catch (StackOverflowError e) { System.err.println("Run out of memory! Try again with a larger JVM heap allocation."); }
		catch (Exception e) { System.err.println("Usage: java Fours <target value> <optional: state machine depth limit>"); }
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
			if (this.evaluate() == target)
			{
				results.add(this);
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

		public double evaluate()
		{
			return new Object()
			{
				int position = -1, currChar;
		
				void nextChar()
				{ currChar = (++position < expression.length()) ? expression.charAt(position) : -1; }

				boolean checkFor(int matchChar)
				{
					if (currChar != matchChar) return false;
					nextChar();
					return true;
				}
				double parse()
				{
					nextChar();
					double x = parseExpression();
					if (position < expression.length()) throw new RuntimeException("Unexpected: " + (char)currChar);
					return x;
				}
		
				double parseExpression()
				{
					double expr = parseTerm();
					while (true)
					{
						if (checkFor('+')) expr += parseTerm();
						else if (checkFor('-')) expr -= parseTerm();
						else return expr;
					}
				}
		
				double parseTerm()
				{
					double term = parseWord();
					while (true)
					{
						if (checkFor('*')) term *= parseWord();
						else if (checkFor('/')) term /= parseWord();
						else return term;
					}
				}

				double parseWord()
				{
					double word = parseFactor();
					while (true)
					{
						if (checkFor('^')) word = Math.pow(word, parseFactor());
						else return word;
					}
				}
		
				double parseFactor()
				{
					double factor = 0;
					int startPos = position;
					if (checkFor('('))
					{
						factor = parseExpression();
						checkFor(')');
					}
					else while (currChar == '4' || currChar == '.')
					{
						nextChar();
						factor = Double.parseDouble(expression.substring(startPos, position));
					}

					return factor;
				}
			}.parse();
		} 
	}
}