package de.impelon.math;

import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * <p> Used for various math-related operations. </p>
 * 
 * @author Impelon
 * 
 */

public class MathUtils {
	
	/**
	 * <p> Parses a mathematical expression in the form of a String with given variables in a Map. </p>
	 * <p><b> Do not allow user input on this. Since this executes the passed String as JavaScript it is possible to abuse this with code-injection. </b></p>
	 * 
	 * @param expression the mathematical expression
	 * @param variables a map of variables (identifier, object) used in the expression
	 * @return the result
	 */
	public static double evaluateExpression(String expression, Map<String, Object> variables) {
		StringBuilder expr = new StringBuilder(expression);
		for (Entry<String, Object> e : variables.entrySet())
			expr.insert(0, "var " + e.getKey() + " = " + e.getValue() + ";");
		return evaluateExpression(expr.toString());
	}
	
	/**
	 * <p> Parses a mathematical expression in the form of a String. </p>
	 * <p><b> Do not allow user input on this. Since this executes the passed String as JavaScript it is possible to abuse this with code-injection. </b></p>
	 * 
	 * @param expression the mathematical expression
	 * @return the result
	 */
	public static double evaluateExpression(String expression) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine jse = manager.getEngineByName("JavaScript");
        try {
            return Double.parseDouble(jse.eval(expression).toString());
        } catch (ScriptException e) {
        	e.printStackTrace();
            return 0;
        }
    }

}
