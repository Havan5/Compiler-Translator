package compiler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author: Havan Patel
 */

public class Token {

	Types type;
	String value;
	ArrayList<Token> tokens;

	public Token(Types type, String value) {
		this.type = type;
		this.value = value;
		if (this.tokens == null)
			this.tokens = new ArrayList<Token>();
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "type: " +""+getType() + "     "+"value: " + getValue();
	}

}
