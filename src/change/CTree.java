package change;

import java.util.ArrayList;
import java.util.List;

import utils.ASTFlattener;

public class CTree {
	byte height = 0, type = -1;
	List<CTree> parents = new ArrayList<CTree>();
	ArrayList<String> lexsymSequence, tokenSequence;
	//ArrayList<Integer> indexSequence;
	
	public CTree(byte type, byte height) {
		this.type = type;
		this.height = height;
	}

	public CTree(byte type, byte height, ArrayList<String> lexsymSequence) {
		this.type = type;
		this.height = height;
		this.lexsymSequence = new ArrayList<String>(lexsymSequence);
	}

	void abstractout() {
		this.tokenSequence = new ArrayList<String>();
		for (int i = 0; i < lexsymSequence.size(); i++) {
			String t = lexsymSequence.get(i);
			if (t.startsWith(ASTFlattener.PREFIX_LITERAL)) {
				tokenSequence.add(t.substring(ASTFlattener.PREFIX_LITERAL.length(), t.indexOf('|', ASTFlattener.PREFIX_LITERAL.length())));
			}
			else
				this.tokenSequence.add(t);
		}
	}
	
	/*void doIndexing() {
		this.indexSequence = new ArrayList<Integer>();
		for (int i = 0; i < this.tokenSequence.size(); i++) {
			String t = this.tokenSequence.get(i);
			int index = CTokenSequence.tokenIndexes.size();
			if (CTokenSequence.tokenIndexes.containsKey(t)) {
				index = CTokenSequence.tokenIndexes.get(t);
			}
			else {
				CTokenSequence.tokenIndexes.put(t, index);
				CTokenSequence.indexTokens.put(index, t);
			}
			this.indexSequence.add(index);
		}
	}*/
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CTree))
			return false;
		CTree other = (CTree) obj;
		return this.lexsymSequence.equals(other.lexsymSequence);
	}
	
	/*public String toIndexString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.indexSequence.get(0));
		for (int i = 1; i < this.indexSequence.size(); i++) {
			sb.append(" " + this.indexSequence.get(i));
		}
		return sb.toString();
	}*/
	public String toIndexString() {
		StringBuilder sb = new StringBuilder();
		sb.append(CTokenSequence.getTokenIndex(this.tokenSequence.get(0)));
		for (int i = 1; i < this.tokenSequence.size(); i++) {
			int index = CTokenSequence.getTokenIndex(this.tokenSequence.get(i));
			sb.append(" " + index);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.height);
		for (int i = 0; i < this.tokenSequence.size(); i++) {
			sb.append(" " + this.tokenSequence.get(i));
		}
		return sb.toString();
	}
}
