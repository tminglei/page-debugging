package tml.pagedebugging.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.catalina.loader.ResourceEntry;
import org.apache.catalina.loader.WebappClassLoader;

/**
 * p><b>NOTE:</b> Requires Apache Tomcat version 6.0 or higher.
 * @author tminglei
 */
public class BridgedClassLoader extends WebappClassLoader {
	private static Set<String> toBeBridgedClasses;
	
	static {
		toBeBridgedClasses = Collections.synchronizedSet( new HashSet<String>() );
		toBeBridgedClasses.add( "tml.pagedebugging.core.FreemarkerPageHook" );
		toBeBridgedClasses.add( "tml.pagedebugging.core.VelocityPageHook" );
		toBeBridgedClasses.add( "tml.pagedebugging.PageDebuggingFilter" );
	}
	
	//--
	private String pageDebuggingJarPath;
	private boolean usePdLite = false;
	
	public BridgedClassLoader() {
		pageDebuggingJarPath = getAndCheckJarPath();
		usePdLite = isUsePageDebuggingLite();
	}
	
	public BridgedClassLoader(ClassLoader parent) {
		super(parent);
		pageDebuggingJarPath = getAndCheckJarPath();
		usePdLite = isUsePageDebuggingLite();
	}
	
	private String getAndCheckJarPath() {
		String jarPath = System.getProperty("pageDebuggingJarPath");
		
		if(jarPath != null && !jarPath.trim().equals("")) 
			return jarPath;
		else
			return null;
	}
	
	private boolean isUsePageDebuggingLite() {
		String pdLiteOn = System.getProperty("usePageDebuggingLite");
		return "On".equalsIgnoreCase(pdLiteOn) || "true".equalsIgnoreCase(pdLiteOn);
	}
	
	@Override
	protected ResourceEntry findResourceInternal(String name, String path) {
		if (usePdLite && pageDebuggingJarPath == null)
			return super.findResourceInternal(name, path);
		else if (path.endsWith(".class") && toBeBridgedClasses.contains(name))
			return loadBridgedClassResource(name, path);
		else
			return super.findResourceInternal(name, path);
	}
	
	//----------------------------------------------------- support methods ---
	
	private ResourceEntry loadBridgedClassResource(String name, String path) {
		if (pageDebuggingJarPath == null)
			throw new IllegalArgumentException("pageDebuggingJarPath not be set!");
		
		ResourceEntry entry = new ResourceEntry();
		
		System.out.println("[BridgedClassLoader] loading bridged class: " + name);
		
		try {
			byte[] classBytes = loadClassBytes(pageDebuggingJarPath, path);
			entry.loadedClass = defineClass(name, classBytes, 0, classBytes.length);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Error occurred when loading "
					+ pageDebuggingJarPath + "#" + name, ex);
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
			if(binaryStream != null) {
                try {
                    binaryStream.close();
                } catch (IOException e) { /* Ignore */}
			}
		}
	}	
}
