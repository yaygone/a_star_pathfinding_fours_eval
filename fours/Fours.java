import java.util.*;
import java.io.*;
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
				for (StateNode node : results) System.out.println(node.expression + " = " + node.eval(node.expression));
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
				queue.add(new StateNode(expression + "4", addDecimal, ++depth));
				queue.add(new StateNode("("+ expression + ")", false, ++depth));
			} 
		}

		public boolean matchesTarget() throws ScriptException
		{
			Double evaluatedValue = eval(expression);
			//System.out.println("Expression found " + evaluatedValue);
			//System.out.println(evaluatedValue == target);
			return evaluatedValue == target; 

			/* double evalutatedValue = eval(expression);
			System.out.println("Expression found " + Double.toString(evalutatedValue));
			return evalutatedValue == target; */
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
 		/* private double parseExpression(String expr) throws ScriptException
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			String [] pieces;
			if (expr.contains("^"))
			{
				pieces = expr.split("^");

				double expressionValue = Math.pow(Double.parseDouble(engine.eval(pieces[0]).toString()), Double.parseDouble(engine.eval(pieces[1]).toString()));
				for(int i = 2; i < pieces.length; i++)
				{
					expressionValue = Math.pow(expressionValue, Double.parseDouble(engine.eval(pieces[i]).toString()));
				}
				return expressionValue;
			}
			System.out.println(engine.eval(expr));
			String something = engine.eval(expr).toString();
			double expressionValue = Double.parseDouble(something);
			return expressionValue;
		}  */


		public double eval(final String str) {
			return new Object() {
				int pos = -1, ch;
		
				void nextChar() {
					ch = (++pos < str.length()) ? str.charAt(pos) : -1;
				}
				boolean eat(int charToEat) {
					while (ch == ' ') nextChar();
					if (ch == charToEat) {
						nextChar();
						return true;
					}
					return false;
				}		
				double parse() {
					nextChar();
					double x = parseExpression();
					if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
					return x;
				}
		
				// Grammar:
				// expression = term | expression `+` term | expression `-` term
				// term = factor | term `*` factor | term `/` factor
				// factor = `+` factor | `-` factor | `(` expression `)`
				//        | number | functionName factor | factor `^` factor
		
				double parseExpression() {
					double expression = parseTerm();
					for (;;) {
						if      (eat('+')) expression += parseTerm(); // addition
						else if (eat('-')) expression -= parseTerm(); // subtraction
						else return expression;
					}
				}
		
				double parseTerm() {
					double term = parseFactor();
					for (;;) {
						if      (eat('*')) term *= parseFactor(); // multiplication
						else if (eat('/')) term /= parseFactor(); // division
						else return term;
					}
				}
		
				double parseFactor() {
					if (eat('+')) return parseFactor(); // unary plus
					if (eat('-')) return -parseFactor(); // unary minus
		
					double factor;
					int startPos = this.pos;
					if (eat('(')) { // parentheses
						factor = parseExpression();
						eat(')');
					} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
						while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
						factor = Double.parseDouble(str.substring(startPos, this.pos));
					} else if (ch >= 'a' && ch <= 'z') { // functions
						while (ch >= 'a' && ch <= 'z') nextChar();
						String func = str.substring(startPos, this.pos);
						factor = parseFactor();
						if (func.equals("sqrt")) factor = Math.sqrt(factor);
						else if (func.equals("sin")) factor = Math.sin(Math.toRadians(factor));
						else if (func.equals("cos")) factor = Math.cos(Math.toRadians(factor));
						else if (func.equals("tan")) factor = Math.tan(Math.toRadians(factor));
						else throw new RuntimeException("Unknown function: " + func);
					} else {
						throw new RuntimeException("Unexpected: " + (char)ch);
					}
		
					if (eat('^')) factor = Math.pow(factor, parseFactor()); // exponentiation
		
					return factor;
				}
			}.parse();
		} 
		
	}
	//###################END of INNER CLASS 
}//################END of FOURS CLASS