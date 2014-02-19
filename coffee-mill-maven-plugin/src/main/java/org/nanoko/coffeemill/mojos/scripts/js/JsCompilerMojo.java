package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
public class JsCompilerMojo extends AbstractCoffeeMillWatcherMojo {
	
	
    public void execute() throws MojoExecutionException {    	
		if(isSkipped()) { 
			return; 
		}
		
		if (!this.getJavaScriptDir().isDirectory()) {
			getLog().warn("JavaScript copy skipped - " + this.getJavaScriptDir().getAbsolutePath() + " does not exist !");
        	return;
		}
		
		getLog().info("Get JavaScript files from " + this.getJavaScriptDir().getAbsolutePath());
    	Collection<File> files = FileUtils.listFiles(this.getJavaScriptDir(), new String[]{"js"}, true);
    	
    	if(files.isEmpty()){
			getLog().warn("JavaScript sources directory "+this.getJavaScriptDir().getAbsolutePath()+" is empty !");
			return;
		}
        	
        try {	
            for(File file : files){
            	copy(file);   
            }            
    	} catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on JsCompilerMojo : cannot copy", e);
        }        
    }
    
    public boolean accept(File file) {
    	return !isSkipped()
    		&& file.getParent().contains( getJavaScriptDir().getAbsolutePath() )
    		&& FSUtils.hasExtension(file, "js");
    }
    
    public boolean fileCreated(File file) throws WatchingException {
    	this.copy(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
    	if(fileDeleted(file)) {
    		return this.fileCreated(file);
    	} else {
    		return false;
    	}
    }

    public boolean fileDeleted(File file) {
    	File deleted = new File(this.getWorkDirectory(), file.getName());
        if (deleted.isFile()){
        	getLog().info("Deleting File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }
    

    private void copy(File f) throws WatchingException {
    	getLog().info("Copy JavaScript files from " + this.getJavaScriptDir().getAbsolutePath());
    	try {
			FileUtils.copyFileToDirectory(f, this.getWorkDirectory());
		} catch (IOException e) { 
			throw new WatchingException("Error during copy files to workDirectory "+this.getWorkDirectory().getAbsolutePath(), e); 
		}
    }
    
    private boolean isSkipped(){
    	if (skipJsCompilation) {
            getLog().info("\033[31m JS Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}
