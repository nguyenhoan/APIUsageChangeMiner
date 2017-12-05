package dependency;

import java.util.HashMap;
import java.util.HashSet;

import change.CMethod;
import change.ChangeEntity;

public class CallGraph {
	private static final HashSet<String> commonNames = new HashSet<String>();
	static {
		commonNames.add("toString(0)");
		commonNames.add("clone(0)");
		commonNames.add("equals(1)");
		commonNames.add("finalize(0)");
		commonNames.add("hashCode(0)");
	}
	// value is the set of methods that call the method in key
	private HashMap<String, HashSet<ChangeEntity>> callersOfMethodName = new HashMap<String, HashSet<ChangeEntity>>();
	private HashMap<CMethod, HashSet<ChangeEntity>> callersOfMethod = new HashMap<CMethod, HashSet<ChangeEntity>>();
	//private HashMap<String, HashSet<String>> calleesOfMethod = new HashMap<String, HashSet<String>>();
	
	public void addMethod(CMethod cMethod) {
		if (!isCommonName(cMethod.getName()) && !callersOfMethodName.containsKey(cMethod.getName())) {
			callersOfMethodName.put(cMethod.getName(), new HashSet<ChangeEntity>());
			//calleesOfMethod.put(name, new HashSet<String>());
		}
		if (!isCommonName(cMethod.getName()) && !callersOfMethod.containsKey(cMethod)) {
			callersOfMethod.put(cMethod, new HashSet<ChangeEntity>());
		}
	}
	
	/*public void addMethod(CField cMethod) {
		if (!isCommonName(cMethod.getName()) && !callersOfMethodName.containsKey(cMethod.getName())) {
			callersOfMethodName.put(cMethod.getName(), new HashSet<ChangeEntity>());
			//calleesOfMethod.put(name, new HashSet<String>());
		}
	}*/
	
	public HashMap<String, HashSet<ChangeEntity>> getCallersOfMethodName() {
		return callersOfMethodName;
	}

	public HashMap<CMethod, HashSet<ChangeEntity>> getCallersOfMethod() {
		return callersOfMethod;
	}

	private boolean isCommonName(String name) {
		return commonNames.contains(name);
	}
	
	public void addCall(ChangeEntity caller, String callee) {
		if (callersOfMethodName.containsKey(callee)) {
			HashSet<ChangeEntity> callers = callersOfMethodName.get(callee);
			callers.add(caller);
			callersOfMethodName.put(callee, callers);
			/*HashSet<String> callees = calleesOfMethod.get(caller);
			callees.add(callee);
			calleesOfMethod.put(caller, callees);*/
		}
	}
	
	public void addCall(ChangeEntity caller, HashSet<CMethod> callees) {
		for (CMethod callee : callees) {
			HashSet<ChangeEntity> callers = this.callersOfMethod.get(callee);
			if (callers != null) {
				callers.add(caller);
				this.callersOfMethod.put(callee, callers);
			}
		}
	}
	
	public void refine() {
		for (String name : this.callersOfMethodName.keySet()) {
			if (!this.callersOfMethodName.get(name).isEmpty()) {
				for (CMethod cm : this.callersOfMethod.keySet()) {
					if (cm.getName().equals(name)) {
						this.callersOfMethodName.get(name).addAll(this.callersOfMethod.get(cm));
					}
				}
			}
		}
	}
}
