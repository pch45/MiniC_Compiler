package test;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import Domain.*;
import Domain.Decl.*;
import Domain.Args.*;
import Domain.Param.*;
import Domain.Stmt.*;
import Domain.Expr.*;
import Domain.Type_spec.*;

public class MiniCAstVisitor extends MiniCBaseVisitor{

	private List<Expression> allExpr(MiniCParser.ArgsContext ctx) {
		List<Expression> list = new ArrayList<Expression>();
		for(int i = 0; i < ctx.expr().size(); i++) {
			list.add((Expression)visit(ctx.expr(i)));
		}
		return list;
	}
	
	public List<Parameter> allParams(MiniCParser.ParamsContext ctx){
		List<Parameter> list = new ArrayList<Parameter>();
		for(int i = 0 ; i < ctx.param().size(); i++) {
			list.add((Parameter) visit(ctx.param(i)));
		}
		return list;
	}
	
	public List<Local_Declaration> allLocal(MiniCParser.Compound_stmtContext ctx){
		List<Local_Declaration> list = new ArrayList<Local_Declaration>();
		for(int i = 0; i < ctx.local_decl().size(); i++) {
			list.add((Local_Declaration) visit(ctx.local_decl(i)));
		}
		return list;
	}
	
	public List<Statement> allStmt(MiniCParser.Compound_stmtContext ctx){
		List<Statement> list = new ArrayList<Statement>();
		for(int i = 0 ; i < ctx.stmt().size(); i++) {
			list.add((Statement)visit(ctx.stmt(i)));
		}
		return list;
	}
	
