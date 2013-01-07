package tml.pagedebugging.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.catalina.loader.ResourceEntry;
import org.apache.catalina.loader.WebappClassLoader;

/**
 * <b>NOTE:</b> Requires Apache Tomcat version 6.0 or higher.
 * @author tminglei
 */
public class BridgedClassLoader extends WebappClassLoader {
	private static Set<String> toBeBridgedClasses = new HashSet<String>() {
		{
			add( "tml.pagedebugging.core.FreemarkerPageHook" );
			add( "tml.pagedebugging.core.VelocityPageHook" );
			add( "tml.pagedebugging.PageDebuggingFilter" );
		}
	};
	
	//--
	private String pageDebuggingJarPath;
	
	public BridgedClassLoader() {
		pageDebuggingJarPath = getPageDebuggingJarPath();
	}
	
	public BridgedClassLoader(ClassLoader parent) {
		super(parent);
		pageDebuggingJarPath = getPageDebuggingJarPath();
	}
	
	private String getPageDebuggingJarPath() {
		String clazzFullPath = getClass().getResource( getClass().getSimpleName() + ".class" ).getPath();
		
		System.out.println("[BridgedClassLoader] clazzFullPath: " + clazzFullPath);
		
		int jarPostIndx = clazzFullPath.indexOf(".jar");
		int filePreIndx = "file:/". length();
		if (jarPostIndx > 0)
			return clazzFullPath.substring(filePreIndx, jarPostIndx + 4);
		else
			throw new IllegalArgumentException("CAN't determine pageDebuggingJarPath!");
	}
	
	@Override
	protected ResourceEntry findResourceInternal(String name, String path) {
		if (path.endsWith(".class") && toBeBridgedClasses.contains(name))
			return loadBridgedClassResource(pageDebuggingJarPath, name, path);
		else
			return super.findResourceInternal(name, path);
	}
	
	//----------------------------------------------------- support methods ---
	
	private ResourceEntry loadBridgedClassResource(String jarPath, String name, String path) {
		ResourceEntry entry = new ResourceEntry();
		
		System.out.println("[BridgedClassLoader] loading bridged class: " + name);
		
		try {
			byte[] classBytes = loadClassBytes(jarPath, path);
			entry.loadedClass = defineClass(name, classBytes, 0, classBytes.length);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Error occurred when loading " + jarPath + "#" + name, ex);
		}
		
        synchronized (resourceEntries) {
            // Ensures that all the threads which may be in a race to load
            // a particular class all end up with the same ResourceEntry
            // instance
            ResourceEntry entry2 = (ResourceEntry) resourceEntries.get(name);
            if (entry2 == null) {
                resourceEntries.put(name, entry);
            } else {
                entry = entry2;
            }
        }
        
		System.out.println("[BridgedClassLoader] loaded bridged class: " + name);
        
		return entry;
	}
	
	private byte[] loadClassBytes(String jarPath, String clazzPath) throws IOException {
		JarFile pageDebuggingJar = new JarFile(jarPath);
		JarEntry clazzEntry = pageDebuggingJar.getJarEntry(clazzPath);
		int contentLength = (int) clazzEntry.getSize();

		InputStream binaryStream = pageDebuggingJar.getInputStream(clazzEntry);
		try {
			byte[] binaryContent = new byte[contentLength];
			int pos = 0;
			while (true) {
				int n = binaryStream.read(binaryContent, pos,
										  binaryContent.length - pos);
				if (n <= 0)
					break;

				pos += n;
			}
			
			return binaryContent;
		} 
		finally {
			if(pageDebuggingJar != null) {
                try {
                	pageDebuggingJar.close();
                } catch (IOException e) { /* Ignore */}
			}
		}
	}
}
