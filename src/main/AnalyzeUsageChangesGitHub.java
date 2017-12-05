package main;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import utils.FileIO;
import change.ChangeAnalyzer;

public class AnalyzeUsageChangesGitHub {
	private static final AtomicInteger currentProject = new AtomicInteger();
	private static ArrayList<String> projectNames;
	
	public static class ImportTask implements Runnable {
		@Override
		public void run() {
			while (true) {
				int c = currentProject.getAndIncrement();
				if (c >= projectNames.size()) break;
				String name = projectNames.get(c);
				long startProjectTime = System.currentTimeMillis();
				System.out.println("Project " + c + ": " + name);
				try {
					ChangeAnalyzer ca = new ChangeAnalyzer(name, -1, "E:/github/repos-5stars-50commits/" + name);
					ca.buildGitConnector();
					ca.analyzeGit();
				} catch (Throwable t) {}
		    	long endProjectTime = System.currentTimeMillis();
		    	System.out.println("Project " + c + ": " + name + " done " + (endProjectTime - startProjectTime));
			}
		}

	}

	public static void main(String[] args) {
		projectNames = readProjectNames("E:/github/repos-5stars-50commits.csv");
		//projectNames = readProjectNames("F:/github/crypto/java-crypto-remain.txt");
		
		for (int i = 0; i < 8; i++) 
			new Thread(new ImportTask()).start();
	}

	private static ArrayList<String> readProjectNames(String path) {
		ArrayList<String> names = new ArrayList<>();
		String content = FileIO.readStringFromFile(path);
		Scanner sc = new Scanner(content);
		while (sc.hasNextLine())
			names.add(sc.nextLine());
		sc.close();
		return names;
	}

}
