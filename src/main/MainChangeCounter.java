package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import change.CProject;
import change.ChangeAnalyzer;
import change.TokenSequenceChange;

import utils.FileIO;

public class MainChangeCounter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String n = "3";
		if (args.length > 0)
			n = args[0];
		
		String outputPath = "F:/changeanalysis/systemstats";
		String inputPath = "S:/sourceforge";
		/*String outputPath = "G:/sourceforge/ChangeRepetitiveness";
		String inputPath = "G:/sourceforge";*/
		/*String outputPath = "./systemstats";
		String inputPath = ".";*/
		String svnRootPath = "file:///" + inputPath + "/reposotories/";
		//String svnRootPath = "https://co3218.engineering.iastate.edu/svn/";
		String content = FileIO.readStringFromFile(inputPath + "/java-svn-latest-" + n + ".csv");
    	String fileDoneName = "change-count-done-" + n + ".csv";
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
			String[] parts = line.split(",");
			int id = Integer.parseInt(parts[0]);
			String name = FileIO.getSVNRepoRootName(parts[parts.length - 2]);
			String url = svnRootPath + name;
			TokenSequenceChange.changes.clear();
			System.out.println("Project " + (++count) + ": " + name + " " + id);
			//ChangeAnalyzer ca = new ChangeAnalyzer(name, id, url, 11610, 11619);
			ChangeAnalyzer ca = new ChangeAnalyzer(name, id, url);
			ca.buildSvnConnector();
			ca.buildLogAndAnalyze();
	    	long endProjectTime = System.currentTimeMillis();
	    	CProject cp = ca.getCproject();
	    	cp.setRunningTime(endProjectTime - startProjectTime);
	    	System.out.println(name + "\t" + cp.getNumOfAllRevisions() + "\t" + cp.getRevisions().size());
	    	FileIO.writeObjectToFile(cp, outputPath + "/" + name + "-" + id + "-count.dat", false);
			ps.println(line + "," + (endProjectTime - startProjectTime));
		}
		sc.close();
	}

}
