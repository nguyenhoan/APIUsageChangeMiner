package treeMapping;

public interface TreeMappingConstants {

	public static final String propertyRevisionMap = "rMap";
	public static final String propertyLocationID = "lid";
	public static final String propertyOffset = "offset";
	public static final String propertyStatus = "status";
	public static final String propertyStartLine = "sl";
	public static final String propertyEndLine = "el";
	public static final int STATUS_UNCHANGED = 0;
	public static final int STATUS_PARTLY_CHANGED = 1;
	public static final int STATUS_FULLY_CHANGED = 2;
	public static final int STATUS_RELABELED = 3;
	public static final int STATUS_DELETED = 4;
	public static final int STATUS_ADDED = 5;
	public static final int STATUS_MOVED = 6;

}