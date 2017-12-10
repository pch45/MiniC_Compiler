package Domain.Stmt;

import Domain.MiniCNode;
import test.ASTVisitor;

public abstract class Statement extends MiniCNode{
	@Override
	public void accept(ASTVisitor v) {
		v.visitStatement(this);
	}

}
