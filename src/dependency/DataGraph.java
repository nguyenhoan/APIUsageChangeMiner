package dependency;

import java.util.HashMap;
import java.util.HashSet;

import change.CClass;

public class DataGraph {
	private HashSet<String> typeNames = new HashSet<String>();
	private HashMap<String, HashSet<CClass>> classesOfName = new HashMap<String, HashSet<CClass>>();
	private HashMap<String, HashSet<String>> parentNamesOfTypeName = new HashMap<String, HashSet<String>>();
	
	public void addName(String name) {
		this.typeNames.add(name);
	}

	public HashSet<String> getTypeNames() {
		return typeNames;
	}
	
	public void addPair(String child, String parent) {
		HashSet<String> names = this.parentNamesOfTypeName.get(child);
		if (names == null)
			names = new HashSet<String>();
		names.add(parent);
		this.parentNamesOfTypeName.put(child, names);
	}

	public void addClass(CClass cc) {
		addName(cc.getSimpleName());
		HashSet<CClass> ccs = this.classesOfName.get(cc.getSimpleName());
		if (ccs == null)
			ccs = new HashSet<CClass>();
		ccs.add(cc);
		this.classesOfName.put(cc.getSimpleName(), ccs);
		HashSet<String> names = this.parentNamesOfTypeName.get(cc.getSimpleName());
		if (names == null)
			names = new HashSet<String>();
		names.addAll(cc.getSuperClassNames());
		this.parentNamesOfTypeName.put(cc.getSimpleName(), names);
	}
}
