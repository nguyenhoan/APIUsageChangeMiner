package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import change.ChangeAnalyzer;
import utils.Config;
import utils.FileIO;

public class MainChangeAnalyzers {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String n = "6";
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
    	String fileDoneName = "change-extract-done-" + n + ".csv";
    	FileOutputStream out; 
		PrintStream ps = null;
        try {
    		// Create a new file output stream
    		out = new FileOutputStream(outputPath + "/" + fileDoneName, true);
    		// Connect print stream to the output stream
    		ps = new PrintStream(out);
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	Scanner sc = null;
    	int count = 0;
    	try {
        	sc = new Scanner(new File(outputPath + "/" + fileDoneName));
        	while (sc.hasNextLine() && !sc.nextLine().trim().isEmpty())
        		count++;
        } catch (FileNotFoundException e) {
        	System.out.println("Starting from the beginning of the list");
        }
		sc = new Scanner(content);
    	for (int i = 0; i < count; i++)
    		sc.nextLine();
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
	    	ca.getCproject().setRunningTime(endProjectTime - startProjectTime);
			FileIO.writeObjectToFile(ca.getApiSpecifications(), outputPath + "/specs-sf/" + name + ".dat", false);
			ps.println(line + "," + (endProjectTime - startProjectTime) / 1000);
		}
		sc.close();
	}

}
