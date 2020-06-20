// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;
import java.util.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template is based on the version in the lectures,
 *  which only handled + - * /, and did not do any checking
 *  for valid expressions
 * The program should handle a wider range of operators and
 *  check and report certain kinds of invalid expressions
 */

public class CPNCalculator {

    static List<String> operatorOrder = Arrays.asList("dist", "sin", "cos", "tan", "log", "ln", "sqrt", "^", "*", "/", "+", "-"); //Order of mathematical operations, used to know when brackets are needed
    static List<String> functions = Arrays.asList("dist", "sin", "cos", "tan", "log", "ln", "sqrt"); //List of operators that are functions

    /**
     * Main Read-evaluate-print loop
     * Reads an expression from the user then evaluates it and prints the result
     * Invalid expressions could cause errors when reading.
     * The try-catch prevents these errors from crashing the programe -
     * the error is caught, and a message printed, then the loop continues.
     */
    public static void main(String[] args) {
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true) {
            UI.println();
            String exprStr = UI.askString("expr:");
            //if(!checkBrackets(exprStr)) {
                //continue;
            //}
            Scanner sc = new Scanner(exprStr);
            try {
                GTNode<String> expr = readExpr(sc, true);
                double answer = evaluate(expr);
                if(!Double.isNaN(answer)) {
                    printExpr(expr); //Prints equation using normal infix
                    UI.println(" = " + answer);
                }
            } catch (Exception e) {
                UI.println("invalid expression" + e);
            }
        }
    }


    /**
     * Recursively construct expression tree from scanner input
     */
    public static GTNode<String> readExpr(Scanner sc, boolean first) {
        if (sc.hasNext("\\(")) {                     // next token is an opening bracket
            sc.next();                               // the opening (

            String op = sc.next();                   // the operator
            if(op.equals(")")) {
                UI.println("Empty brackets");
                return null;
            }
            GTNode<String> node = new GTNode<String>(op);
            while (!sc.hasNext("\\)")) {
                GTNode<String> child = readExpr(sc, false); // the arguments
                if(child == null)
                    return null;
                node.addChild(child);
            }

            sc.next();                               // the closing )
            return node;
        } else if (!first) {                                       // next token must be a number
            String a;
            try {
                a = sc.next();                               // the closing )
            }
            catch (NoSuchElementException ex)
            {
                UI.println("Error: Missing closing bracket");
                return null;
            }

            if(Double.isNaN(Number.parseNumber(a)))
            {
                UI.println("Error: Missing opening bracket");
                return null;
            }

            return new GTNode<String>(a);
        }
        else
        {
            UI.println("Error: Missing first opening bracket");
            return null;
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     */
    public static double evaluate(GTNode<String> expr) {
        if (expr == null) {
            return Double.NaN;
        }
        if (expr.numberOfChildren() == 0) {            // must be a number
            return Number.parseNumber(expr.getItem());
        } else {
            double ans = Double.NaN;                // answer if no valid operator
            if (expr.getItem().equals("+")) {        // addition operator
                if(!checkArgNum("+", expr, "2+")) return Double.NaN;
                ans = 0;
                for (GTNode<String> child : expr) {
                    ans += evaluate(child);
                }
            } else if (expr.getItem().equals("*")) {  // multiplication operator
                if(!checkArgNum("*", expr, "2+")) return Double.NaN;
                ans = 1;
                for (GTNode<String> child : expr) {
                    ans *= evaluate(child);
                }
            } else if (expr.getItem().equals("-")) {  // subtraction operator
                if(!checkArgNum("-", expr, "2+")) return Double.NaN;
                ans = evaluate(expr.getChild(0));
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    ans -= evaluate(expr.getChild(i));
                }
            } else if (expr.getItem().equals("/")) {  // division operator
                if(!checkArgNum("/", expr, "2+")) return Double.NaN;
                ans = evaluate(expr.getChild(0));
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    ans /= evaluate(expr.getChild(i));
                }
            } else if (expr.getItem().equals("^")) {  // division operator
                if(!checkArgNum("^", expr, "2")) return Double.NaN;
                ans = evaluate(expr.getChild(0));
                ans = Math.pow(ans, evaluate(expr.getChild(1)));
            } else if (expr.getItem().equals("sqrt")) {  // division operator
                if(!checkArgNum("sqrt", expr, "1")) return Double.NaN;
                ans = Math.sqrt(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("log")) {  // division operator
                if(!checkArgNum("log", expr, "1")) return Double.NaN;
                ans = Math.log10(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("ln")) {  // division operator
                if(!checkArgNum("ln", expr, "1")) return Double.NaN;
                ans = Math.log(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("sin")) {  // division operator
                if(!checkArgNum("sin", expr, "1")) return Double.NaN;
                ans = Math.sin(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("cos")) {  // division operator
                if(!checkArgNum("cos", expr, "1")) return Double.NaN;
                ans = Math.cos(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("tan")) {  // division operator
                if(!checkArgNum("tan", expr, "1")) return Double.NaN;
                ans = Math.tan(evaluate(expr.getChild(0)));
            } else if (expr.getItem().equals("dist")) {  // division operator
                if(!checkArgNum("dist", expr, "4")) return Double.NaN;
                double x1 = evaluate(expr.getChild(0));
                double y1 = evaluate(expr.getChild(1));
                double x2 = evaluate(expr.getChild(2));
                double y2 = evaluate(expr.getChild(3));

                //Pythagoras with absolute values below
                double x = Math.abs(x1 - x2);
                double y = Math.abs(y1 - y2);

                double pythag = Math.pow(x, 2) + Math.pow(y, 2);

                ans = Math.sqrt(pythag);
            } else if (expr.getItem().equals("avg")) {  // division operator
                if(!checkArgNum("avg", expr, "1+")) return Double.NaN;
                ans = 0;
                for (int i = 0; i < expr.numberOfChildren(); i++) {
                    ans += evaluate(expr.getChild(i));
                }
                ans /= expr.numberOfChildren();
            }
            else
            {
                UI.println("Error: Invalid operator: \"" + expr.getItem() + "\"");
                return Double.NaN;
            }
            return ans;
        }
    }

    /**
     * Helper for printing equation
     * @param expr
     */
    public static void printExpr(GTNode<String> expr)
    {
        printExpr(expr, "");
    }

    /**
     * Prints CPN equation using normal infix
     *
     * Depth first post order traversal of the tree
     *
     * @param expr Expression to print
     * @param lastOperator Value used internally for detecting if brackets are needed
     */
    public static void printExpr(GTNode<String> expr, String lastOperator)
    {
        if(expr.numberOfChildren() == 0) //if its a number
        {
            UI.print(expr.getItem());
        }
        else {
            //Checks if brackets need to be printed according to the order of operation and function rules
            boolean brackets = (operatorOrder.indexOf(expr.getItem()) > operatorOrder.indexOf(lastOperator) && !lastOperator.equals("") && !functions.contains(lastOperator));
            boolean isFunction = functions.contains(expr.getItem());

            if(isFunction)
                UI.print(expr.getItem());
            if(brackets || isFunction)
                UI.print("(");

            for (int i = 0; i < expr.numberOfChildren(); ++i) {
                printExpr(expr.getChild(i), expr.getItem()); //Recursively print children
                if(i < expr.numberOfChildren() - 1) //Only print before the last child
                    if(isFunction)
                        UI.print(","); //Prints comma between function parameters if there are more than one
                    else
                        UI.print(expr.getItem()); //Print operator between sub equations/values
            }

            if(brackets || isFunction)
                UI.print(")");
        }
    }

    /**
     * Checks that an operation has the correct number of arguments
     * @param operator Operator to check against
     * @param expr Input equation
     * @param required Required number of arguments
     * @return Boolean indicating if the number is correct
     */
    public static boolean checkArgNum(String operator, GTNode<String> expr, String required) {
        if (required.endsWith("+")) { // + is used on the end of the string to indicate a number or more e.g 2+ means two or more
            int num = Integer.parseInt(required.substring(0, required.length() - 1)); //Get value

            if(expr.numberOfChildren() < num)
            {
                UI.println("Error: The operator \"" + operator + "\" requires " + num + " or more arguments.");
                return false;
            }
        }
        else
        {
            int num =  Integer.parseInt(required);

            if(expr.numberOfChildren() != num)
            {
                UI.println("Error: The operator \"" + operator + "\" requires " + required + " argument(s).");
                return false;
            }
        }
        return true;
    }

    /**
     * Alternative method for checking bracket validity. This works perfectly but the assignment page said I had to do it inside the "readExpr" method
     * @param str String to check
     * @return Boolean indicating if the brackets are valid
     */
    public static boolean checkBrackets(String str) {
        List<String> brackets = new ArrayList<>();
        Scanner sc = new Scanner(str);

        //Add brackets to array
        while(sc.hasNext())
        {
            String br = sc.next();
            if(br.equals(")") || br.equals("("))
                brackets.add(br);
        }

        //Main bracket check loop. Removes innermost valid pairs of brackets until either 1 or 0 brackets are left.
        //1 remaining bracket indicates a mismatch, 0 means valid
        while (brackets.size() >= 2)
            for(int i = 0; i < brackets.size() - 1; ++i)
                if(brackets.get(i).equals("(") && brackets.get(i + 1).equals(")"))
                {
                    brackets.remove(i); //Remove opening bracket
                    brackets.remove(i); //Remove closing bracket. Removes at same index because index item changes
                    break;
                }

        if(brackets.size() == 0)
            return true;
        else
        {
            if(brackets.get(0).equals(")"))
                UI.println("Error: Missing opening bracket");
            else
                UI.println("Error: Missing closing bracket");
            return false;
        }
    }
}

