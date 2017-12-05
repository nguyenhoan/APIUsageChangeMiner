package change;

public class TreeChange {
	public static final byte CHANGE_TYPE_NONE = 0, CHANGE_TYPE_DEL = 10, CHANGE_TYPE_ADD = 01, CHANGE_TYPE_MOD = 11;
	
	byte type = -1;
	byte height = 0;
	byte component = 0; // 0: entire
	byte changeType = 0; // 0: no change, 10: delete, 01: add, 11: modify
	boolean check = false;
	CTree tree1, tree2;
	
	public TreeChange(byte type, byte height) {
		this.type = type;
		this.height = height;
	}
	
	public TreeChange(byte type, byte height, byte component, byte changeType) {
		this.type = type;
		this.height = height;
		this.component = component;
		this.changeType = changeType;
	}
	
	public TreeChange(byte type, byte height, byte component, byte changeType, boolean check) {
		this.type = type;
		this.height = height;
		this.component = component;
		this.check = check;
		this.changeType = changeType;
		// DEBUG
		if (check)
			System.out.print("");
	}
	
	public byte getType() {
		return type;
	}

	public byte getHeight() {
		return height;
	}

	public CTree getTree1() {
		return tree1;
	}

	public CTree getTree2() {
		return tree2;
	}

	@Override
	public String toString() {
		return "[[" + this.tree1 + "]|[" + this.tree2 + "]]";
	}

	public boolean isChanged() {
		return !this.tree1.equals(this.tree2);
	}

	public void abstractout() {
		if (this.tree1 != null)
			this.tree1.abstractout();
		if (this.tree2 != null)
			this.tree2.abstractout();
	}

	/*public void doIndexing() {
		if (this.tree1 != null)
			this.tree1.doIndexing();
		if (this.tree2 != null)
			this.tree2.doIndexing();
		
	}*/
}
