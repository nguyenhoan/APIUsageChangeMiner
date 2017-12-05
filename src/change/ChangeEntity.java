package change;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;

import treeMapping.TreeMapper;
import treeMapping.TreeMappingConstants;

public abstract class ChangeEntity implements Serializable {
	private static final long serialVersionUID = 4628877646311034500L;

	public enum Type {Unchanged, Deleted, Added, Modified, Modified_Modifiers, Modified_Name, Modified_Body}

	protected static int thresholdDistance = 20;
	
	private Type cType = Type.Unchanged;
	protected HashMap<Integer, Integer> vector;
	protected int vectorLength = 0;
	protected HashMap<ASTNode, ArrayList<ASTNode>> tree;
	int numOfLOCs = 0, numOfNonCommentLOCs = 0, numOfAstNodes = 0, numOfChangeLOCs = 0, numOfChangeAstNodes = 0, numOfChangeTrees = 0;

	
	protected Type getCType() {
		return cType;
	}

	protected void setCType(Type type) {
		this.cType = type;
	}
	
	protected void computeVectorLength()
	{
		this.vectorLength = 0;
		for (int key : vector.keySet())
			this.vectorLength += vector.get(key);
	}

	protected Map<Integer, Integer> getVector() {
		return this.vector;
	}

	protected int getVectorLength() {
		return this.vectorLength;
	}
	
	public int getNumOfLOCs() {
		return numOfLOCs;
	}

	public int getNumOfNonCommentLOCs() {
		return numOfNonCommentLOCs;
	}

	public int getNumOfAstNodes() {
		return numOfAstNodes;
	}

	public int getNumOfChangeLOCs() {
		return numOfChangeLOCs;
	}

	public int getNumOfChangeAstNodes() {
		return numOfChangeAstNodes;
	}

	public int getNumOfChangeTrees() {
		return numOfChangeTrees;
	}

	protected double computeVectorSimilarity(ChangeEntity other)
	{
		HashMap<Integer, Integer> v1 = new HashMap<Integer, Integer>(this.vector);
		HashMap<Integer, Integer> v2 = new HashMap<Integer, Integer>(other.getVector());
		HashSet<Integer> keys = new HashSet<Integer>(v1.keySet());
		keys.retainAll(v2.keySet());
		
		int commonSize = 0;
		for(int key : keys)
		{
			commonSize += Math.min(v1.get(key), v2.get(key));
		}
		return commonSize * 2.0 / (this.vectorLength + other.getVectorLength());
	}

	abstract public String getName();
	
	abstract public CFile getCFile();
	
	public ChangeEntity getMappedEntity() {
		if (this instanceof CClass)
			return ((CClass) this).getMappedClass();
		if (this instanceof CField)
			return ((CField) this).getMappedField();
		if (this instanceof CMethod)
			return ((CMethod) this).getMappedMethod();
		return null;
	}

	abstract public String getQualName();
	
	abstract public CClass getCClass();
	
	public ArrayList<TreeChange> getTreeChanges() {
		if (this instanceof CInitializer)
			return ((CInitializer)this).getTreeChanges();
		if (this instanceof CMethod)
			return ((CMethod)this).getTreeChanges();
		return null;
	}
	
	
	public String printTree() {
		if (this instanceof CField) {
			CField cf = (CField) this;
			if (cf.getMappedField().getInitializer() == null) {
				if (cf.getInitializer() == null)
					return "";
				return "The whole initializer";
			}
			return cf.getInitializer() == null ? null : printTree(tree, cf.getInitializer());
		}
		if (this instanceof CInitializer)
			return printTree(tree, ((CInitializer)this).getInitializer());
		if (this instanceof CMethod)
			return printTree(tree, ((CMethod)this).getDeclaration());
		return null;
	}

	private String printTree(HashMap<ASTNode, ArrayList<ASTNode>> tree, ASTNode root)
	{
		if (root instanceof Javadoc)
			return "";
		StringBuffer buf = new StringBuffer();
		int status = (Integer)root.getProperty(TreeMappingConstants.propertyStatus);
		if(status > TreeMappingConstants.STATUS_FULLY_CHANGED)
		{
			ASTNode simNode = (ASTNode)root.getProperty(TreeMappingConstants.propertyRevisionMap);
			buf.append(root.getProperty(TreeMappingConstants.propertyLocationID) + "@" + 
					root.getProperty(TreeMappingConstants.propertyStartLine) + ": " + TreeMapper.getStatus(status) + "\r\n");
			buf.append(root + "\r\n");
			if(simNode != null)
			{
				buf.append("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\r\n");
				buf.append(simNode.getProperty(TreeMappingConstants.propertyLocationID) + "@" + 
						simNode.getProperty(TreeMappingConstants.propertyStartLine) + "\r\n");
				buf.append(simNode + "\r\n");
			}
		}
		ArrayList<ASTNode> children = tree.get(root);
		for(ASTNode child : children)
		{
			buf.append(printTree(tree, child));
		}
		
		return buf.toString();
	}
	
	void cleanForStats() {
		if (this.tree != null) {
			this.tree.clear();
			this.tree = null;
		}
		if (this.vector != null) {
			this.vector.clear();
			this.vector = null;
		}
	}
}
