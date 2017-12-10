package test;

import Domain.*;
import Domain.Decl.*;
import Domain.Expr.*;
import Domain.Param.*;
import Domain.Stmt.*;
import Domain.Type_spec.*;
import Domain.Args.*;

public interface ASTVisitor {
	public void visitProgram(Program node);
	public void visitDeclaration(Declaration node);
	public void visitFunction_Declaration(Function_Declaration node);
	public void visitLocal_Declaration(Local_Declaration node);
	public void visitArguments(Arguments node);
	public void visitExpression(Expression node);
	public void visitStatement(Statement node);
}
