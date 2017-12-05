package change;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CTokenSequence implements Serializable, Comparable<CTokenSequence> {
	private static final long serialVersionUID = -1856726071348966796L;

	public static List<HashMap<Integer, List<CTokenSequence>>> cTokenSequences = new ArrayList<HashMap<Integer,List<CTokenSequence>>>();
	
	static {
		for (int i = 0; i < 128; i++) {
			cTokenSequences.add(new HashMap<Integer, List<CTokenSequence>>());
		}
	}
	
	public static HashMap<String, Integer> tokenIndexes = new HashMap<String, Integer>();
	public static HashMap<Integer, String> indexTokens = new HashMap<Integer, String>();

	private static CTokenSequence temp = new CTokenSequence();
	private static long numOfInstances = 0; 
	private long id;
	private byte height = 0;
	private String tokenString;
	
	private CTokenSequence() {
	}
	
	private CTokenSequence(byte height, String s) {
		this.height = height;
		this.tokenString = s;
	}
	
	public long getId() {
		return id;
	}

	public String getTokenString() {
		return tokenString;
	}
	
	public int getDensity() {
		return this.tokenString.length() / this.height;
	}

	public int getSize() {
		return this.tokenString.split(" ").length;
	}
	
	public static int getTokenIndex(String token) {
		int index = CTokenSequence.tokenIndexes.size();
		if (CTokenSequence.tokenIndexes.containsKey(token)) {
			index = CTokenSequence.tokenIndexes.get(token);
		}
		else {
			CTokenSequence.tokenIndexes.put(token, index);
			CTokenSequence.indexTokens.put(index, token);
		}
		return index;
	}
	
	public static CTokenSequence getTokenSequence(CTree tree) {
		temp.height = tree.height;
		temp.tokenString = tree.toIndexString();
		List<CTokenSequence> list = cTokenSequences.get(tree.height).get(temp.tokenString.hashCode());
		if (list == null) {
			list = new ArrayList<CTokenSequence>();
			cTokenSequences.get(tree.height).put(temp.tokenString.hashCode(), list);
		}
		int index = Collections.binarySearch(list, temp, new Comparator<CTokenSequence>() {
			@Override
			public int compare(CTokenSequence s1, CTokenSequence s2) {
				return s1.tokenString.compareTo(s2.tokenString);
			}
		});
		if (index < 0) {
			index = -index - 1;
			list.add(index, temp);
			temp.id = ++numOfInstances;
			temp = new CTokenSequence();
		}
		return list.get(index);
	}

	public String getCodeTokenString() {
		StringBuilder sb = new StringBuilder();
		String[] tokens = this.tokenString.split(" ");
		int id = Integer.parseInt(tokens[0]);
		String token = indexTokens.get(id);
		sb.append(token);
		for (int i = 1; i < tokens.length; i++) {
			id = Integer.parseInt(tokens[i]);
			token = indexTokens.get(id);
			sb.append(" " + token);
		}
		return sb.toString();
	}

	public ArrayList<String> getSequence() {
		ArrayList<String> sequence = new ArrayList<String>();
		String[] tokens = this.tokenString.split(" ");
		sequence.add(tokens[0]);
		int id = -1;
		String token = "";
		for (int i = 1; i < tokens.length; i++) {
			id = Integer.parseInt(tokens[i]);
			token = indexTokens.get(id);
			sequence.add(token);
		}
		return sequence;
	}
	
	@Override
	public String toString() {
		return this.tokenString;
	}

	public static CTokenSequence getTokenSequence(CTokenSequence cts, HashMap<Integer, String> indexTokens) {
		if (cts == null) return null;
		StringBuilder sb = new StringBuilder();
		String[] indexes = cts.tokenString.split(" ");
		int id = Integer.parseInt(indexes[0]);
		String token = indexTokens.get(id);
		id = getTokenIndex(token);
		sb.append(id);
		for (int i = 1; i < indexes.length; i++) {
			id = Integer.parseInt(indexes[i]);
			token = indexTokens.get(id);
			id = getTokenIndex(token);
			sb.append(" " + id);
		}
		temp.height = cts.height;
		temp.tokenString = sb.toString();
		List<CTokenSequence> list = cTokenSequences.get(temp.height).get(temp.tokenString.hashCode());
		if (list == null) {
			list = new ArrayList<CTokenSequence>();
			cTokenSequences.get(temp.height).put(temp.tokenString.hashCode(), list);
		}
		int index = Collections.binarySearch(list, temp, new Comparator<CTokenSequence>() {
			@Override
			public int compare(CTokenSequence s1, CTokenSequence s2) {
				return s1.tokenString.compareTo(s2.tokenString);
			}
		});
		if (index < 0) {
			index = -index - 1;
			list.add(index, temp);
			temp.id = ++numOfInstances;
			temp = new CTokenSequence();
		}
		return list.get(index);
	}

	@Override
	public int compareTo(CTokenSequence other) {
		/*if (this.height != other.height) return this.height - other.height;
		if (this.tokenString.length() != other.tokenString.length()) return this.tokenString.length() - other.tokenString.length();*/
		//return this.tokenString.compareTo(other.tokenString);
		//return System.identityHashCode(this) - System.identityHashCode(other);
		if (this.id < other.id) return -1;
		if (this.id > other.id) return 1;
		return 0;
	}
	
	public static int compare(CTokenSequence cts1, CTokenSequence cts2) {
		if (cts1 == null) {
			if (cts2 == null) return 0;
			return -1;
		}
		else {
			if (cts2 == null) return 1;
			return cts1.compareTo(cts2);
		}
	}

	public static boolean isRenaming(CTokenSequence cts3, CTokenSequence cts4) {
		if (cts3 == null && cts4 == null) return true;
		if (cts3 != null && cts4 != null && cts3.compareTo(cts4) == 0) return true;
		/*ArrayList<String> sequence1 = cts3.getSequence(), sequence2 = cts4.getSequence();
		ArrayList<Integer> lcsM = new ArrayList<>(), lcsN = new ArrayList<>();
		StringProcessor.doLCS(sequence1, sequence2, 0, 0, lcsM , lcsN);*/
		return false;
	}
}
