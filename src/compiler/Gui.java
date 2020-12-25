package compiler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;

	static ArrayList<Token> tokens;
	static int current = 0;
	static Token AST;
	static NewToken TransformerTree;
	static StringBuilder finalOutPut;
	static StringBuilder tokenOutPut;
	static JTextArea ta = new JTextArea(10, 30);
	static JTextArea ta1 = new JTextArea(10, 50);
	static JTextArea ta0 = new JTextArea(10, 50);
	static JTextArea textfield2 = new JTextArea(10, 50);

	static JScrollPane scrollPane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	static JScrollPane scrollPane1 = new JScrollPane(ta1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	static JScrollPane scrollPane0 = new JScrollPane(ta0, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	static JScrollPane scrollPane2 = new JScrollPane(textfield2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	public static void main(String[] args) {
		Gui bs = new Gui();
		bs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = bs.getContentPane();
		pane.setLayout(new BorderLayout());

		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Enter Input: ", JLabel.RIGHT);
		label.setFont(new Font("Courier", Font.BOLD, 20));

		JTextField textfield = new JTextField("", 35);
		textfield.setSize(200, 25);

		JButton jb = new JButton("submit");
		JButton jb2 = new JButton("Clear");

		subPanel.add(label);
		subPanel.add(textfield);
		subPanel.add(jb);
		subPanel.add(jb2);
		pane.add(subPanel, BorderLayout.NORTH);

		JPanel subPanel1 = new JPanel();
		JLabel label2 = new JLabel("Final Jave Function Output: ", JLabel.RIGHT);
		label2.setFont(new Font("Courier", Font.BOLD, 15));

		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textfield.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Input is Empty. Can't Have empty input or have valid Input");
				} else {
					tokens = new ArrayList<>();
					tokenOutPut = new StringBuilder();
					finalOutPut = new StringBuilder();
					current = 0;
					String sss = myTokenizer(textfield.getText());
					if (sss != null) {
						tokenOutPut.append("TOKENIZER" + "\n");
						tokenOutPut.append("-------------------" + "\n");

						for (Token t : tokens) {
							tokenOutPut.append(t.toString() + "\n");
						}
						ta.setText("");
						ta.setFont(new Font("Courier", Font.BOLD, 15));
						ta.setText(tokenOutPut.toString());
						ta.setEditable(false);
						ta.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
						pane.add(scrollPane, BorderLayout.WEST);

						String s = "AST TREE" + "\n" + "-------------------" + "\n" + Parser();
						ta1.setText("");
						ta1.setText(s);
						ta1.setFont(new Font("Courier", Font.BOLD, 15));
						ta1.setEditable(false);
						ta1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
						pane.add(scrollPane1, BorderLayout.CENTER);

						String s2 = "Transformed AST TREE" + "\n" + "----------------------" + "\n" + Parser2();

						ta0.setSize(100, 100);
						ta0.setText("");
						ta0.setFont(new Font("Courier", Font.BOLD, 15));
						ta0.setEditable(false);
						ta0.setText(s2);
						ta0.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

						pane.add(scrollPane0, BorderLayout.EAST);

						JavaCode(TransformerTree);

						textfield2.setText(finalOutPut.toString());
						textfield2.setEditable(false);
						subPanel1.add(label2);
						subPanel1.add(scrollPane2);
						pane.add(subPanel1, BorderLayout.SOUTH);
					} else {
						JOptionPane.showMessageDialog(null, "invalid Input");
					}
				}
				pane.revalidate();
				pane.repaint();
			}
		});

		jb2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				current = 0;
				ta.setText("");
				ta1.setText("");
				ta0.setText("");
				textfield2.setText("");
				pane.revalidate();
				pane.repaint();
			}
		});

		bs.setExtendedState(JFrame.MAXIMIZED_BOTH);
		bs.setVisible(true);
	}

	public static void JavaCode(NewToken newToken) {
		if (newToken.expression == Types.CallExpression) {
			finalOutPut.append(newToken.operator + " (");
			for (NewToken t : newToken.tokenList) {
				JavaCode(t);
				if (newToken.tokenList.indexOf(t) != newToken.tokenList.size() - 1) {
					finalOutPut.append(" , ");
				}
			}
			finalOutPut.append(")");
		} else {
			finalOutPut.append(newToken.number);
		}
	}

	public static String Parser() {
		Gson gson = null;
		while (current < tokens.size()) {
			AST = walk();
			gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		}
		return gson.toJson(AST);
	}

	public static String Parser2() {
		Gson gson = null;
		TransformerTree = walk2(AST);
		gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		return gson.toJson(TransformerTree);
	}

	public static Token walk() {
		Token token = tokens.get(current);
		if (token.type == Types.NUMBER) {
			current++;
			return new Token(Types.NumberLiterals, token.value);
		}

		if (token.type == Types.PAREN && token.value == "(") {
			token = tokens.get(++current);
			Token callExpressionToken = new Token(Types.CallExpression, token.value);
			token = tokens.get(++current);

			while ((token.type != Types.PAREN) || (token.type == Types.PAREN && token.value != ")")) {
				callExpressionToken.tokens.add(walk());
				token = tokens.get(current);
			}
			current++;
			return callExpressionToken;
		}

		return null;
	}

	public static NewToken walk2(Token ast) {
		NewToken newToken = new NewToken();
		if (ast.type == Types.CallExpression) {
			newToken.expression = Types.CallExpression;
			newToken.operator = ast.value;
			for (Token t : ast.tokens) {
				newToken.tokenList.add(walk2(t));
			}
			return newToken;
		} else {
			newToken.expression = Types.NumberLiterals;
			newToken.number = ast.value;
			return newToken;
		}
	}

	public static String myTokenizer(String input) {
		int current = 0;
		String regex = "\\d+";
		String sregex = ".*[a-z].*";
		String nvalue = "";
		if (input.toLowerCase().contains("!") || input.toLowerCase().contains("@") || input.toLowerCase().contains("#")
				|| input.toLowerCase().contains("$") || input.toLowerCase().contains("%")
				|| input.toLowerCase().contains("^") || input.toLowerCase().contains("&")
				|| input.toLowerCase().contains("*") || input.toLowerCase().contains("_")
				|| input.toLowerCase().contains("+") || input.toLowerCase().contains("=")
				|| input.toLowerCase().contains(";") || input.toLowerCase().contains(":")
				|| input.toLowerCase().contains("'") || input.toLowerCase().contains("<")
				|| input.toLowerCase().contains(">") || input.toLowerCase().contains(",")
				|| input.toLowerCase().contains("/") || input.toLowerCase().contains("{")
				|| input.toLowerCase().contains("}") || input.toLowerCase().contains("[")
				|| input.toLowerCase().contains("]") || input.toLowerCase().contains("|")
				|| input.toLowerCase().contains("~") || input.toLowerCase().contains("`")) {
			return null;
		}
		char[] inputChar = input.toLowerCase().toCharArray();
		while (current < inputChar.length) {
			char c = inputChar[current];
			String data = Character.toString(c);
			if (c == '(') {
				tokens.add(new Token(Types.PAREN, "("));
				current++;
			} else if (c == ')') {
				tokens.add(new Token(Types.PAREN, ")"));
				current++;
			} else if (c == ' ') {
				nvalue = "";
				current++;
			} else if (c == '-') {
				nvalue = "-";
				current++;
			} else if (data.matches(regex)) {
				while (data.matches(regex)) {
					nvalue += data;
					c = inputChar[++current];
					data = Character.toString(c);
				}
				tokens.add(new Token(Types.NUMBER, nvalue));
			} else if (data.matches(sregex)) {
				String value = "";
				while (data.matches(sregex)) {
					value += data;
					c = inputChar[++current];
					data = Character.toString(c);
				}
				if (value.equals("add") || value.equals("subtract") || value.equals("multiply")
						|| value.equals("divide") || value.equals("mod")) {
					tokens.add(new Token(Types.NAME, value));
				} else {
					return null;
				}
			}
		}
		return "good";
	}
}
