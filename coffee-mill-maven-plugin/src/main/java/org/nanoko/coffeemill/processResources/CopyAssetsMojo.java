package org.nanoko.coffeemill.processResources;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;

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
    	if(isSkipped())
    		return;
    	if (!this.getAssetsDir().isDirectory()){
        	getLog().warn("/!\\ Copy assets skipped - " + this.getAssetsDir().getAbsolutePath() + " does not exist !");
        	return;
        }
    	
        try {   
        	FileUtils.copyDirectory(this.getAssetsDir(), this.getWorkDirectory());
        	FileUtils.copyDirectory(this.getAssetsDir(), this.getBuildDirectory());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() && file.getParent().contains( getAssetsDir().getAbsolutePath() );
    }

    public void copy(File f) throws WatchingException {
    	getLog().info("Copy Asset file "+f.getName()
    			+" to "+this.getWorkDirectory().getAbsolutePath()
    			+" and to "+this.getBuildDirectory().getAbsolutePath() );
    	try {
    		
    		File relativeWorkFile = computeRelativeFile(f, getAssetsDir(), this.getWorkDirectory());
    		File relativeBuildFile = computeRelativeFile(f, getAssetsDir(), this.getBuildDirectory());
    		getLog().info("relativeWorkFile : "+relativeWorkFile);
    		getLog().info("relativeBuildFile : "+relativeBuildFile);  		
    		
    		if (relativeWorkFile.getParentFile() != null) {
    			relativeWorkFile.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(f, relativeWorkFile.getParentFile());
            } else 
                getLog().error("Cannot copy file - parent directory not accessible for " + relativeWorkFile);
            
    		if (relativeBuildFile.getParentFile() != null) {
    			relativeBuildFile.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(f, relativeBuildFile.getParentFile());
            } else 
                getLog().error("Cannot copy file - parent directory not accessible for " + relativeBuildFile);
            
			
		} catch (IOException e) {
			throw new WatchingException(e.getMessage(), e); }
    }


    public boolean fileCreated(File file) throws WatchingException {
    	this.copy(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
    	if(fileDeleted(file)) {
    		this.copy(file);
        	return true;
    	} else
    		return false;
    }

    public boolean fileDeleted(File file) throws WatchingException {
        File deletedFromWork = computeRelativeFile(file, getAssetsDir(), this.getWorkDirectory());
        if (deletedFromWork.isFile()){
        	getLog().info("deleting File : "+file.getName()+" from "+this.getWorkDirectory());    	
        	FileUtils.deleteQuietly(deletedFromWork); 
        }
        File deletedFromBuild = computeRelativeFile(file, getAssetsDir(), this.getBuildDirectory());
        if (deletedFromBuild.isFile()){
        	getLog().info("deleting File : "+file.getName()+" from "+this.getBuildDirectory());    	
        	FileUtils.deleteQuietly(deletedFromBuild); 
        }
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipAssetsCopy) {
            getLog().info("\033[31m Asset copy skipped \033[37m");
            return true;
        }
    	else return false;
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

}
