package org.nanoko.coffeemill.mojos.scripts.js;

import com.google.javascript.jscomp.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Compiles JavaScript files.
 * <ul>
 * <ol>It compiles (checks) JavaScript files from src/main/js.</ol>
 * <ol>It copies these JavaScript files into work directory target/www.</ol>
 * </ul>
 * <p/>
 * This mojo makes the assumption that the files are already copied/generated to their destination directory,
 * when it is executed.
 */
@Mojo(name = "compile-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class JSCompilerMojo extends AbstractCoffeeMillWatcherMojo {
    
    
    public void execute() throws MojoExecutionException, MojoFailureException {        
    	try {
    		if (this.javaScriptDir.isDirectory()) {
	        	Collection<File> files = FileUtils.listFiles(this.javaScriptDir, new String[]{"js"}, true);
	            for(File file : files)
	            	copy(file);
    		}
    	} catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }        
    }
    
    public boolean accept(File file) {
    	return FSUtils.isInDirectory(file.getName(), this.javaScriptDir) && FSUtils.hasExtension(file, "js");
    }
    

    private void copy(File f) throws WatchingException {
    	getLog().info("Copy JavaScript files from " + this.javaScriptDir.getAbsolutePath());
    	try {
			FileUtils.copyFileToDirectory(f, this.getWorkDirectory());
		} catch (IOException e) { e.printStackTrace(); }
    }
    
    public boolean fileCreated(File file) throws WatchingException {
    	this.copy(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
    	if(fileDeleted(file)) {
    		return this.fileCreated(file);
    	} else
    		return false;
    }

    public boolean fileDeleted(File file) {
    	File deleted = new File(this.workDir.getAbsolutePath() + File.separator + file.getName());
        if (deleted.isFile()){
        	getLog().info("Deleting File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }

}
