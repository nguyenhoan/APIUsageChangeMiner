package main;

import java.io.File;

public class SummarizeUses {

	public static void main(String[] args) {
		int numOfProjects = 0, numOfCommits = 0, numOfMethods = 0;
		File dir = new File("T:/api-fixes/iterator");
		for (File user : dir.listFiles()) {
			if (user.isDirectory()) {
				for (File repo : user.listFiles()) {
					if (repo.isDirectory()) {
						numOfProjects++;
						for (File commit : repo.listFiles()) {
							if (commit.isDirectory()) {
								numOfCommits++;
								for (File method : commit.listFiles()) {
									if (method.isDirectory()) {
										numOfMethods++;
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Projects: " + numOfProjects);
		System.out.println("Commits: " + numOfCommits);
		System.out.println("Methods: " + numOfMethods);
	}

}
