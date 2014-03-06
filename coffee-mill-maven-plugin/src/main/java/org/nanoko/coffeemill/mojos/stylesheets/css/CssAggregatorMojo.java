package org.nanoko.coffeemill.mojos.stylesheets.css;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
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
 * Aggregate CSS files.
 */
@Mojo(name = "aggregate-stylesheets", threadSafe = false,
requiresDependencyResolution = ResolutionScope.TEST,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class CssAggregatorMojo extends AbstractCoffeeMillWatcherMojo {


    public String outputFileName = null;

    /**
     * Define ordered Css files list to aggregate
     */
    @Parameter
    protected List<String> cssAggregationFiles;

    @Parameter(defaultValue="true")
    protected boolean failedOnMissingFile;


    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

        try {
            if ( this.getWorkDirectory().isDirectory()) {
                this.aggregate();
            }
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on CssAggregatorMojo : cannot aggregate", e);
        }
    }

    public boolean accept(File file) {
        return !isSkipped() && FSUtils.hasExtension(file, getStylesheetsextensions());
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


    private void aggregate() throws WatchingException {
        if(this.outputFileName == null) {
            this.outputFileName = this.project.getArtifactId()+"-"+this.project.getVersion();
        }

        File output = new File( this.getBuildDirectory(), this.outputFileName + ".css");
        if(output.exists()) {
            FileUtils.deleteQuietly(output);   
        }    	

        // Classic Aggregation (app + ext. libs)
        if (cssAggregationFiles == null || cssAggregationFiles.isEmpty()) {    		
            if(aggregateAppOnly(output)) {
                try {
                    aggregateAppWithLibs(output);
                } catch (IOException e) {
                    throw new WatchingException("Error during aggregation files", e);
                }
            }    
            // else aggregate from pom.xml CssAggregationFiles list
        } else {
            aggregateFromListFiles(output);        	
        }    	
    }

    private boolean aggregateFromListFiles(File output) throws WatchingException {
        Collection<File> files = new ArrayList<File>();

        for (String filename : cssAggregationFiles) {
            File file = FSUtils.resolveFile(filename, getWorkDirectory(), getLibDirectory(), "css");
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
        if(projectHelper != null ){
            projectHelper.attachArtifact(project, "css", output); // "css" -> type (not classifier)
        }
        return true;
    }

    private boolean aggregateAppOnly(File output) throws WatchingException {
        Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"css"}, true);
        if(files.isEmpty()){
            getLog().warn("No Css files in work directory "+this.getWorkDirectory().getAbsolutePath());
            return false;
        }
        getLog().info("Aggregate Css files from " + this.getWorkDirectory().getAbsolutePath());

        joinFiles(output, files);
        if(projectHelper != null ){
            projectHelper.attachArtifact(project, "css", output);// "css" -> type (not classifier)
        }
        return true;
    }

    private void aggregateAppWithLibs(File in) throws WatchingException, IOException {
        File output = new File(this.getBuildDirectory(),  this.outputFileName+"-all.css");
        if(output.exists()) {
            FileUtils.deleteQuietly(output);    
        }

        Collection<File> files = FileUtils.listFiles(this.getLibDirectory(), new String[]{"css"}, true);
        if(files.isEmpty()){
            getLog().warn("JavaScript External libraries directory "+this.getLibDirectory().getAbsolutePath()+" is empty !");
            FileUtils.copyFile(in, output);
            return;
        }

        files.add(in);
        getLog().info("Aggregate Css files from " + this.getLibDirectory().getAbsolutePath());

        joinFiles(output, files);        
    }

    private void joinFiles(File output, Collection<File> files) throws WatchingException{
        try {
            FileAggregation.joinFiles( output, files);
        } catch (IOException e) {
            throw new WatchingException("Error during aggregation files", e);
        }

        if (!output.isFile()) {
            throw new WatchingException("Error during the Css aggregation check log");
        }
    }

    private boolean isSkipped(){
        if (skipCssAggregation || skipCssCompilation) {
            getLog().info("\033[31m CSS Aggregation skipped \033[37m");
            return true;
        } else {
            return false;
        }
    }

}
