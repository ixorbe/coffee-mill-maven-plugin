package org.nanoko.coffeemill.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class FSUtils {

	
	/**
     * Checks whether the given file is inside the given directory.
     * @param file the file
     * @param directory the directory
     * @return {@literal true} if the file is in the directory (or any subdirectories), {@literal false} otherwise.
     */
    public static boolean isInDirectory2(File file, File directory) {
        try {
            return FilenameUtils.directoryContains(directory.getCanonicalPath(), file.getCanonicalPath());
        } catch (IOException e) { //NOSONAR
            return false;
        }
    }
    
    public static boolean isInDirectory(String filename, File file) {    	
    	if (file.isDirectory()) {     
            //do you have permission to read this directory?	
    	    if (file.canRead()) {
	    		for (File temp : file.listFiles()) {
	    		    if (temp.isDirectory()) {
	    		    	if(isInDirectory(filename, temp))
	    		    		return true;
	    		    } else {
		    			if (filename.toLowerCase().equals(temp.getName().toLowerCase()))		
		    			    return true;
	    		    }
	    	    }
	     
	    	 }
        }
    	return false;     
      }
    
    public static boolean hasExtension(File file, String... extensions) {
        String extension = FilenameUtils.getExtension(file.getName());
        for (String s : extensions) {
            if (extension.equals(s)  || ("." + extension).equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static File findExecutableInPath(String exec) {
        // Build candidates
        List<String> candidates = new ArrayList<String>();
        candidates.add(exec);
        // Windows:
        candidates.add(exec + ".exe");
        candidates.add(exec + ".bat");
        candidates.add(exec + ".cmd");
        // Linux / Unix / MacOsX
        candidates.add(exec + ".sh");
        candidates.add(exec + ".bash");

        String systemPath = System.getenv("PATH");

        // Fast failure if we don't have the PATH defined.
        if (systemPath == null) {
            return null;
        }

        String[] pathDirs = systemPath.split(File.pathSeparator);

        for (String pathDir : pathDirs) {
            for (String candidate : candidates) {
                File file = new File(pathDir, candidate);
                if (file.isFile()) {
                    return file;
                }
            }
        }

        // Search not successful.
        return null;
    }
    
    
    
    /**
     * Gets a File object representing a File in the directory <tt>dir</tt> which has the same path as the file
     * <tt>file</tt> from the directory <tt>rel</tt>.
     * @param file
     * @param rel
     * @param dir
     * @return
     */
    public static File computeRelativeFile(File file, File rel, File dir) {
        String path = file.getAbsolutePath();
        String relativePath = path.substring(rel.getAbsolutePath().length());
        return new File(dir, relativePath);
    }
    
    
    public static File resolveFile(final String name, File workDir, File libDir, String extension) {
        // 1) Check for the file in the workDir with a direct name
        File file = new File(workDir, name);
        if (file.isFile()) { return file; }

        // 2) Try to append the extension
        file = new File(workDir, name + "." + extension);
        if (file.isFile()) { return file; }

        // 3) Search in the libDir as prefix
        if (libDir != null  && libDir.exists()) {
            File[] files = libDir.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.startsWith(name);
                }
            });
            if (files.length > 0) { return files[0]; }
        }
        return null;
    }
    
    
}
