package change;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import treeMapping.MapSourceFile;
import utils.FileIO;
import utils.JavaASTUtil;

public class CFile extends ChangeEntity {
	private static final long serialVersionUID = 4073095618526557436L;
	public static final int MAX_CHAR_SIZE = 500000;
	public static final int MAX_LINE_SIZE = 50000;
	private RevisionAnalyzer cRevisionAnalyzer;
	private CSystem cSystem;
	private String path;
	private String simpleName;
	private String packageName = "";
	private HashMap<String, CClass> imports;
	private HashMap<String, String> importedClasses;
	private HashSet<String> importedPackages;
	private CFile mappedFile;
	private CompilationUnit compileUnit;
	private HashSet<CClass> classes = new HashSet<CClass>();
	private MapSourceFile sourceFile = null;
	
	public CFile(CSystem cSystem, File file) {
		this.cSystem = cSystem;
		this.path = file.getAbsolutePath();
		this.simpleName = FileIO.getSimpleFileName(path);
		//System.out.println("\t<File> " + file.getAbsolutePath());
		String source = FileIO.readStringFromFile(file.getAbsolutePath());
		init(source);
	}

	public CFile(CSystem cSystem, String path, String source) {
		this.cSystem = cSystem;
		this.path = path;
		this.simpleName = FileIO.getSimpleFileName(path);
		init(source);
	}

	private void init(String source) {
		ASTNode ast = JavaASTUtil.parseSource(source);
		init(ast);
	}
	
	private void init(ASTNode ast) {
    	CompilationUnit cu = (CompilationUnit) ast;
    	if(cu.types() == null || cu.types().isEmpty() || cu.getPackage() == null) {
    		//System.out.println("\t\tDiscarded " + this.path);
    	}
    	else {
    		this.compileUnit = cu;
			VectorVisitor vectorVisitor = new VectorVisitor();
			compileUnit.accept(vectorVisitor);
    		this.packageName = cu.getPackage().getName().getFullyQualifiedName();
    		this.importedClasses = new HashMap<String, String>();
    		this.importedPackages = new HashSet<String>();
    		if (cu.imports() != null) {
    			for (int i = 0; i < cu.imports().size(); i++) {
    				ImportDeclaration id = (ImportDeclaration) cu.imports().get(i);
    				if (id.isStatic()) {
    					if (id.getName() instanceof QualifiedName) {
	    					QualifiedName name = (QualifiedName) id.getName();
	    					if (id.isOnDemand()) {
	    						this.importedClasses.put(name.getName().getIdentifier(), name.getFullyQualifiedName());
	        				}
	        				else if (id.getName().isQualifiedName()) {
	        					//FIXME
	        					//this.importedClasses.put(name.getName().getIdentifier(), name.getQualifier().getFullyQualifiedName());
	        					this.importedClasses.put(name.getName().getIdentifier(), name.getFullyQualifiedName());
	        				}
    					}
    				}
    				else if (id.isOnDemand()) {
    					this.importedPackages.add(id.getName().getFullyQualifiedName());
    				}
    				else if (id.getName().isQualifiedName()) {
    					QualifiedName name = (QualifiedName) id.getName();
    					//FIXME
    					//this.importedClasses.put(name.getName().getIdentifier(), name.getQualifier().getFullyQualifiedName());
    					this.importedClasses.put(name.getName().getIdentifier(), name.getFullyQualifiedName());
    				}
    			}
    		}
	    	for(int index = 0; index < cu.types().size(); index++)
	    	{
	    		AbstractTypeDeclaration declaration = (AbstractTypeDeclaration) cu.types().get(index); 
	    		switch (declaration.getNodeType())
	    		{
	    		case ASTNode.TYPE_DECLARATION:
	    		{
			    	TypeDeclaration type = (TypeDeclaration) declaration;
			    	this.classes.add(new CClass(this, type, null));
		    		break;
	    		}
	    		case ASTNode.ENUM_DECLARATION:
	    		{
	    			EnumDeclaration type = (EnumDeclaration) declaration;
	    			this.classes.add(new CClass(this, type, null));
	    			break;
	    		}
	    		case ASTNode.ANNOTATION_TYPE_DECLARATION:
	    		{
	    			AnnotationTypeDeclaration type = (AnnotationTypeDeclaration) declaration;
	    			this.classes.add(new CClass(this, type, null));
	    			break;
	    		}
	    		default:
	    		{
	    			System.out.println("Info: Some other type declaration not implemented. " + declaration.getClass().getSimpleName());
	    			break;
	    		}
	    		}
	    	}
    	}
    	//System.out.println("\t</File>");
	}
	
