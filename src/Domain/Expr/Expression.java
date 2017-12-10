package Domain.Expr;

import Domain.MiniCNode;
import test.ASTVisitor;

public class Expression extends MiniCNode {
	@Override
	public void accept(ASTVisitor v) {
		v.visitExpression(this);
	}

}
