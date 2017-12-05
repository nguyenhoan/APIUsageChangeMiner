package main;

import java.util.Scanner;
import utils.Config;
import utils.FileIO;
import change.ChangeAnalyzer;

public class AnalyzeUsageChanges {

	public static void main(String[] args) {
		String n = "8";
		if (args.length > 0)
			n = args[0];
		
		String outputPath = Config.outPath;
		String inputPath = Config.svnRootPath;
		if (args.length > 2) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].equals("-i")) {
					inputPath = args[i+1];
				}
				if (args[i].equals("-o")) {
					outputPath = args[i+1];
				}
			}
		}
		String svnRootPath = "file:///" + inputPath + "/repositories/";
		//String svnRootPath = "https://192.168.10.30:8443/svn/";
		String content = FileIO.readStringFromFile(inputPath + "/java-svn-revisions-rank-even-" + n + ".csv");
    	Scanner sc = new Scanner(content);
    	int count = 0;
		while (sc.hasNextLine()) {
			long startProjectTime = System.currentTimeMillis();
			String line = sc.nextLine();
			final String name = line.substring(0, line.indexOf(','));
			String url = svnRootPath + name;
			System.out.println("Project " + (++count) + ": " + name);
			//ChangeAnalyzer ca = new ChangeAnalyzer(name, id, url, 11610, 11619);
			ChangeAnalyzer ca = new ChangeAnalyzer(name, -1, url);
			ca.buildSvnConnector();
			ca.buildLogAndAnalyze();
	    	long endProjectTime = System.currentTimeMillis();
	    	System.out.println(endProjectTime - startProjectTime);
		}
		sc.close();
	}

}
