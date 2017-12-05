package change;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import repository.GitConnector;
import repository.SVNConnector;
import utils.FileIO;

public class ChangeAnalyzer {
	private String projectName;
	private int projectId;
	private String url;
	private long startRevision = -1, endRevision = -1;
	private SVNConnector svnConn;
	private GitConnector gitConn;
	private HashMap<Long, SVNLogEntry> logEntries;
	private CProject cproject;
	private HashMap<String, HashMap<String, HashSet<HashSet<String>>>> apiSpecifications = new HashMap<>();
	private int numOfCodeRevisions;
	private int numOfRevisions;
	
	public ChangeAnalyzer(String projectName, int projectId, String svnUrl, long start, long end) {
		this.projectName = projectName;
		this.projectId = projectId;
		this.url = svnUrl;
		this.startRevision = start;
		this.endRevision = end;
	}
	
	public ChangeAnalyzer(String projectName, int projectId, String url) {
		this.projectName = projectName;
		this.projectId = projectId;
		this.url = url;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public SVNConnector getSvnConn() {
		return this.svnConn;
	}

	public long getStartRevision() {
		return startRevision;
	}

	public long getEndRevision() {
		return endRevision;
	}
	
	public SVNLogEntry getLogEntry(long revision) {
		return this.logEntries.get(revision);
	}

	public CProject getCproject() {
		return cproject;
	}

	public HashMap<String, HashMap<String, HashSet<HashSet<String>>>> getApiSpecifications() {
		return apiSpecifications;
	}

	public void buildSvnConnector() {
		svnConn = new SVNConnector(url, "guest", "guest");
		svnConn.connect();
		if (this.startRevision == -1) {
			this.startRevision = 1;
			svnConn.setLatestRevision();
			this.endRevision = svnConn.getLatestRevision();
		}
	}

	public void buildGitConnector() {
		this.gitConn = new GitConnector(url + "/.git");
		this.gitConn.connect();
	}
	
	public void buildLogEntries() {
		this.logEntries = new HashMap<Long, SVNLogEntry>();
        long start = this.startRevision;
		while (start <= this.endRevision) {
			long end = start + 99;
			if (end > this.endRevision) {
				end = this.endRevision;
			}
			buildLogEntries(start, end);
			start = end + 1;
			System.out.print(this.logEntries.size() + " ");
		}
		System.out.println();
	}
	
	public void buildLogEntries(long startRevision, long endRevision) {
		Collection<?> logEntries = null;
        try {
            logEntries = svnConn.getRepository().log(new String[] {""}, null, startRevision, endRevision, true, true);
        } catch (SVNException svne) {
            System.out.println("Error while collecting log information for '"
                    + url + "': " + svne.getMessage());
            return;
            //System.exit(1);
        }
        for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
            if (SVNConnector.isFixingCommit(logEntry.getMessage()))
            	this.logEntries.put(logEntry.getRevision(), logEntry);
        }
	}
	
	public void buildLogAndAnalyze() {
		this.cproject = new CProject(projectId, projectName);
		this.cproject.numOfAllRevisions = this.endRevision - this.startRevision + 1;
		this.cproject.revisions = new ArrayList<CRevision>();
		
		this.logEntries = new HashMap<Long, SVNLogEntry>();
        long start = this.startRevision;
		while (start <= this.endRevision) {
			long end = start + 99;
			if (end > this.endRevision) {
				end = this.endRevision;
			}
			buildLogEntries(start, end);
			analyze(start, end);
			start = end + 1;
			//System.out.print(this.logEntries.size() + " ");
		}
		System.out.println();
	}

