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
@Mojo(name = "minify-javascript2", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsNPMMinifierMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String PKG_NPM_NAME = "uglify-js";
    public static final String PKG_NPM_VERSION = "2.4.12";
    
    private NPM ugly;


    public void execute() throws MojoExecutionException {

    	ugly = npm(new MavenLoggerWrapper(this.getLog()), PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
        	compile();
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean accept(File file) {
        return  FSUtils.hasExtension(file, scriptExtensions);
    }

    public void compile() throws WatchingException {
    	
    	String fileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	File input = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+fileName+".js");
    	if(!input.exists())
    		return;
    	File output = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+fileName+"-min.js");
 	
    	if(output.exists())
    		FileUtils.deleteQuietly(output);
    	
        getLog().info("Minifying " + input.getAbsolutePath() + " to " + output.getAbsolutePath());
        int exit = ugly.execute("uglifyjs",input.getAbsolutePath(), "-o",  output.getAbsolutePath(),"-c");
		getLog().debug("Js minification execution exiting with " + exit + " status");

        if (!output.isFile()) {
            throw new WatchingException("Error during the minification of " + input.getAbsoluteFile() + " check log");
        }
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

}
