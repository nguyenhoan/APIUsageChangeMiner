package main;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import utils.FileIO;

public class TestParser {

	public static void main(String[] args) {
		String fileContent = FileIO.readStringFromFile("T:/api-fixes/iterator/3dcitydb/importer-exporter/1a85126a5acb143f1c7a793095a81ba374c02049/1/good.java");
		Map options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(("class MISSING {\n" + fileContent + "\n}").toCharArray());
    	parser.setCompilerOptions(options);
    	parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
    	ASTNode ast = parser.createAST(null);
    	System.out.println(ast);
	}

}
