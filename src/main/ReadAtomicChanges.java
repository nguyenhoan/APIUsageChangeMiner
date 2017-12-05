package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import utils.Config;
import utils.FileIO;

public class ReadAtomicChanges {

	public static void main(String[] args) {
		HashMap<Byte, HashMap<Byte, HashMap<Byte, HashMap<Boolean, Integer>>>> allChanges = new HashMap<>();
		String path = Config.outPath;
		for (File file : new File(path).listFiles()) {
			if (file.getName().endsWith("test-atomic_changes.dat")) {
				StringBuilder sb = new StringBuilder();
				sb.append("AST node,Change type,Component,Check,Count\n");
				HashMap<Byte, HashMap<Byte, HashMap<Byte, HashMap<Boolean, Integer>>>> changes = (HashMap<Byte, HashMap<Byte, HashMap<Byte, HashMap<Boolean, Integer>>>>) FileIO.readObjectFromFile(file.getAbsolutePath());
				ArrayList<Byte> keys1 = new ArrayList<>(changes.keySet());
				Collections.sort(keys1);
				for (byte nodeType : keys1) {
					String sNodeType = String.valueOf(nodeType);
					if (nodeType == ASTNode.ARRAY_ACCESS) sNodeType = "ARRAY_ACCESS";
					else if (nodeType == ASTNode.ASSIGNMENT) sNodeType = "ASSIGNMENT";
					else if (nodeType == ASTNode.BREAK_STATEMENT) sNodeType = "BREAK_STATEMENT";
					else if (nodeType == ASTNode.CAST_EXPRESSION) sNodeType = "CAST_EXPRESSION";
					else if (nodeType == ASTNode.CONTINUE_STATEMENT) sNodeType = "CONTINUE_STATEMENT";
					else if (nodeType == ASTNode.METHOD_INVOCATION) sNodeType = "METHOD_INVOCATION";
					else if (nodeType == ASTNode.RETURN_STATEMENT) sNodeType = "RETURN_STATEMENT";
					else if (nodeType == ASTNode.SINGLE_VARIABLE_DECLARATION) sNodeType = "SINGLE_VARIABLE_DECLARATION";
					else if (nodeType == ASTNode.VARIABLE_DECLARATION_FRAGMENT) sNodeType = "VARIABLE_DECLARATION_FRAGMENT";
					HashMap<Byte, HashMap<Byte, HashMap<Boolean, Integer>>> map1 = changes.get(nodeType);
					ArrayList<Byte> keys2 = new ArrayList<Byte>(map1.keySet());
					Collections.sort(keys2);
					for (byte component : keys2) {
						HashMap<Byte, HashMap<Boolean, Integer>> map2 = map1.get(component);
						ArrayList<Byte> keys3 = new ArrayList<Byte>(map2.keySet());
						for (byte changeType : keys3) {
							String sChangeType = String.valueOf(changeType);
							if (changeType == -1) sChangeType = "DEL";
							else if (changeType == 1) sChangeType = "ADD";
							else if (changeType == 2) sChangeType = "MOD";
							HashMap<Boolean, Integer> map3 = map2.get(changeType);
							if (map3.containsKey(true)) {
								sb.append(sNodeType + "," + component + "," + sChangeType + ",true," + map3.get(true) + "\n");
							}
							if (map3.containsKey(false)) {
								sb.append(sNodeType + "," + component + "," + sChangeType + ",false," + map3.get(false) + "\n");
							}
						}
					}
				}
				FileIO.writeStringToFile(sb.toString(), file.getAbsolutePath() + ".csv");
			}
		}
	}

}
