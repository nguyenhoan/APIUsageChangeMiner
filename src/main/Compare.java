package main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import utils.FileIO;

public class Compare {

	public static void main(String[] args) {
		String path1 = "D:/cochangespecs/specs-sf-misses", path2 = "D:/data/APISpecMining/specs-sf-misses";
		HashMap<String, String> conditions1 = getConditions(path1), conditions2 = getConditions(path2);
		HashSet<String> inter = new HashSet<>(conditions1.keySet());
		inter.retainAll(conditions2.keySet());
		HashSet<String> in1s = new HashSet<>(conditions1.keySet()), in2s = new HashSet<>(conditions2.keySet());
		in1s.removeAll(inter);
		in2s.removeAll(inter);
		System.out.println(in1s.size());
		print(in1s, conditions1);
		System.out.println(in2s.size());
		print(in2s, conditions2);
	}

	private static void print(HashSet<String> set, HashMap<String,String> conditions) {
		for (String s : set) {
			System.out.println(s + ":" + conditions.get(s));
		}
	}

	private static HashMap<String, String> getConditions(String path) {
		HashMap<String, String> conditions = new HashMap<>();
		String content;
		for (File f : new File(path).listFiles()) {
			content = FileIO.readStringFromFile(f.getAbsolutePath());
			Scanner sc = new Scanner(content);
			while (sc.hasNextLine()) {
				conditions.put(sc.nextLine(), f.getName());
			}
			sc.close();
		}
		return conditions;
	}

}
