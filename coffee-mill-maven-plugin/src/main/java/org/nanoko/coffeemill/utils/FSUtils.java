package org.nanoko.coffeemill.utils;

import java.io.File;
import java.io.IOException;

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
}
