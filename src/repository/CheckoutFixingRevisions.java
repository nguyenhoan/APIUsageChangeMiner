package repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import utils.Config;

public class CheckoutFixingRevisions {

	public static void main(String[] args) throws FileNotFoundException {
		//String projectName = "jhotdraw", url = "http://" + projectName + ".svn.sourceforge.net/svnroot/" + projectName;
		//String projectName = "jedit", url = "https://jedit.svn.sourceforge.net/svnroot/jedit/jEdit/trunk/";
		//String projectName = "jedit", url = "https://jedit.svn.sourceforge.net/svnroot/jedit/plugins/";
		String projectName = "argouml", url = "https://seg-pc1.dnsalias.net:8443/svn/argouml/trunk/";
		
		SVNConnector conn = new SVNConnector(url, "hoan", "dobietday");
		conn.connect();
		
		Scanner sc = new Scanner(new File(Config.FIXREF_SUBJECT_SYSTEM_ROOT_PATH + "/" + projectName + "/fixes.csv"));
		while (sc.hasNextLine())
		{
			String line = sc.nextLine();
			long revision = Long.parseLong(line);
			System.out.println(revision);
			conn.update(Config.FIXREF_SUBJECT_SYSTEM_ROOT_PATH + "/" + projectName + "/" + (revision-1) + "/trunk", revision-1);
			conn.update(Config.FIXREF_SUBJECT_SYSTEM_ROOT_PATH + "/" + projectName + "/" + revision + "/trunk", revision);
		}
		sc.close();
	}

}
