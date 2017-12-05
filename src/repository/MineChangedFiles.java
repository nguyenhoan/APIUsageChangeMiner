package repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

import utils.Config;
import utils.FileIO;
import utils.StringProcessor;

public class MineChangedFiles {

	public static void main(String[] args) {
		String n = "debug";
		if (args.length > 0)
			n = args[0];
		
		String inputPath = Config.svnRootPath;
		String outputPath = "D:/changedfiles";
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
		File inDir = new File(inputPath);
		inputPath = inDir.getAbsolutePath();
		String svnRootPath = "file:///" + inputPath + "/repositories/";
		File outDir = new File(outputPath);
		outDir = new File(outDir.getAbsolutePath());
		if (!outDir.exists()) outDir.mkdirs();
    	String fileDoneName = "file-change-extract-" + n +"-done.csv";
    	Scanner sc = null;
    	HashSet<String> doneNames = new HashSet<>();
    	try {
        	sc = new Scanner(new File(outDir.getAbsolutePath() + "/" + fileDoneName));
        	while (sc.hasNextLine()) {
        		String line = sc.nextLine().trim();
        		if (line.isEmpty()) break;
        		int index = line.indexOf(',');
        		doneNames.add(line.substring(0, index));
        	}
        } catch (FileNotFoundException e) {
        	System.out.println("Starting from the beginning of the list");
        }
    	FileOutputStream out; 
		PrintStream ps = null;
        try {
    		// Create a new file output stream
    		out = new FileOutputStream(outDir.getAbsolutePath() + "/" + fileDoneName, true);
    		// Connect print stream to the output stream
    		ps = new PrintStream(out);
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	String content = FileIO.readStringFromFile(inputPath + "/java-svn-revisions-rank-even-" + n + ".csv");
    	sc = new Scanner(content);
    	int i = 0;
    	while (sc.hasNextLine()) {
    		String line = sc.nextLine();
			final String projectName = line.substring(0, line.indexOf(','));
			String url = svnRootPath + projectName;
			if (doneNames.contains(projectName)) continue;
			System.out.println("Project " + (++i) + ": " + projectName);
			SVNConnector svnConn = new SVNConnector(url, "guest", "guest");
			if (svnConn.connect()) {
				long startProjectTime = System.currentTimeMillis();
				Collection<SVNLogEntry> logEntries = new ArrayList<>();
				svnConn.setLatestRevision();
				long start = 1, endRevision = svnConn.getLatestRevision();
				while (start <= endRevision) {
					long end = start + 99;
					if (end > endRevision) {
						end = endRevision;
					}
			        try {
			            logEntries.addAll(svnConn.getRepository().log(new String[] {""}, null, start, end, true, true));
			        } catch (SVNException svne) {
			            System.out.println("Error while collecting log information for '"
			                    + url + "': " + svne.getMessage());
			            logEntries = null;
			            break;
			        }
					start = end + 1;
					//System.out.print(logEntries.size() + " ");
				}
		        if (logEntries == null) {
					ps.println(projectName + ",0");
		            continue;
		        }
		        File projectDir = new File(outDir.getAbsolutePath() + "/" + projectName);
		        if (!projectDir.exists()) projectDir.mkdir();
		        int digits = (String.valueOf(logEntries.size()).length());
		        for (SVNLogEntry logEntry : logEntries) {
		        	System.out.println("Revision: " + logEntry.getRevision() + "/" + logEntries.size());
		        	StringBuilder sb = new StringBuilder();
		        	HashMap<String, String> copiedPaths = new HashMap<>();
		        	HashMap<String, Long> copiedRevisions = new HashMap<>();
		            Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();
	                for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
	                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
	                    String path = entryPath.getPath();
	                    if (entryPath.getCopyPath() != null) {
	                    	copiedPaths.put(path, entryPath.getCopyPath());
	                    	copiedRevisions.put(path, entryPath.getCopyRevision());
	                    }
	                }
	                for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
	                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
	                    String path = entryPath.getPath();
	                    if(path.endsWith(".java") && entryPath.getKind() == SVNNodeKind.FILE) {
	                    	if (entryPath.getType() == SVNLogEntryPath.TYPE_MODIFIED) {
	                    		//if (projectName.equals("deftproject") && path.endsWith("/CSharpParser.java"))
	                    		if (path.endsWith("Parser.java") || path.endsWith("Lexer.java"))
	                    			continue;
	                    		//System.out.println(name + " " + logEntry.getRevision() + " " + path);
	                    		String oldPath = path;
	                    		long oldRevision = logEntry.getRevision() - 1;
	    	                    if (copiedPaths.containsKey(path)) {
	                    			oldPath = copiedPaths.get(path);
	                    		}
	                    		else {
	                    			String prefix = "";
	                    			for (String copiedPath : copiedPaths.keySet()) {
	                    				if (path.startsWith(copiedPath)) {
	                    					if (copiedPath.length() > prefix.length()) {
	                    						prefix = copiedPath;
	                    					}
	                    				}
	                    			}
	                    			if (!prefix.isEmpty()) {
	                					oldPath = copiedPaths.get(prefix) + path.substring(prefix.length());
	                					oldRevision = copiedRevisions.get(prefix);
	                    			}
	                    		}
		                    	String contentM = svnConn.getFile(oldPath, oldRevision), contentN = svnConn.getFile(path, logEntry.getRevision());
		                    	ArrayList<String> linesM = getLines(contentM), linesN = getLines(contentN);
		                		ArrayList<Integer> lcsM = new ArrayList<Integer>();
		                		ArrayList<Integer> lcsN = new ArrayList<Integer>();
		                		StringProcessor.doLCS(linesM, linesN, 0, 0, lcsM, lcsN);
		                    	sb.append(path + "," + linesM.size() + "," + linesN.size() + "," + (linesM.size() - lcsM.size()) + "," + (linesN.size() - lcsN.size()) + "\n");
	                    	}
	                    }
	                }
	                if (sb.length() > 8)
	                	FileIO.writeStringToFile(sb.toString(), projectDir.getAbsolutePath() + "/" + String.format("%0" + digits + "d", logEntry.getRevision()) + ".csv");
		        }
				long endProjectTime = System.currentTimeMillis();
				ps.println(projectName + "," + (endProjectTime - startProjectTime));
			}
			else {
				System.err.println("Error: Cannot connect to the repository at " + url);
				ps.println(projectName + ",0");
			}
		}
    	sc.close();
	}

	private static ArrayList<String> getLines(String content) {
		ArrayList<String> lines = new ArrayList<>();
		Scanner sc = new Scanner(content);
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		sc.close();
		return lines;
	}

}
