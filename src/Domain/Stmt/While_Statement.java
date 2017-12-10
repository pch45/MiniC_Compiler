package Domain.Stmt;

import org.antlr.v4.runtime.tree.TerminalNode;

import Domain.Expr.Expression;

public class While_Statement extends Statement{
	public TerminalNode while_node;
	public Expression expr;
	public Statement stmt;
	
	public While_Statement(TerminalNode while_node, Expression expr, Statement stmt) {
		super();
		this.while_node = while_node;
		this.expr = expr;
		this.stmt = stmt;
	}

	@Override
	public String toString(){
		return while_node.getText() + "(" + expr.toString() + ")\n" + stmt.toString();
	}
}
