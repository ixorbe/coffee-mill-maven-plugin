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
@Mojo(name = "aggregate-stylesheets", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CssAggregatorMojo extends AbstractCoffeeMillWatcherMojo {
	
	
	public String outputFileName = null;
	
    public void execute() throws MojoExecutionException {
    	if(isSkipped())
    		return;
        try {
            if ( this.getWorkDirectory().isDirectory()) {
                this.aggregate();
            }
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() && FSUtils.hasExtension(file, stylesheetsExtensions);
    }

    public void aggregate() throws WatchingException {
    	getLog().info("Aggregate css files from " + this.getWorkDirectory().getAbsolutePath());
    	if(this.outputFileName == null)
    		this.outputFileName = this.project.getArtifactId()+"-"+this.project.getVersion();
    	File output = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+this.outputFileName+".css");
    	
    	if(output.exists())
    		FileUtils.deleteQuietly(output);
    	
        Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"css"}, true);
        if(files.size()==0)
        	return;
        try {
			FileAggregation.joinFiles( output, files);
		} catch (IOException e) {
			e.printStackTrace();
		}

        if (!output.isFile()) {
            throw new WatchingException("Error during the CSS aggregation check log");
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
    	if (skipCssAggregation || skipCssCompilation) {
            getLog().info("\033[31m CSS Aggregation skipped \033[37m");
            return true;
        }
    	else return false;
    }

}
