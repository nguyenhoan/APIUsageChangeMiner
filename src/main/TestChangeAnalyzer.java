package main;

import utils.FileIO;
import change.ChangeAnalyzer;

public class TestChangeAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String projectName = "jhotdraw", svnUrl = "file:///D:/Repositories/jhotdraw/trunk";
		//String projectName = "test", svnUrl = "file:///H:/Repositories/test/trunk";
		//String projectName = "funambol", svnUrl = "http://funambol.svn.sourceforge.net/svnroot/funambol"; // 156, 754, 4760
		//String projectName = "funambol", svnUrl = "https://co3218.engineering.iastate.edu/svn/funambol/"; // 21, 754, 4760
		//String projectName = "gridarta", svnUrl = "https://co3218.engineering.iastate.edu/svn/gridarta/"; // 8879
		//String projectName = "gridarta", svnUrl = "http://gridarta.svn.sourceforge.net/svnroot/gridarta"; // 8879
		//String projectName = "funambol", svnUrl = "file:///G:/sourceforge/reposotories/funambol"; // 754, 4760
		//String projectName = "msscodefactory", svnUrl = "file:///G:/sourceforge/reposotories/msscodefactory"; // 3123
		//String projectName = "jmol", svnUrl = "file:///F:/sourceforge/repositories/jmol"; // 854
		//String projectName = "orcc", svnUrl = "file:///G:/sourceforge/reposotories/orcc"; // 5392
		//String projectName = "a-tides", svnUrl = "http://a-tides.svn.sourceforge.net/svnroot/a-tides"; // 117, 192
		//String projectName = "x10", svnUrl = "http://x10.svn.sourceforge.net/svnroot/x10"; // 37
		//String projectName = "kalypsobase", svnUrl = "http://kalypsobase.svn.sourceforge.net/svnroot/kalypsobase"; // 44717
		//String projectName = "jquant", svnUrl = "http://jquant.svn.sourceforge.net/svnroot/jquant"; // 931
		//String projectName = "groove", svnUrl = "file:///G:/sourceforge/reposotories/groove"; // 1603
		//String projectName = "matsim", svnUrl = "file:///G:/sourceforge/reposotories/matsim"; // 3286
		//String projectName = "echarts", svnUrl = "file:///G:/sourceforge/reposotories/echarts"; // 2391
		//String projectName = "cleanj", svnUrl = "file:///G:/sourceforge/reposotories/cleanj"; // 29
		//String projectName = "qvtparser", svnUrl = "file:///G:/sourceforge/reposotories/qvtparser"; // 15, 16 17, 76
		//String projectName = "dshub", svnUrl = "file:///G:/sourceforge/reposotories/dshub"; // 12, 13, 15
		//String projectName = "boardcad", svnUrl = "file:///G:/sourceforge/reposotories/boardcad"; // 255
		//String projectName = "akmemobilemaps", svnUrl = "file:///G:/sourceforge/reposotories/akmemobilemaps"; // 51
		//String projectName = "webmapreduce", svnUrl = "file:///G:/sourceforge/reposotories/webmapreduce"; // 459
		//String projectName = "rcplayer", svnUrl = "file:///S:/sourceforge/reposotories/rcplayer"; // 12
		//String projectName = "jtimeseries", svnUrl = "file:///G:/sourceforge/reposotories/jtimeseries"; // 214
		//String projectName = "msscodefactory", svnUrl = "file:///F:/sourceforge/repositories/msscodefactory"; // 3271
		String projectName = "ordrumbox", svnUrl = "file:///F:/sourceforge/repositories/ordrumbox"; // all
		ChangeAnalyzer ca = new ChangeAnalyzer(projectName, 1, svnUrl, 12, 12);
		//ChangeAnalyzer ca = new ChangeAnalyzer(projectName, 1, svnUrl, 854, 854);
		ca.buildSvnConnector();
		/*ca.buildLogEntries();
		ca.analyze();*/
		ca.buildLogAndAnalyze();
		FileIO.writeObjectToFile(ca.getApiSpecifications(), projectName + ".dat", false);
		/*for (RevisionAnalyzer ra : ca.getRevisionAnalyzers()) {
			System.out.println("Revision: " + ra.getRevision());
			HashSet<CMethod> methodsM = ra.getMappedMethodsM(), methodsN = ra.getMappedMethodsN();
			HashSet<CField> fieldsM = ra.getMappedFieldsM(), fieldsN = ra.getMappedFieldsN();
			HashSet<CInitializer> initsM = ra.getMappedInitsM(), initsN = ra.getMappedInitsN();
			for (CMethod e : methodsM) {
				String diff = e.printTree();
				System.out.println("Method: " + e.getQualName());
				System.out.println(diff);
			}
			for (CField e : fieldsM) {
				String diff = e.printTree();
				System.out.println("Field: " + e.getQualName());
				System.out.println(diff);
			}
			for (CInitializer e : initsM) {
				String diff = e.printTree();
				System.out.println("Init: " + e.getQualName());
				System.out.println(diff);
			}
		}*/
		System.out.println();
	}

}
