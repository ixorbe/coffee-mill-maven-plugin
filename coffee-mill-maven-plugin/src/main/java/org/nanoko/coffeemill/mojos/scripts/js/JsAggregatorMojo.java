package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.FileAggregation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Compiles Js files.
 */
@Mojo(name = "aggregate-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsAggregatorMojo extends AbstractCoffeeMillWatcherMojo {
	
	public String outputFileName;
	
	/**
     * Define ordered Js files list to aggregate
     */
	@Parameter
    protected List<String> jsAggregationFiles;
	
	@Parameter(defaultValue="true")
	private boolean failedOnMissingFile;
	

    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
    		if(isSkipped()) { 
    			return; 
    			}
    		
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
    	return !isSkipped() && FSUtils.hasExtension(file, scriptExtensions);
    }

    public void aggregate() throws WatchingException {
    	if(this.outputFileName == null) {
    		this.outputFileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	}
    	
    	File output = new File( this.getBuildDirectory(), this.outputFileName + ".js");
    	if(output.exists()) {
    		FileUtils.deleteQuietly(output); 
    	}
    	
    	// Classic Aggregation (app + ext. libs)
    	if (jsAggregationFiles == null || jsAggregationFiles.isEmpty()) {
    		
    		if(aggregateAppOnly(output)) {
    				aggregateAppWithLibs(output);
        	}    		
        } 
    	// Aggregation from pom.xml JsAggregationFiles list
    	else {
        	aggregateFromListFiles(output);        	
        }
    	
    }
    
    
    private boolean aggregateFromListFiles(File output) throws WatchingException {
    	Collection<File> files = new ArrayList<File>();
    	
    	for (String filename : jsAggregationFiles) {
            File file = FSUtils.resolveFile(filename, getWorkDirectory(), getLibDirectory(), "js");
            if (file == null) {
                if (failedOnMissingFile) {
                    throw new WatchingException("Aggregation failed : " + filename + " file missing in " + getWorkDirectory().getAbsolutePath());
                } else {
                    getLog().warn("Issue detected during aggregation : " + filename + " missing");
                }
            } else {
                // The file exists.
                files.add(file);
            }
        }
    	
    	joinFiles(output, files);
        return true;
    }
    

    private boolean aggregateAppOnly(File output) throws WatchingException {
    	Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"js"}, false);
        if(files.isEmpty()){
        	getLog().warn("JavaScript work directory "+this.getWorkDirectory().getAbsolutePath()+" is empty !");
        	return false;
        }
    	getLog().info("Aggregate Js files from " + this.getWorkDirectory().getAbsolutePath());
    	
    	joinFiles(output, files);
        return true;
    }
    
    private void aggregateAppWithLibs(File in) throws WatchingException{
    	File output = new File(this.getBuildDirectory(),  this.outputFileName+"-all.js");
    	if(output.exists()){
    		FileUtils.deleteQuietly(output);
    	}
    	
    	Collection<File> files = FileUtils.listFiles(this.getLibDirectory(), new String[]{"js"}, true);

        if(files.isEmpty()){
        	getLog().warn("JavaScript External libraries directory "+this.getLibDirectory().getAbsolutePath()+" is empty !");
        	try {
				FileUtils.copyFile(in, output);
			} catch (IOException e) {
				this.getLog().error(e.getMessage(), e);
			}
        	return;
        }
        
        files.add(in);
    	getLog().info("Aggregate Js files from " + this.getLibDirectory().getAbsolutePath());
    	  
    	joinFiles(output, files);
    }
    
    
    private void joinFiles(File output, Collection<File> files) throws WatchingException{
    	try {
 			FileAggregation.joinFiles( output, files);
 		} catch (IOException e) {
 			this.getLog().error(e.getMessage(), e);
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
    
    private boolean isSkipped(){
    	if (skipJsAggregation || skipJsCompilation) {
            getLog().info("\033[31m JS Aggregation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }


}
