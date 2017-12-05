package repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.util.io.NullOutputStream;

public class GitConnector {
	private String url;
	private Git git;
	public Git getGit() {
		return git;
	}
	private Repository repository;
	public Repository getRepository() {
		return repository;
	}
	
	public GitConnector(String url) {
		this.url = url;
	}
	
	public boolean connect() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			repository = builder.setGitDir(new File(url))
			  .readEnvironment() // scan environment GIT_* variables
			  .findGitDir() // scan up the file system tree
			  .build();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
		git = new Git(repository);
		return true;
	}
	
	public Iterable<RevCommit> log() {
		try {
			return git.log().call();
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public void getFileChanges(String extension) {
		Iterable<RevCommit> commits = null;
		try {
			commits = git.log().call();
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		if (commits == null) return;
		for (RevCommit commit : commits) {
			if (commit.getParentCount() > 0) {
				RevWalk rw = new RevWalk(repository);
				RevCommit parent = null;
				try {
					parent = rw.parseCommit(commit.getParent(0).getId());
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				if (parent == null) continue;
				DiffFormatter df = new DiffFormatter(NullOutputStream.INSTANCE);
				df.setRepository(repository);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);
				if (extension != null)
					df.setPathFilter(PathSuffixFilter.create(extension));
				List<DiffEntry> diffs = null;
				try {
					diffs = df.scan(parent.getTree(), commit.getTree());
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				if (diffs == null) continue;
				if (!diffs.isEmpty()) {
					System.out.println(commit.getName());
					System.out.println(commit.getFullMessage());
					for (DiffEntry diff : diffs) {
						if (diff.getOldMode().getObjectType() == Constants.OBJ_BLOB && diff.getNewMode().getObjectType() == Constants.OBJ_BLOB) {
							System.out.println(diff.getChangeType() + ": " + diff.getOldPath() + " --> " + diff.getNewPath());
							ObjectLoader ldr = null;
							String oldContent = null, newContent = null;
							try {
								ldr = repository.open(diff.getOldId().toObjectId(), Constants.OBJ_BLOB);
								oldContent = new String(ldr.getCachedBytes());
							} catch (IOException e) {
								System.err.println(e.getMessage());
							}
							try {
								ldr = repository.open(diff.getNewId().toObjectId(), Constants.OBJ_BLOB);
								newContent = new String(ldr.getCachedBytes());
							} catch (IOException e) {
								System.err.println(e.getMessage());
							}
							System.out.println(oldContent);
							System.out.println(newContent);
						}
					}
				}
			}
		}
	}
	
	public String getFileContent(ObjectId objectId, int objectType) {
		String content = null;
		try {
			ObjectLoader ldr = repository.open(objectId, objectType);
			content = new String(ldr.getCachedBytes());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return content;
	}

	private String getFileContent(ObjectId objectId) {
		return getFileContent(objectId, Constants.OBJ_BLOB);
	}
	
	public void getLastSnapshot(String extension) {
		RevWalk rw = new RevWalk(repository);
		try {
			ObjectId object = repository.resolve(Constants.HEAD);
			RevCommit commit = rw.parseCommit(object);
			TreeWalk tw = new TreeWalk(repository);
			tw.reset();
			try {
				tw.addTree(commit.getTree());
				tw.setRecursive(true);
				while (tw.next()) {
					if (!tw.isSubtree()) {
						String path = tw.getPathString();
						if (extension == null || path.endsWith(extension)) {
							System.out.println(path);
							System.out.println(getFileContent(tw.getObjectId(0)));
						}
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			tw.close();
		} catch (RevisionSyntaxException | IOException e) {
			System.err.println(e.getMessage());
		}
		rw.close();
	}
	
	public void getSnapshots(String extension) {
		Iterable<RevCommit> commits = null;
		try {
			commits = git.log().call();
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		if (commits == null) return;
		for (RevCommit commit : commits) {
			TreeWalk tw = new TreeWalk(repository);
			tw.reset();
			try {
				tw.addTree(commit.getTree());
				tw.setRecursive(true);
				while (tw.next()) {
					if (!tw.isSubtree()) {
						String path = tw.getPathString();
						if (extension == null || path.endsWith(extension)) {
							System.out.println(path);
							System.out.println(getFileContent(tw.getObjectId(0)));
						}
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				continue;
			}
			tw.close();
		}
	}
}
