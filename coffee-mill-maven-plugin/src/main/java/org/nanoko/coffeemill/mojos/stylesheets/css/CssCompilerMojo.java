package org.nanoko.coffeemill.mojos.stylesheets.css;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * Compiles less files.
 */
@Mojo(name = "compile-css", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CssCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public void execute() throws MojoExecutionException {
    	
    	if(isSkipped()) { 
    		return; 
    	}
    	
        try {
            if ( getStylesheetsDir().isDirectory()) {
            	Collection<File> files = FileUtils.listFiles(getStylesheetsDir(), new String[]{"css"}, true);
                for(File f: files) {
        			copy(f);
                }
            }
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() 
        	//&& FSUtils.isInDirectory(file.getName(), this.stylesheetsDir)
        	&& file.getParent().contains( getStylesheetsDir().getAbsolutePath() )
        	&& FSUtils.hasExtension(file, "css");
    }

    public void copy(File f) throws WatchingException {
    	getLog().info("Copy css files from " + getStylesheetsDir().getAbsolutePath());
    	try {
			FileUtils.copyFileToDirectory(f, this.getWorkDirectory());
		} catch (IOException e) { 
			this.getLog().error(e); 
		}
    }


    public boolean fileCreated(File file) throws WatchingException {
    	this.copy(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
    	if(fileDeleted(file)) {
    		this.copy(file);
        	return true;
    	} else { 
    		return false;
    	}
    }

    public boolean fileDeleted(File file) throws WatchingException {        
        File deleted = new File(this.getWorkDirectory(), file.getName());
        if (deleted.isFile()){
        	getLog().info("deleting File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipCssCompilation) {
            getLog().info("\033[31m CSS Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}
