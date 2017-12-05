package change;

import java.io.Serializable;
import java.util.HashMap;

public class TokenSequenceChange implements Serializable {
	private static final long serialVersionUID = -1682534439892267588L;

	public static HashMap<Integer, HashMap<CTokenSequence, HashMap<CTokenSequence, HashMap<Integer, HashMap<Integer, Integer>>>>> changes = new HashMap<>();
	
	
	public static void updateTokenSequenceChange(TreeChange change, CTokenSequence cts1, CTokenSequence cts2, int projectId, int revision, int location) {
		byte nodeType = change.type, component = change.component, changeType = change.changeType;
		boolean check = change.check;
		int changeMeta = nodeType + (component << 8) + (changeType << 16);
		if (check) changeMeta = -changeMeta;
		
		HashMap<CTokenSequence, HashMap<CTokenSequence, HashMap<Integer, HashMap<Integer, Integer>>>> map1 = changes.get(changeMeta);
		if (map1 == null) {
			map1 = new HashMap<>();
			changes.put(changeMeta, map1);
		}
		HashMap<CTokenSequence, HashMap<Integer, HashMap<Integer, Integer>>> map2 = map1.get(cts1);
		if (map2 == null) {
			map2 = new HashMap<>();
			map1.put(cts1, map2);
		}
		HashMap<Integer, HashMap<Integer, Integer>> map3 = map2.get(cts2);
		if (map3 == null) {
			map3 = new HashMap<>();
			map2.put(cts2,  map3);
		}
		HashMap<Integer, Integer> map4 = map3.get(revision);
		if (map4 == null) {
			map4 = new HashMap<>();
			map3.put(revision, map4);
		}
		int count = 1;
		if (map4.containsKey(location))
			count = map4.get(location) + 1;
		map4.put(location, count);
	}
}
