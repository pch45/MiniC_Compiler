package Domain.Stmt;

import Domain.Expr.Expression;

public class Expression_Statement extends Statement{
	public Expression expr;

	public Expression_Statement(Expression expr) {
		super();
		this.expr = expr;
	}
	
	@Override
	public String toString(){
		return expr.toString() + ";";
	}
}
