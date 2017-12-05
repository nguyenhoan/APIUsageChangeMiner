package main;

import java.util.Scanner;

import change.CProject;

import utils.Config;
import utils.FileIO;

public class MainSumDensity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numOfHeights = 10 - 2 + 1, numOfDensities = CProject.densityBins.length;
		StringBuilder[] sbs = new StringBuilder[9];
		for (int i = 2; i <= 10; i++) {
			sbs[i-2] = new StringBuilder();
			sbs[i-2].append("Project");
			for (int j = 0; j < CProject.densityBins.length-1; j++) {
				sbs[i-2].append("," + CProject.densityBins[j]);
			}
			sbs[i-2].append(",More");
			sbs[i-2].append("\r\n");
		}
		double[][] repeats = new double[numOfHeights][numOfDensities], total = new double[numOfHeights][numOfDensities];
		for (int i = 0; i < numOfHeights; i++) {
			for (int j = 0; j < numOfDensities; j++) {
				repeats[i][j] = 0.0;
			}
		}
		String rootPath = Config.svnRootPath;
		String content = FileIO.readStringFromFile(rootPath + "/java-svn-revisions-rank.csv");
		Scanner sc = new Scanner(content);
		int count = 0;
		while (sc.hasNextLine()) {
			count++;
			if (count > 2500) break;
			String line = sc.nextLine();
			System.out.println("Project " + count + ": " + line);
			String name = line.substring(0, line.indexOf(','));
			content = FileIO.readStringFromFile(rootPath + "/density/" + name + "-height-repetitions.csv");
			Scanner fsc = new Scanner(content);
			fsc.nextLine();
			for (int i = 2; i <= 10; i++){
				String fline = fsc.nextLine();
				sbs[i-2].append(name + fline.substring(fline.indexOf(',')) + "\r\n");
				String[] parts = fline.split(",");
				double[] values = new double[parts.length - 1];
				for (int j = 1; j < parts.length; j++) {
					if (!parts[j].equals("NaN")) {
						values[j-1] = Double.parseDouble(parts[j]);
						repeats[i-2][j-1] += values[j-1];
						total[i-2][j-1]++;
					}
				}
			}
			fsc.close();
		}
		count--;
		for (int i = 2; i <= 10; i++) {
			FileIO.writeStringToFile(sbs[i-2].toString(), rootPath + "/density/" + i + ".csv");
		}
		for (int i = 0; i < numOfHeights; i++) {
			for (int j = 0; j < numOfDensities; j++) {
				//if (total[i][j] != 0.0)
					repeats[i][j] /= total[i][j];
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Height");
		for (int j = 0; j < CProject.densityBins.length-1; j++) {
			sb.append("," + CProject.densityBins[j]);
		}
		sb.append(",More");
		sb.append("\r\n");
		for (int i = 0; i < numOfHeights; i++) {
			sb.append("Height " + (i + 2));
			for (int j = 0; j < numOfDensities; j++) {
				sb.append("," + repeats[i][j]);
			}
			sb.append("\r\n");
		}
		FileIO.writeStringToFile(sb.toString(), rootPath + "/density/avg.csv");
		sc.close();
	}

}
