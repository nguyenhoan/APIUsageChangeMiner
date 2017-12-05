package dependency;

import change.CSystem;

public class TestDependencyVisitor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CSystem cs = new CSystem("input");
		cs.buildDependencies();
	}

}