	public void analyzeGit() {
		this.cproject = new CProject(projectId, projectName);
		Iterable<RevCommit> commits = null;
		try {
			commits = this.gitConn.getGit().log().call();
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		if (commits == null) return;
		this.numOfCodeRevisions = 0; this.numOfRevisions = 0;
		for (RevCommit commit : commits) {
			this.numOfRevisions++;
			String r = commit.getName();
			//System.out.println("Analyzing revision: " + this.numOfRevisions + " " + r);
			RevisionAnalyzer ra = new RevisionAnalyzer(this, commit);
			boolean analyzed = ra.analyzeGit();
			if (analyzed) {
				HashSet<CMethod> methods = new HashSet<>(ra.getMappedMethodsN());
				for (CMethod e : methods) {
					//System.out.println("Method: " + e.getQualName() + " - " + e.getMappedEntity().getQualName());
					e.buildDependencies();
					e.getMappedMethod().buildDependencies();
					File rdir = new File("T:/api-fixes/iterator/" + this.projectName + "/" + r);
					if (!rdir.exists()) rdir.mkdirs();
					FileIO.writeStringToFile("https://github.com/" + projectName + "/commit/" + r + "\n" + commit.getFullMessage(), rdir.getAbsolutePath() + "/sum.txt");
					File cdir = new File(rdir, "" + (rdir.list().length));
					if (!cdir.exists()) cdir.mkdir();
					FileIO.writeStringToFile(e.getDeclaration().toString(), cdir.getAbsolutePath() + "/good.java");
					FileIO.writeStringToFile(e.getMappedMethod().getDeclaration().toString(), cdir.getAbsolutePath() + "/bad.java");
					StringBuilder sb = new StringBuilder();
					sb.append(e.getMappedMethod().getCFile().getPath() + " --> " + e.getCFile().getPath() + "\n");
					sb.append(e.getMappedMethod().getFullName() + " --> " + e.getFullName() + "\n");
					FileIO.writeStringToFile(sb.toString(), cdir.getAbsolutePath() + "/sum.txt");
					e.cleanForStats();
				}
			}
		}
		this.cproject.numOfAllRevisions = this.numOfRevisions;
	}

	public void analyze() {
		analyze(this.startRevision, this.endRevision);
	}
		
	private void analyze(long startRevision, long endRevision)
	{
		for (long r = startRevision; r <= endRevision; r++)
		{
			if (this.logEntries.containsKey(r)) {
				//System.out.println("Analyzing revision: " + r);
				SVNLogEntry logEntry = this.logEntries.get(r);
				if (logEntry != null && logEntry.getDate() != null) {
					RevisionAnalyzer ra = new RevisionAnalyzer(this, r);
					boolean analyzed = ra.analyze();
					if (analyzed) {
						if (ra.crevision != null) {
							this.cproject.revisions.add(ra.crevision);
						}
						if (!ra.getMappedMethodsM().isEmpty())
							ra.crevision.methods = new ArrayList<CMethod>();
						HashSet<CMethod> methods = new HashSet<>(ra.getMappedMethodsN());
						for (CMethod e : methods) {
							//System.out.println("Method: " + e.getQualName() + " - " + e.getMappedEntity().getQualName());
							e.buildDependencies();
							e.getMappedMethod().buildDependencies();
							File rdir = new File("F:/crypto-fixes-svn/" + this.projectName + "/" + r);
							if (!rdir.exists()) rdir.mkdirs();
							FileIO.writeStringToFile(logEntry.getMessage(), rdir.getAbsolutePath() + "/sum.txt");
							File cdir = new File(rdir, "" + (rdir.list().length));
							if (!cdir.exists()) cdir.mkdir();
							FileIO.writeStringToFile(e.getDeclaration().toString(), cdir.getAbsolutePath() + "/new.java");
							FileIO.writeStringToFile(e.getMappedMethod().getDeclaration().toString(), cdir.getAbsolutePath() + "/old.java");
							StringBuilder sb = new StringBuilder();
							sb.append(e.getMappedMethod().getCFile().getPath() + " --> " + e.getCFile().getPath() + "\n");
							sb.append(e.getMappedMethod().getFullName() + " --> " + e.getFullName() + "\n");
							FileIO.writeStringToFile(sb.toString(), cdir.getAbsolutePath() + "/sum.txt");
							e.cleanForStats();
						}
					}
				}
			}
		}
	}

	public String getSourceCode(String changedPath, long revision) {
		return this.svnConn.getFile(changedPath, revision);
	}

	public void incrementNumOfCodeRevisions() {
		this.numOfCodeRevisions++;
	}

	public GitConnector getGitConn() {
		return this.gitConn;
	}
}
