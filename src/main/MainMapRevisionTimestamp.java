package main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import repository.SVNConnector;

import utils.FileIO;

public class MainMapRevisionTimestamp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dirRootPath = "I:/sourceforge";
		String svnRootPath = "file:///" + dirRootPath + "/reposotories/";
		String content = FileIO.readStringFromFile(dirRootPath + "/java-svn-latest.csv");
		Scanner sc = new Scanner(content);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] parts = line.split(",");
			int id = Integer.parseInt(parts[0]);
			String name = FileIO.getSVNRepoRootName(parts[parts.length - 2]);
			String url = svnRootPath + name;
			System.out.println(url);
			SVNConnector conn = new SVNConnector(url, "", "");
			conn.connect();
			Collection<?> logEntries = null;
	        try {
	            logEntries = conn.getRepository().log(new String[] {""}, null, 1, -1, true, true);
	        } catch (SVNException svne) {
	            System.out.println("Error while collecting log information for '"
	                    + url + "': " + svne.getMessage());
	        }
	        HashMap<Long, Long> revisionTimestamp = new HashMap<Long, Long>(), timestampRevision = new HashMap<Long, Long>();
	        if (logEntries != null) {
		        for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
		            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
		            if (logEntry.getDate() != null) {
		            	long revision = logEntry.getRevision(), timestamp = logEntry.getDate().getTime();
		            	revisionTimestamp.put(revision, timestamp);
		            	timestampRevision.put(timestamp, revision);
		            }
		        }
		        FileIO.writeObjectToFile(revisionTimestamp, dirRootPath + "/timestamp/" + name + "-" + id + "-revisionTimestamp.dat", false);
		        FileIO.writeObjectToFile(timestampRevision, dirRootPath + "/timestamp/" + name + "-" + id + "-timestampRevision.dat", false);
	        }
		}
		sc.close();
	}

}
