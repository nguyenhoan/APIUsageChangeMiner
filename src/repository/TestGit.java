package repository;

import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

public class TestGit {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		GitConnector gc = new GitConnector("D:/writing/boa/muse14/.git");
		if (gc.connect()) {
			gc.getLastSnapshot(".tex");
			gc.getSnapshots(".tex");
			gc.getFileChanges(".tex");
		}
	}

}
