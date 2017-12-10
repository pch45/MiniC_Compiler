package Domain.Type_spec;

import Domain.MiniCNode;
import test.ASTVisitor;

public class TypeSpecification extends MiniCNode {
	public final Type type;

	public enum Type {
		VOID, INT
	}

	public TypeSpecification(Type type) {
		this.type = type;
	}
	@Override
	public void accept(ASTVisitor v) {
	}
	
	@Override
	public String toString(){
		return type.toString().toLowerCase();
	}
}
