package org.nanoko.coffeemill.mojos.stylesheets.css;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
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
 * Compiles less files.
 */
@Mojo(name = "compile-css", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CssCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public void execute() throws MojoExecutionException {
        try {
            if ( this.stylesheetsDir.isDirectory()) {
            	Collection<File> files = FileUtils.listFiles(this.stylesheetsDir, new String[]{"css"}, true);
                for(File f: files)
        			copy(f);
            }
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
        return FSUtils.isInDirectory(file, this.workDir) && FSUtils.hasExtension(file, "css");
    }

    public void copy(File f) throws WatchingException {
    	getLog().info("Copy css files from " + this.stylesheetsDir.getAbsolutePath());
    	try {
			FileUtils.copyFileToDirectory(f, this.getWorkDirectory());
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
    	File outFile = new File(this.getWorkDirectory().getAbsolutePath()+File.pathSeparator+file.getName());
    	if ( FSUtils.isInDirectory(outFile, this.getWorkDirectory()));
    		FileUtils.deleteQuietly(outFile);
        return true;
    }

}
