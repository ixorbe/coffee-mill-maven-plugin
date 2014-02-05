package org.nanoko.coffeemill.mojos.stylesheets.css;


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
 * Minifying Css files.
 */
@Mojo(name = "minify-stylesheets", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CssMinifierMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String CLEANCSS_NPM_NAME = "clean-css";
    public static final String CLEANCSS_NPM_VERSION = "2.0.7";
    
    private NPM cleancss;


    public void execute() throws MojoExecutionException {

        cleancss = npm(new MavenLoggerWrapper(this.getLog()), CLEANCSS_NPM_NAME, CLEANCSS_NPM_VERSION);
        try {
        	compile();
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean accept(File file) {
        return  FSUtils.hasExtension(file, stylesheetsExtensions);
    }

    public void compile() throws WatchingException {
    	
    	String fileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	File input = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+fileName+".css");
    	if(!input.exists())
    		return;
    	File output = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+fileName+"-min.css");
 	
    	if(output.exists())
    		FileUtils.deleteQuietly(output);
    	
        getLog().info("Minifying " + input.getAbsolutePath() + " to " + output.getAbsolutePath());
        int exit = cleancss.execute("cleancss", "-o",  output.getAbsolutePath(),input.getAbsolutePath());
		getLog().debug("Css minification execution exiting with " + exit + " status");

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