	public boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
		return ctx.getChildCount() == 3 
				&& ctx.getChild(1) != ctx.expr(0);
	}
	
	@Override
	public Program visitProgram(MiniCParser.ProgramContext ctx) {
		List<Declaration> list = new ArrayList<Declaration>();
		for(int i = 0 ; i < ctx.getChildCount(); i++) {
			list.add((Declaration)visit(ctx.getChild(i)));
		}
		return new Program(list);
	}
	
	@Override 
	public Declaration visitDecl(MiniCParser.DeclContext ctx) {
		if(ctx.var_decl() != null) {
			Variable_Declaration var_decl = (Variable_Declaration) visit(ctx.var_decl());
			return var_decl;
		}
		else if(ctx.fun_decl() != null) {
			Function_Declaration fun_decl = (Function_Declaration) visit(ctx.fun_decl()); 
			return fun_decl;
		}
		return null; 
	}
	
	@Override 
	public Variable_Declaration visitVar_decl(MiniCParser.Var_declContext ctx) {
		TypeSpecification type = (TypeSpecification) visit(ctx.type_spec());
		TerminalNode IDENT = ctx.IDENT();
		if(ctx.getChildCount() == 3) {
			return new Variable_Declaration(type, IDENT);
		}
		else if(ctx.getChildCount() == 5) {
			return new Variable_Declaration_Assign(type, IDENT, ctx.LITERAL());
		}
		return new Variable_Declaration_Array(type, IDENT, ctx.LITERAL());
	}


	@Override 
	public TypeSpecification visitType_spec(MiniCParser.Type_specContext ctx) {
		if(ctx.getChild(0).getText().toLowerCase().equals("void")) {
			return new TypeSpecification(TypeSpecification.Type.VOID);
		}
		return new TypeSpecification(TypeSpecification.Type.INT);
	}


	@Override 
	public Function_Declaration visitFun_decl(MiniCParser.Fun_declContext ctx) { 
		TypeSpecification type = (TypeSpecification) visit(ctx.type_spec());
		TerminalNode IDENT = (TerminalNode) ctx.IDENT();
		Parameters params = (Parameters) visit(ctx.params());
		Compound_Statement Compound = (Compound_Statement) visit(ctx.compound_stmt());
		return new Function_Declaration(type, IDENT, params, Compound); 
	}


	@Override 
	public Parameters visitParams(MiniCParser.ParamsContext ctx) {
		if(ctx.getChildCount() == 0) {
			return new Parameters();
		}
		else if(ctx.getChild(0).toString().equals("void")) {
			TypeSpecification type = new TypeSpecification(TypeSpecification.Type.VOID);
			return new Parameters(type);
		}
		else {
			return new Parameters(allParams(ctx));
		}
	}


	@Override 
	public Parameter visitParam(MiniCParser.ParamContext ctx) {
		TypeSpecification type = (TypeSpecification) visit(ctx.type_spec());
		TerminalNode IDENT = (TerminalNode) ctx.IDENT();
		if(ctx.getChildCount() == 2) return new Parameter(type,IDENT);
		return new ArrayParameter(type, IDENT);
	}


	@Override 
	public Statement visitStmt(MiniCParser.StmtContext ctx) {
		if(ctx.expr_stmt() != null) {
			return (Expression_Statement) visit(ctx.expr_stmt());
		}
		else if(ctx.compound_stmt() != null) {
			return (Compound_Statement) visit(ctx.compound_stmt());
		}
		else if(ctx.if_stmt() != null) {
			return (If_Statement) visit(ctx.if_stmt());
		}
		else if(ctx.while_stmt() != null) {
			return (While_Statement) visit(ctx.while_stmt());
		}
		else {
			return (Return_Statement) visit(ctx.return_stmt());
		}
	}


	@Override 
	public Expression_Statement visitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
		return new Expression_Statement((Expression)visit(ctx.expr())); 
	}


	@Override
	public While_Statement visitWhile_stmt(MiniCParser.While_stmtContext ctx) {
		return new While_Statement(ctx.WHILE(), 
				(Expression)visit(ctx.expr()), (Statement)visit(ctx.stmt()));
	}

	@Override
	public Compound_Statement visitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		return new Compound_Statement(allLocal(ctx), allStmt(ctx));
	}
	
	@Override 
	public Local_Declaration visitLocal_decl(MiniCParser.Local_declContext ctx) {
		TypeSpecification type = (TypeSpecification)visit(ctx.type_spec());
		if(ctx.getChildCount() == 3) {
			return new Local_Declaration(type, ctx.IDENT());
		}
		
		else if(ctx.getChildCount() == 5) {
			return new Local_Variable_Declaration_Assign(type, ctx.IDENT(),ctx.LITERAL());
			
		}
		else {
			return new Local_Variable_Declaration_Array(type, ctx.IDENT(), ctx.LITERAL());
		}
	}


	@Override 
	public If_Statement visitIf_stmt(MiniCParser.If_stmtContext ctx) {
		TerminalNode ifnode = ctx.IF();
		Expression expr = (Expression) visit(ctx.expr());
		Statement if_stmt = (Statement) visit(ctx.stmt(0));
		if(ctx.getChildCount() == 5) {
			return new If_Statement(ifnode, expr, if_stmt);
		}
		TerminalNode elsenode = ctx.ELSE();
		Statement else_stmt = (Statement) visit(ctx.stmt(1));
		return new If_Statement(ifnode, expr, if_stmt, elsenode, else_stmt);
	}


	@Override
	public Return_Statement visitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
		TerminalNode return_node = ctx.RETURN();
		if(ctx.getChildCount() == 2) {
			return new Return_Statement(return_node);
		}
		return new Return_Statement(return_node, (Expression)visit(ctx.expr()));
	}


	@Override
	public Expression visitExpr(MiniCParser.ExprContext ctx) {
		Expression expr1 = null, expr2 = null;
		String op = null;
		
		if(isBinaryOperation(ctx)) {
			if(ctx.expr().size() == 1) {
				return new AssignNode(ctx.IDENT(), (Expression)visit(ctx.expr(0)));
			}
			expr1 = (Expression) visit(ctx.expr(0));
			expr2 = (Expression) visit(ctx.expr(1));
			op = ctx.getChild(1).getText();
			return new BinaryOpNode(expr1,op,expr2);
		}
		
		else if(ctx.getChildCount() == 1) {
			if(ctx.LITERAL() != null) return new TerminalExpression(ctx.LITERAL());
			else return new TerminalExpression(ctx.IDENT());
		}
		
		else if(ctx.getChildCount() == 2) {
			return new UnaryOpNode(ctx.getChild(0).getText(), (Expression)visit(ctx.expr(0)));
		}
		
		else if(ctx.getChildCount() == 3) {
			return new ParenExpression((Expression)visit(ctx.expr(0)));
		}
		
		else if(ctx.getChildCount() == 4) {
			if(ctx.getChild(2) != ctx.expr(0)) {
				return new FuncallNode(ctx.IDENT(), (Arguments)visit(ctx.args()));
			}
			else {
				return new ArefNode(ctx.IDENT(), (Expression)visit(ctx.expr(0)));
			}
		}
		
		else {
			return new ArefAssignNode(ctx.IDENT(), 
					(Expression)visit(ctx.expr(0)), (Expression)visit(ctx.expr(1)));
		}
	}

	@Override 
	public Arguments visitArgs(MiniCParser.ArgsContext ctx) {
		if(ctx.getChildCount() == 0) return new Arguments();
		return new Arguments(allExpr(ctx));
	}

}