	public CFile(RevisionAnalyzer revisionAnalyzer, String filePath, String content) {
		this.cRevisionAnalyzer = revisionAnalyzer;
		this.path = filePath;
		this.simpleName = FileIO.getSimpleFileName(path);
		//System.out.println("\t<File> " + file.getAbsolutePath());
		sourceFile = new MapSourceFile(content);
		init(sourceFile.getAst());
	}

	public RevisionAnalyzer getcRevisionAnalyzer() {
		return cRevisionAnalyzer;
	}

	public String getPath() {
		return path;
	}

	public String getPackageName() {
		return this.packageName;
	}
	
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public String getName() {
		return this.simpleName;
	}

	public HashSet<CClass> getClasses() {
		return classes;
	}
	
	public CFile getMappedFile() {
		return mappedFile;
	}

	public void setMappedFile(CFile mappedFile) {
		this.mappedFile = mappedFile;
	}

	public MapSourceFile getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(MapSourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	@Override
	public CFile getCFile() {
		return this;
	}

	@Override
	public CClass getCClass() {
		return null;
	}

	public String getSourceCode() {
		return this.sourceFile.getFileContent();
	}

	public CompilationUnit getCompileUnit() {
		return compileUnit;
	}

	public String getFullyQualifiedName() {
		return this.packageName + "." + this.simpleName;
	}

	public void computeSimilarity(CFile otherFile)
	{
		HashSet<CClass> classesM = new HashSet<CClass>(this.classes), classesN = new HashSet<CClass>(otherFile.getClasses());
		HashSet<CClass> mappedClassesM = new HashSet<CClass>(), mappedClassesN = new HashSet<CClass>();
		
		// map class having same name with file
		CClass cM = null, cN = null;
		for (CClass cc : classesM)
		{
			if (cc.getSimpleName().equals(simpleName))
				cM = cc;
		}
		for (CClass cc : classesN)
		{
			if (cc.getSimpleName().equals(otherFile.getSimpleName()))
				cN = cc;
		}
		if (cM != null && cN != null)
		{
			double sim = cM.computeSimilarity(cN, false);
			if (sim >= CClass.thresholdSimilarity)
			{
				CClass.setMap(cM, cN);
				mappedClassesM.add(cM);
				mappedClassesN.add(cN);
				classesM.remove(cM);
				classesN.remove(cN);
			}
		}
		
		CClass.mapAll(classesM, classesN, mappedClassesM, mappedClassesN);
	}

	public void printChanges(PrintStream ps) {
		if (getCType() != Type.Unchanged) {
			ps.println("\tFile: " + getPath() + " --> " + (this.mappedFile == null ? "null" : this.mappedFile.getPath()));
			printChanges(ps, this.classes);
		}
	}

	private void printChanges(PrintStream ps, HashSet<CClass> classes) {
		for (CClass cc : classes)
			cc.printChanges(ps);
	}
	
	@Override
	public String toString() {
		return this.simpleName;
	}

	@Override
	public String getQualName() {
		return this.path;
	}

	public void buildDependencies() {
		for (CClass cc : this.classes) {
			cc.buildDependencies();
		}
	}

}
