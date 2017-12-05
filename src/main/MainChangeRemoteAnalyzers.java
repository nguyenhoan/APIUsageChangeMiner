package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import change.CTokenSequence;
import change.ChangeAnalyzer;
import change.TokenSequenceChange;

import utils.FileIO;

public class MainChangeRemoteAnalyzers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String n = "2";
		if (args.length > 0)
			n = args[0];
		String outputPath = "./ChangeRepetitiveness";
		String inputPath = ".";
		String svnRootPath = "https://co3218.engineering.iastate.edu/svn/";
		String content = FileIO.readStringFromFile(inputPath + "/java-svn-latest-" + n + ".csv");
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
			String[] parts = line.split(",");
			int id = Integer.parseInt(parts[0]);
			String name = FileIO.getSVNRepoRootName(parts[parts.length - 2]);
			String url = svnRootPath + name;
			TokenSequenceChange.changes.clear();
			System.out.println("Project " + (++count) + ": " + name + " " + id);
			//ChangeAnalyzer ca = new ChangeAnalyzer(name, id, url, 11610, 11619);
			ChangeAnalyzer ca = new ChangeAnalyzer(name, id, url);
			ca.buildSvnConnector();
			/*ca.buildLogEntries();
			ca.analyze();*/
			ca.buildLogAndAnalyze();
	    	long endProjectTime = System.currentTimeMillis();
			/*CProject cp = new CProject();
	    	cp.setId(id);
	    	cp.setName(name);
	    	cp.setRevisions(ca.getCReivisions());
			cp.setIndexTokens(CTokenSequence.indexTokens);
			cp.setTokenIndexes(CTokenSequence.tokenIndexes);
			cp.setcTrees(CTokenSequence.cTrees);
			cp.setChanges(TokenSequenceChange.changes);
			cp.setRunningTime(endProjectTime - startProjectTime);
			FileIO.writeObjectToFile(cp, outputPath + "/" + name + "-" + id + ".dat", true);*/
			FileIO.writeObjectToFile(CTokenSequence.indexTokens, outputPath + "/" + name + "-" + id + "-indexTokens.dat", false);
			FileIO.writeObjectToFile(CTokenSequence.tokenIndexes, outputPath + "/" + name + "-" + id + "-tokenIndexes.dat", false);
			FileIO.writeObjectToFile(CTokenSequence.cTokenSequences, outputPath + "/" + name + "-" + id + "-trees.dat", false);
			FileIO.writeObjectToFile(TokenSequenceChange.changes, outputPath + "/" + name + "-" + id + "-changes.dat", false);
			TokenSequenceChange.changes.clear();
			CTokenSequence.indexTokens.clear();
			CTokenSequence.tokenIndexes.clear();
			CTokenSequence.cTokenSequences.clear();
			for (int i = 0; i < 128; i++) {
				CTokenSequence.cTokenSequences.add(new HashMap<Integer, List<CTokenSequence>>());
			}
			ps.println(line + "," + (endProjectTime - startProjectTime) / 1000);
		}
		sc.close();
	}

}
