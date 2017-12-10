package Domain.Args;

import java.util.List;

import Domain.MiniCNode;
import Domain.Expr.Expression;
import test.ASTVisitor;

public class Arguments extends MiniCNode{
	public List<Expression> exprs;
	
	public Arguments(){}
	
	public Arguments(List<Expression> exprs){
		this.exprs = exprs;
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitArguments(this);
	}

	@Override
	public String toString(){
		return exprs == null? "" 
				:exprs.stream().map(t -> ", " + t.toString())
							   .skip(1)
							   .reduce(exprs.get(0).toString(), (acc, param) -> acc + param);
	}
}
