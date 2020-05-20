import java.util.*;
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
				for (StateNode node : results) System.out.println(node.expression + " = " + node.eval());
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

		public boolean matchesTarget() throws ScriptException
		{
			Double evaluatedValue = eval();
			System.out.println("Expression found " + evaluatedValue);
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
		 * 
		 */
		public double eval()
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
					else
						while (currChar == '4' || currChar == '.')
						{
							nextChar();
							factor = Double.parseDouble(expression.substring(startPos, position));
						}

					return factor;
				}
			}.parse();
		} 
		
	}
	//###################END of INNER CLASS 
}//################END of FOURS CLASS