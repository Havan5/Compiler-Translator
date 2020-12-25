package compiler;

import java.util.ArrayList;

/**
 * @author: Havan Patel
 */

public class NewToken {
	Types expression;
	String operator;
	String number;

	ArrayList<NewToken> tokenList;

	public NewToken() {
		if (this.tokenList == null)
			this.tokenList = new ArrayList<NewToken>();
	}

	public Types getExpression() {
		return expression;
	}

	public void setExpression(Types expression) {
		this.expression = expression;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public ArrayList<NewToken> getTokenList() {
		return tokenList;
	}

	public void setTokenList(ArrayList<NewToken> tokenList) {
		this.tokenList = tokenList;
	}

	@Override
	public String toString() {
		return expression + " " + operator;
	}

}