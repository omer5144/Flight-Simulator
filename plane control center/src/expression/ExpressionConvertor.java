package expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import interperter_side.Utilities;

public class ExpressionConvertor {
	public static String infixToPostfix(List<String> split) {
		Stack<String> stack = new Stack<>();
		Queue<String> queue = new LinkedList<>();
		ArrayList<String> splitStr = new ArrayList<>(split);
		replaceVariables(splitStr); removeOperators(splitStr); checkMinuses(splitStr);
		for(int i = 0; i < splitStr.size(); i++) {
			if(Utilities.isDouble(splitStr.get(i))) {
				queue.add(splitStr.get(i));
			}
			else {
				switch(splitStr.get(i)) {
				case "/":
				case "*":
					while(!stack.isEmpty() && (!stack.peek().equals("(")) && (!stack.peek().equals("+") && (!stack.peek().equals("-")))) {
						queue.add(stack.pop());
					}
					stack.push(splitStr.get(i));
					break;
				case "+":
				case "-":
					while(!stack.isEmpty() && (!stack.peek().equals("("))) {
						queue.add(stack.pop());
					}
					stack.push(splitStr.get(i));
					break;
				case "(":
					stack.push("(");
					break;
				case ")":
					while(!stack.isEmpty() && (!stack.peek().equals("("))) {
						queue.add(stack.pop());
					}
					if(!stack.isEmpty()) stack.pop();
					break;
				}
			}
		}
		while(!stack.isEmpty()) {
			queue.add(stack.pop());
		}
		StringBuilder sb = new StringBuilder();
		for(String str : queue) sb.append(str).append(",");
		return sb.toString();
	}
	private static void removeOperators(ArrayList<String> split) {
		for(int i=0; i < split.size(); i++) {
			if(Arrays.asList("+", "-").contains(split.get(i))) {
				int cnt = 1, startIndex = i;
				boolean flag = !split.get(i++).equals("-");
				while(Arrays.asList("+","-").contains(split.get(i))) {
					if(flag && split.get(i).equals("-")) flag = false; //+- -+
					else if(!flag && split.get(i).equals("-")) flag = true; //--
					cnt++; i++;
				}
				for(int j = 0; j<cnt; j++) {
					split.remove(startIndex);
				}
				if(startIndex == 0 || split.get(startIndex - 1).equals("(") || Arrays.asList("*","/").contains(split.get(startIndex - 1))) {
					if(!flag) split.add(startIndex, "-");
				}
				else split.add(startIndex, flag ? "+" : "-");
			}
		}
	}
	private static void checkMinuses(List<String> split) {
		for(int i = 0; i < split.size(); i++) {
			if(split.get(i).equals("-")) {
				if(i == 0) { // if - is in the beginning
					split.remove(i);
					split.set(0, "-" + split.get(0));
				}
				else if(Arrays.asList("*","/","(").contains(split.get(i - 1))) {
					split.remove(i);
					split.set(i, "-" + split.get(i));
				}
			}
		}
	}
	public static double calculatePostfix(String postfix) {
		Stack<Expression> stackExp = new Stack<>();
		String[] expressions = postfix.split(",");
		for(String str : expressions) {
			if(Utilities.isDouble(str)) {
				stackExp.push(new Number(Double.parseDouble(str)));
			}
			else {
				Expression right = stackExp.pop();
				Expression left = stackExp.pop();
				switch(str){
				case "+":
					stackExp.add(new Plus(left, right));
					break;
				case "-":
					stackExp.add(new Minus(left, right));
					break;
				case "*":
					stackExp.add(new Mul(left, right));
					break;
				case "/":
					stackExp.add(new Div(left, right));
					break;
				}
			}
		}
		return Math.floor(stackExp.pop().calculate() * 1000) / 1000;
	}
	public static void replaceVariables(List<String> list) {
		Var v;
		for (int i = 0; i < list.size(); i++) {
			if((v=Utilities.getVar(list.get(i))) != null) {
				if(v.calculate() >= 0) list.set(i, String.valueOf(v.calculate()));
				else {
					list.add(i++, "-");
					list.set(i, String.valueOf(-v.calculate()));
				}
			}
		}
	}
	public static double calculate(String infix)
	{
		return calculatePostfix(infixToPostfix(Arrays.asList(infix.split("(?<=[-+*/()])|(?=[-+*/()])"))));
	}
	public static void main(String[] args) {
		String str = infixToPostfix(Arrays.asList("(","-","5","+","3",")"));
		double val = calculatePostfix(str);
		System.out.println(str);
		System.out.println(val);
	}
}

