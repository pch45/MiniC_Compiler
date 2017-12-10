package test;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import Domain.*;

public class TestMiniC {
	
	public static void main(String[] args) throws Exception{
		MiniCLexer lexer = new MiniCLexer( new ANTLRFileStream("test1.c"));
		CommonTokenStream tokens = new CommonTokenStream( lexer );
		MiniCParser parser  = new MiniCParser( tokens );
		ParseTree tree = parser.program();
		// ������� ���ο� �κ�
		MiniCAstVisitor visitor = new MiniCAstVisitor();
		Program program = (Program)visitor.visit(tree);
		program.accept(new UcodeGenVisitor());
	} 
}