package utils;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class TestASTFlattener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = "a = new int[10];";
		ASTParser parser = ASTParser.newParser(AST.JLS3);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
		parser.setSource(source.toCharArray());
		// In order to parse 1.5 code, some compiler options need to be set to 1.5
		@SuppressWarnings("rawtypes")
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_STATEMENTS);
		ASTNode ast = parser.createAST(null);
		ASTAllNamesFlattener flat = new ASTAllNamesFlattener();
		ast.accept(flat);
		System.out.println(flat.getResult());
	}

}
