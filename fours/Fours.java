import java.util.*;

class Fours
{
	static List<String> results = new ArrayList<String>();
	static double target;
	static int maxDepth;
	int currDepth;
	String expression;
	boolean addDecimal;
	public static void main(String[] args)
	{
		try
		{
			target = Double.parseDouble(args[0]);
			maxDepth = Integer.parseInt(args[1]);
			new Fours("4", true, 0).run();
			for (String s : results)
				System.out.println(s);
		}
		catch (Exception e) { System.err.println("Usage: java Fours <target value> <expression depth>"); }
	}

	public Fours(String expr, boolean addDec, int depth)
	{
		expression = expr;
		addDecimal = addDec;
		currDepth = depth;
	}

	public void run()
	{
		System.out.println(expression);
		if (this.matchesTarget()) results.add(expression);

		if (currDepth == maxDepth) return;

		currDepth++;
		int i = 0;
		Fours[] nextStates = new Fours[8];
		nextStates[i++] = new Fours(expression + "+4", true, currDepth);
		nextStates[i++] = new Fours(expression + "-4", true, currDepth);
		nextStates[i++] = new Fours(expression + "*4", true, currDepth);
		nextStates[i++] = new Fours(expression + "/4", true, currDepth);
		nextStates[i++] = new Fours(expression + "**4", true, currDepth);
		nextStates[i++] = new Fours("("+ expression + ")", false, currDepth);
		if (addDecimal) nextStates[i++] = new Fours(expression + ".4", false, currDepth);
		if (expression.charAt(expression.length() - 1) != ')') nextStates[i++] = new Fours(expression + "4", addDecimal, currDepth);
		try
		{
			for (Fours state : nextStates)
				state.run();
		}
		catch (Exception e) { }
	}

	public boolean matchesTarget()
	{
		// TODO Implement parser
		return false;
	}
}