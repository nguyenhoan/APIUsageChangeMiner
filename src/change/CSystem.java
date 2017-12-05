package change;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;

import repository.SVNConnector;
import dependency.CallGraph;
import dependency.DataGraph;

public class CSystem {
	public static String jdkPath = "D:/systems/jdk6/j2se/src/share/classes";
	public static int MAX_FILES = Integer.MAX_VALUE;
	public static CSystem libSystems = new CSystem();
	private String path;
	private HashMap<String, CFile> mapFiles = new HashMap<String, CFile>();
	private HashMap<String, CSystem> includedLibs = new  HashMap<>();
	private HashSet<String> libClasses = new HashSet<>(), libPackages = new HashSet<>();
	private DataGraph dataGraph = new DataGraph();
	private CallGraph callGraph = new CallGraph();
	
	public static void init() {
		//libSystems.buildStructure("D:\\systems\\jdk6\\j2se\\src\\share\\classes");
		libSystems.buildStructure(jdkPath + "/java");
		libSystems.buildStructure(jdkPath + "/javax");
		libSystems.buildStructure(jdkPath + "/org");
	}
	
	public CSystem() {
	}
	
	public CSystem(String path) {
		this.path = path;
		//System.out.println("<System> " + path);
		buildStructure();
		//System.out.println("</System>");
	}
	
	public CSystem(SVNConnector conn, String path) {
		this.path = path;
		buildStructure(conn);
		buildIncludedLibs(conn);
	}

	public HashMap<String, CFile> getMapFiles() {
		return mapFiles;
	}

	public DataGraph getDataGraph() {
		return dataGraph;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public HashSet<String> getLibClasses() {
		return libClasses;
	}

	public HashSet<String> getLibPackages() {
		return libPackages;
	}

	private void buildStructure()
	{
		buildStructure(path);
	}

	public void buildStructure(String path)
	{
		Stack<File> fs = new Stack<File>();
		fs.push(new File(path));
		while(!fs.isEmpty())
		{
			File file = fs.pop();
			if(file.isDirectory())
			{
				for(File subFile : file.listFiles())
					fs.push(subFile);
			}
			else if(file.getName().endsWith(".java"))
			{
				CFile cf = new CFile(this, file);
				//mapFiles.put(cf.getPath(), cf);
				if (cf.getCompileUnit() != null)
					mapFiles.put(cf.getFullyQualifiedName(), cf);
				if (hasTooManyFiles())
					return;
			}
		}
	}

	private void buildStructure(SVNConnector conn)
	{
		Stack<String> fs = new Stack<String>();
		fs.push(path);
		while(!fs.isEmpty())
		{
			String path = fs.pop();
			try {
				SVNNodeKind kind = conn.getRepository().checkPath(path, conn.getLatestRevision());
				if (kind == SVNNodeKind.DIR) {
					Collection<?> entries = conn.getRepository().getDir(path, conn.getLatestRevision(), null, (Collection<?>)null);
					Iterator<?> iterator = entries.iterator();
					while (iterator.hasNext()) {
						SVNDirEntry entry = (SVNDirEntry)iterator.next();
						fs.push((path.equals("")) ? entry.getName() : path + "/" + entry.getName());
					}
				}
				else if(path.endsWith(".java")) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					conn.getRepository().getFile(path, conn.getLatestRevision(), null, out);
					System.out.println(path);
					String content = out.toString();
					CFile cf = new CFile(this, path, content);
					//mapFiles.put(cf.getPath(), cf);
					if (cf.getCompileUnit() != null)
						mapFiles.put(cf.getFullyQualifiedName(), cf);
				}
			}
			catch (SVNException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void buildIncludedLibs(SVNConnector conn) {
		Stack<String> fs = new Stack<String>();
		fs.push("");
		while(!fs.isEmpty())
		{
			String path = fs.pop();
			try {
				SVNNodeKind kind = conn.getRepository().checkPath(path, conn.getLatestRevision());
				if (kind == SVNNodeKind.DIR) {
					Collection<?> entries = conn.getRepository().getDir(path, conn.getLatestRevision(), null, (Collection<?>)null);
					Iterator<?> iterator = entries.iterator();
					while (iterator.hasNext()) {
						SVNDirEntry entry = (SVNDirEntry)iterator.next();
						if (entry.getName().startsWith("tag") || entry.getName().startsWith("branch") || entry.getName().startsWith("release"))
							continue;
						fs.push((path.equals("")) ? entry.getName() : path + "/" + entry.getName());
					}
				}
				else if(path.endsWith(".jar")) {
					/*String jarName = FileIO.getSimpleFileName(path);
					if (this.includedLibs.containsKey(jarName))
						return;
					this.includedLibs.put(jarName, null);*/
					InputStream is = conn.getInputStream(path, conn.getLatestRevision());
					if (is != null) {
						System.out.println(path);
						try {
							JarInputStream jarInputStream = new JarInputStream(is);
							while (jarInputStream.available() == 1) {
								JarEntry entry = jarInputStream.getNextJarEntry();
								if (entry == null)
									break;
								if (!entry.isDirectory() && entry.getName().endsWith(".class") && !entry.getName().contains("$"))
								{
									/*ClassParser parser = new ClassParser(jarInputStream, entry.getName());
									JavaClass jc = parser.parse();
									String packageName = jc.getPackageName();
									String className = jc.getClassName();*/
									String name = entry.getName();
									name = name.substring(0, name.length() - ".class".length());
									int index = name.lastIndexOf('/');
									if (index == -1) continue;
									String className = name.replace('/', '.');
									String packageName = className.substring(0, index);
									this.libClasses.add(className);
									this.libPackages.add(packageName);
								}
							}
							jarInputStream.close();
							is.close();
						} catch (Exception e) {
							
						}
					}
				}
			}
			catch (SVNException e) {
				e.printStackTrace();
			}
		}
	}

	public void buildDependencies() {
		for (CFile cf : this.mapFiles.values()) {
			System.out.println(cf.getQualName());
			cf.buildDependencies();
		}
	}

	public boolean hasTooManyFiles() {
		return this.mapFiles.size() > MAX_FILES;
	}
}