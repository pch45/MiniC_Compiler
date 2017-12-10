package Domain.Decl;

import Domain.MiniCNode;
import test.ASTVisitor;

public abstract class Declaration extends MiniCNode{
	@Override
	public void accept(ASTVisitor v) {
		v.visitDeclaration(this);
	}
}
