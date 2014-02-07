package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.FileAggregation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * Compiles Js files.
 */
@Mojo(name = "aggregate-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsAggregatorMojo extends AbstractCoffeeMillWatcherMojo {
	
	public String inputFileName;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
    		if (!this.getWorkDirectory().isDirectory()){
            	getLog().warn("JavaScript aggregation skipped - " + this.getWorkDirectory() + " does not exist !");
            	return;
            }    		
    		
            this.aggregate();
            
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
		getLog().info("JS AGGRE : ACCEPT : " +file.getName() + " : " +FSUtils.hasExtension(file, scriptExtensions));
        return FSUtils.hasExtension(file, scriptExtensions);
    }

    public void aggregate() throws WatchingException {
    	Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"js"}, true);
        if(files.isEmpty()){
        	getLog().warn("JavaScript work directory "+this.getWorkDirectory().getAbsolutePath()+" is empty !");
        	return;
        }
        
    	getLog().info("Aggregate Js files from " + this.getWorkDirectory().getAbsolutePath());
    	
    	if(this.inputFileName == null)
    		this.inputFileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	
    	File output = new File( this.getBuildDirectory().getAbsolutePath() + File.separator + this.inputFileName + ".js");
    	if(output.exists())
    		FileUtils.deleteQuietly(output);    	
        
        try {
			FileAggregation.joinFiles( output, files);
		} catch (IOException e) {
			e.printStackTrace();
		}

        if (!output.isFile()) {
            throw new WatchingException("Error during the Js aggregation check log");
        }
    }


    public boolean fileCreated(File file) throws WatchingException {
    	this.aggregate();
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
    	this.aggregate();
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException {
    	this.aggregate();
        return true;
    }

}
