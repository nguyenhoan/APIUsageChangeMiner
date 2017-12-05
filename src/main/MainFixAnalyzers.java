package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import repository.SVNConnector;

import utils.FileIO;

public class MainFixAnalyzers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String outputPath = "D:/Repositories/fixes";
		String inputPath = "D:/Repositories";
		//String svnRootPath = "file:///" + inputPath + "/";
		//String svnRootPath = "https://192.168.10.30:8443/svn/";
		String svnRootPath = "http://triplea.svn.sourceforge.net/svnroot/";
		String content = FileIO.readStringFromFile(inputPath + "/java-svn-latest-debug.csv");
    	String fileDoneName = "fix-extract-done.csv";
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
    	int i = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] parts = line.split(",");
			long last = Integer.parseInt(parts[parts.length-1]);
			if (last <= 0)
				break;
			//int id = Integer.parseInt(parts[0]);
			String name = FileIO.getSVNRepoRootName(parts[parts.length - 2]);
			String url = svnRootPath + name;
			System.out.println((++i) + "\t" + name);
			
	    	SVNConnector conn = new SVNConnector(url, "", "");
			conn.connect();
			ArrayList<Integer> revs = conn.getJavaFixRevisions(27, -1);
			ps.print(name);
			for (int rev : revs) {
				ps.print("," + rev);
			}
			ps.println();
		}
		sc.close();
	}

}
