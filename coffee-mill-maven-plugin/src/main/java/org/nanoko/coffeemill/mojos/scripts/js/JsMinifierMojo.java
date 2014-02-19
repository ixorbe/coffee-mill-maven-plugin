package org.nanoko.coffeemill.mojos.scripts.js;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.java.NPM;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;

import org.nanoko.maven.WatchingException;

import java.io.File;


import static org.nanoko.java.NPM.npm;

/**
 * Minifying Js files.
 */
@Mojo(name = "minify-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsMinifierMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String PKG_NPM_NAME = "uglify-js";
    public static final String PKG_NPM_VERSION = "2.4.12";
    
    public String inputFileName;
    
    private NPM ugly;
    

    public void execute() throws MojoExecutionException {    	
    	if(isSkipped()) { 
    		return; 
    	}    				

    	ugly = npm(new MavenLoggerWrapper(this.getLog()), PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
        	compile();
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on JsMinifierMojo : cannot compile", e);
        }
    }

    public boolean accept(File file) {
        return !isSkipped() && FSUtils.hasExtension(file, scriptExtensions);
    }
    
    public boolean fileCreated(File file) throws WatchingException {
        compile();
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        compile();
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException{
    	compile();
        return true;
    }

    
    private boolean compile() throws WatchingException {
    	getLog().info("Js Minification Compilation");
		
    	if(this.inputFileName == null) {
    		this.inputFileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	}
    	
    	boolean res = minify(this.inputFileName+"-all");
    	boolean res2 = minify(this.inputFileName);
    	return res || res2;
    }
    
    private boolean minify( String baseName) throws WatchingException {
    	// check if input is valid
    	File input = new File( this.getBuildDirectory(), baseName+".js");
		if(!input.exists()) {
			return false;
		}
		
    	// if output exist, delete it
    	File output = new File( this.getBuildDirectory(), baseName+"-min.js");
        if(output.exists()) {
        	FileUtils.deleteQuietly(output);
        }
        
        getLog().info("Minifying " + input.getAbsolutePath() + " to " + output.getAbsolutePath());
        int exit = ugly.execute("uglifyjs",input.getAbsolutePath(), "-o",  output.getAbsolutePath(),"-c");
		getLog().debug("Js minification execution exiting with " + exit + " status");

        if (!output.isFile()) {
            throw new WatchingException("Error during the minification of " + input.getAbsoluteFile() + " check log");
        }
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipJsMinification || skipJsAggregation || skipJsCompilation) {
            getLog().info("\033[31m JS Minification skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}
