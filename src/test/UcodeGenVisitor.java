package test;

import java.util.*;

import Domain.Program;
import Domain.Args.*;
import Domain.Decl.*;
import Domain.Expr.*;
import Domain.Param.*;
import Domain.Stmt.*;

public class UcodeGenVisitor implements ASTVisitor{

	int variable_decl = 1;
	int local_decl = 1;
	int label_num = 0;
	boolean has_return = false;
	int expression_depth = 1;

	HashMap<String, String> var_code = new HashMap<String,String>();
	HashMap<String, String> local_var_code = new HashMap<String,String>();
	Stack<Integer> stack = new Stack<Integer>();
	List<Parameter> params = new ArrayList<Parameter>();
	List<String> local_Array = new ArrayList<String>();
	List<String> Array = new ArrayList<String>();
	
	@Override
	public void visitProgram(Program node) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < node.decls.size(); i++) {
			visitDeclaration(node.decls.get(i));
		}
		System.out.println("           bgn        0");
		System.out.println("           ldp");
		System.out.println("           call main");
		System.out.println("           end");
	}

	@Override
	public void visitDeclaration(Declaration node) {
		// TODO Auto-generated method stub

		if(node instanceof Variable_Declaration_Array) {
			Variable_Declaration_Array t = (Variable_Declaration_Array) node;
			var_code.put(t.lhs.toString(), "1"+variable_decl);
			Array.add(t.lhs.toString());
			System.out.println("           sym        1 "+ variable_decl +" "+ t.rhs.toString());
			variable_decl += Integer.parseInt(t.rhs.toString());
		}

		else if(node instanceof Variable_Declaration_Assign) {
			Variable_Declaration_Assign t = (Variable_Declaration_Assign) node;
			var_code.put(t.lhs.toString(), "1"+variable_decl);
			System.out.println("           sym        1 "+ (variable_decl++) + " 1");
			String depth = var_code.get(t.lhs.toString());
			String value = t.rhs.toString();
			if(value.charAt(0) == '-') {
				System.out.println("           ldc        "+value.substring(1));
				System.out.println("           neg");
				System.out.println("           str        "+depth.charAt(0)+" "+depth.substring(1));
			}
			else {
				System.out.println("           ldc        "+value);
				System.out.println("           str        "+depth.charAt(0)+" "+depth.substring(1));
			}

		}

		else if(node instanceof Function_Declaration) {
			Function_Declaration t = (Function_Declaration) node;
			visitFunction_Declaration(t);
			local_var_code = new HashMap<String,String>();
		}

		else {
			Variable_Declaration t = (Variable_Declaration) node;
			var_code.put(t.lhs.toString(), "1"+variable_decl);
			System.out.println("           sym        1 "+ (variable_decl++) +" 1");
		}
	}

	@Override
	public void visitFunction_Declaration(Function_Declaration node) {
		// TODO Auto-generated method stub
		int size = 0;
		if(node.params.params != null) {
			for(int i = 0 ; i < node.params.params.size(); i++) {
				params.add(node.params.params.get(i));
				if(node.params.params.get(i) instanceof Parameter) {
					size++;
				}
			}
		}

		System.out.print(node.t_node);
		for(int i = 0 ; i < 11-node.t_node.toString().length(); i++) 
			System.out.print(" ");
		for(int i = 0 ; i < node.compount_stmt.local_decls.size(); i++) {
			if(!(node.compount_stmt.local_decls.get(i) instanceof Local_Variable_Declaration_Array)) {
				size++;
			}
			else {
				Local_Variable_Declaration_Array temp = (Local_Variable_Declaration_Array)node.compount_stmt.local_decls.get(i);
				size += Integer.parseInt(temp.rhs.toString());
			}
		}


		System.out.println("proc       " + size + " 2 2");


		visitStatement(node.compount_stmt);
		if(!has_return) {
			System.out.println("           ret");
			System.out.println("           end");
			has_return = false;
		}
		local_decl = 1;
		params = new ArrayList<Parameter>();
		local_Array = new ArrayList<String>();
	}

	@Override
	public void visitLocal_Declaration(Local_Declaration node) {
		// TODO Auto-generated method stub

		if(node instanceof Local_Variable_Declaration_Assign) {
			local_var_code.put(node.lhs.toString(), "2"+local_decl);
			System.out.println("           sym        2 "+(local_decl++)+" 1");

			Local_Variable_Declaration_Assign t = (Local_Variable_Declaration_Assign) node;
			String depth = local_var_code.get(t.lhs.toString());
			String value = t.rhs.toString();
			if(value.charAt(0) == '-') {
				System.out.println("           ldc        "+value.substring(1));
				System.out.println("           neg");
				System.out.println("           str        "+depth.charAt(0)+" "+depth.substring(1));
			}
			else {
				System.out.println("           ldc        "+value);
				System.out.println("           str        "+depth.charAt(0)+" "+depth.substring(1));
			}
		}
		else if(node instanceof Local_Variable_Declaration_Array) {
			Local_Variable_Declaration_Array t = (Local_Variable_Declaration_Array) node;
			String length = t.rhs.toString();
			local_Array.add(node.lhs.toString());
			local_var_code.put(node.lhs.toString(), "2"+local_decl);
			System.out.println("           sym        2 "+local_decl+" "+length);
			local_decl += Integer.parseInt(length);
		}
		else {
			local_var_code.put(node.lhs.toString(), "2"+local_decl);
			System.out.println("           sym        2 "+(local_decl++)+" 1");
		}
	}

	@Override
	public void visitArguments(Arguments node) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < node.exprs.size(); i++) {
			visitExpression(node.exprs.get(i));
		}
	}

	@Override
	public void visitExpression(Expression node) {
		// TODO Auto-generated method stub
		boolean isparam = false;
		expression_depth++;
		if(node instanceof ArefAssignNode) {
			ArefAssignNode t = (ArefAssignNode) node;
			for(int i = 0 ; i < params.size(); i++) {
				if(local_var_code.containsKey(params.get(i).t_node.toString()) &&
						t.t_node.toString().equals(params.get(i).t_node.toString())) {
					isparam = true;
					break;
				}
			}

			String code = null;
			if(!isparam) {
				if(local_var_code.containsKey(t.t_node.toString()))
					code = local_var_code.get(t.t_node.toString());
				else if(var_code.containsKey(t.t_node.toString()))
					code = var_code.get(t.t_node.toString());
				System.out.println("           lda        "+code.charAt(0)+" "+code.substring(1));
				visitExpression(t.lhs);
				System.out.println("           add");
				visitExpression(t.rhs);
				System.out.println("           sti");
			}

			else {
				code = local_var_code.get(t.t_node.toString());
				System.out.println("           lod        "+code.charAt(0)+" "+code.substring(1));
				visitExpression(t.lhs);
				System.out.println("           add");
				visitExpression(t.rhs);
				System.out.println("           sti");
			}
		}

		else if(node instanceof ArefNode) {
			ArefNode t = (ArefNode) node;
			for(int i = 0 ; i < params.size(); i++) {
				if(local_var_code.containsKey(params.get(i).t_node.toString()) &&
						t.t_node.toString().equals(params.get(i).t_node.toString())) {
					isparam = true;
					break;
				}
			}

			String code = null;

			if(!isparam) {
				if(local_var_code.containsKey(t.t_node.toString()))
					code = local_var_code.get(t.t_node.toString());
				else if(var_code.containsKey(t.t_node.toString()))
					code = var_code.get(t.t_node.toString());

				visitExpression(t.expr);
				System.out.println("           lda        "+code.charAt(0)+" "+code.substring(1));
				System.out.println("           add");
				System.out.println("           ldi");
			}
			else {
				code = local_var_code.get(t.t_node.toString());
				visitExpression(t.expr);
				System.out.println("           lod        "+code.charAt(0)+" "+code.substring(1));
				System.out.println("           add");
				System.out.println("           ldi");
			}
		}

		else if(node instanceof AssignNode) {
			AssignNode t = (AssignNode) node;
			String code = null;
			if(local_var_code.containsKey(t.t_node.toString()))
				code = local_var_code.get(t.t_node.toString());
			else if(var_code.containsKey(t.t_node.toString()))
				code = var_code.get(t.t_node.toString());
			visitExpression(t.expr);
			System.out.println("           str        "+code.charAt(0)+" "+code.substring(1));
		}

		else if(node instanceof BinaryOpNode) {
			BinaryOpNode t = (BinaryOpNode) node;
			visitExpression(t.lhs);
			visitExpression(t.rhs);
			System.out.println("           "+binary_opcode(t.op));
		}

		else if(node instanceof FuncallNode) {
			FuncallNode t = (FuncallNode) node;
			System.out.println("           ldp");
			if(t.args.exprs != null) visitArguments(t.args);
			System.out.println("           call "+t.t_node);
		}

		else if(node instanceof ParenExpression) {
			expression_depth--;
			ParenExpression t = (ParenExpression) node;
			visitExpression(t.expr);
		}

		else if(node instanceof TerminalExpression) {
			TerminalExpression t = (TerminalExpression) node;
			String code = null;

			if(local_var_code.containsKey(t.t_node.toString())) {
				code = local_var_code.get(t.t_node.toString());
				if(local_Array.contains(t.t_node.toString())) {
					System.out.println("           lda        "+code.charAt(0)+" "+code.substring(1));
				}
				else {
					System.out.println("           lod        "+code.charAt(0)+" "+code.substring(1));
				}
			}
			else if(var_code.containsKey(t.t_node.toString())) {
				code = var_code.get(t.t_node.toString());
				if(Array.contains(t.t_node.toString())) {
					System.out.println("           lda        "+code.charAt(0)+" "+code.substring(1));
				}
				else {
					System.out.println("           lod        "+code.charAt(0)+" "+code.substring(1));
				}
			}
			else {
				System.out.println("           ldc        "+t.t_node.toString());
			}
		}

		else if(node instanceof UnaryOpNode) {
			UnaryOpNode t = (UnaryOpNode) node;
			String code = null;
			boolean only_unary = false;
			if(expression_depth == 2) only_unary = true;

			if(!(t.expr instanceof ArefNode)) {
				if(local_var_code.containsKey(t.expr.toString()))
					code = local_var_code.get(t.expr.toString());
				else if(var_code.containsKey(t.expr.toString()))
					code = var_code.get(t.expr.toString());
				visitExpression(t.expr);
				String op;
				if((op = unary_opcode(t.op)) != null) {
					System.out.println("           "+op);
				}
				if(code != null) {
					System.out.println("           str        "+code.charAt(0)+" "+ code.substring(1));
					if(!only_unary) {
						System.out.println("           lod        "+code.charAt(0)+" "+ code.substring(1));
					}
				}
			}

			else if(t.expr instanceof ArefNode) {
				ArefNode aref = (ArefNode) t.expr;
				for(int i = 0 ; i < params.size(); i++) {
					if(local_var_code.containsKey(params.get(i).t_node.toString()) &&
							aref.t_node.toString().equals(params.get(i).t_node.toString())) {
						isparam = true;
						break;
					}
				}

				String op = null;
				if(!isparam) {
					if(local_var_code.containsKey(aref.t_node.toString()))
						code = local_var_code.get(aref.t_node.toString());
					else if(var_code.containsKey(aref.t_node.toString()))
						code = var_code.get(aref.t_node.toString());
					visitExpression(aref.expr);
					System.out.println("           lda        "+code.charAt(0)+" "+code.substring(1));
				}

				else {
					code = local_var_code.get(aref.t_node.toString());
					visitExpression(aref.expr);
					System.out.println("           lod        "+code.charAt(0)+" "+code.substring(1));
				}
				
				
				System.out.println("           add");
				if((op = unary_opcode(t.op)) != null) {
					visitExpression(t.expr);
					System.out.println("           "+op);
					System.out.println("           sti");
					if(!only_unary) {
						System.out.println("           ldi");
					}
				}
				else {
					System.out.println("           ldi");
				}
			}
		}
	}

	private String unary_opcode(String op) {
		if(op.equals("-")) return "neg";
		else if(op.equals("++")) return "inc";
		else if(op.equals("--")) return "dec";
		else if(op.equals("!")) return "notop";
		return null;
	}

	private String binary_opcode(String op) {
		if(op.equals("*")) return "mult";
		else if(op.equals("/")) return "div";
		else if(op.equals("%")) return "mod";
		else if(op.equals("+")) return "add";
		else if(op.equals("-")) return "sub";
		else if(op.equals("==")) return "eq";
		else if(op.equals("!=")) return "ne";
		else if(op.equals("<=")) return "le";
		else if(op.equals("<")) return "lt";
		else if(op.equals(">=")) return "ge";
		else if(op.equals(">")) return "gt";
		else if(op.equals("and")) return "and";
		else if(op.equals("or")) return "or";
		return null;
	}

	@Override
	public void visitStatement(Statement node) {
		// TODO Auto-generated method stub
		expression_depth = 1;
		if(node instanceof Compound_Statement) {
			Compound_Statement t = (Compound_Statement) node;
			int j = 0;
			while(j != params.size()) {
				Parameter param = params.get(j++);
				local_var_code.put(param.t_node.toString(), "2"+local_decl);
				if(param instanceof ArrayParameter)
					System.out.println("           sym        2 "+(local_decl++)+" 1");
				else
					System.out.println("           sym        2 "+(local_decl++)+" 0");
			}

			for(int i = 0; i < t.local_decls.size(); i++) {
				visitLocal_Declaration(t.local_decls.get(i));
			}
			for(int i = 0; i < t.stmts.size(); i++) {
				visitStatement(t.stmts.get(i));
				if(i != t.stmts.size() - 1)
					has_return = false;
			}
		}

		else if(node instanceof While_Statement) {
			While_Statement t = (While_Statement) node;
			String label = "$$"+label_num++;
			stack.push(label_num);
			System.out.print(label);
			for(int i = 0 ; i < 11-label.length(); i++)
				System.out.print(" ");
			System.out.println("nop");
			visitExpression(t.expr);
			System.out.println("           fjp $$"+label_num++);	// ++ 해야되나?
			visitStatement(t.stmt);
			System.out.println("           ujp "+label);
			System.out.println("$$"+stack.pop()+"        nop");
		}

		else if (node instanceof Expression_Statement) {
			visitExpression(((Expression_Statement) node).expr);
		}

		else if(node instanceof If_Statement) {						//전체적으로 다시봐야됨
			If_Statement t = (If_Statement) node;
			String label = "$$"+label_num++;

			System.out.print(label);
			for(int i = 0 ; i < 11-label.length(); i++)
				System.out.print(" ");
			System.out.println("nop");
			visitExpression(t.expr);

			if(t.else_stmt == null) {
				stack.push(label_num);
				System.out.println("           fjp "+"$$"+label_num++);
				visitStatement(t.if_stmt);
			}

			if(t.else_stmt != null) {
				stack.push(label_num);
				System.out.println("           fjp "+"$$"+label_num++);
				visitStatement(t.if_stmt);
				stack.push(label_num);
				System.out.println("           ujp "+"$$"+stack.peek());
				label_num++;
				int nested = stack.pop();
				int current = stack.pop();
				System.out.print("$$"+current);
				for(int i = 0; i < 11-("$$"+current).length(); i++) {
					System.out.print(" ");
				}
				System.out.println("nop");
				visitStatement(t.else_stmt);
				stack.push(nested);
			}
			int current = stack.pop();
			System.out.print("$$"+current);
			for(int i = 0; i < 11-("$$"+current).length(); i++) {
				System.out.print(" ");
			}
			System.out.println("nop");
		}

		else if(node instanceof Return_Statement) {
			Return_Statement t = (Return_Statement) node;
			if(t.expr != null) {
				visitExpression(t.expr);
				System.out.println("           retv");
			}
			else {
				System.out.println("           ret");
			}
			has_return = true;
		}
	}
}
