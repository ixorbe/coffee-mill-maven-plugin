package org.nanoko.coffeemill.processResources;


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
    	if(isSkipped())
    		return;
        try {
            if ( this.assetsDir.isDirectory()) {
            	FileUtils.copyDirectory(this.assetsDir, new File(this.getBuildDirectory(),"assets"));
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() && FSUtils.isInDirectory(file.getName(), this.assetsDir);
    }

    public void copy(File f) throws WatchingException {
    	
    	try {
    		String baseUrl = f.getAbsolutePath().substring(0, this.assetsDir.getAbsolutePath().length());
    		baseUrl = baseUrl.substring(0, - f.getName().length());
    		System.out.println("***************************************");
    		System.out.println(baseUrl);
			FileUtils.copyFileToDirectory(f, new File(this.getBuildDirectory(),"assets/"+baseUrl));
		} catch (IOException e) { e.printStackTrace(); }
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
        File deleted = new File(this.getWorkDirectory().getAbsolutePath() + File.separator + file.getName());
        if (deleted.isFile()){
        	getLog().info("deleting File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
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

}
