package org.nanoko.coffeemill.mojos.processResources;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import java.io.File;
import java.io.IOException;


/**
 * Compiles less files.
 */
@Mojo(name = "copy-assets", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CopyAssetsMojo extends AbstractCoffeeMillWatcherMojo {
	
	@Parameter(defaultValue="false")
	protected boolean skipAssetsCopy;
		
    public void execute() throws MojoExecutionException {
    	
    	if(isSkipped()) { 
    		return; 
    	}
    	
    	if (!this.getAssetsDir().isDirectory()){
        	getLog().warn("/!\\ Copy assets skipped - " + this.getAssetsDir().getAbsolutePath() + " does not exist !");
        	return;
        }
    	
    	File[] sourceAssets = getAssetsDir().listFiles();
    	try {
	    	for(File file : sourceAssets){
	    		if(file.isDirectory()){
		    		FileUtils.copyDirectoryToDirectory(file, getWorkDirectory());					
	    		}else{
	    			if(file.isFile() && !FSUtils.hasExtension(file, "js","css")){
	    				FileUtils.copyFileToDirectory(file, getWorkDirectory());
	    			}
	    		}
	    	}
    	} catch (IOException e) {
    		throw new MojoExecutionException("Error during copy assets from source directory : "+e.getMessage(), e);
		}
    }


    public boolean accept(File file) {
        return !isSkipped() && file.getParent().contains( getAssetsDir().getAbsolutePath() );
    }

    public void copy(File f) throws WatchingException {
    	getLog().info("Copy Asset file "+f.getName() + " to " + this.getWorkDirectory().getAbsolutePath()  );
    	try {
    		
    		File relativeWorkFile = FSUtils.computeRelativeFile(f, getAssetsDir(), this.getWorkDirectory());	
    		
    		if (relativeWorkFile.getParentFile() != null) {
    			relativeWorkFile.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(f, relativeWorkFile.getParentFile());
            } else {
                getLog().error("Cannot copy file - parent directory not accessible for " + relativeWorkFile);
            }
			
		} catch (IOException e) {
			throw new WatchingException(e.getMessage(), e); 
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
        File deletedFromWork = FSUtils.computeRelativeFile(file, getAssetsDir(), this.getWorkDirectory());
        if (deletedFromWork.isFile()){
        	getLog().info("deleting File : "+file.getName()+" from "+this.getWorkDirectory());    	
        	FileUtils.deleteQuietly(deletedFromWork); 
        }
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipAssetsCopy) {
            getLog().info("\033[31m Asset copy skipped \033[37m");
            return true;
        } else{
        	return false;
        }
    }

}
