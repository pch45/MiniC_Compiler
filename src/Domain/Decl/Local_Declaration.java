package Domain.Decl;

import org.antlr.v4.runtime.tree.TerminalNode;

import Domain.MiniCNode;
import Domain.Type_spec.TypeSpecification;
import test.ASTVisitor;

public class Local_Declaration extends MiniCNode{
	public TypeSpecification type;
	public TerminalNode lhs;
	
	public Local_Declaration(TypeSpecification type, TerminalNode lhs) {
		this.type = type;
		this.lhs = lhs;
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitLocal_Declaration(this);
	}
	
	@Override
	public String toString(){
		return type.toString() + " " + lhs.getText() + ";";
	}
}
