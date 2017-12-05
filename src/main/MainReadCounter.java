package main;

import java.io.File;

import utils.FileIO;

import change.CProject;
import change.CRevision;

public class MainReadCounter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		long numOfProjects = 0, numOfAllRevisions = 0, numOfJavaRevisions = 0, numOfFiles = 0;
		String dirPath = "F:/changeanalysis/ChangeRepetitiveness";
		File dir = new File(dirPath);
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith("-info.dat")) {
				CProject cp = (CProject) FileIO.readObjectFromFile(file.getAbsolutePath());
				System.out.println(cp.getName());
				if (cp.getRevisions().isEmpty())
					continue;
				numOfProjects++;
				numOfAllRevisions += cp.getNumOfAllRevisions();
				numOfJavaRevisions += cp.getRevisions().size();
				int files = 0;
				for (CRevision cr : cp.getRevisions()) {
					files += cr.getNumOfFiles();
				}
				numOfFiles += files;
				sb.append(cp.getName() + "," + cp.getNumOfAllRevisions() + "," + cp.getRevisions().size() + "," + files + "\r\n");
			}
		}
		System.out.println(numOfProjects + "\t" + numOfAllRevisions + "\t" + numOfJavaRevisions + "\t" + numOfFiles);
		//FileIO.writeStringToFile(sb.toString(), dirPath + "/sum.csv");
	}

}
